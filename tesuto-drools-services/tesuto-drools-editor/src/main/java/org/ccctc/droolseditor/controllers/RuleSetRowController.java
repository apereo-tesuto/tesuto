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
package org.ccctc.droolseditor.controllers;

import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rulesetrows")
public class RuleSetRowController {

    @Autowired
    private RuleSetRowService ruleSetRowService;
    
    @RequestMapping(method = RequestMethod.GET)
    public List<RuleSetRowDTO> getRuleSetRows(@RequestBody RuleAttributeFacetSearchForm form) {
        List<RuleSetRowDTO> ruleSets = ruleSetRowService.find(form);
        return ruleSets;
    }
    
    @RequestMapping(value="/{ruleSetRowId}", method = RequestMethod.GET)
    public RuleSetRowDTO get(@PathVariable("ruleSetRowId") String ruleSetRowId) {
        RuleSetRowDTO RuleSetRowDTO = ruleSetRowService.getRuleSetRow(ruleSetRowId);
        return RuleSetRowDTO;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public RuleSetRowDTO save(@RequestBody RuleSetRowDTO RuleSetRowDTO) {
        RuleSetRowDTO updatedRuleSetRowDTO = ruleSetRowService.save(RuleSetRowDTO);
        return updatedRuleSetRowDTO;
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleSetRowId}")
    public RuleSetRowDTO updateRuleSetRow(@RequestBody RuleSetRowDTO RuleSetRowDTO,
            @PathVariable("ruleSetRowId") String ruleSetId) {
        RuleSetRowDTO.setId(ruleSetId);
        RuleSetRowDTO updatedRuleDTO = ruleSetRowService.save(RuleSetRowDTO);
        return updatedRuleDTO;
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleSetRowId}/duplicate")
    public RuleSetRowDTO duplicateRuleSetRow(@PathVariable("ruleSetRowId") String ruleSetId) {
        RuleSetRowDTO duplicateRuleSetRowDTO = ruleSetRowService.duplicate(ruleSetId);
        return duplicateRuleSetRowDTO;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value="/{ruleSetRowId}")
    public void deleteRuleSetRow(@PathVariable("ruleSetRowId") String ruleId) {
        ruleSetRowService.delete(ruleId);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/variables/{ruleSetRowId}")
    public String updateVariables(@PathVariable("ruleSetRowId") String ruleSetRowId, @RequestBody Map<String,String> variableValues) {
        return ruleSetRowService.updateVariables(ruleSetRowId, variableValues);
    }

}
