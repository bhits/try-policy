package gov.samhsa.c2s.trypolicy.service;

import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;

import java.util.List;

public interface TryPolicyService {
    TryPolicyResponse getSegmentDocXHTML(String patientId, String consentId, String documentId, String purposeOfUse);

    List<SampleDocDto> getSampleDocuments();
}