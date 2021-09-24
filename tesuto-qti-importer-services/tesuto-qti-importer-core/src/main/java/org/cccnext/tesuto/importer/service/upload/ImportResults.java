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
package org.cccnext.tesuto.importer.service.upload;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ImportResults extends Results implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] competencyMapUrls;

    private String[] assessmentSessionUrls;

    private List<String> assessmentSessionIds;

    private Map<String, String> competencyMapOrderUrls;

    private Map<String, String> competencyMapOrderIds;

    public String[] getCompetencyMapUrls() {
        return competencyMapUrls;
    }

    public void setCompetencyMapUrls(String[] competencyMapUrls) {
        this.competencyMapUrls = competencyMapUrls;
    }

    public List<String> getAssessmentSessionIds() {
        return assessmentSessionIds;
    }

    public void setAssessmentSessionIds(List<String> assessmentSessionIds) {
        this.assessmentSessionIds = assessmentSessionIds;
    }

    public String[] getAssessmentSessionUrls() {
        return assessmentSessionUrls;
    }

    public void setAssessmentSessionUrls(String[] assessmentSessionUrls) {
        this.assessmentSessionUrls = assessmentSessionUrls;
    }

    public Map<String, String> getCompetencyMapOrderUrls() {
        return competencyMapOrderUrls;
    }

    public void setCompetencyMapOrderUrls(Map<String, String> competencyMapOrderUrls) {
        this.competencyMapOrderUrls = competencyMapOrderUrls;
    }

    public Map<String, String> getImportedCompetencyMapOrderIds() {
        return competencyMapOrderIds;
    }

    public void setImportedCompetencyMapOrderIds(Map<String, String> importedCompetencyMapOrderIds) {
        this.competencyMapOrderIds = importedCompetencyMapOrderIds;
    }


}
