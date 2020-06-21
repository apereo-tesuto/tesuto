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

import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.CompetenciesItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.CompetencyRefItemMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jasonbrown on 1/21/16.
 */
@Component(value = "competenciesItemMetadataDtoAssembler")
public class CompetenciesItemMetadataDtoAssemblerImpl implements CompetenciesItemMetadataDtoAssembler {

    @Autowired
    CompetencyRefItemMetadataAssembler competencyRefItemMetadataAssembler;

    @Override
    public CompetenciesItemMetadataDto assembleDto(CompetenciesItemMetadata competenciesItemMetadata) {
        if (competenciesItemMetadata == null) {
            return null;
        }

        CompetenciesItemMetadataDto competenciesItemMetadataDto = new CompetenciesItemMetadataDto();
        List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtoList = competencyRefItemMetadataAssembler
                .assembleDto(competenciesItemMetadata.getCompetencyRef());
        competenciesItemMetadataDto.setCompetencyRef(competencyRefItemMetadataDtoList);
        competenciesItemMetadataDto.setSkippedCompetency(competenciesItemMetadata.getSkippedCompetency());
        competenciesItemMetadataDto.setSkippedCompetencyRefId(competenciesItemMetadata.getSkippedCompetencyRefId());
        return competenciesItemMetadataDto;
    }

    @Override
    public CompetenciesItemMetadata disassembleDto(CompetenciesItemMetadataDto competenciesItemMetadataDto) {
        if (competenciesItemMetadataDto == null) {
            return null;
        }

        CompetenciesItemMetadata competenciesItemMetadata = new CompetenciesItemMetadata();
        List<CompetencyRefItemMetadata> competencyRefItemMetadataList = competencyRefItemMetadataAssembler
                .disassembleDto(competenciesItemMetadataDto.getCompetencyRef());
        competenciesItemMetadata.setCompetencyRef(competencyRefItemMetadataList);
        competenciesItemMetadata.setSkippedCompetency(competenciesItemMetadataDto.getSkippedCompetency());
        competenciesItemMetadata.setSkippedCompetencyRefId(competenciesItemMetadataDto.getSkippedCompetencyRefId());
        return competenciesItemMetadata;
    }
}
