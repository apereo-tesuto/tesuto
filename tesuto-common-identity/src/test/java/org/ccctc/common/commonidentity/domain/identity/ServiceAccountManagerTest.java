package org.ccctc.common.commonidentity.domain.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ccctc.common.commonidentity.domain.identity.response.ClientCredentials;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by andrew on 11/15/16.
 */
public class ServiceAccountManagerTest {

    private RestTemplate rt = mock(RestTemplate.class);
    private ClientCredentials cc = mock(ClientCredentials.class);
    private HttpHeaders headers;
    private HttpEntity<MultiValueMap<String, String>> request;

    @Before
    public void setup() throws Exception {
        String preJWT = "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJJREsxMjM0IiwiYXpwIjoiY2xpZW50IiwiZXBwbiI6ImFuZHJld3N0dWFydEBkZW1vY29sbGVnZS5lZHUiLCJzY29wZSI6WyJlcHBuIiwib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIl0sInJvbGVzIjpbIlJPTEVfREVWRUxPUEVSIiwiUk9MRV9VU0VSIiwic3RhZmYiLCJST0xFX0FETUlOIiwidXNlciIsIlJPTEVfQ0NDX0FETUlOIl0sImlzcyI6Imh0dHA6XC9cL21pdHJlLnRlc3RcL2ZcLyIsImV4cCI6MTQ3ODMwMzI5NywibWlzQ29kZSI6IlpaMSIsImlhdCI6MTQ3ODI5OTY5NywianRpIjoiYjMwODg3NTMtZDY1NC00MTAzLWJmOTAtNzU4Mjk4ODNhNWY0IiwiY2NjSWQiOiJJREsxMjM0In0.W7Xd3F24GeF3tET4NWrw_7uPfolaRxly0u6S5HTr5WBkcRxW5S0ngqgq-LEhPwNF5COJjxb8n7vtt2FKs1QyYjaElqRB2DHQmbEuxwQC_hq-nW9KjjBWcl6E0-gIHf9QBH2dGRFzwUf4XYoD2kDVSUGyptAXR-c5bTdxNMZpcdAnJR8HrfZR-0RSXsSBWYchpAln7WKX45e9fxGqT1_Wd4KLjoquHQy7JdE6YISLRFOaPvqBHjhqrCQMp9Bu426XG-pUC4VE-VHMvqB0gjciAIQJ0eejkz_mXs8lP1KrElfvdQvKV1P0JU8JNE1ydLRR4QmvXWvgvT95Rp-Yk-U3lQ";
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        this.request = this.buildRequest("bar", "baz");
        when(cc.getAccessTokenValue()).thenReturn(preJWT);
        when(rt.postForEntity(any(URI.class), any(Object.class), eq(ClientCredentials.class)))
                .thenReturn(new ResponseEntity<>(cc, HttpStatus.OK));
    }

    @Test
    public void getToken() throws Exception {
        ServiceAccountManager m = new ServiceAccountManager.ServiceAccountManagerBuilder()
                .baseEndpoint("http://foo.bar")
                .clientId("bar")
                .clientSecret("baz")
                .restTemplate(rt)
                .scope("superuser")
                .scope("toothfairy")
                .build();
        final Map<String, String> expectedBody = new HashMap<>(2);
        expectedBody.put("client_id", "bar");
        expectedBody.put("client_secret", "baz");
//        verify(rt).postForEntity(
//                eq(new URI("http://foo.bar/token?grant_type=client_credentials&response_type=token&scope=superuser%2Btoothfairy")), 
//                eq(this.request), 
//                eq(ClientCredentials.class));
        final JWTUserIdentity t = m.getToken();
        assertEquals("IDK1234", t.getCccId());
    }

    @Test
    public void getTokenDefaults() throws Exception {
        // restTemplate must be provided only during testing, to allow for mocking
        // by default a restTemplate is automatically created by ServiceAccountManagerBuilder
        ServiceAccountManager m = new ServiceAccountManager.ServiceAccountManagerBuilder()
                .restTemplate(rt)
                .build();
//        verify(rt).postForEntity(
//                eq(new URI("http://localhost:8080/token?grant_type=client_credentials&response_type=token")), 
//                eq(this.buildRequest("blankClientId", "blankClientSecret")),
//                eq(ClientCredentials.class));
        final JWTUserIdentity t = m.getToken();
        assertEquals("IDK1234", t.getCccId());
    }
    
    @Test
    public void getTokenUnauthorized() {
        RestTemplate rt = mock(RestTemplate.class);
        when(rt.postForEntity(any(URI.class), eq(null), eq(ClientCredentials.class)))
            .thenReturn(new ResponseEntity<>(cc, HttpStatus.UNAUTHORIZED));
        try {
            ServiceAccountManager m = new ServiceAccountManager.ServiceAccountManagerBuilder()
                .baseEndpoint("http://foo.bar")
                .clientId("bar")
                .clientSecret("baz")
                .restTemplate(rt)
                .scope("superuser")
                .scope("toothfairy")
                .build();
            fail("should get an exception, due to unauthorized credentials");
        } catch (Exception e) {
            // ignore
        }
        try {
//            verify(rt).postForEntity(
//                    eq(new URI("http://foo.bar/token?grant_type=client_credentials&response_type=token&scope=superuser%2Btoothfairy")), 
//                    eq(this.request), 
//                    eq(ClientCredentials.class));
        } catch (RestClientException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
    }

    @Test
    public void withBearer() throws Exception {
        ServiceAccountManager m = new ServiceAccountManager.ServiceAccountManagerBuilder()
                .baseEndpoint("http://foo.bar")
                .clientId("bar")
                .clientSecret("baz")
                .restTemplate(rt)
                .build();

        final HttpEntity<Map<String, String>> b = m.withBearer(Collections.singletonMap("foo", "bar"));
        
    }

    private HttpEntity<MultiValueMap<String, String>> buildRequest(String clientId, String clientSecret) {
        final MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<String, String>();
        formParameters.add("client_id", clientId);
        formParameters.add("client_secret", clientSecret);
        return new HttpEntity<MultiValueMap<String, String>>(formParameters, headers);
    }

}
