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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.ActivationStatusChange;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.activation.service.ActivationStatus;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.service.TestLocationReader;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.PrerequisitesService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.delivery.service.DeliveryServiceListener;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.util.TesutoUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by bruce on 10/19/15.
 */
@Slf4j
@Service
public class ActivationService implements DeliveryServiceListener, ActivationStatus {

    @Value("${activation.minDurationDays}") int minDurationDays;
    @Value("${activation.minExpirationHour}") int minExpirationHour;

    @Autowired Mapper mapper;

    @Autowired(required = false)
    AssessmentReader assessmentService;

    @Autowired(required=false)
    ActivationDao dao;

    @Autowired(required=false)
    AssessmentSessionReader deliveryService;

    @Autowired(required=false)
    PrerequisitesService prerequisitesService;

    @Autowired(required=false)
    TestLocationReader testLocationService;

    @Autowired(required=false)
    UserAccountReader userAccountService;

    @Autowired(required=false)
    StudentReader studentService;


    public ActivationDao getDao() {
        return dao;
    }

    public void setDao(ActivationDao dao) {
        this.dao = dao;
    }

    public AssessmentSessionReader getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(AssessmentSessionReader deliveryService) {
        this.deliveryService = deliveryService;
    }

    public boolean assessmentCompleted(String userId, ScopedIdentifier assessmentId) {
        return true;
    }

    public boolean arePrerequisitesMet(String userId, ScopedIdentifier assessmentId) {
        Set<ScopedIdentifier> prerequisites = prerequisitesService.get(assessmentId);
        return prerequisites.stream().reduce(true,
                ((allMetSoFar, prerequisite) -> allMetSoFar && assessmentCompleted(userId, prerequisite)),
                Boolean::logicalAnd);
    }

    public String launch(String activationId, UserAccountDto launchByUser, String proctorId) {
        return launch(find(activationId), launchByUser, proctorId);
    }

    public String launch(Activation activation, UserAccountDto requestor, String proctorId) {
        return launch(activation, null, requestor, proctorId);
    }

    public String launch(Activation activation, Integer assessmentVersion, UserAccountDto requestor, String proctorId) {
        if (activation.getStatus() == Activation.Status.READY) {
            return launchReadyActivation(activation, assessmentVersion, requestor, proctorId);
        } else if (activation.getStatus() == Activation.Status.PAUSED
                || activation.getStatus() == Activation.Status.IN_PROGRESS) {
            return relaunchActivation(activation, requestor, proctorId);
        } else {
            throw new AccessDeniedException("Cannot launch activation " + activation.getActivationId() + " with status "
                    + activation.getStatus());
        }
    }

    private String launchReadyActivation(Activation activation, Integer assessmentVersion, UserAccountDto requestor, String proctorId) {
        // TODO: There's a race condition here.
        if (!activation.getStatus().equals(Activation.Status.READY)) {
            throw new AccessDeniedException("Cannot launch activation " + activation.getActivationId() + " with status "
                    + activation.getStatus());
        }
        String sessionId;
        if (assessmentVersion != null) {
            sessionId = deliveryService.createUserAssessmentSession(activation.getUserId(), new ScopedIdentifier(activation.getAssessmentScopedIdentifier()),
                    assessmentVersion, activation.getDeliveryType(), activation.getAttributes());
        } else {
            sessionId = deliveryService.createUserAssessmentSession(activation.getUserId(), new ScopedIdentifier(activation.getAssessmentScopedIdentifier()),
                    activation.getDeliveryType(), activation.getAttributes());
        }
        activation.addAssessmentSessionId(sessionId); //dao.update is handled in addStatusChange
        addStatusChange(requestor, activation,  Activation.Status.IN_PROGRESS, proctorId);
        return sessionId;
    }

    private String relaunchActivation(Activation activation, UserAccountDto requestor, String proctorId) {
        if (activation.getStatus() != Activation.Status.PAUSED && activation.getStatus() != Activation.Status.IN_PROGRESS) {
            throw new AccessDeniedException("Cannot restart activation " + activation.getActivationId()
                    + " with status " + activation.getStatus());
        }
        addStatusChange(requestor, activation, Activation.Status.IN_PROGRESS, proctorId);
        return activation.getCurrentAssessmentSessionId();
    }

