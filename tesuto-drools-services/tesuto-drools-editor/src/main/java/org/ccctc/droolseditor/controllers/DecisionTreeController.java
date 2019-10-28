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

import java.io.Serializable;
import java.util.List;

import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleStatus;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.droolseditor.services.DecisionTreeViewMapper;
import org.ccctc.droolseditor.validation.ValidationDistributor;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = DecisionTreeController.REQUEST_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
public class DecisionTreeController implements Serializable {

	private static final long serialVersionUID = 1L;

	static final String REQUEST_ROOT = "/service/v1/rules/decisiontree";

	@Autowired
	private RuleSetRowService service;

	@Autowired
	private ValidationDistributor validationService;

	@Autowired
	private DrlService builderService;

	@Autowired
	private DecisionTreeViewMapper assembler;

	@RequestMapping(method = RequestMethod.GET, value = "/{decisiontreeid}")
	public DecisionTreeView getDecisionTree(@PathVariable String decisiontreeid) {
		return assembler.mapTo(service.getRuleSetRow(decisiontreeid));
	}

	public List<DecisionTreeView> getDecisionTreesByRuleId(@PathVariable String ruleId) {
		return assembler.mapTo(service.getRuleSetRowsByRuleId(ruleId));
	}

	@RequestMapping(method = RequestMethod.POST)
	public List<DecisionTreeView> getDeveloperDecisionTrees(
			@RequestBody(required = true) RuleAttributeFacetSearchForm form) {
		return assembler.mapTo(service.find(form));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upsert")
	public DecisionTreeView upsertDecisionTree(@RequestBody(required = true) DecisionTreeView decisionTreeView) {
	    validate(decisionTreeView);
	    return assembler.mapTo(service.save(assembler.mapFrom(decisionTreeView)));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/validate")
	public DrlValidationResults validateDeveloperDecisionTree(
			@RequestBody(required = true) DecisionTreeView decisionTreeView) {
		return validationService.validate(assembler.mapFrom(decisionTreeView));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/drl")
	public String getDrl(@RequestBody(required = true) DecisionTreeView decisionTreeView) {
		RuleSetRowDTO dto = assembler.mapFrom(decisionTreeView);
		StringBuffer drl = new StringBuffer();
		drl.append(builderService.buildPackage());
		drl.append(builderService.generateDRL(null, dto));
		return drl.toString();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/publish")
	public DrlValidationResults publishDecisionTree(
			@RequestBody(required = true) DecisionTreeView decisionTreeView) {
		DrlValidationResults results = validationService.validate(assembler.mapFrom(decisionTreeView));
		if(results.getIsValid()) {
			service.save(assembler.mapFrom(decisionTreeView));
		}
		return results;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/find")
	public List<DecisionTreeView> find(@RequestBody(required = true) RuleAttributeFacetSearchForm form) {
		return assembler.mapTo(service.find(form));
	}

	public void deleteDecisionTree(String ruleId) {
		service.delete(ruleId);
	}
	
    private void validate(DecisionTreeView decisionTreeView) {
        if(decisionTreeView.getStatus().equals(RuleStatus.PUBLISHED)) {
            DrlValidationResults results = validationService.validate(assembler.mapFrom(decisionTreeView));
            if(!results.getIsValid()) {
                throw new RuntimeException("You are attempting to publish an ruleset that is not valid. Either change status or fix validation.");
            }
        }
    }
}
