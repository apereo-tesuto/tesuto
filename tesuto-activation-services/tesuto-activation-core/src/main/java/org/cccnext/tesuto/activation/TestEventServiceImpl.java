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
package org.cccnext.tesuto.activation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.activation.service.TestEventService;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.SecurityGroupReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 11/16/16.
 */
@Service("testEventService")
public class TestEventServiceImpl implements TestEventService {

    @Autowired private AssessmentReader assessmentService;
    @Autowired private TestEventDao dao;
    @Autowired private ActivationService activationService;
    @Autowired private RemotePasscodeService remotePasscodeService;
    @Autowired(required=false) private CollegeReader collegeService;
    @Autowired(required=false) private SecurityGroupReader securityGroupReader;

    public void acknowledge(TestEvent testEvent) {
        dao.acknowledge(testEvent);
    }

    public Set<String> assessmentTitles(TestEvent testEvent) {
        return testEvent.getAssessmentScopedIdentifiers().stream()
                .map(id -> assessmentService.readLatestPublishedVersion(id).getTitle())
                .collect(Collectors.toSet());
    }

    public int create(String creatorId, TestEvent testEvent) throws ValidationException {
        testEvent.setCreatedBy(creatorId);
        testEvent.setUpdatedBy(creatorId);
        Date now = new Date();
        testEvent.setCreateDate(now);
        testEvent.setUpdateDate(now);
        validate(testEvent);
        testEvent.setRemotePasscode(remotePasscodeService.createRemotePasscode());
        TestEvent created = dao.create(testEvent);
        return created.getTestEventId();
    }

    public UserAccountDto createRemoteProctorFromTestEvent(String uid, TestEvent testEvent) {
        UserAccountDto remoteProctor = new UserAccountDto();
        remoteProctor.setUserAccountId(uid);
        remoteProctor.setUsername(testEvent.getName());
        remoteProctor.setEmailAddress(testEvent.getProctorEmail());
        remoteProctor.setFirstName(testEvent.getProctorFirstName());
        remoteProctor.setLastName(testEvent.getProctorLastName());
        remoteProctor.setDisplayName(testEvent.getProctorFirstName() + " " + testEvent.getProctorLastName());
        remoteProctor.setPhoneNumber(testEvent.getProctorPhone());
        CollegeDto college = collegeService.read(testEvent.getCollegeId());
        Set<TestLocationDto> authorizedLocation = college.getTestLocations().stream().filter(
                loc -> loc.getId().equals(testEvent.getTestLocationId())
        ).collect(Collectors.toSet());
        college.setTestLocations(authorizedLocation);
        remoteProctor.setColleges(Collections.singleton(college));
        SecurityGroupDto group = securityGroupReader.getSecurityGroupByGroupName("REMOTE_PROCTOR");
        Set<SecurityGroupDto> securityGroups = new HashSet<>();
        securityGroups.add(group);
        remoteProctor.setSecurityGroupDtos(securityGroups);
        remoteProctor.setGrantedAuthorities(group.getGrantedAuthorities());
        return remoteProctor;
    }

    public void delete(int testEventId) {
        dao.delete(testEventId);
    }

    public TestEvent find(int testEventId) { return dao.find(testEventId); }

    public Set<TestEvent> findByCollegeId(String collegeId) { return dao.findByCollegeId(collegeId); }

    public TestEvent findByUuid(String uuid) { return dao.findByUuid(uuid); }


    public void update(String updater, TestEvent testEvent) throws ValidationException {
        List<String> messages = new ArrayList<>();
        TestEvent original = dao.find(testEvent.getTestEventId());

        if(!original.getCollegeId().equals(testEvent.getCollegeId())){
            messages.add("Cannot change the college associated to a test event.");
        }

        if(!original.getTestLocationId().equals(testEvent.getTestLocationId())){
            messages.add("Cannot change the test location associated to a test event.");
        }

        if(messages.size() > 0){
            throw new ValidationException(messages);
        }
        testEvent.setCreateDate(original.getCreateDate());
        testEvent.setCreatedBy(original.getCreatedBy());
        testEvent.setUpdatedBy(updater);
        testEvent.setUpdateDate(new Date());
        validate(testEvent);
        dao.update(testEvent);
    }

    public void updateTestEvent(UserAccountDto updater, TestEvent testEvent) {
        update(updater.getUserAccountId(), testEvent);
        updateActivationsForTestEvent(updater, testEvent);
    }

    private void updateActivationsForTestEvent(UserAccountDto updater, TestEvent testEvent) {
        Set<Activation> activations = activationService.findActivationsByTestEventId(testEvent.getTestEventId());
        for (Activation activation : activations) {
            ((TestEventActivation) activation).setTestEvent(testEvent);
        }
        activations.forEach(act ->
                act.addStatusChange(updater.getUserAccountId(), updater.getDisplayName(), updater.getUserAccountId(), act.getStatus(), "Updated test event")
        );
        activationService.update(activations);
    }

