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

import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "defaultValueQtiDtoAssembler")
public class AssessmentDefaultValueQtiDtoAssemblerImpl implements AssessmentDefaultValueQtiDtoAssembler {

    @Override
    public AssessmentDefaultValueDto assembleDto(DefaultValue defaultValue) {
        // Drop out immediately if there is nothing to assemble.
        if (defaultValue == null) {
            return null;
        }

        AssessmentDefaultValueDto defaultValueDto = new AssessmentDefaultValueDto();
        defaultValueDto.setDescription(defaultValue.getInterpretation());
        List<String> valueList = new LinkedList<String>();
        for (FieldValue fieldValue : defaultValue.getFieldValues()) {
            String value = fieldValue.getSingleValue().toQtiString();
            valueList.add(value);
        }
        defaultValueDto.setValues(valueList);

        return defaultValueDto;
    }

    @Override
    public DefaultValue disassembleDto(AssessmentDefaultValueDto defaultValueDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
