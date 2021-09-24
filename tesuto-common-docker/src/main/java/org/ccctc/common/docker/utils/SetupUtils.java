package org.ccctc.common.docker.utils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;



import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class SetupUtils {
    public static final String DOCKER_IMAGE = "docker.dev.ccctechcenter.org/docker-rest-test-service:1.0.0-SNAPSHOT";
    public static final int EXPOSED_PORT = 8080;
    public static final int MAPPED_PORT = 8088;
    public static final String WAIT_FOR_LOG_MESSAGE = "Tomcat started on port(s): " + EXPOSED_PORT + " (http)";
    public static final String DOCKER_ENDPOINT_URL = "http://localhost:" + MAPPED_PORT;
    public static final String SETUP_ENDPOINT_URL = DOCKER_ENDPOINT_URL + "/setup";
    public static final String ANSWER_ENDPOINT_URL = DOCKER_ENDPOINT_URL + "/answer";

    private RestTemplate rs;

    /**
     * SetupUtils is a utility to allow you to easily interact with the Docker-Rest-Test-Service
     * docker container.  It contains the relevant port, URI, image, and message information
     * so that you can more easily configure your DockerConfig.  In addition, it makes
     * it easier to send messages to the docker container for setup, status, and clearing
     * any endpoints that have setup in that docker container's Spring Boot application.
     */
    public SetupUtils() {
        rs = new RestTemplate();
        // This is needed so that the REST call can return error HTTP Status
        // codes, like 401, 403, and 404
        // without automatically being intercepted
        rs.setErrorHandler(new ResponseErrorHandler() {

            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });
    }

    public RestTemplate getRestTemplate() {
        return rs;
    }

    public void send(String fileName) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource(fileName);
        List data = mapper.readValue(resource.getInputStream(), List.class);
        log.debug("List of endpoint configurations to send to setup [" + data + "]");

        HttpEntity<Object> entity = new HttpEntity<Object>(data, null);

        URI url = URI.create(SETUP_ENDPOINT_URL);
        ResponseEntity<String> response = rs.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCodeValue() != 200) {
            throw new RuntimeException("Did not get correct response on setup," + "received ["
                    + response.getStatusCode() + "], expected [200]");
        }
    }

    public void clear() {
        URI url = URI.create(SETUP_ENDPOINT_URL);
        rs.delete(url);
    }

    public Map get() {
        URI url = URI.create(SETUP_ENDPOINT_URL);
        ResponseEntity<Map> response = rs.exchange(url, HttpMethod.GET, null, Map.class);
        return response.getBody();
    }
}
