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

import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyPerformanceMetadataDto;
import org.cccnext.tesuto.content.model.metadata.CompetencyCategoryMetadata;
import org.cccnext.tesuto.content.model.metadata.CompetencyPerformanceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component(value = "competencyPerformanceMetadataDtoAssembler")
public class CompetencyPerformanceMetadataDtoAssemblerImpl implements CompetencyPerformanceMetadataDtoAssembler {
    @Autowired
    CompetencyCategoryMetadataDtoAssembler competencyCategoryMetadataDtoAssembler;

    @Override
    public CompetencyPerformanceMetadataDto assembleDto(CompetencyPerformanceMetadata competencyPerformanceMetadata) {
        if (competencyPerformanceMetadata == null) {
            return null;
        }

        CompetencyPerformanceMetadataDto competencyPerformanceMetadataDto = new CompetencyPerformanceMetadataDto();
        List<CompetencyCategoryMetadataDto> competencyCategoryMetadataDtoList = competencyCategoryMetadataDtoAssembler
                .assembleDto(competencyPerformanceMetadata.getCompetencyCategories());
        competencyPerformanceMetadataDto.setCompetencyCategories(competencyCategoryMetadataDtoList);

        return competencyPerformanceMetadataDto;
    }

    @Override
    public CompetencyPerformanceMetadata disassembleDto(CompetencyPerformanceMetadataDto competencyPerformanceMetadataDto) {
        if (competencyPerformanceMetadataDto == null) {
            return null;
        }

        CompetencyPerformanceMetadata competencyPerformanceMetadata = new CompetencyPerformanceMetadata();
        List<CompetencyCategoryMetadata> competencyCategoryMetadataList = competencyCategoryMetadataDtoAssembler
                .disassembleDto(competencyPerformanceMetadataDto.getCompetencyCategories());
        competencyPerformanceMetadata.setCompetencyCategories(competencyCategoryMetadataList);

        return competencyPerformanceMetadata;
    }
}
