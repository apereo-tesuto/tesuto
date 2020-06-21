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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.activation.TestEventDao;
import org.cccnext.tesuto.activation.TestEventWithUuid;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

/**
 * Created by bruce on 11/16/16.
 */
@Service("testEventDao")
public class JpaTestEventDao implements TestEventDao {

    @Autowired private TestEventRepository repository;
    @Autowired private TestEventAcknowledgementRepository acknowledgementRepository;
    @Autowired private PasscodeRecordRepository passcodeRecordRepository;
    @Autowired private JpaTestEventAssembler assembler;

    @Override
    @Transactional
    public void acknowledge(TestEvent testEvent) {
        JpaTestEventAcknowledgement acknowledgement = new JpaTestEventAcknowledgement();
        acknowledgement.setAcknowledgementDate(new Date());
        acknowledgement.setTestEventId(testEvent.getTestEventId());
        acknowledgementRepository.save(acknowledgement);
    }

    @Override
    @Transactional
    public TestEvent create(TestEvent testEvent) {
        JpaTestEvent jpaTestEvent = assembler.disassembleDto(testEvent);
        jpaTestEvent.setUuid(TesutoUtils.newId());
        jpaTestEvent = repository.save(jpaTestEvent);
        passcodeRecordRepository.save(new JpaPasscodeRecord(jpaTestEvent));
        return assembler.assembleDto(jpaTestEvent);
    }

    @Override
    @Transactional
    public void delete(int testEventId) {
        repository.deleteById(testEventId);
    }

    @Override
    @Transactional(readOnly = true)
    public TestEvent find(int testEventId) {
        return assembler.assembleDto(repository.getOne(testEventId));
    }

    @Override
    @Transactional(readOnly = true)
    public TestEventWithUuid findWithUuid(int testEventId) {
        return assembler.assembleWithUuid(repository.getOne(testEventId));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TestEvent> findByCollegeId(String collegeId) {
        return assembler.assembleDto(repository.findByCollegeId(collegeId));
    }

    @Override
    @Transactional(readOnly = true)
    public TestEvent findByUuid(String uuid) {
        return assembler.assembleDto(repository.findByUuid(uuid));
    }

    @Override
    @Transactional
    public void update(TestEvent testEvent) {
        JpaTestEvent jpaTestEvent = assembler.disassembleDto(testEvent);
        JpaTestEvent original = repository.getOne(testEvent.getTestEventId());
    	// if updated test event record does not have a remote passcode, so we pull it from the original.
        if (StringUtils.isBlank(jpaTestEvent.getRemotePasscode())) {
        	jpaTestEvent.setRemotePasscode(original.getRemotePasscode());
        } else {
        	// if the passcode changed, record the new passcode
        	if (!StringUtils.equals(jpaTestEvent.getRemotePasscode(), original.getRemotePasscode())) {
                passcodeRecordRepository.save(new JpaPasscodeRecord(jpaTestEvent));
        	}
        }
        jpaTestEvent.setUuid(original.getUuid()); //currently cannot updated uuid
        repository.save(jpaTestEvent);
    }
}
