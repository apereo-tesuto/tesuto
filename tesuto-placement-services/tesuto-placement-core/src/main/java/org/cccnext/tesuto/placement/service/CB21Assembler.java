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
package org.cccnext.tesuto.placement.service;

import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.placement.model.CB21;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CB21Assembler extends AbstractAssembler<CB21ViewDto, CB21> {

    @Autowired
    Mapper mapper;

    @Override
    protected CB21ViewDto doAssemble(CB21 entity) {
        return mapper.map(entity, CB21ViewDto.class);
    }

    @Override
    protected CB21 doDisassemble(CB21ViewDto dto) {
        return mapper.map(dto, CB21.class);
    }

}
