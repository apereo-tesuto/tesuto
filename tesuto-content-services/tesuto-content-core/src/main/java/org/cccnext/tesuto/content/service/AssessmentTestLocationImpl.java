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
package org.cccnext.tesuto.content.service;

import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationAssessmentDto;
import org.cccnext.tesuto.admin.service.TestLocationAssessmentReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.AssessmentTestLocationService;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentTestLocationImpl implements AssessmentTestLocationService {
	
	@Autowired
    AssessmentReader assessmentService;
	
	@Autowired
	TestLocationAssessmentReader service;

    public Set<AssessmentViewDto> getByTestLocation(String testLocationId) {
        Set<TestLocationAssessmentDto> testLocationAssessments = service.getByTestLocation(testLocationId);
        Set<AssessmentViewDto> assessmentViewDtos = new HashSet<>();
        for (TestLocationAssessmentDto testLocationAssessment : testLocationAssessments) {
            AssessmentViewDto assessmentViewDto = assessmentService.readViewDto(testLocationAssessment.getAssessmentNamespace(), testLocationAssessment.getAssessmentIdentifier());
            if (assessmentViewDto != null) {
                assessmentViewDtos.add(assessmentViewDto);
            }
        }
        return assessmentViewDtos;
    }
}
