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
import org.cccnext.tesuto.placement.model.MMAPEquivalent;
import org.cccnext.tesuto.placement.view.MMAPEquivalentDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class MMAPEquivalentAssembler extends AbstractAssembler<MMAPEquivalentDto, MMAPEquivalent> {

    @Autowired
    Mapper mapper;

    @Override
    protected MMAPEquivalentDto doAssemble(MMAPEquivalent mmapEquivalent) {
        return mapper.map(mmapEquivalent, MMAPEquivalentDto.class);
    }

    @Override
    protected MMAPEquivalent doDisassemble(MMAPEquivalentDto mmapEquivalentDto) {
        return mapper.map(mmapEquivalentDto, MMAPEquivalent.class);
    }
}
