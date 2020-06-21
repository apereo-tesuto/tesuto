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

import org.cccnext.tesuto.content.dto.metadata.OverallPerformanceMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.content.model.metadata.OverallPerformanceMetadata;
import org.cccnext.tesuto.content.model.metadata.PerformanceRangeMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component(value = "overallPerformanceMetadataDtoAssembler")
public class OverallPerformanceMetadataDtoAssemblerImpl implements OverallPerformanceMetadataDtoAssembler {
    @Autowired
    PerformanceRangeMetadataDtoAssembler performanceRangeMetadataDtoAssembler;

    @Override
    public OverallPerformanceMetadataDto assembleDto(OverallPerformanceMetadata overallPerformanceMetadata) {
        if (overallPerformanceMetadata == null) {
            return null;
        }

        OverallPerformanceMetadataDto overallPerformanceMetadataDto = new OverallPerformanceMetadataDto();
        List<PerformanceRangeMetadataDto> performanceRangeMetadataDtoList = performanceRangeMetadataDtoAssembler.
                assembleDto(overallPerformanceMetadata.getPerformanceRangeMetadataList());
        overallPerformanceMetadataDto.setPerformanceRanges(performanceRangeMetadataDtoList);

        return overallPerformanceMetadataDto;
    }

    @Override
    public OverallPerformanceMetadata disassembleDto(OverallPerformanceMetadataDto overallPerformanceMetadataDto) {
        if (overallPerformanceMetadataDto == null) {
            return null;
        }

        OverallPerformanceMetadata overallPerformanceMetadata = new OverallPerformanceMetadata();
        List<PerformanceRangeMetadata> performanceRangeMetadataList = performanceRangeMetadataDtoAssembler
                .disassembleDto(overallPerformanceMetadataDto.getPerformanceRanges());
        overallPerformanceMetadata.setPerformanceRangeMetadataList(performanceRangeMetadataList);

        return overallPerformanceMetadata;
    }
}