    public void addStatusChange(UserAccountDto requestor, Activation activation, Activation.Status status, String proctorId) {
        addStatusChange(requestor, activation, status, proctorId,null);
    }


    public String displayName(UserAccountDto user) {
        if (user != null && !user.isStudent()) {
            return user.getDisplayName();
        } else {
            return null;
        }

    }

    public void addStatusChange(UserAccountDto requestor, Activation activation, Activation.Status status, String proctorId, String reason) {
        addStatusChange(requestor, activation, status, proctorId, reason, new Date());
    }

    public void addStatusChange(UserAccountDto requestor, Activation activation, Activation.Status status, String proctorId, String reason, Date statusChangeDate) {
        String displayName = displayName(requestor);
        String userAccountId = (requestor == null) ? activation.getUserId() : requestor.getUserAccountId();
        activation.addStatusChange(userAccountId, displayName, proctorId, status, reason, statusChangeDate);
        dao.update(activation);
    }

    /**
     * Change the status of an activation to canceled.
     *
     * @param requestor
     *            user who is initiating this change
     * @param activationId
     *            activation to be changed
     */
    public void cancel(String activationId, UserAccountDto requestor, String reason) {
        cancel(find(activationId), requestor, reason);
    }


    public void cancel(Activation activation, UserAccountDto requestor, String reason) {
        addStatusChange(requestor, activation, Activation.Status.DEACTIVATED, requestor.getUserAccountId(), reason);
    }


    /**
     * Change the status of an activation to paused.
     *
     * @param requestor
     *            user who is initiating this change
     * @param activation
     *            activation to be changed
     */
    public void pause(Activation activation, UserAccountDto requestor) {
        addStatusChange(requestor, activation, Activation.Status.PAUSED, null);
    }

    /**
     * Change the status of an activation to paused.
     *
     * @param requestor
     *            user who is initiating this change
     * @param activationId
     *            activation to be changed
     */
    public void pause(String activationId, UserAccountDto requestor) {
        Activation activation = find(activationId);
        pause(activation, requestor);
    }

    /**
     * Restore status of an activation so
     *
     * @param requestor
     *            user who is initiating this change
     * @param activationId
     *            activation to be changed
     */
    public void reactivate(String activationId, UserAccountDto requestor) {
        reactivate(find(activationId), requestor);
    }

    /**
     * Restore status of an activation so
     *
     * @param requestor
     *            user who is initiating this change
     * @param activation
     *            activation to be changed
     */
    public void reactivate(Activation activation, UserAccountDto requestor) {
        activation.setEndDate(calculateExpirationTime(activation.getStartDate()));

        LocalDate today = LocalDate.now();
        LocalDate localStartDate = activation.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // set the new activation status depending on the start date to ready or upcoming
        Activation.Status status =  localStartDate.isAfter(today) ? Activation.Status.ACTIVATED : Activation.Status.READY;
        addStatusChange(requestor, activation, status, requestor.getUserAccountId());
    }

    /**
     * Notify an activation that it's assessment session has been completed
     *
     * @param assessmentSessionId
     */
    public void completeAssessment(String assessmentSessionId, UserAccountDto requestor) {
        Collection<Activation> activations = dao.findActivationsByAssessmentSessionId(assessmentSessionId);
        if (activations.size() != 1) {
            log.error("Found " + activations.size() + " activations with current assessment session id "
                    + assessmentSessionId);
        }
        activations.forEach(activation ->
                addStatusChange(requestor, activation, Activation.Status.COMPLETE, null)
        );
    }

    public void pendingAssessment(String assessmentSessionId, UserAccountDto requestor) {
        Collection<Activation> activations = dao.findActivationsByAssessmentSessionId(assessmentSessionId);
        if (activations.size() != 1) {
            log.error("Found " + activations.size() + " activations with current assessment session id "
                    + assessmentSessionId);
        }
        activations.forEach(activation -> addStatusChange(requestor, activation, Activation.Status.PENDING_SCORING,null));
    }

