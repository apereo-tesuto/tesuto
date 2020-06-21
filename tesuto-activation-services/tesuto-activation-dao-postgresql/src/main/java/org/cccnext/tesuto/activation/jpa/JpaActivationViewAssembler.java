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

import java.util.stream.Collectors;

import org.cccnext.tesuto.activation.IndividualActivation;
import org.cccnext.tesuto.activation.TestEventActivation;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 12/19/16.
 */
@Service
public class JpaActivationViewAssembler
        extends AbstractAssembler<Activation, JpaActivationView> {

    @Autowired
    private Mapper mapper;

    @Override
    protected Activation doAssemble(JpaActivationView jpa) {
        Activation activation = null;
        if (jpa.getTestEventId() == null) {
            activation = mapper.map(jpa, IndividualActivation.class);
        } else {
            TestEventActivation testEventActivation = mapper.map(jpa, TestEventActivation.class);
            TestEvent event = mapper.map(jpa, TestEvent.class);
            event.setTestLocationId(jpa.getLocationId());
            event.setName(jpa.getTestEventName());
            testEventActivation.setTestEvent(event);
            activation = testEventActivation;
        }
        activation.setAssessmentScopedIdentifier(new ScopedIdentifierDto(jpa.getNamespace(), jpa.getAssessmentIdentifier()));
        activation.setAllAttributes(null);
        activation.setStatusChangeHistory(
                jpa.getStatusChangeHistory().stream().map(change -> change.getActivationStatusChange()).collect(Collectors.toList())
        );
        return activation;
    }

    @Override
    protected JpaActivationView doDisassemble(Activation activation) {
        throw new UnsupportedOperationException("Cannot disassemble into an activation view");
    }
}
