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
package org.cccnext.tesuto.importer.qti.assembler;

import uk.ac.ed.ph.jqtiplus.value.BaseType;

import org.cccnext.tesuto.content.dto.AssessmentBaseType;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "baseTypeQtiDtoAssembler")
public class AssessmentBaseTypeQtiDtoAssemblerImpl implements AssessmentBaseTypeQtiDtoAssembler {

    @Override
    public AssessmentBaseType assembleDto(BaseType baseType) {
        // Drop out immediately if there is nothing to assemble.
        if (baseType == null) {
            return null;
        }

        AssessmentBaseType baseTypeDto;
        switch (baseType.toQtiString()) {
        case "identifier":
            baseTypeDto = AssessmentBaseType.IDENTIFIER;
            break;
        case "boolean":
            baseTypeDto = AssessmentBaseType.BOOLEAN;
            break;
        case "integer":
            baseTypeDto = AssessmentBaseType.INTEGER;
            break;
        case "float":
            baseTypeDto = AssessmentBaseType.FLOAT;
            break;
        case "string":
            baseTypeDto = AssessmentBaseType.STRING;
            break;
        case "point":
            baseTypeDto = AssessmentBaseType.POINT;
            break;
        case "pair":
            baseTypeDto = AssessmentBaseType.PAIR;
            break;
        case "directedPair":
            baseTypeDto = AssessmentBaseType.DIRECTED_PAIR;
            break;
        case "duration":
            baseTypeDto = AssessmentBaseType.DURATION;
            break;
        case "file":
            baseTypeDto = AssessmentBaseType.FILE;
            break;
        case "uri":
            baseTypeDto = AssessmentBaseType.URI;
            break;
        default:
            baseTypeDto = null;
        }

        return baseTypeDto;
    }

    @Override
    public BaseType disassembleDto(AssessmentBaseType baseTypeDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
