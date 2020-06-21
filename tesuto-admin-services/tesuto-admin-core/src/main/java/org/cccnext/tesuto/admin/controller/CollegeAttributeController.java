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

import org.cccnext.tesuto.admin.dto.CollegeAttributeDto;
import org.cccnext.tesuto.admin.service.CollegeAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 8/3/16.
 */

@Service
public class CollegeAttributeController {

    @Autowired
    CollegeAttributeService collegeAttributeService;
    
    public ResponseEntity<CollegeAttributeDto> updateCollegeAttribute(CollegeAttributeDto collegeAttributeDto) {
        CollegeAttributeDto savedCollegeAttributeDto = collegeAttributeService.create(collegeAttributeDto);
        return new ResponseEntity<CollegeAttributeDto>(savedCollegeAttributeDto, HttpStatus.OK);
    }

    public ResponseEntity<CollegeAttributeDto> get(String id) {
        CollegeAttributeDto collegeAttribute = collegeAttributeService.read(id);
        return new ResponseEntity<CollegeAttributeDto>(collegeAttribute, HttpStatus.OK);
    }
}
