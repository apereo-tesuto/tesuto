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
package org.cccnext.tesuto.content.assembler.item.metadata;

import java.util.List;

import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ToolsItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.CompetenciesItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.CompetencyRefItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.ItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.ToolsItemMetadata;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "itemMetadataDtoAssembler")
public class ItemMetadataDtoAssemblerImpl implements ItemMetadataDtoAssembler {

    @Autowired
    CompetenciesItemMetadataDtoAssembler competenciesItemMetadataDtoAssembler;
    @Autowired
    ToolsItemMetadataDtoAssembler toolsItemMetadataDtoAssembler;

    @Override
    public ItemMetadataDto assembleDto(ItemMetadata itemMetadata) {
        if (itemMetadata == null) {
            return null;
        }

        ItemMetadataDto itemMetadataDto = new ItemMetadataDto();
        itemMetadataDto.setType(itemMetadata.getType());
        itemMetadataDto.setIdentifier(itemMetadata.getIdentifier());
        itemMetadataDto.setAuthoringTool(itemMetadata.getAuthoringTool());
        itemMetadataDto.setAuthoringToolVersion(itemMetadata.getAuthoringToolVersion());
        itemMetadataDto.setAuthoringToolAnswerType(itemMetadata.getAuthoringToolAnswerType());
        itemMetadataDto.setAuthor(itemMetadata.getAuthor());
        itemMetadataDto.setDifficulty(itemMetadata.getDifficulty());
        itemMetadataDto.setWeightedCategory(itemMetadata.getWeightedCategory());
        itemMetadataDto.setContextual(itemMetadata.getContextual());
        itemMetadataDto.setTheme(itemMetadata.getTheme());
        itemMetadataDto.setCommonCoreRef(itemMetadata.getCommonCoreRef());
        itemMetadataDto.setLexile(itemMetadata.getLexile());
        itemMetadataDto.setPassage(itemMetadata.getPassage());
        itemMetadataDto.setPassageType(itemMetadata.getPassageType());
        CompetenciesItemMetadataDto competenciesItemMetadataDto = competenciesItemMetadataDtoAssembler
                .assembleDto(itemMetadata.getCompetencies());
        itemMetadataDto.setCompetencies(competenciesItemMetadataDto);

        ToolsItemMetadataDto toolsItemMetadataDto = toolsItemMetadataDtoAssembler.assembleDto(itemMetadata.getTools());
        itemMetadataDto.setTools(toolsItemMetadataDto);
        itemMetadataDto.setCalibratedDifficulty(itemMetadata.getCalibratedDifficulty());
        itemMetadataDto.setItemBankStatusType(itemMetadata.getItemBankStatusType());
        return itemMetadataDto;
    }

    @Override
    public ItemMetadata disassembleDto(ItemMetadataDto itemMetadataDto) {
        if (itemMetadataDto == null) {
            return null;
        }
        ItemMetadata itemMetadata = new ItemMetadata();
        itemMetadata.setType(itemMetadataDto.getType());
        itemMetadata.setIdentifier(itemMetadataDto.getIdentifier());
        itemMetadata.setAuthoringTool(itemMetadataDto.getAuthoringTool());
        itemMetadata.setAuthoringToolVersion(itemMetadataDto.getAuthoringToolVersion());
        itemMetadata.setAuthoringToolAnswerType(itemMetadataDto.getAuthoringToolAnswerType());
        itemMetadata.setAuthor(itemMetadataDto.getAuthor());
        itemMetadata.setDifficulty(itemMetadataDto.getDifficulty());
        itemMetadata.setWeightedCategory(itemMetadataDto.getWeightedCategory());
        itemMetadata.setContextual(itemMetadataDto.getContextual());
        itemMetadata.setTheme(itemMetadataDto.getTheme());
        itemMetadata.setCommonCoreRef(itemMetadataDto.getCommonCoreRef());
        itemMetadata.setLexile(itemMetadataDto.getLexile());
        itemMetadata.setPassage(itemMetadataDto.getPassage());
        itemMetadata.setPassageType(itemMetadataDto.getPassageType());
        CompetenciesItemMetadata competenciesItemMetadata = competenciesItemMetadataDtoAssembler
                .disassembleDto(itemMetadataDto.getCompetencies());
        itemMetadata.setCompetencies(competenciesItemMetadata);

        ToolsItemMetadata toolsItemMetadata = toolsItemMetadataDtoAssembler.disassembleDto(itemMetadataDto.getTools());
        itemMetadata.setTools(toolsItemMetadata);
        itemMetadata.setCalibratedDifficulty(itemMetadataDto.getCalibratedDifficulty());
        itemMetadata.setItemBankStatusType(itemMetadataDto.getItemBankStatusType());
        return itemMetadata;
    }

}
