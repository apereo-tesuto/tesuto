package org.ccctc.common.commonidentity.domain.identity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.Validate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Decorator class for {@link ResponseErrorHandler} that adds INFO logging of the response code and body.
 * Used only in the deprecated {@link CCCRestTemplate} class.
 * 
 * See: {@link CCCRestTemplate}
 */
@Deprecated
@Slf4j
public class RestResponseErrorHandlerLoggingDecorator implements ResponseErrorHandler{

    private ResponseErrorHandler handler;

    public RestResponseErrorHandlerLoggingDecorator(ResponseErrorHandler delegate) {
        Validate.notNull(delegate, "The delegate ResponseErrorHandler must not be null");
        this.handler = delegate;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return this.handler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info("REST client response code: {}", response.getRawStatusCode());
        log.info("REST client response body: {}", this.getResponseBodyAsString(response));
        this.handler.handleError(response);
    }

    public String getResponseBodyAsString(ClientHttpResponse response) {
        final byte[] responseBody = this.getResponseBody(response);
        final Charset responseCharset = this.getCharset(response);
        return new String(responseBody, responseCharset);
    }

    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException ignore) {
        }
        return new byte[0];
    }

    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : StandardCharsets.UTF_8);
    }

}
