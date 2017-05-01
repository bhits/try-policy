package gov.samhsa.c2s.trypolicy.infrastructure;

import gov.samhsa.c2s.trypolicy.infrastructure.dto.SensitivityCategoryDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "pcm")
public interface PcmService {

    @RequestMapping(value = "/patients/{patientId}/consents/{consentId}/shareSensitivityCategories", method = RequestMethod.GET)
    List<SensitivityCategoryDto> getSharedSensitivityCategories(@PathVariable("patientId") String patientId, @PathVariable("consentId") String consentId);

}