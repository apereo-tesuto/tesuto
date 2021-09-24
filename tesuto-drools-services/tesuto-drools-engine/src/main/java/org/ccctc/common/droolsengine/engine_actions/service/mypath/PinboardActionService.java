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

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PinboardActionService extends AbstractRestActionService {
    public static final String NAME = "PINBOARD";
        
    private final Set<String> requiredParameters = new HashSet<String>(Arrays.asList("pinboardFname"));
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public Set<String> getRequiredParameters() {
        return requiredParameters;
    }
    
    @Override
    public String getTargetUrl(RulesAction action, Map<String, Object> facts) {
        String favoritesUrl = getConfig().getFavoritesUrl();

        String cccid = getFactsUtils().getCccId(facts);
        String pinboardFname = (String) action.getActionParameters().get("pinboardFname");
        String targetUrl = favoritesUrl + "/" + cccid + "/pinboard/launchers/" + pinboardFname;
        log.debug("Pinboard targetUrl:["+ targetUrl + "]");

        return targetUrl;
    }
    
    /**
     * 409 may occur if the card is already part of the user's layout - no need to error out.
     * @see org.ccctc.common.droolsengine.engine_actions.service.AbstractRestActionService#getExpectedHttpStatuses(org.ccctc.common.droolscommon.RulesAction, java.util.Map)
     */
    @Override
    public List<HttpStatus> getExpectedHttpStatuses(RulesAction action, Map<String, Object> facts) {
        return Arrays.asList(HttpStatus.CONFLICT, HttpStatus.CREATED);
    }
}
