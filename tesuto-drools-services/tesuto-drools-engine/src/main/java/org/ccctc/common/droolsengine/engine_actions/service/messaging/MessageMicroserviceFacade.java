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
package org.ccctc.common.droolsengine.engine_actions.service.messaging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService;
import org.ccctc.common.droolsengine.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolsengine.utils.FactsUtils;


import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class MessageMicroserviceFacade extends AbstractRestActionService {
    public static final String NAME = "MESSAGE";

    private final String MESSAGING_ROUTE = "ccc/api/messages/v1/sendMessagesFromRule";

    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("subject", "message-body-html"));

    /**
     * Rules must send all relevant information on how to contact the user (phone, email, cccid) using the new endpoint.
     * Because the RULE is sending the detail, it is possible to skip SMS by having the rule set the phone auth to false.
     * 
     * @see org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService#buildContentBody(net.ccctechcenter.drools.RulesAction,
     *      java.util.Map)
     */
    @Override
    public Object buildContentBody(RulesAction action, Map<String, Object> facts) {
        Map<String, Object> actionParameters = action.getActionParameters();
        log.trace("facts:[" + facts + "]");

        updateActionParams(actionParameters, "misCode", getFactsUtils().getFamily(facts));

        // In order for SMS to happen, this must be added
        updateActionParams(actionParameters, FactsUtils.MAIN_PHONE_AUTH_FIELD,
                        getFactsUtils().getBooleanField(facts, FactsUtils.MAIN_PHONE_AUTH_FIELD));
        updateActionParams(actionParameters, FactsUtils.MAIN_PHONE_FIELD,
                        getFactsUtils().getField(facts, FactsUtils.MAIN_PHONE_FIELD));

        updateActionParams(actionParameters, FactsUtils.SECONDARY_PHONE_AUTH_FIELD,
                        getFactsUtils().getBooleanField(facts, FactsUtils.SECONDARY_PHONE_AUTH_FIELD));
        updateActionParams(actionParameters, FactsUtils.SECONDARY_PHONE_FIELD,
                        getFactsUtils().getField(facts, FactsUtils.SECONDARY_PHONE_FIELD));

        updateActionParams(actionParameters, FactsUtils.EMAIL_FIELD, getFactsUtils().getField(facts, FactsUtils.EMAIL_FIELD));
        
        // If there are no users in the params, we add a dummy value so that the message service doesn't error out. 
        if (!actionParameters.containsKey("users")) {
            actionParameters.put("users", Arrays.asList("ZZZ9999"));
        }
        return actionParameters;
    }

    @Override
    public List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        return Arrays.asList(HttpStatus.ACCEPTED);
    }

    @Override
    public HttpMethod getHttpMethod(RulesAction action, Map<String, Object> facts) {
        return HttpMethod.POST;
    }

    public String getName() {
        return NAME;
    }

    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }

    @Override
    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        String messagingHost = getConfig().getEmailHost();
        if (StringUtils.isBlank(messagingHost)) {
            log.error("unable to retrieve [MESSAGING_URL] from environment, cannot connect to messaging service");
            throw new ObjectNotFoundException("Environment Parameter", "MESSAGING_URL");
        }
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(messagingHost).path(MESSAGING_ROUTE);
        String messagingUrl = uri.build().toString();
        return messagingUrl;
    }

    /**
     * Default behavior is override any existing params with the facts (DRAGNET mode: just the facts!).
     * Override this behavior by adding an action param:
     * "skip_override" : true
     * If set, an action parameter will not be overwritten by fact values.
     * 
     * This allows a rule to manipulate the values being used by creating the action parameters that should be used.
     * 
     * @param actionParams
     * @param key
     * @param value
     * @return the modified actionParams (supports chaining the calls)
     */
    protected Map<String, Object> updateActionParams(Map<String, Object> actionParams, String key, Object factValue) {
        boolean skipOverride = actionParams.get("skip_override") == null ? false : Boolean.valueOf(actionParams.get("skip_override").toString());
        if (skipOverride && actionParams.containsKey(key) && factValue != null) {
            return actionParams;
        }
        actionParams.put(key, factValue);
        return actionParams;
    }
}
