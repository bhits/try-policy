package gov.samhsa.c2s.trypolicy.service;

import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;

import java.util.List;
import java.util.Locale;

public interface TryPolicyService {
    TryPolicyResponse getSegmentDocXHTML(String patientId, String consentId, String documentId, String purposeOfUse, Locale locale);

    List<SampleDocDto> getSampleDocuments();
}