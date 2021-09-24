package org.ccctc.common.commonidentity.openidc;



import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class OpenIdResponseErrorHandler implements ResponseErrorHandler {

    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode.equals(HttpStatus.UNAUTHORIZED) ||
            statusCode.equals(HttpStatus.BAD_REQUEST) ||
            statusCode.equals(HttpStatus.FORBIDDEN) ||
            statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) ||
            statusCode.equals(HttpStatus.NOT_FOUND)) {
            return true;
        }
        return false;
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        log.error("error:[" + statusCode + ":" + response.getStatusText() + "]");
        if (statusCode.equals(HttpStatus.UNAUTHORIZED) ||
                statusCode.equals(HttpStatus.FORBIDDEN)) {
            // TODO
        }
    }
}
