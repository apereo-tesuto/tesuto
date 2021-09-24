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

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.item.AssessmentCorrectResponseDto;
import org.cccnext.tesuto.content.model.item.AssessmentCorrectResponse;


import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "correctResponseDtoAssembler")
public class AssessmentCorrectResponseDtoAssemblerImpl implements AssessmentCorrectResponseDtoAssembler {

    @Override
    public AssessmentCorrectResponseDto assembleDto(AssessmentCorrectResponse correctResponse) {
        // Drop out immediately if there is nothing to assemble.
        if (correctResponse == null) {
            return null;
        }

        AssessmentCorrectResponseDto correctResponseDto = new AssessmentCorrectResponseDto();
        correctResponseDto.setDescription(correctResponse.getDescription());
        List<String> valueList = new LinkedList<String>();
        for (String value : correctResponse.getValues()) {
            valueList.add(value);
        }
        correctResponseDto.setValues(valueList);

        return correctResponseDto;
    }

    @Override
    public AssessmentCorrectResponse disassembleDto(AssessmentCorrectResponseDto correctResponseDto) {
        if (correctResponseDto == null) {
            return null;
        }

        AssessmentCorrectResponse correctResponse = new AssessmentCorrectResponse();
        correctResponse.setDescription(correctResponseDto.getDescription());
        List<String> valueList = new LinkedList<String>();
        for (String value : correctResponseDto.getValues()) {
            valueList.add(value);
        }
        correctResponse.setValues(valueList);

        return correctResponse;
    }
}
