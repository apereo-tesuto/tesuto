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
package org.ccctc.common.droolsdb.services;

import java.util.List;

import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.model.RuleDTO;

public interface RuleService {
    public RuleDTO getRule(String ruleId);
    public RuleDTO duplicate(String ruleId);
    public List<RuleDTO> getAllRules();
    public List<RuleDTO> find(RuleAttributeFacetSearchForm form);
    public List<RuleDTO> getRulesByEngine(String application);
    public List<RuleDTO> getRulesByEngineAndStatus(String application, String status);
    public List<RuleDTO> getRulesByStatus(String status);
    public RuleDTO save(RuleDTO ruleDTO);
    public void delete(String ruleId);
}
