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

import org.cccnext.tesuto.admin.assembler.CollegeDtoAssembler;
import org.cccnext.tesuto.admin.assembler.view.CollegeViewDtoAssembler;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "collegeService")
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    CollegeRepository repository;
   
    @Autowired
    CollegeDtoAssembler assembler;
    
    @Autowired
    CollegeViewDtoAssembler viewAssembler;

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDto> read() {
        return assembler.assembleDto(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeDto read(String id) {
        return assembler.assembleDto(repository.getOne(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDto> read(List<String> ids) {
        return assembler.assembleDto(repository.findAllById(ids));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Set<CollegeViewDto> read(Set<String> ids) {
        return new HashSet<>(viewAssembler.assembleViewDto(assembler.assembleDto(repository.findAllById(ids))));
    }

    @Override
    @Transactional(readOnly = true)
    public String getEppnSuffix(String id) {
        CollegeDto collegeDto =  assembler.assembleDto(repository.getOne(id));
        if(collegeDto == null) {
            throw new NotFoundException(
                    String.format("Unable to find college with id %s when looking for an EPPN Suffix.", id));
        }

        return collegeDto.getEppnSuffix();
    }

	@Override
	public CollegeViewDto getCollegeByMisCode(String misCode) {
		return viewAssembler.assembleViewDto(read(misCode));
	}

}
