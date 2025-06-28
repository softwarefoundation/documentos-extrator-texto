package br.com.devchampions.documentosextratortexto.service;


import br.com.devchampions.documentosextratortexto.response.TikaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Service
public class TikaIntegrationService {

    @Value("${tika.url}")
    private String tikaUrl;

    private final RestTemplate restTemplate;

    public TikaIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String extrairTextoDoDocumento(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null;
            }

            InputStream inputStream = file.getInputStream();
            InputStreamResource resource = new InputStreamResource(inputStream);


            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);


            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<TikaResponse> response = restTemplate.postForEntity(
                    tikaUrl.concat("/form/main"),
                    requestEntity,
                    TikaResponse.class
            );

            TikaResponse tikaReponse = response.getBody();

            return tikaReponse.getxTikaContent().replaceAll("<[^>]*>", "").replaceAll("\\s+", " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
