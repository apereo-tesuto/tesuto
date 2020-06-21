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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.assembler.TestLocationDtoAssembler;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.repository.TestLocationRepository;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service(value = "testLocationService")
@Transactional
public class TestLocationServiceImpl implements TestLocationService {

    public static final String NAME_MISSING = "Test location name is missing";
    public static final String LOCATION_TYPE_MISSING = "Location Type is missing";

    @Autowired
    TestLocationRepository repository;
    @Autowired
    TestLocationDtoAssembler assembler;
    @Autowired
    TestLocationAssessmentService testLocationAssessmentService;
    @Autowired
    Mapper mapper;

    @Override
    @Transactional(value = "transactionManagerAdmin", readOnly = true)
    public List<TestLocationDto> read() {
        return assembler.assembleDto(repository.findAll());
    }

    @Override
    @Transactional(value = "transactionManagerAdmin", readOnly = true)
    public TestLocationDto read(String id) {
        return assembler.assembleDto(repository.getOne(id));
    }

    @Override
    @Transactional(value = "transactionManagerAdmin", readOnly = true)
    public Set<TestLocationDto> read(Set<String> ids){
        Set<TestLocation> testLocations = repository.getTestLocationsByIds(ids);
        return assembler.assembleDto(testLocations);
    }
    
    @Override
    @Transactional(value = "transactionManagerAdmin", readOnly = true)
    public Set<TestLocation> readModels(Set<String> ids){
        return repository.getTestLocationsByIds(ids);
    }

    @Override
    @Transactional(value = "transactionManagerAdmin")
    public TestLocationDto create(TestLocationDto testLocationDto) {
        TestLocation testLocation = assembler.disassembleDto(testLocationDto);
        testLocation.setId(TesutoUtils.newId());
        TestLocation savedTestLocation = repository.save(testLocation);
        return assembler.assembleDto(savedTestLocation);
    }
    
    @Override
    @Transactional(value = "transactionManagerAdmin")
    public TestLocationDto upsert(TestLocationDto testLocationDto) {
        TestLocation testLocation = assembler.disassembleDto(testLocationDto);
        if(StringUtils.isBlank(testLocation.getId())) {
            testLocation.setId(TesutoUtils.newId());
        }
        TestLocation savedTestLocation = repository.save(testLocation);
        return assembler.assembleDto(savedTestLocation);
    }

    @Override
    @Transactional(value = "transactionManagerAdmin")
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<String> validateTestLocation(TestLocationDto testLocation) {
        List<String> messages = new ArrayList<>();
        if (StringUtils.isBlank(testLocation.getName())) {
            messages.add(NAME_MISSING);
        }
        if (StringUtils.isBlank(testLocation.getLocationType())) {
            messages.add(LOCATION_TYPE_MISSING);
        }
        return messages;
    }

    

    @Transactional(value = "transactionManagerAdmin")
    @Override
    public TestLocationDto createTestLocationWithAssessments(TestLocationDto testLocationDto, Set<ScopedIdentifierDto> assessments) {
        TestLocationDto savedTestLocation = create(testLocationDto);

        testLocationAssessmentService.create(savedTestLocation.getId(), assessments);

        return savedTestLocation;
    }

    @Transactional(value = "transactionManagerAdmin")
    @Override
    public TestLocationDto editTestLocationWithAssessments(String testLocationId, TestLocationDto testLocationDto, Set<ScopedIdentifierDto> assessments) {
        TestLocationDto originalTestLocation = read(testLocationId);

        mapper.map(testLocationDto, originalTestLocation);
        originalTestLocation.setId(testLocationId);
        originalTestLocation.setLastUpdatedDate(new Date());

        testLocationAssessmentService.update(testLocationId, assessments);

        return upsert(originalTestLocation);
    }

    @Transactional(value = "transactionManagerAdmin")
    @Override
    public void enableTestLocation(String testLocationId, boolean enabled) {
        TestLocationDto testLocationDto = read(testLocationId);
        testLocationDto.setEnabled(enabled);
        upsert(testLocationDto);
    }
}
