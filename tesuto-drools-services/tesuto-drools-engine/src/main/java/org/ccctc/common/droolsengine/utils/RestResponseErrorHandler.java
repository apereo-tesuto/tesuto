/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.ccctc.common.droolsengine.utils;

import java.io.IOException;



import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class RestResponseErrorHandler implements ResponseErrorHandler {
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
            // Ignore these codes
        }
    }

}
