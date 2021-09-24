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
package org.cccnext.tesuto.admin.service;

import org.cccnext.tesuto.admin.assembler.DistrictDtoAssembler;
import org.cccnext.tesuto.admin.dto.DistrictDto;
import org.cccnext.tesuto.admin.repository.DistrictRepository;
import org.cccnext.tesuto.admin.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service(value = "districtService")
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    DistrictDtoAssembler assembler;
    @Autowired
    DistrictRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> read(Set<String> collegeIds) {
        return assembler.assembleDto(repository.findAllWithColleges(collegeIds));
    }
}
