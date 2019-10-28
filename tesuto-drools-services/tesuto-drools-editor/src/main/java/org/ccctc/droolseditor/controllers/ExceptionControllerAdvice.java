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
package org.ccctc.droolseditor.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ccctc.common.droolscommon.exceptions.DrlInvalidSyntaxException;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object>  objectNotFoundExceptionHandler(ObjectNotFoundException e, 
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("message", e.getMessage());
        responseMap.put("status",  HttpStatus.NOT_FOUND);
        responseMap.put("statusCode", HttpStatus.NOT_FOUND.value());
        ResponseEntity<Object> result = new ResponseEntity<Object>(responseMap, null, HttpStatus.NOT_FOUND);
        return result;
    }
    
    @ExceptionHandler(DrlInvalidSyntaxException.class)
    public ResponseEntity<Object> drlInvalidSyntaxExceptionHandlere(DrlInvalidSyntaxException e, 
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("message", e.getMessage());
        responseMap.put("drl",  e.getDrl());
        responseMap.put("errors", e.getErrors());
        responseMap.put("status",  HttpStatus.BAD_REQUEST);
        responseMap.put("statusCode", HttpStatus.BAD_REQUEST.value());
        ResponseEntity<Object> result = new ResponseEntity<Object>(responseMap, null, HttpStatus.BAD_REQUEST);
        return result;        
    }
    
    @ExceptionHandler(SaveException.class)
    public ResponseEntity<Object> saveExceptionHandler(SaveException e, HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("message", e.getMessage());
        responseMap.put("status",  HttpStatus.BAD_REQUEST);
        responseMap.put("statusCode", HttpStatus.BAD_REQUEST.value());
        ResponseEntity<Object> result = new ResponseEntity<Object>(responseMap, null, HttpStatus.BAD_REQUEST);
        return result;                
    }
}
