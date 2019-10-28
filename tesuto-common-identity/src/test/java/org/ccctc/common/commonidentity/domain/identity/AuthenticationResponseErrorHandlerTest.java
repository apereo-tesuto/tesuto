package org.ccctc.common.commonidentity.domain.identity;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;

import org.ccctc.common.commonidentity.domain.identity.AuthenticationResponseErrorHandler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class AuthenticationResponseErrorHandlerTest {
    private static final String DEFAULT_URL = "http://localhost:8080/test";
    private static final String DEFAULT_RESULT = "test";
    
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new AuthenticationResponseErrorHandler());
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void nominal() {
        mockServer.expect(requestTo(DEFAULT_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(DEFAULT_RESULT, MediaType.APPLICATION_JSON));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>("", headers);        
        ResponseEntity<String> response = restTemplate.exchange(DEFAULT_URL,HttpMethod.GET, entity, String.class);   
        mockServer.verify();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), DEFAULT_RESULT);
    }
    
    @Test
    public void authenticationFailure() {
        // By default a restTemplate will throw an exception and end the current stack flow.
        // We're using a custom error handler, AuthenticationResponseErrorHandler, which does not
        // throw exceptions.  That way we can see and handle status codes ourselves.
        mockServer.expect(requestTo(DEFAULT_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>("", headers);                
        ResponseEntity<String> response = restTemplate.exchange(DEFAULT_URL,HttpMethod.GET, entity, String.class);   
        mockServer.verify();

        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}
