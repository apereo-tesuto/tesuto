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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ApplicationLayoutService extends AbstractRestActionService {
    
    @Value("${APPLICATION_LAYOUT_URL}")
    private String layoutURL;

    public static final String NAME = "APPLICATIONLAYOUT";

    @Override
    public String getName() {
        return NAME;
    }
    
    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("action","attribute_name","attribute_value"));
    @Override
    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }
    
    @Override
    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        String layoutUrl = getConfig().getEngineLayoutUrl();

        String cccid = getFactsUtils().getCccId(facts);
        String cccMisCode = getFactsUtils().getFamily(facts);
        String attribute = (String) action.getActionParameters().get("attribute_name");
        String value = (String) action.getActionParameters().get("attribute_value");
        String targetUrl = layoutUrl + "/ccc/api/users/" + cccid +"/cccMisCode/" + cccMisCode + "/attributes/" + attribute;
        if (getHttpMethod(action, facts).equals(HttpMethod.POST)) {
            targetUrl += "/value/" + value;
        }
        log.debug("ApplicationLayoutService targetUrl:[" + targetUrl + "]");
        return targetUrl;
    }
    
    @Override
    public List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        String pinboardAction = (String) action.getActionParameters().get("action");
        if ("delete".equals(pinboardAction)) {
            return Arrays.asList(HttpStatus.NO_CONTENT);
        }
        return Arrays.asList(HttpStatus.OK);
    }
    
}
