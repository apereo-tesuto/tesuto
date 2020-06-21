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

import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;

import java.util.LinkedList;
import java.util.List;



import org.springframework.stereotype.Component;

/**
 * It is unfortunate that this algorithm is implemented again because it is
 * exactly the same as the one in CorrectResponseDtoAssemblerImpl.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "defaultValueDtoAssembler")
public class AssessmentDefaultValueDtoAssemblerImpl implements AssessmentDefaultValueDtoAssembler {

    @Override
    public AssessmentDefaultValueDto assembleDto(AssessmentDefaultValue defaultValue) {
        // Drop out immediately if there is nothing to assemble.
        if (defaultValue == null) {
            return null;
        }

        AssessmentDefaultValueDto defaultValueDto = new AssessmentDefaultValueDto();
        defaultValueDto.setDescription(defaultValue.getDescription());
        List<String> values = new LinkedList<String>();
        for (String value : defaultValue.getValues()) {
            values.add(value);
        }
        defaultValueDto.setValues(values);

        return defaultValueDto;
    }

    @Override
    public AssessmentDefaultValue disassembleDto(AssessmentDefaultValueDto defaultValueDto) {
        // Drop out immediately if there is nothing to assemble.
        if (defaultValueDto == null) {
            return null;
        }

        AssessmentDefaultValue defaultValue = new AssessmentDefaultValue();
        defaultValue.setDescription(defaultValueDto.getDescription());
        List<String> valueList = new LinkedList<String>();
        for (String value : defaultValueDto.getValues()) {
            valueList.add(value);
        }
        defaultValue.setValues(valueList);

        return defaultValue;
    }
}