    public void assessedAssessment(String assessmentSessionId, UserAccountDto requestor, Date statusChangeDate) {
        Collection<Activation> activations = dao.findActivationsByAssessmentSessionId(assessmentSessionId);
        if (activations.size() != 1) {
            log.error("Found " + activations.size() + " activations with current assessment session id "
                    + assessmentSessionId);
        }
        activations.forEach(activation -> {
            if (!hasBeenAssessed(activation)) {
                addStatusChange(null, activation, Activation.Status.ASSESSED, requestor.getUserAccountId(), null, statusChangeDate);
            }
        });
    }

    public boolean hasBeenAssessed(Activation activation) {
        boolean hasBeenAssessed = false;
        List<ActivationStatusChange> statusChangeHistory = activation.getStatusChangeHistory();
        for (ActivationStatusChange activationStatusChange : statusChangeHistory) {
            if (activationStatusChange.getNewStatus() == Activation.Status.ASSESSED) {
                hasBeenAssessed = true;
                break;
            }
        }
        return hasBeenAssessed;
    }
    private void initializeActivations(String creatorId, Collection<Activation> activations){
        UserAccountDto creator = userAccountService.getUserAccount(creatorId);
        activations.forEach(act -> {
                    act.setActivationId(TesutoUtils.newId());
                    act.setCreateDate(new Date());
                    act.setCreatorId(creatorId);
                    act.setStatusUpdateDate(act.getCreateDate());
                    AssessmentDto assessment = assessmentService.readLatestPublishedVersion(new ScopedIdentifier(act.getAssessmentScopedIdentifier()));
                    if (assessment == null) {
                        throw new CannotCreateActivationException("No such assessment " + act.getAssessmentScopedIdentifier());
                    }
                    act.setAssessmentTitle(assessment.getTitle());
                    if (creator != null) {
                        act.setCreatorName(creator.getDisplayName());
                    }
                });
    }

    private String createActivation(String creatorId, Activation activation) {
        initializeActivations(creatorId, Collections.singleton(activation));
        dao.create(activation);
        return activation.getActivationId();

    }

    /**
     * Persist an Activation
     *
     * @param creatorId
     * @param proto
     *            -- an activation prototype to be copied
     * @return the activationId
     */
    public String create(String creatorId, ProtoActivation proto) {
        IndividualActivation activation = mapper.map(proto, IndividualActivation.class);
        activation.setAllAttributes(proto.getAttributes());
        if (activation.getStartDate() == null) {
            activation.setStartDate(new Date());
        }
        activation.setEndDate(calculateExpirationTime(activation.getStartDate()));
        return createActivation(creatorId, activation);
    }


    public Set<Activation> createActivations(String creatorId, TestEvent testEvent, Collection<ProtoActivation> protos) {
        Set<Activation> activations = protos.stream().map(proto -> {
            TestEventActivation activation = mapper.map(proto, TestEventActivation.class);
            activation.setAllAttributes(proto.getAttributes());
            activation.setTestEvent(testEvent);
            return activation;
        }).collect(Collectors.toSet());
        initializeActivations(creatorId, activations);
        return activations;
    }


    public boolean delete(String activationId) {
        if (activationId != null)
            return dao.delete(activationId);
        else
            return false;
    }

    /**
     * @param activationId
     * @return An activation with the desired activationId.
     **/
    public Activation find(String activationId) {
        Set<String> activationIds = new HashSet<>(1);
        activationIds.add(activationId);
        Set<Activation> activations = find(activationIds);
        if (activations.size() == 1) {
            return activations.iterator().next();
        } else if (activations.size() == 0) {
            throw new ActivationNotFoundException(activationId);
        } else { // (activations.size() > 1)
            throw new RuntimeException("multiple activations found for id " + activationId);
        }
    }

