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
package org.cccnext.tesuto.content.assembler.section;

import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.section.AssessmentItemRef;
import org.cccnext.tesuto.content.model.section.AssessmentSection;
import org.cccnext.tesuto.content.model.shared.AssessmentComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Created by jcorbin on 2/8/16.
 */
@Component(value = "assessmentComponentDtoAssembler")
public class AssessmentComponentDtoAssemblerImpl implements AssessmentComponentDtoAssembler {

    @Autowired
    AssessmentItemRefDtoAssembler assessmentItemRefDtoAssembler;
    @Autowired
    AssessmentSectionDtoAssembler assessmentSectionDtoAssembler;

    @Override
    public AssessmentComponentDto assembleDto(AssessmentComponent assessmentComponent) {

        AssessmentComponentDto assessmentComponentDto = null;

        if (assessmentComponent instanceof AssessmentSection) {
            assessmentComponentDto = assessmentSectionDtoAssembler.assembleDto((AssessmentSection) assessmentComponent);
        } else {
            if (assessmentComponent instanceof AssessmentItemRef) {
                assessmentComponentDto = assessmentItemRefDtoAssembler
                        .assembleDto((AssessmentItemRef) assessmentComponent);
            }
        }
        return assessmentComponentDto;
    }

    @Override
    public AssessmentComponent disassembleDto(AssessmentComponentDto assessmentComponentDto) {

        AssessmentComponent assessmentComponent = null;

        if (assessmentComponentDto instanceof AssessmentSectionDto) {
            assessmentComponent = assessmentSectionDtoAssembler
                    .disassembleDto((AssessmentSectionDto) assessmentComponentDto);
        } else {
            if (assessmentComponentDto instanceof AssessmentItemRefDto) {
                assessmentComponent = assessmentItemRefDtoAssembler
                        .disassembleDto((AssessmentItemRefDto) assessmentComponentDto);
            }
        }
        return assessmentComponent;
    }
}
