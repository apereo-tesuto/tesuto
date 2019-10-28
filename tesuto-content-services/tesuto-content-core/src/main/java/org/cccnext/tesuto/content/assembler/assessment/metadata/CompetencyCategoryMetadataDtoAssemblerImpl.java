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
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.content.model.metadata.CompetencyCategoryMetadata;
import org.cccnext.tesuto.content.model.metadata.PerformanceRangeMetadata;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component(value = "competencyCategoryMetadataDtoAssembler")
public class CompetencyCategoryMetadataDtoAssemblerImpl implements CompetencyCategoryMetadataDtoAssembler {
    @Autowired
    PerformanceRangeMetadataDtoAssembler performanceRangeMetadataDtoAssembler;
    @Autowired
    Mapper mapper;

    @Override
    public CompetencyCategoryMetadataDto assembleDto(CompetencyCategoryMetadata competencyCategoryMetadata) {
        if (competencyCategoryMetadata == null) {
            return null;
        }

        CompetencyCategoryMetadataDto competencyCategoryMetadataDto = mapper.map(competencyCategoryMetadata, CompetencyCategoryMetadataDto.class);
        List<PerformanceRangeMetadataDto> performanceRangeMetadataDtoList = performanceRangeMetadataDtoAssembler
                .assembleDto(competencyCategoryMetadata.getPerformanceRanges());
        competencyCategoryMetadataDto.setPerformanceRanges(performanceRangeMetadataDtoList);

        return competencyCategoryMetadataDto;
    }

    @Override
    public CompetencyCategoryMetadata disassembleDto(CompetencyCategoryMetadataDto competencyCategoryMetadataDto) {
        if (competencyCategoryMetadataDto == null) {
            return null;
        }


        CompetencyCategoryMetadata competencyCategoryMetadata = mapper.map(competencyCategoryMetadataDto, CompetencyCategoryMetadata.class);
        List<PerformanceRangeMetadata> performanceRangeMetadataList = performanceRangeMetadataDtoAssembler
                .disassembleDto(competencyCategoryMetadataDto.getPerformanceRanges());
        competencyCategoryMetadata.setPerformanceRanges(performanceRangeMetadataList);

        return competencyCategoryMetadata;
    }
}