    /**
     * @param activationIds
     * @return A set of activations with the desired activationIds
     */
    Set<Activation> find(Set<String> activationIds) {
        return dao.find(activationIds);
    }

    /**
     * @param assessmentSessionId
     * @return An activation with the requested current assessmentSessionId
     */
    public Set<Activation> findActivationsByAssessmentSessionId(String assessmentSessionId) {
        return dao.findActivationsByAssessmentSessionId(assessmentSessionId);
    }

    /**
     * @param testEventId
     * @return An activation with the requested current testEventId
     */
    public Set<Activation> findActivationsByTestEventId(int testEventId) {
        Set<Activation> activations = dao.findActivationsByTestEventId(testEventId);
        activations.forEach( activation ->
                activation.setStudent(studentService.getStudentById(activation.getUserId()))
        );
        return activations;
    }

    public void persist(Set<Activation> newActivations, Set<Activation> updated) {
        dao.persist(newActivations, updated);
    }

    public void update(Set<Activation> updatedActivations){
        dao.update(updatedActivations);
    }

    public void update(UserAccountDto requestor, ProtoActivation protoActivation) {
        StringBuilder errors = new StringBuilder();
        if (protoActivation.getActivationId() == null) {
            errors.append("ActivationId must be specified.  ");
            throw new ValidationException(errors.toString());
        }

        IndividualActivation persistedActivation = (IndividualActivation) find(protoActivation.getActivationId());

        if ((protoActivation.getLocationId() != null) && !persistedActivation.getLocationId().equals(protoActivation.getLocationId())) {
            errors.append("Cannot update LocationId.  ");
        }

        if ((protoActivation.getAssessmentScopedIdentifier() != null) && !persistedActivation.getAssessmentScopedIdentifier().equals(protoActivation.getAssessmentScopedIdentifier())) {
            errors.append("Cannot update AssessmentId.  ");
        }

        if ((protoActivation.getUserId() != null) && !persistedActivation.getUserId().equals(protoActivation.getUserId())) {
            errors.append("Cannot update UserId.  ");
        }

        if ((protoActivation.getStartDate() != null) && !persistedActivation.getStartDate().equals(protoActivation.getStartDate())) {
            errors.append("Cannot update Start Date.  ");
        }

        if ((protoActivation.getEndDate() != null) && !persistedActivation.getEndDate().equals(protoActivation.getEndDate())) {
            errors.append("Cannot update End Date.  ");
        }

        if ((protoActivation.getDeliveryType() != null) && !persistedActivation.getDeliveryType().equals(protoActivation.getDeliveryType())) {
            errors.append("Cannot update DeliveryType.  ");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }

        persistedActivation.setAllAttributes(protoActivation.getAttributes());
        addStatusChange(requestor, persistedActivation, persistedActivation.getStatus(), requestor.getUserAccountId(), "Updated");
    }

    /**
     * whether or not an activation requires a passcode
     */
    public boolean requiresPasscode(Activation activation) {
        AssessmentDto assessment;
        if (activation.getCurrentAssessmentSessionId() != null) {
            int assessmentVersionId = deliveryService.getAssessmentVersionForSession(activation.getCurrentAssessmentSessionId());
            assessment = assessmentService.readVersion(new ScopedIdentifier(activation.getAssessmentScopedIdentifier()), assessmentVersionId);
        } else {
            assessment = assessmentService.readLatestPublishedVersion(new ScopedIdentifier(activation.getAssessmentScopedIdentifier()));
        }
        if (assessment.getAssessmentMetadata() == null) {
            return false;
        } else {
            return assessment.getAssessmentMetadata().isRequirePasscode();
        }
    }


    /**
     * Get a list of activation counts by location, asessment, and proctor,
     * including "rollups" (indicated by nulls). sorted by location, assessment,
     * proctor.
     *
     * @param from
     *            (start date at which activations start)
     * @param to
     *            (end date at which.
     * @return List of activation counts
     */
    public List<ActivationCount> report(Calendar from, Calendar to) {
        return dao.report(from, to);
    }

