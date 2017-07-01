package gov.samhsa.c2s.trypolicy.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "c2s.try-policy")
@Data
public class TryPolicyProperties {
    @NotNull
    @Valid
    private List<SampleUploadedDocData> sampleUploadedDocuments;

    @Data
    public static class SampleUploadedDocData {
        @NotEmpty
        private String filePath;

        @NotEmpty
        private String fileName;

        @NotEmpty
        private String documentName;

        @NotEmpty
        private String contentType;
    }
}