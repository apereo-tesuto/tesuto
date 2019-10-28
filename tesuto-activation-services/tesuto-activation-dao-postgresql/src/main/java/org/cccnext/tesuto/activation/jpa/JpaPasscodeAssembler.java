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
package org.cccnext.tesuto.activation.jpa;

import org.cccnext.tesuto.activation.Passcode;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/15/16.
 */
@Service
public class JpaPasscodeAssembler extends AbstractAssembler<Passcode, JpaPasscode> {

    @Autowired
    private Mapper mapper;

    @Override
    protected Passcode doAssemble(JpaPasscode jpa) {
        Passcode passcode = mapper.map(jpa, Passcode.class);
        return passcode;
    }

    @Override
    protected JpaPasscode doDisassemble(Passcode passcode) {
        JpaPasscode jpa = mapper.map(passcode, JpaPasscode.class);
        return jpa;
    }
}


