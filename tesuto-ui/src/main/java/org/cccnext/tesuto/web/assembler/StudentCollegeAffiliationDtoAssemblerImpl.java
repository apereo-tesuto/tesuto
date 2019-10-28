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
package org.cccnext.tesuto.web.assembler;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.web.model.StudentCollegeAffiliation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component("studentCollegeAffiliationDtoAssembler")
public class StudentCollegeAffiliationDtoAssemblerImpl implements StudentCollegeAffiliationDtoAssembler {

    @Autowired
    Mapper mapper;

    @Override
    public StudentCollegeAffiliationDto assembleDto(StudentCollegeAffiliation affiliation) {
        if (affiliation == null) {
            return null;
        }
        return mapper.map(affiliation, StudentCollegeAffiliationDto.class);
    }

    @Override
    public StudentCollegeAffiliation disassembleDto(StudentCollegeAffiliationDto dto) {
        if (dto == null) {
            return null;
        }
        return mapper.map(dto, StudentCollegeAffiliation.class);
    }
}
