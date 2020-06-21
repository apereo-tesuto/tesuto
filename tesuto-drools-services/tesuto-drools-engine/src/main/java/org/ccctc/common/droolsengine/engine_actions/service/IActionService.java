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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccctc.common.droolscommon.RulesAction;

public interface IActionService {

    public String getName();

    /**
     * Execute the defined action service based on the results of a rule.  IActionServices use RuleAction object
     * to store the parameters that this instance of the service obtains from the rule being fired, 
     * and the Map<String, Object> facts object
     * contains parameters from the general environment that the service may need.
     * @param action
     * @param facts
     * @return a List<String> of errors.  An empty list will be returned if there are no errors.
     */
    public List<String> execute(RulesAction action, Map<String, Object> facts);
    
    public Set<String> getRequiredParameters();
 
    /**
     * verifyRequiredParameters looks at the parameters provided in the action and makes sure
     * that all required parameters are present
     * @param action
     * @return a List of error strings.  List will be empty if processing succeeded without error.
     */
    public List<String> verifyRequiredParameters(RulesAction action);

}
