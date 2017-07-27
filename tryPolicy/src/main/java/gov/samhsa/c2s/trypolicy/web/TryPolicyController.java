package gov.samhsa.c2s.trypolicy.web;

import gov.samhsa.c2s.trypolicy.service.TryPolicyService;
import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
public class TryPolicyController {

    private final TryPolicyService tryPolicyService;

    @Autowired
    public TryPolicyController(TryPolicyService tryPolicyService) {
        this.tryPolicyService = tryPolicyService;
    }

    @GetMapping("/sampleDocuments")
    public List<SampleDocDto> getSampleDocuments() {
        return tryPolicyService.getSampleDocuments();
    }

    @GetMapping("/tryPolicySampleXHTML/{patientId}")
    public TryPolicyResponse tryPolicyXHTMLWithSampleDoc(@PathVariable String patientId,
                                                         @RequestParam String consentId,
                                                         @RequestParam int documentId,
                                                         @RequestParam String purposeOfUseCode,
                                                         @RequestHeader("Accept-Language") Locale locale) {
        return tryPolicyService.getSegmentDocXHTMLUseSampleDoc(patientId, consentId, documentId, purposeOfUseCode, locale);
    }

    @RequestMapping(value = "/tryPolicyXHTML", method = RequestMethod.GET)
    public TryPolicyResponse tryPolicyByConsentIdXHTML(@RequestParam("documentId") String documentId,
                                                       @RequestParam("consentId") String consentId,
                                                       @RequestParam("patientId") String patientId,
                                                       @RequestParam("purposeOfUseCode") String purposeOfUseCode,
                                                       @RequestHeader("Accept-Language") Locale locale) {
        return tryPolicyService.getSegmentDocXHTML(documentId, consentId, patientId, purposeOfUseCode, locale);
    }
}