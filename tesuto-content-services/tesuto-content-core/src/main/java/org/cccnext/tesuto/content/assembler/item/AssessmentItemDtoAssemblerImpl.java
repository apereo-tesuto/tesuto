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
package org.cccnext.tesuto.content.assembler.item;

import java.util.List;

import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentChoiceInteractionDtoAssembler;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentExtendedTextInteractionDtoAssembler;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentInteractionDtoAssembler;
import org.cccnext.tesuto.content.assembler.item.interaction.AssessmentTextEntryInteractionDtoAssembler;
import org.cccnext.tesuto.content.assembler.item.metadata.ItemMetadataDtoAssembler;
import org.cccnext.tesuto.content.assembler.shared.AssessmentOutcomeDeclarationDtoAssembler;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.cccnext.tesuto.content.model.item.AssessmentResponseProcessing;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInteraction;
import org.cccnext.tesuto.content.model.item.metadata.ItemMetadata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentItemDtoAssembler")
public class AssessmentItemDtoAssemblerImpl implements AssessmentItemDtoAssembler {

    @Autowired
    AssessmentResponseVarDtoAssembler responseVarDtoAssembler;
    @Autowired
    AssessmentOutcomeDeclarationDtoAssembler outcomeDeclarationDtoAssembler;
    @Autowired
    AssessmentTemplateDeclarationDtoAssembler templateDeclarationDtoAssembler;
    @Autowired
    AssessmentTemplateProcessingDtoAssembler templateProcessingDtoAssembler;
    @Autowired
    AssessmentStimulusRefDtoAssembler assessmentStimulusRefDtoAssembler;
    @Autowired
    AssessmentResponseProcessingDtoAssembler responseProcessingDtoAssembler;
    @Autowired
    AssessmentInteractionDtoAssembler assessmentInteractionDtoAssembler;
    @Autowired
    AssessmentChoiceInteractionDtoAssembler choiceInteractionDtoAssembler;
    @Autowired
    AssessmentTextEntryInteractionDtoAssembler textEntryInteractionDtoAssembler;
    @Autowired
    AssessmentExtendedTextInteractionDtoAssembler extendedTextInteractionDtoAssembler;
    @Autowired
    ItemMetadataDtoAssembler itemMetadataDtoAssembler;

    @Override
    public AssessmentItemDto assembleDto(AssessmentItem assessmentItem) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItem == null) {
            return null;
        }

        AssessmentItemDto assessmentItemDto = new AssessmentItemDto();

        assessmentItemDto.setBody(assessmentItem.getBody());
        assessmentItemDto.setNamespace(assessmentItem.getNamespace());
        List<AssessmentInteraction> assessmentInteractionList = assessmentItem.getInteractions();
        List<AssessmentInteractionDto> assessmentInteractionDtoList = assessmentInteractionDtoAssembler
                .assembleDto(assessmentInteractionList);
        assessmentItemDto.setInteractions(assessmentInteractionDtoList);
        assessmentItemDto.setId(assessmentItem.getId());
        assessmentItemDto.setIdentifier(assessmentItem.getIdentifier());
        assessmentItemDto.setVersion(assessmentItem.getVersion());
        assessmentItemDto.setPublished(assessmentItem.isPublished());
        assessmentItemDto.setResources(assessmentItem.getResources());
        assessmentItemDto.setLabel(assessmentItem.getLabel());
        assessmentItemDto.setLanguage(assessmentItem.getLanguage());

        assessmentItemDto.setOutcomeDeclarationDtos(outcomeDeclarationDtoAssembler.assembleDto(assessmentItem.getOutcomeDeclarations()));
        AssessmentResponseProcessingDto responseProcessingDto = responseProcessingDtoAssembler
                .assembleDto(assessmentItem.getResponseProcessing());
        assessmentItemDto.setResponseProcessing(responseProcessingDto);
        assessmentItemDto.setResponseVars(responseVarDtoAssembler.assembleDto(assessmentItem.getResponseVars()));
        assessmentItemDto
                .setStimulusRef(assessmentStimulusRefDtoAssembler.assembleDto(assessmentItem.getStimulusRef()));
        assessmentItemDto.setTemplateProcessing(
                templateProcessingDtoAssembler.assembleDto(assessmentItem.getTemplateProcessing()));
        assessmentItemDto
                .setTemplateVars(templateDeclarationDtoAssembler.assembleDto(assessmentItem.getTemplateVars()));

        assessmentItemDto.setTitle(assessmentItem.getTitle());
        assessmentItemDto.setToolName(assessmentItem.getToolName());
        assessmentItemDto.setToolVersion(assessmentItem.getToolVersion());
        assessmentItemDto.setStylesheets(assessmentItem.getStylesheets());

        ItemMetadataDto itemMetadataDto = itemMetadataDtoAssembler.assembleDto(assessmentItem.getItemMetadata());
        assessmentItemDto.setItemMetadata(itemMetadataDto);
        return assessmentItemDto;
    }

    @Override
    public AssessmentItem disassembleDto(AssessmentItemDto assessmentItemDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItemDto == null) {
            return null;
        }

        AssessmentItem assessmentItem = new AssessmentItem();

        assessmentItem.setBody(assessmentItemDto.getBody());
        assessmentItem.setNamespace(assessmentItemDto.getNamespace());
        List<AssessmentInteractionDto> assessmentInteractionDtoList = assessmentItemDto.getInteractions();
        List<AssessmentInteraction> assessmentInteractionList = assessmentInteractionDtoAssembler
                .disassembleDto(assessmentInteractionDtoList);
        assessmentItem.setInteractions(assessmentInteractionList);
        assessmentItem.setId(assessmentItemDto.getId());
        assessmentItem.setIdentifier(assessmentItemDto.getIdentifier());
        assessmentItem.setVersion(assessmentItemDto.getVersion());
        assessmentItem.setPublished(assessmentItemDto.isPublished());
        assessmentItem.setResources(assessmentItemDto.getResources());
        assessmentItem.setLabel(assessmentItemDto.getLabel());
        assessmentItem.setLanguage(assessmentItemDto.getLanguage());

        assessmentItem.setOutcomeDeclarations(outcomeDeclarationDtoAssembler.disassembleDto(assessmentItemDto.getOutcomeDeclarationDtos()));
        AssessmentResponseProcessing responseProcessing = responseProcessingDtoAssembler
                .disassembleDto(assessmentItemDto.getResponseProcessing());
        assessmentItem.setResponseProcessing(responseProcessing);
        assessmentItem.setResponseVars(responseVarDtoAssembler.disassembleDto(assessmentItemDto.getResponseVars()));
        assessmentItem
                .setStimulusRef(assessmentStimulusRefDtoAssembler.disassembleDto(assessmentItemDto.getStimulusRef()));
        assessmentItem.setTemplateProcessing(
                templateProcessingDtoAssembler.disassembleDto(assessmentItemDto.getTemplateProcessing()));
        assessmentItem
                .setTemplateVars(templateDeclarationDtoAssembler.disassembleDto(assessmentItemDto.getTemplateVars()));

        assessmentItem.setTitle(assessmentItemDto.getTitle());
        assessmentItem.setToolName(assessmentItemDto.getToolName());
        assessmentItem.setToolVersion(assessmentItemDto.getToolVersion());
        assessmentItem.setStylesheets(assessmentItemDto.getStylesheets());

        ItemMetadata itemMetadata = itemMetadataDtoAssembler.disassembleDto(assessmentItemDto.getItemMetadata());
        assessmentItem.setItemMetadata(itemMetadata);
        return assessmentItem;
    }

}
