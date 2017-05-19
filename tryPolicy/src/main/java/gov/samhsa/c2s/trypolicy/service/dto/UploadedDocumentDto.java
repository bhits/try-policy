package gov.samhsa.c2s.trypolicy.service.dto;

import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class UploadedDocumentDto {
    private Long documentId;
    private String patientMrn;
    private byte[] documentContents;
    private String documentFileName;
    private String documentName;
    private String documentContentType;
    private String documentDescription;

    @Override
    public String toString() {
        return this.documentContents == null ? "" : new String(this.getDocumentContents(), StandardCharsets.UTF_8);
    }
}