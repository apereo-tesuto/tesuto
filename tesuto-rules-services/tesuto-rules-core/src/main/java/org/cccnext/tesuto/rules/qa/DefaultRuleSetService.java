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
package org.cccnext.tesuto.rules.qa;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.cccnext.tesuto.qa.QAService;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultRuleSetService implements QAService<RuleSetDTO> {

    @Autowired
    private RuleSetService ruleSetService;
   

    @Override
    public void setDefaults() throws IOException {
        for(RuleSetDTO ruleSet: getResources(RuleSetDTO.class)) {
            try {
                ruleSetService.getRuleSet(ruleSet.getId());
            } catch(ObjectNotFoundException exp) {
                ruleSetService.save(ruleSet);
            }
        }
    }
  
    public void addCollege(String cccMisCode) throws IOException {
        Set<RuleSetDTO> ruleSets = this.getResources(RuleSetDTO.class);
        
        for(RuleSetDTO ruleSetDTO:ruleSets) {
        	
        	if(hasComparable(ruleSetDTO, cccMisCode))
        		continue;
        	
            ruleSetDTO.setId(UUID.randomUUID().toString());
            ruleSetDTO.setFamily(cccMisCode);
            ruleSetService.save(ruleSetDTO);
        }
    }
    
    private Boolean hasComparable(RuleSetDTO ruleSetDTO, String cccMisCode) {
    	RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
    	form.setEngine(ruleSetDTO.getEngine());
    	form.setCategory(ruleSetDTO.getCategory());
    	form.setFamily(cccMisCode);
    	form.setEvent(ruleSetDTO.getEvent());
    	form.setCompetencyMapDiscipline(ruleSetDTO.getCompetencyMapDiscipline());
    	form.setTitle(ruleSetDTO.getTitle());
    	List<RuleSetDTO> rules = ruleSetService.find(form);
    	if(rules != null && !rules.isEmpty())
    		return true;
    	return false;
    }

    @Override
    public String getDirectoryPath() {
        return "classpath:defaults/rule_set";
    }
}
