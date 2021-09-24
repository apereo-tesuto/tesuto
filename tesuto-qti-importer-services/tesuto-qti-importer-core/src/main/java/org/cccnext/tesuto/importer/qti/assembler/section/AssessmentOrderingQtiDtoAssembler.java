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
package org.cccnext.tesuto.importer.qti.assembler.section;

import uk.ac.ed.ph.jqtiplus.node.test.Ordering;

import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto;
import org.cccnext.tesuto.domain.assembler.DtoAssembler;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AssessmentOrderingQtiDtoAssembler extends DtoAssembler<AssessmentOrderingDto, Ordering> {
    @Override
    AssessmentOrderingDto assembleDto(Ordering ordering);

    @Override
    Ordering disassembleDto(AssessmentOrderingDto orderingDto);
}
