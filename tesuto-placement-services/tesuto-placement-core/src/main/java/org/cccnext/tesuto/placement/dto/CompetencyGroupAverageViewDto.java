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
package org.cccnext.tesuto.placement.dto;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CompetencyGroupAverageViewDto {

    private String competencyGroupTitle;
    private Integer competencyGroupId;
    private double competencyGroupAverage;

    public String getCompetencyGroupTitle() {
        return competencyGroupTitle;
    }

    public void setCompetencyGroupTitle(String competencyGroupTitle) {
        this.competencyGroupTitle = competencyGroupTitle;
    }

    public Integer getCompetencyGroupId() {
        return competencyGroupId;
    }

    public void setCompetencyGroupId(Integer competencyGroupId) {
        this.competencyGroupId = competencyGroupId;
    }

    public double getCompetencyGroupAverage() {
        return competencyGroupAverage;
    }

    public void setCompetencyGroupAverage(double competencyGroupAverage) {
        this.competencyGroupAverage = competencyGroupAverage;
    }

    @Override
    public String toString() {
        return "CompetencyGroupAverageViewDto{" +
                "competencyGroupTitle='" + competencyGroupTitle + '\'' +
                ", competencyGroupId=" + competencyGroupId +
                ", competencyGroupAverage=" + competencyGroupAverage +
                '}';
    }
}
