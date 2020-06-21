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
public class EmailFacade extends AbstractRestActionService {    
    private final String EMAIL_ROUTE = "ccc/api/messages/v1/sendEmail";

    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("to", "subject", "message-body-text", "message-body-html"));
    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }

    private final String NAME = "EMAIL";
    public String getName() {
        return NAME;
    }

    @Override
    public Object buildContentBody(RulesAction action, Map<String, Object> facts) {
        Map<String, Object> actionParameters = action.getActionParameters();
        return actionParameters;
    }
    
    @Override
    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        String emailHost = getConfig().getEmailHost();
        if (StringUtils.isBlank(emailHost)) {
            log.error("System property [MESSAGING_URL] cannot be blank");
            throw new IllegalArgumentException("System property [MESSAGING_URL] cannot be blank");
        }
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(emailHost).path(EMAIL_ROUTE);
        String messagingUrl = uri.build().toString();
        return messagingUrl;
    }

    @Override
    public HttpMethod getHttpMethod(RulesAction action, Map<String, Object> facts) {
        return HttpMethod.POST;
    }

    @Override
    public List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        return Arrays.asList(HttpStatus.CREATED);
    }
}
