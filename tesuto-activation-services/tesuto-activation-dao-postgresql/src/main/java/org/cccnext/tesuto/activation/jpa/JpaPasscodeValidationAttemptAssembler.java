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

import org.cccnext.tesuto.activation.PasscodeValidationAttempt;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/15/16.
 */
@Service
public class JpaPasscodeValidationAttemptAssembler extends AbstractAssembler<PasscodeValidationAttempt, JpaPasscodeValidationAttempt> {

    @Autowired
    private Mapper mapper;

    @Autowired
    private JpaActivationAssembler activationAssembler;

    @Override
    protected PasscodeValidationAttempt doAssemble(JpaPasscodeValidationAttempt jpa) {
        PasscodeValidationAttempt attempt = mapper.map(jpa, PasscodeValidationAttempt.class);
        if (jpa.getJpaActivation() != null) {
            attempt.setActivation(activationAssembler.assembleDto(jpa.getJpaActivation()));
        }
        return attempt;
    }

    @Override
    protected JpaPasscodeValidationAttempt doDisassemble(PasscodeValidationAttempt attempt) {
        JpaPasscodeValidationAttempt jpa = mapper.map(attempt, JpaPasscodeValidationAttempt.class);
        if (attempt.getActivation() != null){
            jpa.setJpaActivation(activationAssembler.doDisassemble(attempt.getActivation()));
        }
        return jpa;
    }
}
