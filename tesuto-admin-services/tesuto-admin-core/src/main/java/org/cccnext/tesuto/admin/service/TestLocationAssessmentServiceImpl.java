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

import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.assembler.TestLocationAssessmentDtoAssembler;
import org.cccnext.tesuto.admin.dto.TestLocationAssessmentDto;
import org.cccnext.tesuto.admin.model.TestLocationAssessment;
import org.cccnext.tesuto.admin.model.TestLocationAssessmentId;
import org.cccnext.tesuto.admin.repository.TestLocationAssessmentRepository;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service(value = "testLocationAssessmentService")
public class TestLocationAssessmentServiceImpl implements TestLocationAssessmentService {

    @Autowired
    TestLocationAssessmentRepository repository;
    

    
    @Autowired
    TestLocationAssessmentDtoAssembler assembler;

    @Override
    public void create(String testLocationId, String namespace, String identifier) {
        TestLocationAssessment testLocationAssessment = new TestLocationAssessment();
        testLocationAssessment.setTestLocationId(testLocationId);
        testLocationAssessment.setAssessmentIdentifier(identifier);
        testLocationAssessment.setAssessmentNamespace(namespace);
        repository.save(testLocationAssessment);
    }


    @Override
    public void delete(String testLocationId, String namespace, String identifier) {
        repository.deleteById(new TestLocationAssessmentId(testLocationId, namespace, identifier));
    }

    @Transactional
    @Override
    public void update(String testLocationId, Set<ScopedIdentifierDto> updateScopedIdentifiers) {
        Set<TestLocationAssessmentDto> currentAssessments = getByTestLocation(testLocationId);

        // delete old ones
        Set<TestLocationAssessment> toBeDeletedAssessments = new HashSet<>();
        for (TestLocationAssessmentDto assessment : currentAssessments) {
            if (!updateScopedIdentifiers.contains(new ScopedIdentifierDto(assessment.getAssessmentNamespace(), assessment.getAssessmentIdentifier()))) {
                toBeDeletedAssessments.add(assembler.disassembleDto(assessment));
            }
        }
        repository.deleteInBatch(toBeDeletedAssessments);

        // add new ones
        if (updateScopedIdentifiers != null) {
            for (ScopedIdentifierDto assessment : updateScopedIdentifiers) {
                if (!currentAssessments.contains(assessment)) {
                    create(testLocationId, assessment.getNamespace(), assessment.getIdentifier());
                }
            }
        }
    }

    public void create(String testLocationId, Set<ScopedIdentifierDto> assessments) {
        if (assessments != null) {
            for (ScopedIdentifierDto assessment : assessments) {
                create(testLocationId, assessment.getNamespace(), assessment.getIdentifier());
            }
        }
    }

    public Set<TestLocationAssessmentDto> getByTestLocation(String testLocationId) {
        return assembler.assembleDto(repository.findAllByTestLocationId(testLocationId));
    }


	@Override
	public void deleteInBatch(Set<TestLocationAssessmentDto> assessments) {
		repository.deleteInBatch(assembler.disassembleDto(assessments));
	}


	@Override
	public void create(String testLocationId, ScopedIdentifierDto assessment) {
		TestLocationAssessment testLocationAssessment = new TestLocationAssessment();
        testLocationAssessment.setTestLocationId(testLocationId);
        testLocationAssessment.setAssessmentIdentifier(assessment.getIdentifier());
        testLocationAssessment.setAssessmentNamespace(assessment.getNamespace());
        repository.save(testLocationAssessment);	
	}
}
