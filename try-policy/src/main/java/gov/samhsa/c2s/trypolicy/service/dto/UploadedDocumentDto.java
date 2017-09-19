package gov.samhsa.c2s.trypolicy.service.dto;

import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class UploadedDocumentDto {

    private Long id;
    private String patientMrn;
    private byte[] contents;
    private String fileName;
    private String documentName;
    private String contentType;
    private String description;
    private Long documentTypeCodeId;
    private String documentTypeDisplayName;

    @Override
    public String toString() {
        return this.contents == null ? "" : new String(this.getContents(), StandardCharsets.UTF_8);
    }
}