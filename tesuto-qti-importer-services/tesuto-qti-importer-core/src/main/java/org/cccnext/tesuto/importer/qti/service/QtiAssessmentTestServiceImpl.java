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
package org.cccnext.tesuto.importer.qti.service;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.importer.qti.assembler.assessment.AssessmentQtiDtoAssembler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service(value = "qtiAssessmentTestServiceImpl")
public class QtiAssessmentTestServiceImpl implements QtiAssessmentTestService {

    @Autowired
    private AssessmentQtiDtoAssembler assessmentTestAssembler;

    public AssessmentDto parse(AssessmentTest assessmentTest) {
        AssessmentDto assessmentDto = assessmentTestAssembler.assembleDto(assessmentTest);
        return assessmentDto;
    }
}
