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
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/15/16.
 */
@Service
public class JpaIndividualActivationAssembler
        extends AbstractAssembler<IndividualActivation, JpaIndividualActivation>
        implements JpaActivationAssemblyUtil {

    @Autowired
    private Mapper mapper;

    @Override
    protected IndividualActivation doAssemble(JpaIndividualActivation jpa) {
        IndividualActivation activation = mapper.map(jpa, IndividualActivation.class);
        assembly(activation, jpa);
        return activation;
    }

    @Override
    protected JpaIndividualActivation doDisassemble(IndividualActivation activation) {
        JpaIndividualActivation jpa = mapper.map(activation, JpaIndividualActivation.class);
        disassembly(jpa, activation);
        return jpa;
    }
}