    private String display(Object object) {
        // if object is null, then empty string, otherwise escape double quotes
        String value = object == null ? "" : object.toString().replaceAll("\"", "\"\"");
        // enclose value in double quotes
        return "\"" + value + "\"";
    }

    private String standardCsvRow(Map<String, TestLocationDto> locationMap, ActivationCount activationCount) {
        TestLocationDto testLocationDto = locationMap.get(activationCount.getLocationId());
        List<Object> values = Arrays.asList(testLocationDto.getName(), testLocationDto.getCollegeName(),
                activationCount.getLocationId(), activationCount.getAssessment(),
                activationCount.getProctor(), activationCount.getCount(Activation.Status.READY),
                activationCount.getCount(Activation.Status.IN_PROGRESS),
                activationCount.getCount(Activation.Status.COMPLETE));
        return values.stream().map(value -> display(value)).collect(Collectors.joining(","));
    }

    public List<String> reportAsCsv(Calendar from, Calendar to) {
        List<ActivationCount> activationCounts = report(from, to);
        Map<String, TestLocationDto> locationMap = testLocationService.readMap();
        String header = "College,Test Location,Location Id,Assessment,Proctor,Ready,In Progress,Completed";
        Stream.Builder<String> headerStream = Stream.builder();
        headerStream.add(header);
        return Stream.concat(headerStream.build(), activationCounts.stream().map(count -> standardCsvRow(locationMap, count)))
                .collect(Collectors.toList());
    }

    public List<String> activationStatusChangeReport(Collection<Activation> activations) {
        String header = "ActivationId,Assessment Title,Activation UserId, StatusChange UserId, StatusChange UserName, Status, Count, Dates";
        List<String> report = new ArrayList<>();
        report.add(header);
        Map<String, List<Date>> map = createCompleteMapForActivationStatusChangeReport(activations);
        for (String k : map.keySet()) {
            report.add(String.format("%s,%s,%s", k, map.get(k).size(), map.get(k).toString()));
        }
        return report;
    }

    private Date calculateExpirationTime(Date startDate) {
        Calendar expiration = new GregorianCalendar();
        expiration.setTime(startDate);
        expiration.add(Calendar.DAY_OF_MONTH, minDurationDays);
        if (expiration.get(Calendar.HOUR) > minExpirationHour) {
            expiration.add(Calendar.DAY_OF_MONTH, 1);
        }
        expiration.set(Calendar.HOUR_OF_DAY, minExpirationHour);
        expiration.set(Calendar.MINUTE, 59);
        expiration.set(Calendar.SECOND, 59);
        return new Date(expiration.getTimeInMillis());
    }

    private Map<String, List<Date>> createCompleteMapForActivationStatusChangeReport(
            Collection<Activation> activations) {
        Map<String, List<Date>> map = new HashMap<>();
        activations.forEach(activation -> map.putAll(createMapOfActivationStatusChangeReportRows(activation)));
        return map;
    }

    private Map<String, List<Date>> createMapOfActivationStatusChangeReportRows(Activation activation) {
        Map<String, List<Date>> dateMap = new HashMap<>();
        activation.getStatusChangeHistory().forEach(statusChange -> {
            String status = createActivationStatusChangeRow(activation, statusChange);
            if (dateMap.containsKey(status)) {
                dateMap.get(status).add(statusChange.getChangeDate());
            } else {
                List<Date> dateList = new ArrayList<Date>();
                dateList.add(statusChange.getChangeDate());
                dateMap.put(status, dateList);
            }
        });
        return dateMap;
    }

    private String createActivationStatusChangeRow(Activation activation, ActivationStatusChange statusChange) {
        List<Object> values = Arrays.asList(activation.getActivationId(), activation.getAssessmentTitle(),
                activation.getUserId(), statusChange.getUserId(), statusChange.getUserName(),
                statusChange.getNewStatus());
        return values.stream().map(value -> display(value)).collect(Collectors.joining(","));
    }

    public boolean isTestEventActivation(Activation activation) {
        return (activation instanceof TestEventActivation);
    }
}
