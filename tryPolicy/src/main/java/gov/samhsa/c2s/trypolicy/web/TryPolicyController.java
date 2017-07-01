package gov.samhsa.c2s.trypolicy.web;

import gov.samhsa.c2s.trypolicy.service.TryPolicyService;
import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyRequest;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/tryPolicySampleXHTML")
    public TryPolicyResponse tryPolicyByConsentIdXHTMLUsSampleDoc(@RequestBody TryPolicyRequest request) {
        return tryPolicyService.getSegmentDocXHTMLUseSampleDoc(request);
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