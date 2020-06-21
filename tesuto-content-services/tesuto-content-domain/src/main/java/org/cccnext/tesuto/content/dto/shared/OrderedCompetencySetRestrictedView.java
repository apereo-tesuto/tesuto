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
package org.cccnext.tesuto.content.dto.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrderedCompetencySetRestrictedView implements Serializable {

    
    private static final long serialVersionUID = 1L;
    
    SelectedOrderedRestrictedViewCompetencies competenciesForMap;
    
    List<SelectedOrderedRestrictedViewCompetencies> competenciesByTopic;
    
    public OrderedCompetencySetRestrictedView() {
        
    }
    
    public OrderedCompetencySetRestrictedView(SelectedOrderedRestrictedViewCompetencies competenciesForMap, List<SelectedOrderedRestrictedViewCompetencies> competenciesByTopic) {
        this.competenciesForMap = competenciesForMap;
        this.competenciesByTopic = competenciesByTopic;
    }

    public SelectedOrderedRestrictedViewCompetencies getCompetenciesForMap() {
        return competenciesForMap;
    }

    public void setCompetenciesForMap(SelectedOrderedRestrictedViewCompetencies competenciesForMap) {
        this.competenciesForMap = competenciesForMap;
    }

    public List<SelectedOrderedRestrictedViewCompetencies> getCompetenciesByTopic() {
        return competenciesByTopic;
    }

    public void setCompetenciesByTopic(List<SelectedOrderedRestrictedViewCompetencies> competenciesByTopic) {
        this.competenciesByTopic = competenciesByTopic;
    }
}
