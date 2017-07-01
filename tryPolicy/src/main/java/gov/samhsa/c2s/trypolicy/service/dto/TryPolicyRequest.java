package gov.samhsa.c2s.trypolicy.service.dto;

import lombok.Data;

import java.util.Locale;

@Data
public class TryPolicyRequest {
    String indexOfDocuments;
    String consentId;
    String patientId;
    String purposeOfUseCode;
    Locale locale;
}