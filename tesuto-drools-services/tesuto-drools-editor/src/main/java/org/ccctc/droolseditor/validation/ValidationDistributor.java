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
package org.ccctc.droolseditor.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolscommon.validation.IDrlValidator;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.common.droolsdb.services.DrlSyntaxValidator;
import org.ccctc.droolseditor.services.DecisionTreeViewMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidationDistributor implements InitializingBean {

	private Map<String, IDrlValidator> validatorMap = new HashMap<>();

	@Autowired
	private DrlService builderService;

	@Autowired
	private DrlSyntaxValidator syntaxValidator;

	@Autowired
	private DecisionTreeViewMapper treeMapper;

	@Autowired(required = false)
	private List<IDrlValidator> validators;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (CollectionUtils.isNotEmpty(validators)) {
			validators.stream().forEach(fp -> validatorMap.put(fp.getEvent(), fp));
		}
	}
	
	public DrlValidationResults validate(RuleSetDTO ruleSetDTO) {
	    if(StringUtils.isBlank(ruleSetDTO.getRuleSetDrl())) {
	        DrlValidationResults results = new DrlValidationResults();
	        results.setIsValid(true);
	        results.setDrl("Nothing to validate because the Rule Set DRL is blank.");
	        return results;
	    }
	    
	    if(StringUtils.isBlank(ruleSetDTO.getRuleSetDrlValidationCsv())) {
            DrlValidationResults results = new DrlValidationResults();
            results.setIsValid(false);
            results.setDrl("CSV Validation is blank and if drl is not blank it must be able to be validated.");
            return results;
        }
        String drl = ruleSetDTO.getRuleSetDrl().replaceAll("\\$\\{rule_set_id\\}", ruleSetDTO.getId());
        String validationCSV = ruleSetDTO.getRuleSetDrlValidationCsv().replaceAll("\\$\\{rule_set_id\\}", ruleSetDTO.getId());
        if (validatorMap.containsKey(ruleSetDTO.getEvent())) {
            IDrlValidator validator = validatorMap.get(ruleSetDTO.getEvent());
            return validator.validate(drl, validationCSV);
        } else {
            return syntaxValidator.validate(drl);
        }
    }

	public DrlValidationResults validate(RuleSetRowDTO ruleSetRowDTO) {
		String drl = buildDrl(ruleSetRowDTO);
		String validationCSV = updateIds(ruleSetRowDTO);
		if (validatorMap.containsKey(ruleSetRowDTO.getEvent())) {
			IDrlValidator validator = validatorMap.get(ruleSetRowDTO.getEvent());
			return validator.validate(drl, validationCSV);
		} else {
			return syntaxValidator.validate(drl);
		}
	}

	public DrlValidationResults validate(RuleDTO ruleDTO) {
		String drl = buildDrl(ruleDTO);
		if (validatorMap.containsKey(ruleDTO.getEvent())) {
			IDrlValidator validator = validatorMap.get(ruleDTO.getEvent());
			return validator.validate(drl);
		} else {
			return syntaxValidator.validate(drl);
		}
	}

	public DrlValidationResults validate(String drl) {
		return syntaxValidator.validate(drl);
	}

	private String updateIds(RuleSetRowDTO dto) {
		if(StringUtils.isEmpty(dto.getValidationCsv())) {
			return "";
		}
		String validationCSV = dto.getValidationCsv().replaceAll("\\$\\{rule_set_row_id\\}", dto.getId());
		return validationCSV.replaceAll("\\$\\{rule_id\\}", dto.getRuleId());
	}

	String buildDrl(RuleSetRowDTO ruleSetRowDTO) {
		StringBuffer drl = new StringBuffer();
		drl.append(builderService.buildPackage());
		drl.append(builderService.generateDRL(null, ruleSetRowDTO));
		return drl.toString();
	}

	String buildDrl(RuleDTO ruleDTO) {
		StringBuffer drl = new StringBuffer();
		drl.append(builderService.buildPackage());
		List<RuleVariableRowDTO> variableRows = treeMapper.generateVariableMaps(ruleDTO.getTestTokenValues());
		drl.append(builderService.generateDrl(ruleDTO, variableRows));
		return drl.toString();
	}
}
