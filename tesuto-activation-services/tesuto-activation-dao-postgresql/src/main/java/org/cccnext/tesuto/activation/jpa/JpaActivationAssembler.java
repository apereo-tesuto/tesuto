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

import org.cccnext.tesuto.activation.IndividualActivation;
import org.cccnext.tesuto.activation.TestEventActivation;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/15/16.
 */
@Service
public class JpaActivationAssembler extends AbstractAssembler<Activation, JpaActivation> {

    @Autowired private JpaIndividualActivationAssembler individualAssembler;
    @Autowired private JpaTestEventActivationAssembler testEventAssembler;

    @Override
    protected Activation doAssemble(JpaActivation jpa) {
        return jpa instanceof JpaTestEventActivation ?
                testEventAssembler.doAssemble((JpaTestEventActivation)jpa) :
                individualAssembler.doAssemble((JpaIndividualActivation)jpa);
    }

    @Override
    protected JpaActivation doDisassemble(Activation activation) {
        return activation instanceof TestEventActivation ?
                testEventAssembler.disassembleDto((TestEventActivation)activation) :
                individualAssembler.disassembleDto((IndividualActivation)activation);

    }
}
