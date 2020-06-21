package org.ccctc.common.commonidentity.domain.identity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;



import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
/**
 * AuthenticationResponseErrorHandler is called by the assigned RestTemplate.  Instead
 * of throwning an exception automatically, like the default ResponseErrorHandler would, 
 * we basically ignore the error handler and let the response fall back up to the callling application
 * to deal with the status code.
 * @author mgillian
 *
 */


/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class AuthenticationResponseErrorHandler implements ResponseErrorHandler {
    private final Collection<HttpStatus> errorCodes = Arrays.asList(
            HttpStatus.UNAUTHORIZED, 
            HttpStatus.BAD_REQUEST, 
            HttpStatus.FORBIDDEN, 
            HttpStatus.INTERNAL_SERVER_ERROR, 
            HttpStatus.NOT_FOUND);
    
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        if (errorCodes.contains(statusCode)) {
            return true;
        }        
        return false;
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        log.debug("error:[" + statusCode + ":" + response.getStatusText() + "]");
        if (errorCodes.contains(statusCode)) {
            // ignore
        }
    }
}
