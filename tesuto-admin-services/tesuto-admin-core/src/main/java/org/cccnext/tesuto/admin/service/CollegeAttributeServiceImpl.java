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

import org.cccnext.tesuto.admin.assembler.CollegeAttributeAssembler;
import org.cccnext.tesuto.admin.dto.CollegeAttributeDto;
import org.cccnext.tesuto.admin.model.CollegeAttribute;
import org.cccnext.tesuto.admin.repository.CollegeAttributeRepository;
import org.cccnext.tesuto.admin.service.CollegeAttributeService;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jasonbrown on 8/1/16.
 */
@Service(value = "collegeAttributeService")
public class CollegeAttributeServiceImpl implements CollegeAttributeService {

    @Autowired
    CollegeAttributeRepository collegeAttributeRepository;
    @Autowired
    CollegeAttributeAssembler collegeAttributeAssembler;

    @Value("${placement.options.english}")
    private String[] validEnglishPlacementOptions;

    @Value("${placement.options.esl}")
    private String[] validEslPlacementOptions;

    @Override
    @Transactional
    public CollegeAttributeDto create(CollegeAttributeDto collegeAttributeDto) {
        if(!Arrays.asList(validEnglishPlacementOptions).contains(collegeAttributeDto.getEnglishPlacementOption())
                || !Arrays.asList(validEslPlacementOptions).contains(collegeAttributeDto.getEslPlacementOption())) {
            throw new PoorlyFormedRequestException(String.format("CollegeAttribute %s must contain a valid english placement "
                    + "option %s and valid esl placement option %s",
                    collegeAttributeDto, validEnglishPlacementOptions, validEslPlacementOptions));
        }
        CollegeAttribute collegeAttribute = collegeAttributeAssembler.disassembleDto(collegeAttributeDto);

        CollegeAttribute savedCollegeAttribute = collegeAttributeRepository.save(collegeAttribute);

        return collegeAttributeAssembler.assembleDto(savedCollegeAttribute);
    }

    @Override
    @Transactional
    public List<CollegeAttributeDto> create(List<CollegeAttributeDto> collegeAttributeDtos) {
        List<CollegeAttributeDto> savedCollegeAttributes = new ArrayList<>();
        for(CollegeAttributeDto collegeAttributeDto: collegeAttributeDtos){
            savedCollegeAttributes.add(create(collegeAttributeDto));
        }
        return savedCollegeAttributes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeAttributeDto> read() {
        return collegeAttributeAssembler.assembleDto(collegeAttributeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeAttributeDto read(String id) {
    	try {
    		CollegeAttribute collegeAttribute = collegeAttributeRepository.findById(id).get();
    		return collegeAttributeAssembler.assembleDto(collegeAttribute);
    	} catch(Exception exp) {
    		throw new RuntimeException(exp);
    	}
    }

    @Override
    @Transactional
    public void delete(String id) {
        collegeAttributeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(List<CollegeAttributeDto> collegeAttributeDtos) {
        List<CollegeAttribute> collegeAttributes = collegeAttributeAssembler.disassembleDto(collegeAttributeDtos);
        collegeAttributeRepository.deleteAll(collegeAttributes);
    }
}
