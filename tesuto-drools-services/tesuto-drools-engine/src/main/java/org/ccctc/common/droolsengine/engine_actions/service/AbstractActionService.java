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
package org.ccctc.common.droolsengine.engine_actions.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.utils.FactsUtils;


import org.springframework.beans.factory.annotation.Autowired;


/**
 * AbstractActionService is a sample implementation of IActionService, the basis
 * of actions that the Rules Engine knows how to execute.  This class implements
 * the basic method but allows the user to create specific actions by implementing
 * the abstract methods and by overriding default methods.
 * <p>This class is designed to make a call to a REST endpoint.  It adds security headers
 * by default and sets the content-type to handle JSON.  The content body does not need to be set
 * if the REST endpoint doesn't require it, but it can be set.  The user also has the opportunity to
 * add additional headers manually if necessary.
 * <p>This class performs some basic validation of the available Facts and the ActionParameters that
 * are available from the RuleAction.  Basic parameter validation just verifies that there is a parameter of 
 * the names listed in getRequiredParameters().  You can do additional validations by overriding the
 * verifyCustomParameters() method.
 * <p>Basic Facts validation ensures that the user's cccid, cccMisCoad, and OAuthToken are set.  Additional
 * fact validation can be done by overriding the verifyCustomFacts() method.
 * <p>In order to create a new action using this class, the user implements the following
 * abstract methods:
 * <ul><li>getName() - this is the name that will be set in in the RuleAction when the
 * user creates a rule in the .DRL file</li>
 * <li>getTargetUrl() - this is the external system that will be called and passed the 
 * JSON payload in the content body</li>
 * <li>getHttpMethod() - tells the service what HttpMethod to use: GET, POST, PUT, or DELETE</li>
 * <li>getExpectedHttpStatus() - tells the service what the Response should be after the call.</li>
 * </ul>
 * <p>To send a content body, override the default buildContentBody() method.  By default, the body is blank.
 * <p>To send custom headers, override the default addCustomHeaaders() method.  By default, only authorization, 
 * cookie (if present), and content-type headers are sent
 * @author mgillian
 *
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AbstractActionService implements IActionService {
    private static final String DEFAULT_MIS = "000";
    
    @Autowired
    private FactsUtils factsUtils;
    public FactsUtils getFactsUtils() {
        return factsUtils;
    }
    
    @Autowired
    private DroolsEngineEnvironmentConfiguration config;
    public DroolsEngineEnvironmentConfiguration getConfig() {
    	return config;
    }

    /**
     * Must be defined.  Sets the name of the action.  This name must match the name
     * of the rule specified in the RuleAction defined in .DRL files
     */
    public abstract String getName();

    
    public final List<String> execute(RulesAction action, Map<String, Object> facts) {        
        List<String> errors = new ArrayList<String>();
        log.trace("Executing [" + getName() + "] with facts:[" + facts + "]");
        
        errors.addAll(verifyRequiredFacts(facts));
        errors.addAll(verifyRequiredParameters(action));
        errors.addAll(verifyCustomFacts(facts));
        errors.addAll(verifyCustomParameters(action));
        if (errors.size() > 0) {
            log.error("Unable to process [" + this.getName() + "] due to rule configuration errors");
            return errors;
        }
        
        errors.addAll(doExecute(action, facts));
        return errors;
    }
    
    /**
     * doExecute is responsible for actually doing the work, whatever it is.  execute() verifies the data.  doExecute()
     * is the abstract function that actually sends any messages or updates any data tables
     * @param action RulesAction that contains the properties used
     * @param facts Facts that may be needed by the service for processing
     * @return a list of error strings that occurs during processing
     */
    public abstract List<String> doExecute(RulesAction action, Map<String, Object> facts);

    public final List<String> verifyRequiredParameters(RulesAction action) {
        List<String> errors = new ArrayList<String>();
        Map<String, Object> actionParameters = action.getActionParameters();
        if (!actionParameters.keySet().containsAll(getRequiredParameters())) {
            errors.add("[" + this.getName() + " requires [" + getRequiredParameters().toString() + "] to be part of RulesAction, parameters sent were:[" + actionParameters.keySet() + "]");
        }
        return errors;
    }
    
    public final List<String> verifyRequiredFacts(Map<String, Object> facts) {
        List<String> errors = new ArrayList<String>();
        String cccid = (String) facts.get(FactsUtils.CCCID_FIELD);
        if (StringUtils.isBlank(cccid)) {
            errors.add("Facts has a blank cccid for this student, cannot execute action");
        }
        String cccMisCode = (String) facts.get(FactsUtils.MISCODE_FIELD);
        if (StringUtils.isBlank(cccMisCode)) {
            errors.add("Student Profile has a blank cccMisCode, cannot execute action");
        }
        return errors;
    }
    
    /**
     * Can be overridden.  Use this to perform additional validation on facts that are
     * available to this action.
     * @param facts
     * @return
     */
    public List<String> verifyCustomFacts(Map<String, Object> facts) {
        return new ArrayList<String>();    
    }
    
    /**
     * Can be overridden.  Use this to perform additional validation on the actionParameters
     * assigned to this action by the Rule.
     * @param action
     * @return
     */
    public List<String> verifyCustomParameters(RulesAction action) {
        return new ArrayList<String>();    
    }
    
    /**
     * Can and probably should be overridden.  Use this to ensure that the RuleAction created in a Rule
     * has the necessary parameters.  If the action requires three actionParameters, this method should
     * return a set of those three parameter names.  Then when the Rule executes and the RuleAction tries to call
     * this action, this action can ensure all of the parameters have been set by the rule.
     */
    public Set<String> getRequiredParameters() {
        return new HashSet<String>();
    }

    /**
     * Rather than being configurable or allowing rules to define the value, we are hardcoding the override at this time. This keeps 
     * rules authors from accidently "cutting themselves with sharp objects". If we find that we truly desire the overriding of the 
     * facts by the rules/actions this code can be updated/re-evaluated. 
     * @param action
     * @param facts
     */
    protected void applyFactsFilter(RulesAction action, Map<String, Object> facts) {
        if (action.getActionParameters().containsKey("override-for-guest")) {
            log.debug("Override set, using MIS: " + DEFAULT_MIS);
            facts.put("cccMisCode", DEFAULT_MIS);
        }
    };
}