    public void validate(TestEvent testEvent) throws ValidationException {
        List<String> messages = new ArrayList<String>();
        if (testEvent.getName() != null && testEvent.getName().length() > 255) {
            messages.add("Test event name cannot be longer than 255 characters.");
        }
        if (testEvent.getStartDate() == null) {
            messages.add("Start date is required.");
        }
        if (testEvent.getEndDate() == null) {
            messages.add("End date is required.");
        }
        if (testEvent.getStartDate().getTime() >= testEvent.getEndDate().getTime()) {
            messages.add("Start date must be before end date.");
        }
        if (testEvent.getCollegeId() == null) {
            messages.add("College id is required.");
        }
        if (testEvent.getTestLocationId() == null) {
            messages.add("Test location id is required.");
        }
        if (testEvent.getDeliveryType() == null) {
            messages.add("DeliveryType must not be null.");
        }
        if (testEvent.getProctorFirstName() == null) {
            messages.add("Proctor first name is required.");
        } else if (testEvent.getProctorFirstName().length() > 64) {
            messages.add("Proctor first name may only be 64 characters");
        }
        if (testEvent.getProctorLastName() == null) {
            messages.add("Proctor last name is required.");
        } else if (testEvent.getProctorLastName().length() > 64) {
            messages.add("Proctor last name may only be 64 characters");
        }
        if (testEvent.getProctorEmail() == null) {
            messages.add("Proctor emailname is required.");
        } else {
            try {
                InternetAddress address = new InternetAddress(testEvent.getProctorEmail());
                address.validate();
            } catch (AddressException e) {
                messages.add(testEvent.getProctorEmail() + " is not a valid email address");
            }
        }
        if (CollectionUtils.isEmpty(testEvent.getAssessmentScopedIdentifiers())) {
            messages.add("Assessment Identifiers are required.");
        }
        if (testEvent.getProctorPhone()!= null && !testEvent.getProctorPhone().matches("^[0-9()-_]+$")) {
            messages.add("Phone number can only include numerals, parentheses, dash, and underscore");
        }
        if (messages.size() > 0) {
            throw new ValidationException(messages);
        }
    }

    private void statusChanges(Set<Activation> activations, UserAccountDto creator, Activation.Status status, String reason) {
        String creatorId = creator.getUserAccountId();
        String displayName = creator.getDisplayName();
        activations.forEach(act ->
                act.addStatusChange(creatorId, displayName, creatorId, status, reason)
        );
    }


    public Set<Activation> createActivationsFor(UserAccountDto creator, TestEvent testEvent, Set<String> studentIds) {
        // TODO: Hook in remote proctor email shizzle somewhere in here.
        Set<Activation> originals = activationService.findActivationsByTestEventId(testEvent.getTestEventId());
        Set<String> originalUsers = originals.stream().map(act -> act.getUserId()).collect(Collectors.toSet());
        Set<Activation> toCancel = originals.stream().filter(act ->
                !studentIds.contains(act.getUserId()) && act.getStatus() != Activation.Status.DEACTIVATED
        ).collect(Collectors.toSet());
        statusChanges(toCancel, creator, Activation.Status.DEACTIVATED, "");
        Set<Activation> toReactivate = originals.stream().filter(act ->
                act.getStatus() == Activation.Status.DEACTIVATED && studentIds.contains(act.getUserId())
        ).collect(Collectors.toSet());
        statusChanges(toReactivate, creator, Activation.Status.READY, "");
        Set<String> newUsers = studentIds.stream().filter(st ->
                !originalUsers.contains(st)
        ).collect(Collectors.toSet());
        Set<Activation> statusChanges = toCancel;
        statusChanges.addAll(toReactivate);
        Set<ProtoActivation> protos = testEvent.getAssessmentScopedIdentifiers().stream().
                flatMap(assessmentIdentifier -> {
                    return newUsers.stream().map(studentId -> {
                        ProtoActivation proto = new ProtoActivation();
                        proto.setStartDate(testEvent.getStartDate());
                        proto.setEndDate((testEvent.getEndDate()));
                        proto.setLocationId(testEvent.getTestLocationId());
                        proto.setDeliveryType(testEvent.getDeliveryType());
                        proto.setUserId(studentId);
                        proto.setAssessmentScopedIdentifier(new ScopedIdentifierDto(assessmentIdentifier.getNamespace(), assessmentIdentifier.getIdentifier()));
                        return proto;
                    });
                }).collect(Collectors.toSet());
        Set<Activation> newActivations = activationService.createActivations(creator.getUserAccountId(), testEvent, protos);
        activationService.persist(newActivations, statusChanges);
        return newActivations;
    }

    public void cancelTestEvent(UserAccountDto updatedBy, TestEvent testEvent) {

        cancelActivationsFor(updatedBy, testEvent);

        testEvent.setCanceled(true);
        testEvent.setUpdateDate(new Date());
        testEvent.setUpdatedBy(updatedBy.getUserAccountId());

        update(updatedBy.getUserAccountId(), testEvent);
    }

    private void cancelActivationsFor(UserAccountDto updatedBy, TestEvent testEvent) {
        Set<Activation> activations = activationService.findActivationsByTestEventId(testEvent.getTestEventId());

        statusChanges(activations, updatedBy, Activation.Status.DEACTIVATED, "Canceled Test Event");

        activationService.update(activations);
    }
}
