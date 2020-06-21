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
package org.ccctc.common.droolsengine.engine_actions.service.mypath;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService;


import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * AdvisorCardFacade handles rules action of "ADVISORCARD". This action calls a REST endpoint
 * exposed by the uPortal Advisor Card portlet. If advisorCardTaskTitle is not blank, the Advisor Card
 * portlet will add an event for that task. If the advisorCardTaskTitle is blank, the Advisor Card
 * portlet will add an event for all tasks on that advisor card.
 * 
 * @author mgillian
 *
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdvisorCardFacade extends AbstractRestActionService {

    private final String NAME = "ADVISORCARDEVENT";

    public String getName() {
        return NAME;
    }

    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("advisorCardTitle", "state"));

    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }

    @Override
    public Object buildContentBody(RulesAction action, Map<String, Object> facts) {
        applyFactsFilter(action, facts);
        String advisorCardTitle = (String) action.getActionParameters().get("advisorCardTitle");
        String advisorCardTaskTitle = "";
        if (action.getActionParameters().containsKey("advisorCardTaskTitle")) {
            advisorCardTaskTitle = (String) action.getActionParameters().get("advisorCardTaskTitle");
        }
        String state = (String) action.getActionParameters().get("state");
        String cccid = (String) facts.get("cccid");
        String cccMisCode = (String) facts.get("cccMisCode");

        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put("advisorCardTitle", advisorCardTitle);
        eventParams.put("advisorCardTaskTitle", advisorCardTaskTitle);
        eventParams.put("state", state);
        eventParams.put("username", cccid);
        eventParams.put("timestamp", new Date());
        eventParams.put("cccMisCode", cccMisCode);
        eventParams.put("createdby", "RULE");
        log.debug("Advisor Card content body:[" + eventParams + "]");
        return eventParams;
    }

    @Override
    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        String targetUrl = getConfig().getAdvisorcardUrl();
        String advisorCardUrl = targetUrl + "/advisorcard/api/v1/events/bytitle";
        return advisorCardUrl;
    }

    @Override
    public HttpMethod getHttpMethod(RulesAction action, Map<String, Object> facts) {
        return HttpMethod.POST;
    }

    /**
     * Generally speaking, No Content indicates the call went through, but there was no such card as the one named
     * @see org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService#getExpectedHttpStatuses(org.ccctc.common.droolscommon.RulesAction, java.util.Map)
     */
    @Override
    public List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        return Arrays.asList(HttpStatus.CREATED, HttpStatus.NO_CONTENT);
    }
}
