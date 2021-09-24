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
package org.cccnext.tesuto.rules.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.springframework.beans.factory.annotation.Value;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService;
import org.springframework.stereotype.Service;

@Service
public class PlacementActionService extends AbstractRestActionService {

    public final static String PLACEMENT_ACTION_NAME = "PLACEMENT_ACTION";

    @Value("${CREATE_PLACEMENT_ACTION_TARGET_URL}")
    public String placementTargetUrl;

    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("placement"));

    @Override
    public String getName() {
        return PLACEMENT_ACTION_NAME;
    }

    @Override
    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }

    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        return placementTargetUrl;
    }

    @Override
    public Object buildContentBody(RulesAction action, Map<String, Object> facts) {
         action.getActionParameters().put("action", "add");
         @SuppressWarnings("unchecked")
         PlacementActionResult placement = (PlacementActionResult)action.getActionParameters().get("placement");
         if(placement != null) {
                 return placement;
         }
        PlacementActionResult failedPlacement = new PlacementActionResult();
        failedPlacement.setCccid((String)facts.get(PlacementMapKeys.CCCID_KEY));
        failedPlacement.setCollegeId((String)facts.get(PlacementMapKeys.MISCODE_KEY));
        failedPlacement.setSubjectAreaId(Integer.parseInt((String)facts.get(PlacementMapKeys.SUBJECT_AREA_KEY)));
        failedPlacement.setSubjectAreaVersion(Integer.parseInt((String)facts.get(PlacementMapKeys.SUBJECT_AREA_VERSION_KEY)));
        return failedPlacement;
    }

}
