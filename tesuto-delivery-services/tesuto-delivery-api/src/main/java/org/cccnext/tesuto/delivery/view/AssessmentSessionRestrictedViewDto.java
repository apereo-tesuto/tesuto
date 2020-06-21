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
package org.cccnext.tesuto.delivery.view;

import java.util.Date;
import java.util.Map;

public class AssessmentSessionRestrictedViewDto {



    private String assessmentSessionId; // primary key
    private String title;
    private String userId;
    private String language;
    private Map<String, String> assessmentSettings;
    private Date deadline; // could be null
    private String stylesheets;
    private int assessmentVersion;
    private String assessmentContentId;
    
    private TaskSetViewDto currentTaskSet;

    public String getAssessmentSessionId() {
        return assessmentSessionId;
    }

    public void setAssessmentSessionId(String assessmentSessionId) {
        this.assessmentSessionId = assessmentSessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getAssessmentSettings() {
        return assessmentSettings;
    }

    public void setAssessmentSettings(Map<String, String> assessmentSettings) {
        this.assessmentSettings = assessmentSettings;
    }


    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeRemaining() { // in seconds
        if (getDeadline() != null) {
            return (getDeadline().getTime() - System.currentTimeMillis()) / 1000;
        }
        return Long.MAX_VALUE;
    }

    public int getAssessmentVersion() {
        return assessmentVersion;
    }

    public void setAssessmentVersion(int assessmentVersion) {
        this.assessmentVersion = assessmentVersion;
    }

    public String getAssessmentContentId() {
        return assessmentContentId;
    }

    public void setAssessmentContentId(String assessmentContentId) {
        this.assessmentContentId = assessmentContentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AssessmentSessionRestrictedViewDto))
            return false;

        AssessmentSessionRestrictedViewDto that = (AssessmentSessionRestrictedViewDto) o;

        if (getAssessmentSessionId() != null ? !getAssessmentSessionId().equals(that.getAssessmentSessionId())
                : that.getAssessmentSessionId() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null)
            return false;
        if (getLanguage() != null ? !getLanguage().equals(that.getLanguage()) : that.getLanguage() != null)
            return false;
        if (getAssessmentSettings() != null ? !getAssessmentSettings().equals(that.getAssessmentSettings())
                : that.getAssessmentSettings() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        if (getAssessmentContentId() != null ? !getAssessmentContentId().equals(that.getAssessmentContentId()) : that.getAssessmentContentId() != null)
            return false;
        if (getAssessmentVersion() != that.getAssessmentVersion())
            return false;
        return !(getDeadline() != null ? !getDeadline().equals(that.getDeadline()) : that.getDeadline() != null);

    }

    @Override
    public int hashCode() {
        int result = getAssessmentSessionId() != null ? getAssessmentSessionId().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        result = 31 * result + (getAssessmentSettings() != null ? getAssessmentSettings().hashCode() : 0);
        result = 31 * result + (getDeadline() != null ? getDeadline().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + getAssessmentVersion();
        result = 31 * result + (getAssessmentContentId() != null ? getAssessmentContentId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssessmentSessionViewDto{" +
                "assessmentSessionId='" + assessmentSessionId + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", language='" + language + '\'' +
                ", assessmentSettings=" + assessmentSettings +
                ", deadline=" + deadline +
                ", assessmentVersion=" + assessmentVersion +
                ", assessmentContentId='" + assessmentContentId + '\'' +
                '}';
    }

    public String getStylesheets() {
        return stylesheets;
    }

    public void setStylesheets(String stylesheets) {
        this.stylesheets = stylesheets;
    }

	public TaskSetViewDto getCurrentTaskSet() {
		return currentTaskSet;
	}

	public void setCurrentTaskSet(TaskSetViewDto currentTaskSet) {
		this.currentTaskSet = currentTaskSet;
	}
}
