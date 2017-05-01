package gov.samhsa.c2s.trypolicy.infrastructure;

import gov.samhsa.c2s.trypolicy.service.dto.CCDDto;
import gov.samhsa.c2s.trypolicy.service.exception.TryPolicyException;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@FeignClient(name = "phr")
public interface PhrService {

    //TODO: Remove mock data; Get CCD Document by calling the PHR Service
    /*@RequestMapping(value = "/patients/{patientId}/documents/{documentId}/", method = RequestMethod.GET)
    CCDDto getCCDByDocumentId(@PathVariable String("patientId") patientId, @PathVariable("documentId") String documentId);*/


    //Mock Data
    @RequestMapping(value = "/patients/{patientId}/documents/{documentId}/", method = RequestMethod.GET)
    default CCDDto getCCDByDocumentId(@PathVariable("patientId") String patientId, @PathVariable("documentId") String documentId){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Test_valid_CCDA1_1_CCD.xml").getFile());
        byte[] bytes = null;
        //Load file
        try{
            InputStream is = new FileInputStream(file);

            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                // File is too large
                throw new TryPolicyException("File is too large to read" +file.getName());
            }
            bytes = new byte[(int)length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file "+file.getName());
            }

            is.close();
        } catch (IOException io){
            throw new TryPolicyException("Could not completely read file "+file.getName() + io);
        }

        CCDDto mockData = new CCDDto();
        mockData.setCCDFile(bytes);
        return mockData;
    }
}