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

import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.cccnext.tesuto.content.model.item.AssessmentItemResponseMapping;
import org.springframework.stereotype.Component;

@Component(value = "assessmentItemResponseMappingDtoAssembler")
public class AssessmentItemResponseMappingDtoAssemblerImpl implements AssessmentItemResponseMappingDtoAssembler {

    @Override
    public AssessmentItemResponseMappingDto assembleDto(AssessmentItemResponseMapping mapping) {
        if (mapping == null) {
            return null;
        }
        AssessmentItemResponseMappingDto mappingDto = new AssessmentItemResponseMappingDto();
        mappingDto.setDefaultValue(mapping.getDefaultValue());
        mappingDto.setLowerBound(mapping.getLowerBound());
        mappingDto.setUpperBound(mapping.getUpperBound());
        mappingDto.setMapping(mapping.getMapping());
        return mappingDto;
    }

    @Override
    public AssessmentItemResponseMapping disassembleDto(AssessmentItemResponseMappingDto mappingDto) {
        if (mappingDto == null) {
            return null;
        }
        AssessmentItemResponseMapping mapping = new AssessmentItemResponseMapping();
        mapping.setDefaultValue(mappingDto.getDefaultValue());
        mapping.setLowerBound(mappingDto.getLowerBound());
        mapping.setUpperBound(mappingDto.getUpperBound());
        mapping.setMapping(mappingDto.getMapping());
        return mapping;
    }

}
