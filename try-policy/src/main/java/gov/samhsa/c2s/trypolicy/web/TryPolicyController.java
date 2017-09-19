package gov.samhsa.c2s.trypolicy.web;

import gov.samhsa.c2s.trypolicy.service.TryPolicyService;
import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/tryPolicyXHTML/{patientId}")
    public TryPolicyResponse tryPolicyByConsentIdXHTML(@PathVariable String patientId,
                                                       @RequestParam String consentId,
                                                       @RequestParam String documentId,
                                                       @RequestParam String purposeOfUseCode) {
        return tryPolicyService.getSegmentDocXHTML(patientId, consentId, documentId, purposeOfUseCode);
    }
}