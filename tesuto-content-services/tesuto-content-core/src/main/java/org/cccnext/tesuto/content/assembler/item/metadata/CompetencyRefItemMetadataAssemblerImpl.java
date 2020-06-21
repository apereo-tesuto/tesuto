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

import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.CompetencyRefItemMetadata;
import org.springframework.stereotype.Component;

@Component(value = "competencyRefItemMetadataAssembler")
public class CompetencyRefItemMetadataAssemblerImpl implements CompetencyRefItemMetadataAssembler {

    @Override
    public CompetencyRefItemMetadataDto assembleDto(CompetencyRefItemMetadata competencyMapRefItemMetadata) {
        if (competencyMapRefItemMetadata == null) {
            return null;
        }
        CompetencyRefItemMetadataDto competencyRefItemMetadataDto = new CompetencyRefItemMetadataDto();
        competencyRefItemMetadataDto.setMapDiscipline(competencyMapRefItemMetadata.getMapDiscipline());
        competencyRefItemMetadataDto.setCompetencyMapDiscipline(competencyMapRefItemMetadata.getCompetencyMapDiscipline());
        competencyRefItemMetadataDto.setCompetencyId(competencyMapRefItemMetadata.getCompetencyId());
        competencyRefItemMetadataDto.setCompetencyRefId(competencyMapRefItemMetadata.getCompetencyRefId());
        return competencyRefItemMetadataDto;
    }

    @Override
    public CompetencyRefItemMetadata disassembleDto(CompetencyRefItemMetadataDto competencyRefItemMetadataDto) {
        if (competencyRefItemMetadataDto == null) {
            return null;
        }
        CompetencyRefItemMetadata competencyMapRefItemMetadata = new CompetencyRefItemMetadata();
        competencyMapRefItemMetadata.setMapDiscipline(competencyRefItemMetadataDto.getMapDiscipline());
        competencyMapRefItemMetadata.setCompetencyMapDiscipline(competencyRefItemMetadataDto.getCompetencyMapDiscipline());
        competencyMapRefItemMetadata.setCompetencyId(competencyRefItemMetadataDto.getCompetencyId());
        competencyMapRefItemMetadata.setCompetencyRefId(competencyRefItemMetadataDto.getCompetencyRefId());
        return competencyMapRefItemMetadata;
    }

}
