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
package org.cccnext.tesuto.content.assembler.section;

import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto;
import org.cccnext.tesuto.content.model.section.AssessmentOrdering;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "orderingDtoAssembler")
public class AssessmentOrderingDtoAssemblerImpl implements AssessmentOrderingDtoAssembler {

    @Override
    public AssessmentOrderingDto assembleDto(AssessmentOrdering ordering) {
        // Drop out immediately if there is nothing to assemble.
        if (ordering == null) {
            return null;
        }

        AssessmentOrderingDto orderingDto = new AssessmentOrderingDto();
        // orderingDto.setExtensions(ordering.get); // Not for pilot.
        orderingDto.setShuffle(ordering.isShuffle());
        orderingDto.setExtensions(ordering.getExtensions());

        return orderingDto;
    }

    @Override
    public AssessmentOrdering disassembleDto(AssessmentOrderingDto orderingDto) {
        // Drop out immediately if there is nothing to assemble.
        if (orderingDto == null) {
            return null;
        }

        AssessmentOrdering ordering = new AssessmentOrdering();
        // orderingDto.setExtensions(ordering.get); // Not for pilot.
        ordering.setShuffle(orderingDto.isShuffle());
        ordering.setExtensions(orderingDto.getExtensions());
        return ordering;
    }
}
