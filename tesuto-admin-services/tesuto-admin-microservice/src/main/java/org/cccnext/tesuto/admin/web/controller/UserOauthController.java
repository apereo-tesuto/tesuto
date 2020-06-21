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
package org.cccnext.tesuto.admin.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.springboot.web.AuthorizerService;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.service.StudentService;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "service/v1/oauth2")
public class UserOauthController extends BaseController {

    @Autowired
    StudentService service;
    
    @Autowired
    UserAccountService userService;
    
    @Autowired
    AuthorizerService authorizerService;
    
    
    @PreAuthorize("hasAnyAuthority('FIND_ANY_STUDENT','API')")
    @RequestMapping(value = "username", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody UserAccountDto findUserByUsername(@RequestParam("username") String username) throws UnsupportedEncodingException {
    	return userService.getUserAccountByUsername(URLDecoder.decode(username, "UTF-8"));
    }
    
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{userAccountId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody UserAccountDto findUser(@PathVariable("userAccountId") String userAccountId) throws UnsupportedEncodingException {
    	 return userService.getUserAccountByUserAccountId(userAccountId);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{userAccountId}/login/failed", method = RequestMethod.PUT)
    public ResponseEntity<Void> loginFailed(@PathVariable("userAccountId") String userAccountId) throws UnsupportedEncodingException {
    	  userService.failedLogin(userAccountId);
    	  return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{userAccountId}/login/succeeded", method = RequestMethod.PUT)
    public ResponseEntity<Void> loggedin(@PathVariable("userAccountId") String userAccountId) throws UnsupportedEncodingException {
    	  userService.clearFailedLogins(userAccountId);
    	  return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
