package org.ccctc.common.commonidentity.openidc.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@RequiredArgsConstructor
public class HTTPIntrospectValidator implements TokenValidator {
    private ObjectMapper mapper = new ObjectMapper();

    @Setter
    private RestTemplate rt = new RestTemplate();

    @NonNull
    private String introspectUrl;
    @NonNull
    private String clientId;
    @NonNull
    private String clientSecret;

    public Map<String, String> introspect(String token) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(introspectUrl);

        HashMap<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<HashMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, String>> restResponse = rt.exchange(
                builder.build().encode().toUri(),
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String,String>>(){});

        if (restResponse.getStatusCode() != HttpStatus.OK) {
            log.error("Introspection request failed with Http Status Code of [" + restResponse.getStatusCode() +"] and message of [" + restResponse.getBody() + "]");
            return Collections.emptyMap();
        }

        return restResponse.getBody();
    }

    @Override
    public boolean validate(String token) {
        final Map<String, String> tokenStatus = introspect(token);
        return "true".equals(tokenStatus.get("active"));
    }
}
