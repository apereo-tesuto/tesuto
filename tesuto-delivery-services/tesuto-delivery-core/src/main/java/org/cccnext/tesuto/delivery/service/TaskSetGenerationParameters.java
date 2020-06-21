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

import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;

/*
 * Class used to hold necessary variables for recursive method in TaskSetService
 * 
 * @author jbrown@unicon.net
 */
class TaskSetGenerationParameters {

    int taskSetCount;
    int taskCount;
    int itemSessionCount;
    TaskSet prevTaskSet;
    AssessmentSession assessmentSession;
    AssessmentPartNavigationMode navigationMode;
    AssessmentPartSubmissionMode submissionMode;
    AssessmentItemSessionControlDto itemSessionControl;
    AssessmentMetadataDto metadata;

    public TaskSetGenerationParameters(AssessmentSession assessmentSession) {
        this.assessmentSession = assessmentSession;
    }

    public int getTaskSetCount() {
        return taskSetCount;
    }

    public int iterateGetTaskSetCount() {
        return ++taskSetCount;
    }

    public void setTaskSetCount(int taskSetCount) {
        this.taskSetCount = taskSetCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int iterateGetTaskCount() {
        return ++taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getItemSessionCount() {
        return itemSessionCount;
    }

    // UI gets the question number from the itemSessionIndex.
    // UI expects the index to start at zero.
    public int iterateGetItemSessionCount() {
        return ++itemSessionCount;
    }

    public void setItemSessionCount(int itemSessionCount) {
        this.itemSessionCount = itemSessionCount;
    }

    public TaskSet getPrevTaskSet() {
        return prevTaskSet;
    }

    public void setPrevTaskSet(TaskSet prevTaskSet) {
        this.prevTaskSet = prevTaskSet;
    }

    public AssessmentSession getAssessmentSession() {
        return assessmentSession;
    }

    public void setAssessmentSession(AssessmentSession assessmentSession) {
        this.assessmentSession = assessmentSession;
    }

    public AssessmentPartNavigationMode getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(AssessmentPartNavigationMode navigationMode) {
        this.navigationMode = navigationMode;
    }

    public AssessmentPartSubmissionMode getSubmissionMode() {
        return submissionMode;
    }

    public void setSubmissionMode(AssessmentPartSubmissionMode submissionMode) {
        this.submissionMode = submissionMode;
    }

    public AssessmentItemSessionControlDto getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControl(AssessmentItemSessionControlDto itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public AssessmentMetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(AssessmentMetadataDto metadata) {
        this.metadata = metadata;
    }
}
