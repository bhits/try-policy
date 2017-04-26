package gov.samhsa.c2s.trypolicy.infrastructure;

import gov.samhsa.c2s.trypolicy.service.dto.CCDDto;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@FeignClient(name = "phr")
public interface PhrService {

    //TODO: Remove mock data; Get CCD Document by calling the PHR Service
    /*@RequestMapping(value = "/patients/{patientId}/document/{documentId}/", method = RequestMethod.GET)
    CCDDto getCCDByDocumentId(@PathVariable String("patientId") patientId, @PathVariable("documentId") String documentId);*/


    //Mock Data
    default CCDDto getCCDByDocumentId(@PathVariable("patientId") String patientId, @PathVariable("documentId") String documentId){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Test_valid_CCDA1_1_CCD.xml").getFile());
        byte[] bytes;
        byte[] encoded = null;
        //Load file
        try{
            InputStream is = new FileInputStream(file);

            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                // File is too large
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
            encoded = Base64.encodeBase64(bytes);
        } catch (IOException io){
            System.out.println("Could not completely read file "+file.getName());
        }

        CCDDto mockData = new CCDDto();
        mockData.setCCDFile(encoded);
        return mockData;
    }
}