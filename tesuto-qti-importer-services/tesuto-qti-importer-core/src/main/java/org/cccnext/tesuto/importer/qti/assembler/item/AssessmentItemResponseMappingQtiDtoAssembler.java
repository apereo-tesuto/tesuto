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

import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.cccnext.tesuto.domain.assembler.DtoAssembler;

import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.Mapping;

public interface AssessmentItemResponseMappingQtiDtoAssembler
        extends DtoAssembler<AssessmentItemResponseMappingDto, Mapping> {

    @Override
    AssessmentItemResponseMappingDto assembleDto(Mapping mapping);

    @Override
    Mapping disassembleDto(AssessmentItemResponseMappingDto assessmentItemResponseMapping);
}
