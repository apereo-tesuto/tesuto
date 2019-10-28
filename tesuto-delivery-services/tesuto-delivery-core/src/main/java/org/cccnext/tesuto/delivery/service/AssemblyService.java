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
package org.cccnext.tesuto.delivery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.service.ItemSessionNotFoundException;
import org.cccnext.tesuto.delivery.view.AssessmentItemViewDto;
import org.cccnext.tesuto.delivery.view.ItemSessionResponseViewDto;
import org.cccnext.tesuto.delivery.view.ItemSessionViewDto;
import org.cccnext.tesuto.delivery.view.ResponseVarViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.delivery.view.TaskSetViewDto;
import org.cccnext.tesuto.delivery.view.TaskViewDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class AssemblyService {

    @Autowired private AssessmentItemReader assessmentItemReader;
    @Autowired private TaskSetService taskSetService;

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public TaskSetService getTaskSetService() {
        return taskSetService;
    }

    public void setTaskSetService(TaskSetService taskSetService) {
        this.taskSetService = taskSetService;
    }

    public AssessmentSessionViewDto assembleAssessmentSession(AssessmentSession session) {
        if (session == null) {
            return null;
        }
        AssessmentSessionViewDto view = new AssessmentSessionViewDto();
        view.setInternalSession(session);
        view.setAssessmentSessionId(session.getAssessmentSessionId());
        AssessmentDto assessment = session.getAssessment();
        view.setTitle(assessment.getTitle());
        view.setLanguage(assessment.getLanguage());
        view.setAssessmentSettings(session.getAssessmentSettings());
        view.setDeadline(session.getDeadline());
        view.setCurrentTaskSet(assembleTaskSet(taskSetService.getCurrentTaskSet(session)));
        view.setStylesheets(assessment.getStylesheets());
        view.setUserId(session.getUserId());
        view.setAssessmentVersion(session.getAssessment().getVersion());
        view.setAssessmentContentId(session.getContentId());
        return view;
    }

    public TaskSetViewDto assembleTaskSet(TaskSet taskSet) {
        if (taskSet == null) {
            return null;
        }
        TaskSetViewDto view = new TaskSetViewDto();
        view.setTaskSetId(taskSet.getTaskSetId());
        view.setTaskSetIndex(taskSet.getTaskSetIndex());
        view.setNavigationMode(taskSet.getNavigationMode());
        view.setSubmissionMode(taskSet.getSubmissionMode());
        if(taskSet.getDuration() != null){
            view.setDuration(taskSet.getDuration());
        }
        view.setTasks(
                taskSet.getTasksOrderedByIndex().stream().map(task -> assembleTask(task)).collect(Collectors.toList()));
        return view;
    }

    public TaskViewDto assembleTask(Task task) {
        if (task == null) {
            return null;
        }
        TaskViewDto view = new TaskViewDto();
        view.setTaskId(task.getTaskId());
        view.setStimulus(task.getStimulus());
        view.setTaskIndex(task.getTaskIndex());
        view.setItemSessions(task.getItemSessionsOrderedByIndex().stream().map(session -> assembleItemSession(session))
                .collect(Collectors.toList()));
        return view;
    }

    public ItemSessionViewDto assembleItemSession(ItemSession session) {
        if (session == null) {
            return null;
        }
        AssessmentItemDto item = assessmentItemReader.read(session.getItemId());
        if (item == null) {
            log.error("Null item found for " + session.getItemId());
            return null;
        }
        ItemSessionViewDto view = new ItemSessionViewDto();
        view.setItemSessionId(session.getItemSessionId());
        view.setItemSessionIndex(session.getItemSessionIndex());
        view.setLanguage(item.getLanguage());
        view.setAllowSkipping(session.getAllowSkipping());
        view.setValidateResponses(session.getValidateResponses());
        view.setAssessmentItem(assembleAssessmentItem(item));
        List<ItemSessionResponseViewDto> responses = new ArrayList<>();
        session.getResponses().keySet().forEach(key -> responses
                .add(assembleItemSessionResponse(session.getItemSessionId(), session.getResponse(key))));

        view.setResponses(responses);
        return view;
    }

    public ItemSessionResponseViewDto assembleItemSessionResponse(String itemSessionId, Response response) {
        if (response == null) {
            return null;
        }
        ItemSessionResponseViewDto view = new ItemSessionResponseViewDto();
        view.setItemSessionId(itemSessionId);
        view.setResponseIdentifier(response.getResponseId());
        view.setValues(response.getValues());
        return view;
    }

    public AssessmentItemViewDto assembleAssessmentItem(AssessmentItemDto item) {
        if (item == null) {
            return null;
        }
        AssessmentItemViewDto view = new AssessmentItemViewDto();
        view.setStylesheets(item.getStylesheets());
        view.setItemBody(item.getBody());
        view.setResponseVars(
                item.getResponseVars().stream().map(var -> assembleResponseVar(var)).collect(Collectors.toList()));
        view.setInteractions(item.getInteractions());
        if (item.getItemMetadata() != null)
            view.setTools(item.getItemMetadata().getTools());
        return view;
    }

    public ResponseVarViewDto assembleResponseVar(AssessmentResponseVarDto responseVar) {
        ResponseVarViewDto view = new ResponseVarViewDto();
        view.setIdentifier(responseVar.getIdentifier());
        view.setCardinality(responseVar.getCardinality().getValue());
        view.setBaseType(responseVar.getBaseType().getValue());
        // TODO: view.setDefaultValue(responseVar.getDefaultValueDto()
        return view;
    }

    public Response assembleResponse(ItemSessionResponseViewDto view) {
        Response response = new Response();
        response.setResponseId(view.getResponseIdentifier());
        response.setValues(view.getValues());
        return response;
    }

    public void mergeResponse(TaskResponseViewDto view, Task task) {
        view.getResponses().forEach(responseView -> {
            String id = responseView.getItemSessionId();
            ItemSession session = task.getItemSession(id);
            if (session == null) {
                throw new ItemSessionNotFoundException(id);
            }
            session.addResponse(assembleResponse(responseView));
        });
    }

}
