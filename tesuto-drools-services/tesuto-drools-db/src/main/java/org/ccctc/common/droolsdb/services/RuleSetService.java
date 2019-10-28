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

import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;

public interface RuleSetService {
    public RuleSetDTO getRuleSet(String ruleSetId);
    public RuleSetDTO duplicate(String ruleSetId);
    
    public List<RuleSetDTO> getRuleSetsByParameters(String application, String status, String cccMisCode);
    public List<RuleSetDTO> getAllRuleSets();
    public List<RuleSetDTO> getRuleSetsByEngine(String application);
    public List<RuleSetDTO> getRulesSetByEngineAndStatus(String application, String status);
    public List<RuleSetDTO> getRuleSetsByStatus(String status);
    public List<RuleSetDTO> getRuleSetsByEngineAndStatusAndFamily(String application, 
            String status, String cccMisCode);
    public List<RuleSetDTO> getRuleSetsByEngineAndFamily(String application, String cccMisCode);
    public List<RuleSetDTO> getRuleSetsByStatusAndFamily(String status, String cccMisCode);
    public List<RuleSetDTO> getRuleSetsByFamily(String cccMisCode);
    
    public List<RuleSetDTO> find(RuleAttributeFacetSearchForm form);

    public RuleSetDTO save(RuleSetDTO ruleSetDTO);
    public void publish(String ruleSetId, Boolean publish);
    public void delete(String ruleSetId);
}
