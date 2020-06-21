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
package org.cccnext.tesuto.springboot.web;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.util.CCCUserUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.springboot.web.security.exception.AssessForbiddenException;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.user.service.UserContextService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class BaseController {

    @Autowired
    UserAccountReader userAccountService = null;
    
    @Autowired
    UserContextService userContextService = null;

    @Autowired
    private RequestForwardService requestForwardService;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private StudentReader studentService;

    //Wrap an error message in something that Spring will serialize into the correct format
    public static Set<String> error(String message) {
        return Collections.singleton(message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> accessDeniedHandler(AccessDeniedException ex) {
        return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AssessForbiddenException.class)
    public ResponseEntity<Void> forbiddenHandler(AssessForbiddenException ex) {
        return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundHandler(NotFoundException ex) {
        return new ResponseEntity<>(error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationExceptionHandler(ValidationException e) {
        return new ResponseEntity<>(error(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Void> exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> notReadableHandler(HttpMessageNotReadableException ex) {
        return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
    }

    // Note; this makes call into database if currentUserId is not
    public UserAccountDto getUser() {
        String currentUserId = userContextService.getCurrentUserId();
        UserAccountDto userAccount = null;
        if (currentUserId != null) {
        	if(userContextService.getCurrentAuthentication() != null) {
        		Object userObj = userContextService.getCurrentAuthentication().getPrincipal();
        	 if(userObj  instanceof UserAccountDto) {
        		userAccount = (UserAccountDto)userObj;
        	} else if(userObj.getClass().getCanonicalName().equals(UserAccountDto.class.getCanonicalName())) {
        		try {
					String json = mapper.writeValueAsString(userObj);
					userAccount = mapper.readValue(json, UserAccountDto.class);
				} catch (Exception e) {
					throw new RuntimeException("unable to parse user to useracountdto");
				}
        	} else
        		userAccount = userAccountService.getUserAccountByUsername(currentUserId);
        	}
         }
        return userAccount;
    }

    protected String getCurrentUserId() {
        UserAccountDto userAccountDto = getUser();
        return (userAccountDto == null ? null : userAccountDto.getUserAccountId().toString());
    }
    
    protected String getCurrentUsername() {
        UserAccountDto userAccountDto = getUser();
        return (userAccountDto == null ? null : userAccountDto.getUsername().toString());
    }

    protected String getBaseURL(HttpServletRequest request) {
        return requestForwardService.getForwardUrl(request, "");
    }
    
    protected StudentViewDto buildStudent() {
        UserAccountDto currentUserAccount = getUser();
        StudentViewDto student =  buildStudent(currentUserAccount.getUsername());
        if(student == null) {
            return CCCUserUtils.buildStudentFromUserAccount(currentUserAccount);
        }
       
        return student;
    }
    
    protected StudentViewDto buildStudent(String cccid) {
        StudentViewDto student = null;
        try {
             student = studentService.getStudentById(cccid);
        } catch (HttpClientErrorException ex) {
            log.error("Unable to find student " + cccid + " through service, apparently failure caused by http error. Service may be down.", ex);
        } catch (Exception ex) {
            log.error("Error caused by service. Service may be down.", ex);
        }
        return student;
       
    }

    protected Boolean atLeastOneCommonCollegeToCurrentUser(String cccid) {
        StudentViewDto student = buildStudent(cccid);
        UserAccountDto currentUserAccount = getUser();
        for(String collegeId:student.getCollegeStatuses().keySet()) {
            if(currentUserAccount.getCollegeIds().contains(collegeId)){
                return true;
            }
        }
        return false;
    }
    
    public boolean userIsAffiliated(String cccCollegeMisCode) {
    	UserAccountDto user = getUser();
    	
        if(user == null || CollectionUtils.isEmpty(user.getCollegeIds())) return false;
        return user.getCollegeIds().contains(cccCollegeMisCode);
    }
    
    public boolean userHasRole(String permission) {
    	UserAccountDto user = getUser();
    	if(user != null)
    		return user.hasPermission(permission);
    	return false;
    }
    
    public boolean isStudent() {
    	UserAccountDto user = getUser();
    	if(user != null) {
    		return user.isStudent();
    	}
    	return false;
    }
}
