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

import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "cardinalityQtiDtoAssembler")
public class AssessmentCardinalityQtiDtoAssemblerImpl implements AssessmentCardinalityQtiDtoAssembler {
    @Override
    public AssessmentCardinality assembleDto(Cardinality cardinality) {
        // Drop out immediately if there is nothing to assemble.
        if (cardinality == null) {
            return null;
        }

        AssessmentCardinality cardinalityDto;
        switch (cardinality.toQtiString()) {
        case "single":
            cardinalityDto = AssessmentCardinality.SINGLE;
            break;
        case "multiple":
            cardinalityDto = AssessmentCardinality.MULTIPLE;
            break;
        case "ordered":
            cardinalityDto = AssessmentCardinality.ORDERED;
            break;
        case "record":
            cardinalityDto = AssessmentCardinality.RECORD;
            break;
        default:
            cardinalityDto = null;
        }

        return cardinalityDto;
    }

    @Override
    public Cardinality disassembleDto(AssessmentCardinality cardinalityDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
