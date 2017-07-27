package gov.samhsa.c2s.trypolicy.service;

import gov.samhsa.c2s.common.document.converter.DocumentXmlConverter;
import gov.samhsa.c2s.common.document.transformer.XmlTransformer;
import gov.samhsa.c2s.common.log.Logger;
import gov.samhsa.c2s.common.log.LoggerFactory;
import gov.samhsa.c2s.common.param.Params;
import gov.samhsa.c2s.trypolicy.config.DSSProperties;
import gov.samhsa.c2s.trypolicy.config.TryPolicyProperties;
import gov.samhsa.c2s.trypolicy.infrastructure.DssService;
import gov.samhsa.c2s.trypolicy.infrastructure.PcmService;
import gov.samhsa.c2s.trypolicy.infrastructure.PhrService;
import gov.samhsa.c2s.trypolicy.infrastructure.dto.SensitivityCategoryDto;
import gov.samhsa.c2s.trypolicy.service.dto.DSSRequest;
import gov.samhsa.c2s.trypolicy.service.dto.DSSResponse;
import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.SubjectPurposeOfUse;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;
import gov.samhsa.c2s.trypolicy.service.dto.UploadedDocumentDto;
import gov.samhsa.c2s.trypolicy.service.dto.XacmlResult;
import gov.samhsa.c2s.trypolicy.service.exception.NoDocumentsFoundException;
import gov.samhsa.c2s.trypolicy.service.exception.TryPolicyException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.URIResolver;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class TryPolicyServiceImpl implements TryPolicyService {

    private final static String CDA_XSL_ENGLISH = "CDA_flag_redact.xsl";

    private final static String CDA_XSL_SPANISH = "CDA_flag_redact_spanish.xsl";
    private static final String ENGLISH_CODE = "en";
    private static final String SPANISH_CODE = "es";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DSSProperties dssProperties;

    private final TryPolicyProperties tryPolicyProperties;

    private final DocumentXmlConverter documentXmlConverter;

    private final XmlTransformer xmlTransformer;

    private final PcmService pcmService;

    private final DssService dssService;

    private final PhrService phrService;

    @Autowired
    public TryPolicyServiceImpl(DSSProperties dssProperties,
                                TryPolicyProperties tryPolicyProperties,
                                DocumentXmlConverter documentXmlConverter,
                                XmlTransformer xmlTransformer, PcmService pcmService,
                                DssService dssService, PhrService phrService) {
        this.dssProperties = dssProperties;
        this.tryPolicyProperties = tryPolicyProperties;
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
            String docStr = new String(ccdStrDto.getContents());
            List<SensitivityCategoryDto> sharedSensitivityCategoryDto = pcmService.getSharedSensitivityCategories(patientId, consentId);

            List<String> sharedSensitivityCategoryValues = sharedSensitivityCategoryDto.stream().map(s -> s.getIdentifier().getValue()).collect(toList());
            DSSRequest dssRequest = createDSSRequest(patientId, docStr, sharedSensitivityCategoryValues, purposeOfUseCode);
            DSSResponse response = dssService.segmentDocument(dssRequest);
            return getTaggedClinicalDocument(response, locale);
        } catch (Exception e) {
            logger.info(() -> "Apply TryPolicy failed: " + e.getMessage());
            logger.debug(e::getMessage, e);
            throw new TryPolicyException();
        }
    }

    @Override
    public TryPolicyResponse getSegmentDocXHTMLUseSampleDoc(String patientId, String consentId, int documentId, String purposeOfUseCode, Locale locale) {
        try {
            String docStr = getSampleDocByDocumentId(documentId);
            List<String> sharedSensitivityCategoryValues = pcmService.getSharedSensitivityCategories(patientId, consentId)
                    .stream()
                    .map(sensitivityCategoryDto -> sensitivityCategoryDto.getIdentifier().getValue())
                    .collect(toList());
            DSSRequest dssRequest = createDSSRequest(patientId, docStr, sharedSensitivityCategoryValues, purposeOfUseCode);
            DSSResponse response = dssService.segmentDocument(dssRequest);
            return getTaggedClinicalDocument(response, locale);
        } catch (Exception e) {
            logger.error(() -> "Apply TryPolicy failed: " + e);
            logger.debug(e::getMessage, e);
            throw new TryPolicyException("Apply TryPolicy failed: " + e);
        }
    }

    @Override
    public List<SampleDocDto> getSampleDocuments() {
        return tryPolicyProperties.getSampleUploadedDocuments()
                .stream()
                .sorted(Comparator.comparing(TryPolicyProperties.SampleDocData::getFilePath))
                .map(sampleDocData -> SampleDocDto.builder()
                        .id(assignNegativeIndexAsDocumentId(sampleDocData))
                        .isSampleDocument(true)
                        .documentName(sampleDocData.getDocumentName())
                        .filePath(sampleDocData.getFilePath())
                        .build())
                .collect(toList());
    }

    /**
     * Assign negative index as unique document ID to all configured sample clinical documents
     *
     * @param sampleDocData
     * @return
     */
    private int assignNegativeIndexAsDocumentId(TryPolicyProperties.SampleDocData sampleDocData) {
        int indexOfSampleDocuments = tryPolicyProperties.getSampleUploadedDocuments().indexOf(sampleDocData);
        return (indexOfSampleDocuments + 1) * -1;
    }

    private String getSampleDocByDocumentId(int documentId) {
        String SampleDocFilePath = getSampleDocuments().stream()
                .filter(sampleDocDto -> sampleDocDto.getId() == documentId)
                .peek(sampleDocDto -> {
                    if (!sampleDocDto.isSampleDocument()) {
                        throw new NoDocumentsFoundException("The document is not sample document with DocumentId: " + documentId);
                    }
                })
                .map(SampleDocDto::getFilePath)
                .findAny()
                .orElseThrow(NoDocumentsFoundException::new);

        try {
            ClassPathResource classPathResource = new ClassPathResource(SampleDocFilePath);
            InputStream sampleDocInputStream = classPathResource.getInputStream();
            return new String(IOUtils.toByteArray(sampleDocInputStream));
        } catch (Exception e) {
            logger.error(() -> "Unable to get sample document: " + e);
            logger.debug(e::getMessage, e);
            throw new NoDocumentsFoundException("Unable to get sample document");
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
        //TODO: Purpose of Use should come from PCM service instead of using a enum
        xacmlResult.setSubjectPurposeOfUse(SubjectPurposeOfUse.fromAbbreviation(purposeOfUse));
        xacmlResult.setPatientId(patientId);
        xacmlResult.setPdpObligations(sharedSensitivityCategoryValues);

        dssRequest.setXacmlResult(xacmlResult);

        return dssRequest;
    }

    private static String getLocaleSpecificCdaXSL(Locale locale) {
        if (locale == null) {
            return CDA_XSL_ENGLISH;
        }
        if (locale.getLanguage().equalsIgnoreCase(ENGLISH_CODE)) {
            return CDA_XSL_ENGLISH;
        } else if (locale.getLanguage().equalsIgnoreCase(SPANISH_CODE)) {
            return CDA_XSL_SPANISH;
        } else {
            //Default/Unsupported language
            return CDA_XSL_ENGLISH;
        }
    }
}