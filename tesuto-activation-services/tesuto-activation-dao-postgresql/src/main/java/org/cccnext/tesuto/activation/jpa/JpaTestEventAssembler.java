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

import org.cccnext.tesuto.activation.TestEventWithUuid;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bruce on 11/15/16.
 */
@Service
public class JpaTestEventAssembler extends AbstractAssembler<TestEvent,JpaTestEvent> {

    @Autowired
    private Mapper mapper;

    private void setAssessmentScopedIdentifiers(TestEvent event, JpaTestEvent jpaTestEvent) {
        Set<ScopedIdentifier> identifiers =
                jpaTestEvent.getAssessmentScopedIdentifiers().stream().map(id -> mapper.map(id, ScopedIdentifier.class)).collect(Collectors.toSet());
        event.setAssessmentScopedIdentifiers(identifiers);
    }

    @Override
    protected TestEvent doAssemble(JpaTestEvent jpaTestEvent) {
        TestEvent event =  mapper.map(jpaTestEvent, TestEvent.class);
    	/*TestEvent event = new TestEvent();
    	event.setCanceled(jpaTestEvent.isCanceled());
    	event.setCollegeId(jpaTestEvent.getCollegeId());
    	event.setCreateDate(jpaTestEvent.getCreateDate());
    	event.setCreatedBy(jpaTestEvent.getCreatedBy());
    	event.setEndDate(jpaTestEvent.getEndDate());
    	event.setDeliveryType(jpaTestEvent.getDeliveryType());
    	event.setName(jpaTestEvent.getName());
    	event.setProctorEmail(jpaTestEvent.getProctorEmail());
    	event.setProctorFirstName(jpaTestEvent.getProctorFirstName());
    	event.setProctorLastName(jpaTestEvent.getProctorLastName());
    	event.setProctorPhone(jpaTestEvent.getProctorPhone());
    	event.setRemotePasscode(jpaTestEvent.getRemotePasscode());
    	event.setStartDate(jpaTestEvent.getStartDate());
    	event.setTestEventId(jpaTestEvent.getTestEventId());
    	event.setTestLocationId(jpaTestEvent.getTestLocationId());
    	event.setUpdateDate(jpaTestEvent.getUpdateDate());
    	event.setUpdatedBy(jpaTestEvent.getUpdatedBy());*/
        setAssessmentScopedIdentifiers(event, jpaTestEvent);
        return event;
    }

    @Override
    protected JpaTestEvent doDisassemble(TestEvent testEvent) {
        JpaTestEvent event = mapper.map(testEvent, JpaTestEvent.class);
        Set<JpaScopedIdentifier> identifiers =
                testEvent.getAssessmentScopedIdentifiers().stream().map(id -> mapper.map(id, JpaScopedIdentifier.class)).collect(Collectors.toSet());
        event.setAssessmentScopedIdentifiers(identifiers);
        return event;
    }

    public TestEventWithUuid assembleWithUuid(JpaTestEvent jpaTestEvent) {
        TestEventWithUuid event = mapper.map(jpaTestEvent, TestEventWithUuid.class);
        setAssessmentScopedIdentifiers(event, jpaTestEvent);
        return event;
    }
}
