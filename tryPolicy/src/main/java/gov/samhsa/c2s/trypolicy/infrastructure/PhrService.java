package gov.samhsa.c2s.trypolicy.infrastructure;

import gov.samhsa.c2s.trypolicy.service.dto.UploadedDocumentDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "phr")
@Service
public interface PhrService {

    @RequestMapping(value = "/uploadedDocuments/patients/{patientMrn}/documents/{id}/", method = RequestMethod.GET)
    UploadedDocumentDto getPatientDocument(@PathVariable("patientMrn") String patientId, @PathVariable("id") String documentId);
}