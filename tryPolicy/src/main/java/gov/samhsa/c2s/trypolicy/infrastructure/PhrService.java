package gov.samhsa.c2s.trypolicy.infrastructure;

import gov.samhsa.c2s.trypolicy.infrastructure.dto.PatientDto;
import gov.samhsa.c2s.trypolicy.config.OAuth2FeignClientConfig;
import gov.samhsa.c2s.trypolicy.service.dto.CCDDto;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@FeignClient(name = "phr", configuration = OAuth2FeignClientConfig.class)
public interface PhrService {

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    PatientDto getPatient();

    //Mock Data
    default CCDDto getCCDByDocumentId(@PathVariable("documentId") String documentId){
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