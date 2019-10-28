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
package org.ccctc.common.droolsengine.exceptions;

import java.util.HashMap;
import java.util.Map;



import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@ControllerAdvice
public class InvalidRulesExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ InvalidActionException.class, MissingParametersException.class, StudentProfileException.class,
                    InvalidConditionException.class, ConditionRequiredException.class, ActionRequiredException.class })
    protected ResponseEntity<Object> handle(Exception e, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("error: [" + e.getMessage() + "]");
        Map<String, String> error = new HashMap<String, String>();
        error.put("status", status.toString());
        error.put("message", e.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, status, request);
    }

    @ExceptionHandler({ AuthenticationException.class })
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        log.error("error: [" + e.getMessage() + "]");
        Map<String, String> error = new HashMap<String, String>();
        error.put("status", status.toString());
        error.put("message", e.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, status, request);
    }

    @ExceptionHandler({ ObjectNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(ObjectNotFoundException e, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.error("error: [" + e.getMessage() + "]");
        Map<String, String> error = new HashMap<String, String>();
        error.put("status", status.toString());
        error.put("message", e.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, status, request);
    }
}
