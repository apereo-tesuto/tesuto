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
package org.cccnext.tesuto.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.user.service.CollegeAssociatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CollegeAssociatedUserController {

    @Autowired
    CollegeAssociatedUserService service;

    public ResponseEntity<CollegeAssociatedUser> findByCccId(String username) throws UnsupportedEncodingException {
        return new ResponseEntity<CollegeAssociatedUser>(service.getCollegeAssociatedUser(URLDecoder.decode(username, "UTF-8")), HttpStatus.OK);
    }
    
    public ResponseEntity<String> isAssociatedToCollege(String username, String cccCollegeMisCode) throws UnsupportedEncodingException {
    	Boolean isAssociated = service.isAssociatedToCollege(URLDecoder.decode(username, "UTF-8"), cccCollegeMisCode);
        return new ResponseEntity<String>(isAssociated ? "IS_ASSOCIATED": "IS_NOT_ASSOCIATED", HttpStatus.OK);
    }
}
