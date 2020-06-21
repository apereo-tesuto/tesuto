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
import org.cccnext.tesuto.domain.assembler.DtoAssembler;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AssessmentCardinalityQtiDtoAssembler extends DtoAssembler<AssessmentCardinality, Cardinality> {

    @Override
    AssessmentCardinality assembleDto(Cardinality cardinality);

    @Override
    Cardinality disassembleDto(AssessmentCardinality cardinalityDto);
}
