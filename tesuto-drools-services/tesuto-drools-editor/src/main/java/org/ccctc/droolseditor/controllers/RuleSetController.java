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

import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleStatus;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.ccctc.droolseditor.validation.ValidationDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rulesets")
public class RuleSetController {

    @Autowired
    private IFamilyService collegeService;
    
    @Autowired
    private RuleSetService ruleSetService;

    @Autowired
    private ValidationDistributor validationService;
    
    @RequestMapping(method = RequestMethod.DELETE, value="/{ruleSetId}")
    public void deleteRuleSet(@PathVariable("ruleSetId") String ruleSetId) {
        ruleSetService.delete(ruleSetId);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleSetId}/duplicate")
    public RuleSetDTO duplicateRuleSet(@PathVariable("ruleSetId") String ruleSetId) {
        RuleSetDTO duplicateRuleSetDTO = ruleSetService.duplicate(ruleSetId);
        return duplicateRuleSetDTO;
    }
    
    @RequestMapping(value="/find",method = RequestMethod.GET)
    public List<RuleSetDTO> find(@RequestBody RuleAttributeFacetSearchForm form) {
        List<RuleSetDTO> ruleSets = ruleSetService.find(form);
        return ruleSets;
    }
    
    @RequestMapping(value="/{ruleSetId}", method = RequestMethod.GET)
    public RuleSetDTO get(@PathVariable("ruleSetId") String ruleSetId) {
        RuleSetDTO ruleSetDTO = ruleSetService.getRuleSet(ruleSetId);
        return ruleSetDTO;
    }
    
    @RequestMapping(value="/all", method = RequestMethod.GET)
    public List<RuleSetDTO> getAll() {
        List<RuleSetDTO> ruleSets = ruleSetService.getAllRuleSets();
        return ruleSets;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<RuleSetDTO> getRuleSets(@RequestParam(value="application", required=false) String application,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="cccMisCode", required=false) String cccMisCode) {
        List<RuleSetDTO> ruleSets = ruleSetService.getRuleSetsByParameters(application, status, cccMisCode);
        return ruleSets;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/publish")
    public DrlValidationResults publishRuleSet(
            @RequestBody(required = true) RuleSetDTO ruleSetDTO) {
        DrlValidationResults results = validationService.validate(ruleSetDTO);
        if(results.getIsValid()) {
            ruleSetService.save(ruleSetDTO);
            collegeService.updateTimestamp(ruleSetDTO.getFamily());
        }
        return results;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public RuleSetDTO save(@RequestBody RuleSetDTO ruleSetDTO) {
        validate(ruleSetDTO);
        RuleSetDTO updatedRuleSetDTO = ruleSetService.save(ruleSetDTO);
        return updatedRuleSetDTO;
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="/{ruleSetId}")
    public RuleSetDTO updateRuleSet(@RequestBody RuleSetDTO ruleSetDTO,
            @PathVariable("ruleSetId") String ruleSetId, @PathVariable("doPublish") boolean doPublish) {
        ruleSetDTO.setId(ruleSetId);
        validate(ruleSetDTO);
        RuleSetDTO updatedRuleDTO = ruleSetService.save(ruleSetDTO);
        if (doPublish) {
            collegeService.updateTimestamp(ruleSetDTO.getFamily());
        }
        return updatedRuleDTO;
    }
    
    
    private void validate(RuleSetDTO ruleSetDTO) {
        if(ruleSetDTO.getStatus().equals(RuleStatus.PUBLISHED)) {
            DrlValidationResults results = validationService.validate(ruleSetDTO);
            if(!results.getIsValid()) {
                throw new RuntimeException("You are attempting to publish an ruleset that is not valid. Either change status or fix validation.");
            }
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/validate")
    public DrlValidationResults validateRuleSet(@RequestBody RuleSetDTO  ruleSetDTO) {
        DrlValidationResults result = validationService.validate(ruleSetDTO);
        return result;
    }
    

}
