package org.ccctc.common.commonidentity.domain.identity;

import java.io.IOException;
import java.net.URI;

import org.ccctc.common.commonidentity.utils.CCCIdentityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jwt.JWT;

import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * While still functional, the org.ccctc.web.client.rest.CCCRestCallHandler in the ccc-web-common library offers a bit more flexibility if you need to add
 * pieces to the header etc.
 */
@Deprecated
@NoArgsConstructor
public class CCCRestTemplate extends RestTemplate {
    @Setter(onMethod = @__(@Required)) private JWTGetter jwtGetter;

    public CCCRestTemplate(JWTGetter jwtGetter) {
        this.setJwtGetter(jwtGetter);
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        AuthHeaderRequestCallbackWrapper wrapper = new AuthHeaderRequestCallbackWrapper(requestCallback);
        return super.doExecute(url, method, wrapper, responseExtractor);
    }

    void modifyHeaders(HttpHeaders headers) {
        // first, add the Authorization header...
        String token = getBearerToken();
        if (token != null) {
            HttpHeaders authHeader = CCCIdentityUtils.getBearerHeaders(token);
            headers.putAll(authHeader);
        }
    }

    private String getBearerToken() {
        if (this.jwtGetter == null) {
           return null;
        }
        JWT jwt = jwtGetter.getJWT();
        if (jwt == null) {
            return null;
        }

        return jwt.serialize();
    }

    private class AuthHeaderRequestCallbackWrapper implements RequestCallback {
        private RequestCallback wrappedRequestCallback;

        public AuthHeaderRequestCallbackWrapper(final RequestCallback requestCallback) {
            wrappedRequestCallback = requestCallback;
        }

        @Override
        public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
            modifyHeaders(clientHttpRequest.getHeaders());

            // wrappedRequestCallback may be null for some operations (ie. DELETE)
            if (wrappedRequestCallback != null) {
                // the process the request normally...
                wrappedRequestCallback.doWithRequest(clientHttpRequest);
            }
        }
    }

    @Override
    public ResponseErrorHandler getErrorHandler() {
        final ResponseErrorHandler errorHandler = super.getErrorHandler();
        if (!(errorHandler instanceof RestResponseErrorHandlerLoggingDecorator)) {
            super.setErrorHandler(new RestResponseErrorHandlerLoggingDecorator(errorHandler));
        }
        return super.getErrorHandler();
    }

    @Override
    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
        if (!(errorHandler instanceof RestResponseErrorHandlerLoggingDecorator)) {
            super.setErrorHandler(new RestResponseErrorHandlerLoggingDecorator(errorHandler));
        }
    }

}
