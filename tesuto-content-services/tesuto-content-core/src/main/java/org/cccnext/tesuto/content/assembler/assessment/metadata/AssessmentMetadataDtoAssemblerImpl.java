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
package org.cccnext.tesuto.content.assembler.assessment.metadata;

import java.util.List;

import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyPerformanceMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.OverallPerformanceMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PrerequisiteMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto;
import org.cccnext.tesuto.content.model.metadata.AssessmentMetadata;
import org.cccnext.tesuto.content.model.metadata.CompetencyPerformanceMetadata;
import org.cccnext.tesuto.content.model.metadata.DeliveryTypeMetadata;
import org.cccnext.tesuto.content.model.metadata.OverallPerformanceMetadata;
import org.cccnext.tesuto.content.model.metadata.PrerequisiteMetadata;
import org.cccnext.tesuto.content.model.metadata.SectionMetadata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "metadataDtoAssembler")
public class AssessmentMetadataDtoAssemblerImpl implements AssessmentMetadataDtoAssembler {

    @Autowired
    PrerequisiteMetadataDtoAssembler prerequisiteMetadataDtoAssembler;
    @Autowired
    SectionMetadataDtoAssembler sectionsMetadataDtoAssembler;
    @Autowired
    DeliveryTypeMetadataDtoAssembler deliveryTypeMetadataDtoAssembler;
    @Autowired
    OverallPerformanceMetadataDtoAssembler overallPerformanceMetadataDtoAssembler;
    @Autowired
    CompetencyPerformanceMetadataDtoAssembler competencyPerformanceMetadataDtoAssembler;

    @Override
    public AssessmentMetadataDto assembleDto(AssessmentMetadata assessmentMetadata) {
        if (assessmentMetadata == null) {
            return null;
        }

        AssessmentMetadataDto assessmentMetadataDto = new AssessmentMetadataDto();
        assessmentMetadataDto.setType(assessmentMetadata.getType());
        assessmentMetadataDto.setIdentifier(assessmentMetadata.getIdentifier());
        assessmentMetadataDto.setAuthoringToolVersion(assessmentMetadata.getAuthoringToolVersion());
        assessmentMetadataDto.setAuthoringTool(assessmentMetadata.getAuthoringTool());
        assessmentMetadataDto.setAvailable(assessmentMetadata.getAvailable());
        assessmentMetadataDto.setAuthor(assessmentMetadata.getAuthor());
        assessmentMetadataDto.setDisplayInHistory(assessmentMetadata.getDisplayInHistory());
        assessmentMetadataDto.setDisplayGeneralInstructions(assessmentMetadata.getDisplayGeneralInstructions());
        assessmentMetadataDto.setDisplayGeneralClosing(assessmentMetadata.getDisplayGeneralClosing());
        assessmentMetadataDto.setAutoActivate(assessmentMetadata.getAutoActivate());
        assessmentMetadataDto.setRequirePasscode(assessmentMetadata.getRequirePasscode());
        PrerequisiteMetadataDto prerequisiteDto = prerequisiteMetadataDtoAssembler
                .assembleDto(assessmentMetadata.getPreRequisite());
        assessmentMetadataDto.setPreRequisite(prerequisiteDto);
        assessmentMetadataDto.setScaleAdditiveTerm(assessmentMetadata.getScaleAdditiveTerm());
        assessmentMetadataDto.setScaleMultiplicativeTerm(assessmentMetadata.getScaleMultiplicativeTerm());
        assessmentMetadataDto.setCompetencyMapDisciplines(assessmentMetadata.getCompetencyMapDisciplines());
        List<SectionMetadata> sectionMetadataList = assessmentMetadata.getSection();
        List<SectionMetadataDto> sectionsMetadataDtoList = sectionsMetadataDtoAssembler
                .assembleDto(sectionMetadataList);
        assessmentMetadataDto.setSection(sectionsMetadataDtoList);
        OverallPerformanceMetadataDto overallPerformanceMetadataDto = overallPerformanceMetadataDtoAssembler
                .assembleDto(assessmentMetadata.getOverallPerformanceMetadata());
        assessmentMetadataDto.setOverallPerformance(overallPerformanceMetadataDto);
        CompetencyPerformanceMetadataDto competencyPerformanceMetadataDto = competencyPerformanceMetadataDtoAssembler
                .assembleDto(assessmentMetadata.getCompetencyPerformanceMetadata());
        assessmentMetadataDto.setCompetencyPerformance(competencyPerformanceMetadataDto);
        assessmentMetadataDto.setInstructions(assessmentMetadata.getInstructions());
        DeliveryTypeMetadataDto deliveryTypeDto = deliveryTypeMetadataDtoAssembler
                .assembleDto(assessmentMetadata.getDeliveryType());
        assessmentMetadataDto.setDeliveryType(deliveryTypeDto);
        assessmentMetadataDto.setGenerateAssessmentPlacement(assessmentMetadata.getGenerateAssessmentPlacement());
        return assessmentMetadataDto;
    }

