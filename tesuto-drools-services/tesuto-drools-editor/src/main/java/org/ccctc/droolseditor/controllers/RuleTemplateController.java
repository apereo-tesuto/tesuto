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

import org.ccctc.common.droolscommon.model.RuleStatus;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.droolseditor.services.RuleTemplateViewMapper;
import org.ccctc.droolseditor.validation.ValidationDistributor;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ruletemplates")
public class RuleTemplateController {

	 @Autowired
	    private RuleService ruleService;
	 
	 @Autowired
	 private ValidationDistributor validationService;

	 @Autowired
	   private RuleTemplateViewMapper mapper;
	    
	    @RequestMapping(method = RequestMethod.POST, value="/find")
	    public List<RuleTemplateView> find(@RequestBody RuleAttributeFacetSearchForm form)  {	       
	        return mapper.mapTo(ruleService.find(form));
	    }
	    
	    @RequestMapping(method = RequestMethod.GET, value="/{ruleId}")
	    public RuleTemplateView getRuleById(@PathVariable("ruleId") String ruleId) {
	        return mapper.mapTo( ruleService.getRule(ruleId));
	    }
	    
	    @RequestMapping(method = RequestMethod.POST)
	    public RuleTemplateView save(@RequestBody RuleTemplateView ruleTemplateView) {
	        validate(ruleTemplateView);
	        RuleDTO updatedRuleDTO = ruleService.save(mapper.mapFrom( ruleTemplateView));
	        return  mapper.mapTo( updatedRuleDTO);
	    }
	    
	    @RequestMapping(method = RequestMethod.DELETE, value="/{ruleId}")
	    public void deleteRule(@PathVariable("ruleId") String ruleId) {
	        ruleService.delete(ruleId);
	    }
	    
	    @RequestMapping(method = RequestMethod.POST, value="/validate")
	    public DrlValidationResults validateRule(@RequestBody RuleTemplateView ruleTemplateView) {
	    	DrlValidationResults result = validationService.validate(mapper.mapFrom(ruleTemplateView));
	    	return result;
	    }
	    
	    @RequestMapping(method = RequestMethod.POST, value = "/publish")
		public DrlValidationResults publishRuleTemplateView(
				@RequestBody(required = true) RuleTemplateView ruleTemplateView) {
			DrlValidationResults results = validationService.validate(mapper.mapFrom(ruleTemplateView));
			if(results.getIsValid()) {
				ruleService.save(mapper.mapFrom(ruleTemplateView));
			}
			return results;
		}
	    
	    private void validate(RuleTemplateView ruleTemplateView) {
	        if(ruleTemplateView.getStatus().equals(RuleStatus.PUBLISHED)) {
	            DrlValidationResults results = validationService.validate(mapper.mapFrom(ruleTemplateView));
	            if(!results.getIsValid()) {
	                throw new RuntimeException("You are attempting to publish an ruleset that is not valid. Either change status or fix validation.");
	            }
	        }
	    }
}
