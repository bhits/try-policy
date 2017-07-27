package gov.samhsa.c2s.trypolicy.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleDocDto {
    private int id;
    private boolean isSampleDocument;
    private String documentName;
    private String filePath;
}