    @Override
    public AssessmentMetadata disassembleDto(AssessmentMetadataDto assessmentMetadataDto) {
        if (assessmentMetadataDto == null) {
            return null;
        }
        AssessmentMetadata assessmentMetadata = new AssessmentMetadata();
        assessmentMetadata.setType(assessmentMetadataDto.getType());
        assessmentMetadata.setIdentifier(assessmentMetadataDto.getIdentifier());
        assessmentMetadata.setAuthoringToolVersion(assessmentMetadataDto.getAuthoringToolVersion());
        assessmentMetadata.setAuthoringTool(assessmentMetadataDto.getAuthoringTool());
        assessmentMetadata.setAuthor(assessmentMetadataDto.getAuthor());
        assessmentMetadata.setAvailable(assessmentMetadataDto.getAvailable());
        assessmentMetadata.setDisplayInHistory(assessmentMetadataDto.getDisplayInHistory());
        assessmentMetadata.setDisplayGeneralInstructions(assessmentMetadataDto.getDisplayGeneralInstructions());
        assessmentMetadata.setDisplayGeneralClosing(assessmentMetadataDto.getDisplayGeneralClosing());
        assessmentMetadata.setAutoActivate(assessmentMetadataDto.getAutoActivate());
        assessmentMetadata.setRequirePasscode(assessmentMetadataDto.getRequirePasscode());
        PrerequisiteMetadata prerequisite = prerequisiteMetadataDtoAssembler
                .disassembleDto(assessmentMetadataDto.getPreRequisite());
        assessmentMetadata.setPreRequisite(prerequisite);
        assessmentMetadata.setScaleAdditiveTerm(assessmentMetadataDto.getScaleAdditiveTerm());
        assessmentMetadata.setScaleMultiplicativeTerm(assessmentMetadataDto.getScaleMultiplicativeTerm());
        assessmentMetadata.setCompetencyMapDisciplines(assessmentMetadataDto.getCompetencyMapDisciplines());
        List<SectionMetadataDto> sectionMetadataDtoList = assessmentMetadataDto.getSection();
        List<SectionMetadata> sectionsMetadataList = sectionsMetadataDtoAssembler
                .disassembleDto(sectionMetadataDtoList);
        assessmentMetadata.setSection(sectionsMetadataList);
        OverallPerformanceMetadata overallPerformanceMetadata = overallPerformanceMetadataDtoAssembler
                .disassembleDto(assessmentMetadataDto.getOverallPerformance());
        assessmentMetadata.setOverallPerformanceMetadata(overallPerformanceMetadata);
        CompetencyPerformanceMetadata competencyPerformanceMetadata = competencyPerformanceMetadataDtoAssembler
                .disassembleDto(assessmentMetadataDto.getCompetencyPerformance());
        assessmentMetadata.setCompetencyPerformanceMetadata(competencyPerformanceMetadata);
        assessmentMetadata.setInstructions(assessmentMetadataDto.getInstructions());
        DeliveryTypeMetadata deliveryType = deliveryTypeMetadataDtoAssembler
                .disassembleDto(assessmentMetadataDto.getDeliveryType());
        assessmentMetadata.setDeliveryType(deliveryType);
        assessmentMetadata.setGenerateAssessmentPlacement(assessmentMetadataDto.getGenerateAssessmentPlacement());
        return assessmentMetadata;
    }

}
