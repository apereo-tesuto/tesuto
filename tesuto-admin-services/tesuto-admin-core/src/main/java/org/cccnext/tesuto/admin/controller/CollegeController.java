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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.assembler.view.CollegeViewDtoAssembler;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class CollegeController {

    @Autowired
    CollegeService service;
    
    @Autowired
    CollegeViewDtoAssembler assembler;
    
    public ResponseEntity<List<CollegeDto>> read(List<String> collegeIds) {
        return new ResponseEntity<List<CollegeDto>>(service.read(collegeIds), HttpStatus.OK);
    }
    
    public ResponseEntity<Set<CollegeViewDto>> readView(List<String> collegeIds) {
        return new ResponseEntity<Set<CollegeViewDto>>(assembler.assembleViewDto(new HashSet<>(service.read(collegeIds))), HttpStatus.OK);
    }

    public @ResponseBody CollegeViewDto getCollegeByMisCode(String misCode) throws IllegalAccessException {
        return assembler.assembleViewDto(service.read(misCode));
    }
    
    public @ResponseBody CollegeDto read(String misCode) throws IllegalAccessException {
        return service.read(misCode);
    }
}
