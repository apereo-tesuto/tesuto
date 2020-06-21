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
package org.cccnext.tesuto.delivery.qa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.cccnext.tesuto.activation.ProtoActivation;
import org.cccnext.tesuto.activation.client.ActivationQARestClient;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.service.AssemblyService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.delivery.service.DeliveryServiceAdapter;
import org.cccnext.tesuto.delivery.service.TaskSetService;
import org.cccnext.tesuto.delivery.view.ItemSessionResponseViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.domain.util.RandomGenerator;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.joda.time.DateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class DeliveryModuleSeedDataService {
    
    @Autowired private AssessmentReader assessmentReader;

    @Autowired private AssessmentItemReader assessmentItemReader;

    @Autowired private AssessmentSessionDao assessmentSessionnDao;

    @Autowired private RandomGenerator randomizer;

    @Value("${report.test.student}")
    String reportTestStudent;

    @Autowired private AssemblyService assemblyService;

    
    @Autowired private TaskSetService taskSetService;

    @Autowired private DeliveryServiceAdapter deliveryService;
    
    @Autowired private ActivationQARestClient qaService;

    @Autowired private UserAccountReader userAccountService;

    public AssessmentSessionDao getAssessmentSessionDao() {
        return assessmentSessionnDao;
    }

    public void setAssessmentSessionDao(AssessmentSessionDao assessmentSessionnDao) {
        this.assessmentSessionnDao = assessmentSessionnDao;
    }

    public AssessmentReader getAssessmentReader() {
        return assessmentReader;
    }

    public void setAssessmentReader(AssessmentReader assessmentReader) {
        this.assessmentReader = assessmentReader;
    }

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemService(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }


    public DeliveryServiceAdapter getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(DeliveryServiceAdapter deliveryService) {
        this.deliveryService = deliveryService;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public List<AssessmentSession> generateAssessmentSessions(String proctorUserName,
            ScopedIdentifier identifier,
            Integer numberOfAttempts,
            Double percentInProgressThreshold,
            Integer startDateRangeInDays,
            Boolean expireInProgress) {
        String userId = reportTestStudent;

        return generateAssessmentSessions( proctorUserName,
                userId,
                "21",
                identifier,
                numberOfAttempts,
                percentInProgressThreshold,
                startDateRangeInDays,
                expireInProgress);

    }

    public List<AssessmentSession> generateAssessmentSessions(String proctorUserName,
            String userId,
            String testlocationId,
            ScopedIdentifier identifier,
            Integer numberOfAttempts,
            Double percentInProgressThreshold,
            Integer startDateRangeInDays,
            Boolean expireInProgress) {
        List<AssessmentSession> assessmentSessions = new ArrayList<AssessmentSession>();
        log.debug("Total number of assessment sessions to be generated {}", numberOfAttempts);
        for(int i = 0; i < numberOfAttempts; i++) {

            UserAccountDto requestor = getRequestor(userId);

            String proctorId = proctorUserName;
            
            // TODO: 5/31/16 for Jim: is the requestor supposed to be the simulatedProctor?
            String assessmentSessionId = launchAssessmentSession(identifier,  testlocationId, requestor, proctorId);

            AssessmentSession session = assessmentSessionnDao.find(assessmentSessionId);
            session.setAssessment(assessmentReader.read(session.getContentId()));
            updateActivationStartDate(session, startDateRangeInDays);
            boolean remainInProgress = true;
            do {
                TaskSet taskSet =  taskSetService.getCurrentTaskSet(session);
                if(taskSet == null  || taskSet.getTasksOrderedByIndex() == null) {
                    remainInProgress = false;
                    continue;
                }
                Map<String, TaskResponseViewDto> taskSetResponses = new HashMap<String, TaskResponseViewDto>();
                List<ItemSessionResponseViewDto> taskSetItemResponses = new ArrayList<ItemSessionResponseViewDto>();
                for(Task task:taskSet.getTasksOrderedByIndex()){
                    for(ItemSession itemSession:task.getItemSessionsOrderedByIndex()){
                        AssessmentItemDto item = assessmentItemReader.read(itemSession.getItemId());
                        List<Response> responses = getResponses( item,  itemSession);
                        TaskResponseViewDto taskResponse = new TaskResponseViewDto();
                        Long duration;
                        try {
                            duration = randomizer.getRandomIndex(10000) + 300L;
                        } catch (InterruptedException e) {
                            duration = 1000L;
                        }

                        for(Response response:responses) {
                            ItemSessionResponseViewDto itemSessionResponseViewDto = new ItemSessionResponseViewDto();
                            itemSessionResponseViewDto.setItemSessionId(itemSession.getItemSessionId());
                            itemSessionResponseViewDto.setResponseIdentifier(response.getResponseId());
                            itemSessionResponseViewDto.setValues(response.getValues());
                            taskSetItemResponses.add(itemSessionResponseViewDto);
                        }

                        taskResponse.setDuration(duration);
                        taskResponse.setResponses(taskSetItemResponses);
                        taskSetResponses.put(task.getTaskId(), taskResponse);
                    }
                }
                remainInProgress = remainInProgress(percentInProgressThreshold);
                if(remainInProgress) {
                    //guarantees taskSet has no responses, better simulation of expired sessions
                    try {
                        deliveryService.complete(assemblyService.assembleAssessmentSession(session), taskSetResponses, requestor);
                    } catch(Exception e) {
                        log.error("error trying to close an task set, not critical \n", e);
                        remainInProgress = false;
                    }
                }

            }while(taskSetService.getCurrentTaskSet(session) != null && 
            		remainInProgress == true);
            if(!remainInProgress) {
                updateActivationEndDate(session, expireInProgress);
            }
            assessmentSessions.add(session);

        }
        return assessmentSessions;
    }

    private String launchAssessmentSession(ScopedIdentifier identifier, String testlocationId, UserAccountDto requestor, String simulatedProctor) {
        ProtoActivation proto = new ProtoActivation();
        proto.setLocationId(String.valueOf(UUID.randomUUID()));
        
        proto.setUserId(requestor.getUsername());
        proto.setAssessmentScopedIdentifier(new ScopedIdentifierDto(identifier.getNamespace(), identifier.getIdentifier()));
        proto.setLocationId(testlocationId);
        proto.setDeliveryType(DeliveryType.ONLINE);
        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("accommodations", "[{\"name\":\"No special accommodation\",\"id\":1,\"code\":\"N\"}]");
        attributes.put("accommodationsOther", "");
        proto.setAttributes(attributes);
        
        return qaService.launchAssessmentSession(proto, simulatedProctor, requestor.getUserAccountId());
        
    }

    private void updateActivationStartDate(AssessmentSession session, Integer startDateRangeInDays) {
        if(startDateRangeInDays != null)
            try {
                Date startDate = new DateTime().minusDays(randomizer.getRandomIndex(startDateRangeInDays)).toDate();
                qaService.updateActivationStartDate(session.getAssessmentSessionId(), startDate);
            } catch (InterruptedException exception) {
                log.warn("Activation Start Date not Set", exception );
            }
    }

    private void updateActivationEndDate(AssessmentSession session, Boolean expireInProgress) {
        if(expireInProgress != null && expireInProgress == true) {
            DateTime endDate = new DateTime().plusSeconds(5);
            qaService.updateActivationEndDate(session.getAssessmentSessionId(), endDate.toDate());
        }
    }

    boolean remainInProgress(Double keepInProgressPercent) {
        if(keepInProgressPercent == null) {
            return true;
        }
        Random random = new Random(System.currentTimeMillis());
        Double percent = random.nextDouble() * 100;
        return percent > keepInProgressPercent ? false:true;
    }

    private List<Response> getResponses(AssessmentItemDto item, ItemSession session) {
        List<Response> responses = new ArrayList<Response>();
        for(AssessmentInteractionDto interaction:item.getInteractions()) {
            switch(interaction.getType()) {
            case CHOICE_INTERACTION:
                AssessmentChoiceInteractionDto choice = (AssessmentChoiceInteractionDto)interaction;

                int numberOfResponses = 1;
                try {
                    numberOfResponses = randomizer.getRandomIndex(choice.getMaxChoices()) + 1;
                } catch (InterruptedException e1) {
                    log.error("Errror randomizing");
                }
                for(int i=0; i < numberOfResponses; i++) {
                    List<String> pickedChoices = new ArrayList<String>();
                    AssessmentSimpleChoiceDto res = null;
                    try {
                        res = choice.getChoices().get(randomizer.getRandomIndex(choice.getChoices().size()));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if(pickedChoices.contains(res.getIdentifier())) {
                        continue;
                    }
                    Response response = new Response();
                    response.setResponseId(choice.getResponseIdentifier());
                    response.setValues(Arrays.asList(res.getIdentifier()));
                    pickedChoices.add(res.getIdentifier());
                    responses.add(response);
                }

                break;
            case EXTENDED_TEXT_INTERACTION:
                Response exttresponse = new Response();
                exttresponse.setResponseId(interaction.getResponseIdentifier());
                exttresponse.setValues(Arrays.asList("ME EXTENDED TEXT ANSWER"));
                responses.add(exttresponse);
                break;
            case INLINE_CHOICE_INTERACTION:
                AssessmentInlineChoiceInteractionDto inlineChoice = (AssessmentInlineChoiceInteractionDto)interaction;
                inlineChoice.getInlineChoices();
                AssessmentInlineChoiceDto inchoice;
                try {
                    inchoice = inlineChoice.getInlineChoices().get(randomizer.getRandomIndex(inlineChoice.getInlineChoices().size()));
                } catch (InterruptedException e) {
                    log.error("", e);
                    break;
                }
                Response inresponse = new Response();
                inresponse.setResponseId(inlineChoice.getResponseIdentifier());
                inresponse.setValues(Arrays.asList(inchoice.getIdentifier()));
                responses.add(inresponse);
                break;
            case MATCH_INTERACTION:
                AssessmentMatchInteractionDto match = (AssessmentMatchInteractionDto)interaction;
                AssessmentSimpleMatchSetDto rows = match.getMatchSets().get(0);
                AssessmentSimpleMatchSetDto columns = match.getMatchSets().get(1);
                int maxAssociations = match.getMaxAssociations();
                int countAssociations = 0;
                Map<String,Integer> maxColumns = new HashMap<String,Integer>();
                Map<String,Integer> minColumns = new HashMap<String,Integer>();
                Map<String,Integer> countColumns = new HashMap<String,Integer>();
                for(AssessmentSimpleAssociableChoiceDto column:columns.getMatchSet()) {
                    maxColumns.put(column.getResponseIdentifier(), column.getMatchMax());
                    minColumns.put(column.getResponseIdentifier(), column.getMatchMin());
                    countColumns.put(column.getResponseIdentifier(), 0);
                }


                for(AssessmentSimpleAssociableChoiceDto row:rows.getMatchSet()) {
                        int rowMax = row.getMatchMax();
                        int rowCount= 0;
                        for(AssessmentSimpleAssociableChoiceDto column:columns.getMatchSet()) {
                            try {
                                if(randomizer.getRandomIndex(100) < 30) {
                                    Integer columnCount = countColumns.get(column.getResponseIdentifier());
                                    if(columnCount >=  maxColumns.get(column.getResponseIdentifier())) {
                                        break;
                                    }
                                    if(countAssociations >= maxAssociations) {
                                        break;
                                    }
                                    if(rowCount >= rowMax) {
                                        break;
                                    }
                                    Response matchResponse = new Response();
                                    matchResponse.setResponseId(match.getResponseIdentifier());
                                    matchResponse.setValues(Arrays.asList(row.getIdentifier() + " " + column.getIdentifier()));
                                    responses.add(matchResponse);
                                    countAssociations++;
                                    rowCount++;
                                    columnCount = columnCount + 1;
                                    countColumns.put(column.getResponseIdentifier(), columnCount);
                                    if(columnCount >  maxColumns.get(column.getResponseIdentifier())) {
                                        break;
                                    }
                                    if(countAssociations > maxAssociations) {
                                        break;
                                    }
                                    if(rowCount > rowMax) {
                                        break;
                                    }

                                }
                            } catch (InterruptedException e) {
                                log.error("", e);
                            }
                        }
                }
                break;
            case NULL_INTERACTION:
                break;
            case TEXT_ENTRY_INTERACTION:
                Response txtresponse = new Response();
                txtresponse.setResponseId(interaction.getResponseIdentifier());
                txtresponse.setValues(Arrays.asList("MY ANSWER"));
                responses.add(txtresponse);
                break;
            default:
                break;

            }
        }
        return responses;

    }
    
    private UserAccountDto getRequestor(String userId) {
    	UserAccountDto userAccount = userAccountService.getUserAccount(userId);
    	if(userAccount == null) {
    		userAccount = new UserAccountDto();
    		userAccount.setUserAccountId("A123456");
    		userAccount.setUsername("A123456");
    		userAccount.setFirstName("Larry");
    		userAccount.setLastName("Local");
    		userAccount.setDisplayName("Larry Local");
    	}
    	return userAccount;
    }
}
