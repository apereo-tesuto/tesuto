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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;
    
    @RequestMapping(method = RequestMethod.GET)
    public List<RuleDTO> getRules(@RequestParam(value="application", required=false) String application,
            @RequestParam(value="status", required=false) String status) {
        List<RuleDTO> rules = new ArrayList<RuleDTO>();
        if (!StringUtils.isBlank(application)) {
            if (!StringUtils.isBlank(status)) {
                rules = ruleService.getRulesByEngineAndStatus(application, status);
            } else {
                rules = ruleService.getRulesByEngine(application);
            }
        } else {
            if (!StringUtils.isBlank(status)) {
                rules = ruleService.getRulesByStatus(status);
            } else {
                rules = ruleService.getAllRules();                
            }
        }
        return rules;
    }
    
    @RequestMapping(method = RequestMethod.GET, value="/{ruleId}")
    public RuleDTO getRuleById(@PathVariable("ruleId") String ruleId) {
        RuleDTO rule = ruleService.getRule(ruleId);
        return rule;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public RuleDTO addRule(@RequestBody RuleDTO ruleDTO) {
        ruleDTO.setId("");
        RuleDTO updatedRuleDTO = ruleService.save(ruleDTO);
        return updatedRuleDTO;
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleId}")
    public RuleDTO updateRule(@RequestBody RuleDTO ruleDTO,
            @PathVariable("ruleId") String ruleId) {
        ruleDTO.setId(ruleId);
        RuleDTO updatedRuleDTO = ruleService.save(ruleDTO);
        return updatedRuleDTO;
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleId}/duplicate")
    public RuleDTO duplicateRule(@PathVariable("ruleId") String ruleId) {
        RuleDTO duplicateRuleDTO = ruleService.duplicate(ruleId);
        return duplicateRuleDTO;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value="/{ruleId}")
    public void deleteRule(@PathVariable("ruleId") String ruleId) {
        ruleService.delete(ruleId);
    }
    
}
