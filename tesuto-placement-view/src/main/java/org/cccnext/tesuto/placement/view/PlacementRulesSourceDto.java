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
package org.cccnext.tesuto.placement.view;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class PlacementRulesSourceDto {
    private List<PlacementComponentViewDto> mmapPlacementComponents = new ArrayList<>();
    private List<PlacementComponentViewDto> assessmentPlacementComponents = new ArrayList<>();

    public List<PlacementComponentViewDto> getMmapPlacementComponents() {
        return mmapPlacementComponents;
    }

    public void setMmapPlacementComponents(List<PlacementComponentViewDto> mmapPlacementComponents) {
        this.mmapPlacementComponents = mmapPlacementComponents;
    }

    public List<PlacementComponentViewDto> getAssessmentPlacementComponents() {
        return assessmentPlacementComponents;
    }

    public void setAssessmentPlacementComponents(List<PlacementComponentViewDto> assessmentPlacementComponents) {
        this.assessmentPlacementComponents = assessmentPlacementComponents;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
