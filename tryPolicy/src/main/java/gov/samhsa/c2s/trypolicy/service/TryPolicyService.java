package gov.samhsa.c2s.trypolicy.service;

import gov.samhsa.c2s.trypolicy.service.dto.SampleDocDto;
import gov.samhsa.c2s.trypolicy.service.dto.TryPolicyResponse;

import java.util.List;
import java.util.Locale;

public interface TryPolicyService {
    TryPolicyResponse getSegmentDocXHTML(String documentId, String consentId, String patientId, String purposeOfUse, Locale locale);

    List<SampleDocDto> getSampleDocuments();

    TryPolicyResponse getSegmentDocXHTMLUseSampleDoc(String patientId, String consentId, int documentId, String purposeOfUseCode, Locale locale);
}