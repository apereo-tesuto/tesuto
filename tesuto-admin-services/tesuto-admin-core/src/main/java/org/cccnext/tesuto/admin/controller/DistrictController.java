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

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.DistrictDto;
import org.cccnext.tesuto.admin.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class DistrictController {

    @Autowired
    DistrictService service;

    public ResponseEntity<List<DistrictDto>> read(Set<String> collegeIds) {
        List<DistrictDto> districts = service.read(collegeIds);
        return new ResponseEntity<List<DistrictDto>>(districts, HttpStatus.OK);
    }
}
