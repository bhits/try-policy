package gov.samhsa.c2s.trypolicy.service;

import gov.samhsa.c2s.common.document.converter.DocumentXmlConverter;
import gov.samhsa.c2s.common.document.transformer.XmlTransformer;
import gov.samhsa.c2s.common.log.Logger;
import gov.samhsa.c2s.common.log.LoggerFactory;
import gov.samhsa.c2s.common.param.Params;
import gov.samhsa.c2s.trypolicy.config.DSSProperties;
import gov.samhsa.c2s.trypolicy.infrastructure.DssService;
import gov.samhsa.c2s.trypolicy.infrastructure.PcmService;
import gov.samhsa.c2s.trypolicy.infrastructure.PhrService;
import gov.samhsa.c2s.trypolicy.infrastructure.dto.SensitivityCategoryDto;
import gov.samhsa.c2s.trypolicy.service.dto.DSSRequest;
import gov.samhsa.c2s.trypolicy.service.dto.DSSResponse;
import gov.samhsa.c2s.trypolicy.service.dto.SubjectPurposeOfUse;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;
import gov.samhsa.c2s.trypolicy.service.dto.UploadedDocumentDto;
import gov.samhsa.c2s.trypolicy.service.dto.XacmlResult;
import gov.samhsa.c2s.trypolicy.service.exception.TryPolicyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.URIResolver;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class TryPolicyServiceImpl implements TryPolicyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DSSProperties dssProperties;

    private final DocumentXmlConverter documentXmlConverter;

    private final XmlTransformer xmlTransformer;

    private final PcmService pcmService;

    private final DssService dssService;

    private final PhrService phrService;

    @Autowired
    public TryPolicyServiceImpl(DSSProperties dssProperties, DocumentXmlConverter documentXmlConverter, XmlTransformer xmlTransformer, PcmService pcmService, DssService dssService, PhrService phrService) {
        this.dssProperties = dssProperties;
        this.documentXmlConverter = documentXmlConverter;
        this.xmlTransformer = xmlTransformer;
        this.pcmService = pcmService;
        this.dssService = dssService;
        this.phrService = phrService;
    }

    @Override
    public TryPolicyResponse getSegmentDocXHTML(String documentId, String consentId, String patientId, String purposeOfUseCode, Locale locale) {
        try {
            UploadedDocumentDto ccdStrDto = phrService.getPatientDocument(patientId, documentId);
            String docStr = new String(ccdStrDto.getCCDFile());
            List<SensitivityCategoryDto> sharedSensitivityCategoryDto = pcmService.getSharedSensitivityCategories(patientId, consentId);

            List<String> sharedSensitivityCategoryValues = sharedSensitivityCategoryDto.stream().map(s-> s.getIdentifier().getValue()).collect(toList());
            DSSRequest dssRequest = createDSSRequest(patientId, docStr, sharedSensitivityCategoryValues, purposeOfUseCode);
            DSSResponse response = dssService.segmentDocument(dssRequest);
            return getTaggedClinicalDocument(response, locale);
        } catch (Exception e) {
            logger.info(() -> "Apply TryPolicy failed: " + e.getMessage());
            logger.debug(e::getMessage, e);
            throw new TryPolicyException();
        }
    }

    private TryPolicyResponse getTaggedClinicalDocument(DSSResponse dssResponse, Locale locale) {
        String segmentedClinicalDocument = new String(dssResponse.getTryPolicyDocument(), StandardCharsets.UTF_8);
        final Document taggedClinicalDocument = documentXmlConverter
                .loadDocument(segmentedClinicalDocument);
        final NodeList taggedClinicalDocumentList = taggedClinicalDocument
                .getElementsByTagName("entry");

        logger.debug("Segmented Clinical Document: " + segmentedClinicalDocument);
        logger.info("Tagged Clinical Document Entry size: " + taggedClinicalDocumentList.getLength());
        logger.info("Is Segmented CCDA document: " + dssResponse.isCCDADocument());

        // xslt transformation
        final String xslUrl = Thread.currentThread().getContextClassLoader().getResource(getLocaleSpecificCdaXSL(locale)).toString();
        final String output = xmlTransformer.transform(taggedClinicalDocument, xslUrl, Optional.<Params>empty(), Optional.<URIResolver>empty());

        TryPolicyResponse tryPolicyResponse = new TryPolicyResponse();
        tryPolicyResponse.setDocument(output.getBytes());
        return tryPolicyResponse;
    }

    private DSSRequest createDSSRequest(String patientId, String ccdStr, List<String> sharedSensitivityCategoryValues, String purposeOfUse) {
        DSSRequest dssRequest = new DSSRequest();
        dssRequest.setAudited(Boolean.valueOf(dssProperties.getDefaultIsAudited()));
        dssRequest.setAuditFailureByPass(Boolean.valueOf(dssProperties.getDefaultIsAuditFailureByPass()));
        dssRequest.setDocument(ccdStr.getBytes(StandardCharsets.UTF_8));
        dssRequest.setEnableTryPolicyResponse(true);
        dssRequest.setDocumentEncoding(dssProperties.getDocumentEncoding());

        XacmlResult xacmlResult = new XacmlResult();
        xacmlResult.setHomeCommunityId(dssProperties.getHomeCommunityId());
        xacmlResult.setMessageId(UUID.randomUUID().toString());
        xacmlResult.setPdpDecision(dssProperties.getPdpDecision());
        xacmlResult.setSubjectPurposeOfUse(SubjectPurposeOfUse.fromAbbreviation(purposeOfUse));
        xacmlResult.setPatientId(patientId);
        xacmlResult.setPdpObligations(sharedSensitivityCategoryValues);

        dssRequest.setXacmlResult(xacmlResult);

        return dssRequest;
    }

    private static String getLocaleSpecificCdaXSL(Locale locale) {

        if(locale == null){
            return "CDA_flag_redact.xsl";
        }
        if(locale.getLanguage().equalsIgnoreCase("en")){
            return "CDA_flag_redact.xsl";
        } else if(locale.getLanguage().equalsIgnoreCase("es")){
            return "CDA_flag_redact_spanish.xsl";
        } else {
            //Default/Unsupported language
            return "CDA_flag_redact.xsl";
        }
    }
}