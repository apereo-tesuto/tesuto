package org.ccctc.common.commonidentity.domain.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientResponseException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * JUnit test class for {@link CCCRestTemplate}.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class CCCRestTemplateTest {

    @Before
    public void setUp() throws Exception {
    }

    @Mock ClientHttpRequest request;
    @Mock ClientHttpRequestFactory requestFactory;
    @Mock ClientHttpResponse response;
    @Mock HttpHeaders headers;
    @Mock OutputStream requestBody;

    private String responseBody = "{ \"foo\":\"bar\" }";
    private String serviceUrl = "http://127.0.0.1/fake/endpoint";
    private CCCRestTemplate restTemplate;
    private Logger responseErrorHandlerLoggingDecoratorLogger;
    private ListAppender<ILoggingEvent> loggingListAppender;

    @Before
    public void setup() throws Throwable {
        MockitoAnnotations.initMocks(this);
        this.restTemplate = new CCCRestTemplate();
        this.restTemplate.setRequestFactory(this.requestFactory);
        this.restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        given(this.requestFactory.createRequest(any(), any())).willReturn(this.request);
        given(this.request.getHeaders()).willReturn(this.headers);
        given(this.request.getBody()).willReturn(this.requestBody);
        given(this.request.execute()).willReturn(this.response);
        given(this.response.getHeaders()).willReturn(this.headers);
        //given(this.response.getStatusCode()).willReturn(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testResponseIsLoggedWhenResponseIsBadRequest() throws Exception {
        this.trackResponseErrorLogging();
        this.givenRestCallReturns(400, this.responseBody);
        this.makeRestCallForResponseErrorLoggingTestExpectingLogging();
        this.verifyRestResponseWasLogged(400, this.responseBody);
    }

    @Test
    public void testResponseIsLoggedWhenResponseIsInternalServiceError() throws Exception {
        this.trackResponseErrorLogging();
        this.givenRestCallReturns(500, this.responseBody);
        this.makeRestCallForResponseErrorLoggingTestExpectingLogging();
        this.verifyRestResponseWasLogged(500, this.responseBody);
    }

    @Test
    public void testResponseIsNotLoggedWhenResponseIsOkay() throws Exception {
        this.trackResponseErrorLogging();
        this.givenRestCallReturns(200, this.responseBody);
        this.makeRestCallForResponseErrorLoggingTestExpectingNoLogging();
        this.verifyRestResponseWasNotLogged();
    }

    private void givenRestCallReturns(int code, String body) throws IOException {
        given(this.response.getRawStatusCode()).willReturn(code);
        given(this.response.getBody()).willReturn(new ByteArrayInputStream(body.getBytes()));
    }

    private void trackResponseErrorLogging() {
        this.responseErrorHandlerLoggingDecoratorLogger = log;
        this.loggingListAppender = new ListAppender<>();
        this.loggingListAppender.start();
        this.responseErrorHandlerLoggingDecoratorlog.addAppender(this.loggingListAppender);
    }

    private void makeRestCallForResponseErrorLoggingTestExpectingNoLogging() {
        this.restTemplate.exchange(this.serviceUrl, HttpMethod.POST, new HttpEntity<Object>(Collections.emptyMap(), new HttpHeaders()), Map.class);
    }
    private void makeRestCallForResponseErrorLoggingTestExpectingLogging() {
        try {
            this.restTemplate.exchange(this.serviceUrl, HttpMethod.POST, new HttpEntity<Object>(Collections.emptyMap(), new HttpHeaders()), Map.class);
        } catch (RestClientResponseException e) {
            // good because this exception still needs to be thrown after logging has been done
            return;
        }
        fail("Expected to catch RestClientResponseException, but did not.");
    }

    private void verifyRestResponseWasLogged(int responseCode, String responseBody) {
        List<ILoggingEvent> logsList = this.loggingListAppender.list;
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals("REST client response code: " + responseCode, logsList.get(0).getFormattedMessage());
        assertEquals(Level.INFO, logsList.get(1).getLevel());
        assertEquals("REST client response body: " + responseBody, logsList.get(1).getFormattedMessage());
    }

    private void verifyRestResponseWasNotLogged() {
        assertTrue(this.loggingListAppender.list.isEmpty());
    }

}
