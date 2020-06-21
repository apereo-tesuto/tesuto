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
package org.cccnext.tesuto.content.assembler.assessment;

import org.cccnext.tesuto.content.assembler.assessment.metadata.AssessmentMetadataDtoAssembler;
import org.cccnext.tesuto.content.assembler.shared.AssessmentOutcomeDeclarationDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentOutcomeProcessingDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.model.Assessment;
import org.cccnext.tesuto.content.model.AssessmentOutcomeProcessing;
import org.cccnext.tesuto.content.model.metadata.AssessmentMetadata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentDtoAssembler")
public class AssessmentDtoAssemblerImpl implements AssessmentDtoAssembler {

    @Autowired
    AssessmentOutcomeDeclarationDtoAssembler outcomeDeclarationDtoAssembler;
    @Autowired
    AssessmentOutcomeProcessingDtoAssembler outcomeProcessingDtoAssembler;
    @Autowired
    AssessmentPartDtoAssembler assessmentPartDtoAssembler;
    @Autowired
    AssessmentMetadataDtoAssembler metadataDtoAssembler;

    @Override
    public AssessmentDto assembleDto(Assessment assessment) {
        // Drop out immediately if there is nothing to assemble.
        if (assessment == null) {
            return null;
        }

        AssessmentDto assessmentDto = new AssessmentDto();

        assessmentDto.setNamespace(assessment.getNamespace());
        assessmentDto.setAssessmentParts(assessmentPartDtoAssembler.assembleDto(assessment.getAssessmentParts()));
        assessmentDto.setId(assessment.getId());
        assessmentDto.setIdentifier(assessment.getIdentifier());
        assessmentDto.setVersion(assessment.getVersion());
        assessmentDto.setPublished(assessment.isPublished());
        assessmentDto.setResources(assessment.getResources());
        // Not sure where to get Language. This is only available at the item
        // level. We need it on the assessment level.
        // For now we will default to English.
        assessmentDto.setLanguage(assessment.getLanguage()); // Not sure if this
                                                             // is actually
                                                             // going to be the
                                                             // code we use.
        AssessmentOutcomeProcessingDto outcomeProcessingDto = outcomeProcessingDtoAssembler
                .assembleDto(assessment.getOutcomeProcessing());
        assessmentDto.setOutcomeProcessing(outcomeProcessingDto);
        assessmentDto.setOutcomeDeclarations(outcomeDeclarationDtoAssembler.assembleDto(assessment.getOutcomeDeclarations()));
        assessmentDto.setTitle(assessment.getTitle());
        assessmentDto.setToolName(assessment.getToolName());
        assessmentDto.setToolVersion(assessment.getToolVersion());
        assessmentDto.setStylesheets(assessment.getStylesheets());

        assessmentDto.setDuration(assessment.getDuration());

        AssessmentMetadataDto assessmentMetadataDto = metadataDtoAssembler
                .assembleDto(assessment.getAssessmentMetadata());
        assessmentDto.setAssessmentMetadata(assessmentMetadataDto);
        return assessmentDto;
    }

    @Override
    public Assessment disassembleDto(AssessmentDto assessmentDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentDto == null) {
            return null;
        }

        Assessment assessment = new Assessment();

        assessment.setNamespace(assessmentDto.getNamespace());
        assessment.setAssessmentParts(assessmentPartDtoAssembler.disassembleDto(assessmentDto.getAssessmentParts()));
        assessment.setId(assessmentDto.getId());
        assessment.setIdentifier(assessmentDto.getIdentifier());
        assessment.setVersion(assessmentDto.getVersion());
        assessment.setPublished(assessmentDto.isPublished());
        assessment.setResources(assessmentDto.getResources());
        // Not sure where to get Language. This is only available at the item
        // level. We need it on the assessment level.
        // For now we will default to English.
        assessment.setLanguage(assessmentDto.getLanguage()); // Not sure if this
                                                             // is actually
                                                             // going to be the
                                                             // code we use.
        AssessmentOutcomeProcessing outcomeProcessing = outcomeProcessingDtoAssembler
                .disassembleDto(assessmentDto.getOutcomeProcessing());
        assessment.setOutcomeProcessing(outcomeProcessing);

        assessment.setOutcomeDeclarations(outcomeDeclarationDtoAssembler.disassembleDto(assessmentDto.getOutcomeDeclarations()));

        assessment.setTitle(assessmentDto.getTitle());
        assessment.setToolName(assessmentDto.getToolName());
        assessment.setToolVersion(assessmentDto.getToolVersion());
        assessment.setStylesheets(assessmentDto.getStylesheets());

        assessment.setDuration(assessmentDto.getDuration());

        AssessmentMetadata assessmentMetadata = metadataDtoAssembler
                .disassembleDto(assessmentDto.getAssessmentMetadata());
        assessment.setAssessmentMetadata(assessmentMetadata);
        return assessment;
    }
}
