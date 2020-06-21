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

import org.cccnext.tesuto.activation.TestEventActivation;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/15/16.
 */
@Service
public class JpaTestEventActivationAssembler
        extends AbstractAssembler<TestEventActivation, JpaTestEventActivation>
        implements JpaActivationAssemblyUtil {

    @Autowired private Mapper mapper;
    @Autowired private JpaTestEventAssembler testEventAssembler;

    @Override
    protected TestEventActivation doAssemble(JpaTestEventActivation jpa) {
        TestEventActivation activation = mapper.map(jpa, TestEventActivation.class);
        assembly(activation, jpa);
        activation.setTestEvent(testEventAssembler.assembleDto(jpa.getTestEvent()));
        return activation;
    }

    @Override
    protected JpaTestEventActivation doDisassemble(TestEventActivation activation) {
        JpaTestEventActivation jpa = mapper.map(activation, JpaTestEventActivation.class);
        disassembly(jpa, activation);
        jpa.setTestEvent(testEventAssembler.disassembleDto(activation.getTestEvent()));
        return jpa;
    }
}
