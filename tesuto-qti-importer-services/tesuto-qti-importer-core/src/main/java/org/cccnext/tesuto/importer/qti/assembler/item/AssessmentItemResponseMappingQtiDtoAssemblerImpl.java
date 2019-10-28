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
package org.cccnext.tesuto.importer.qti.assembler.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.springframework.stereotype.Component;

import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.MapEntry;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.Mapping;

@Component(value = "assessmentItemResponseMappingQtiDtoAssembler")
public class AssessmentItemResponseMappingQtiDtoAssemblerImpl implements AssessmentItemResponseMappingQtiDtoAssembler {

    @Override
    public AssessmentItemResponseMappingDto assembleDto(Mapping mapping) {
        if (mapping == null) {
            return null;
        }
        AssessmentItemResponseMappingDto assessmentItemResponseMappingDto = new AssessmentItemResponseMappingDto();

        assessmentItemResponseMappingDto.setDefaultValue(mapping.getDefaultValue());
        assessmentItemResponseMappingDto.setLowerBound(mapping.getLowerBound());
        assessmentItemResponseMappingDto.setUpperBound(mapping.getUpperBound());
        Map<String, Double> map = new HashMap<String, Double>();
        List<MapEntry> mapEntries = mapping.getMapEntries();
        for (MapEntry mapEntry : mapEntries) {
            map.put(mapEntry.getMapKey().toQtiString(), mapEntry.getMappedValue());
        }
        assessmentItemResponseMappingDto.setMapping(map);
        return assessmentItemResponseMappingDto;
    }

    @Override
    public Mapping disassembleDto(AssessmentItemResponseMappingDto assessmentItemResponseMapping) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }

}
