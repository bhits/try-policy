package gov.samhsa.c2s.trypolicy.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensitivityCategoryDto {
    private String description;
    private String display;
    @NotNull
    private Long id;

    @Valid
    @NotNull
    private IdentifierDto identifier;
}