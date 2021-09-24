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
package org.ccctc.droolseditor.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsdb.dynamodb.utils.AbstractMapper;
import org.ccctc.common.droolsdb.model.ConditionRowDTO;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class RuleTemplateViewMapper extends AbstractMapper<RuleTemplateView, RuleDTO> {

	@Autowired
	Mapper mapper;

	@Override
	protected RuleTemplateView doMapTo(RuleDTO dto) {
		RuleTemplateView view = mapper.map(dto, RuleTemplateView.class);
		view.setImports(mapToImports(dto.getCustomImports()));
		view.setDefinitions(dto.getConditionFreetext());
		view.setWhenStatement(mapConditionRows(dto.getConditionRows()));
		view.setThenClause(dto.getActionFreetext());
		view.adjustValuesForUI();
		return view;
	}

	@Override
	protected RuleDTO doMapFrom(RuleTemplateView view) {
		view.adjustValuesForStorage();
		RuleDTO dto = mapper.map(view, RuleDTO.class);
		dto.setCustomImports(mapToCustomImports(view.getImports()));
		dto.setConditionFreetext(view.getDefinitions());
		dto.setConditionRows(mapWhenToConditionRows(view.getWhenStatement()));
		dto.setActionFreetext(view.getThenClause());
		return dto;
	}

	private String mapToImports(List<String> customImports) {
		if (CollectionUtils.isEmpty(customImports)) {
			return "";
		}
		StringBuilder imports = new StringBuilder();
		imports.append("import java.util.Map\n");
		imports.append("import java.util.List\n");
		imports.append("import java.util.ArrayList\n");
		imports.append("import net.ccctechcenter.drools.RulesAction\n");
		customImports.forEach(ci -> imports.append("import ").append(ci).append("\n"));
		return imports.toString();
	}

	private List<String> mapToCustomImports(String importStatement) {
		List<String> customImports = new ArrayList<>();
		if(StringUtils.isBlank(importStatement)) {
			return customImports;
		}
		importStatement = importStatement.replaceAll("\\s*import\\s*java.util.Map\\s*\n*", "");
		importStatement = importStatement.replaceAll("\\s*import\\s*java.util.List\\s*\n*", "");
		importStatement = importStatement.replaceAll("\\s*import\\s*java.util.ArrayList\\s*\n*", "");
		importStatement = importStatement.replaceAll("\\s*import\\s*net.ccctechcenter.drools.RulesAction\\s*\n*", "");
		String[] possibleImports = importStatement.split("\n");
		for (String str : possibleImports) {
			if (str.contains("import ")) {
				String trimmedStr = str.trim();
				trimmedStr = trimmedStr.split("\\s*import\\s*")[1].replaceAll("\\s+", " ");
				if (StringUtils.isNotBlank(trimmedStr)) {
					customImports.add(trimmedStr);
				}
			}
		}
		return customImports;
	}

	String mapConditionRows(List<ConditionRowDTO> conditionRows) {
		if (CollectionUtils.isEmpty(conditionRows)) {
			return "";
		}
		StringBuilder conditions = new StringBuilder();
		conditionRows.forEach(ci -> conditions.append(ci.getFreetext()).append("\n"));
		return conditions.toString();
	}
	
	List<ConditionRowDTO> mapWhenToConditionRows(String whenStatement) {
		List<ConditionRowDTO> conditionRows = new ArrayList<ConditionRowDTO>();
		if(StringUtils.isBlank(whenStatement)) {
			return conditionRows;
		}
		String[] conditions = whenStatement.split("\n");
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		for(String condition:conditions) {
			if(StringUtils.isBlank(condition)) {
				continue;
			}
			ConditionRowDTO conditionRowDTO = new ConditionRowDTO();
			conditionRowDTO.setFreetext(condition.trim());
			Matcher matcher = pattern.matcher(condition.trim());
			Set<String> tokensFromFreeText = new HashSet<>();
			if(matcher.find()) {
				tokensFromFreeText.add(matcher.group(1));
			}
			if(CollectionUtils.isNotEmpty(tokensFromFreeText)) {
				conditionRowDTO.setTokensFromFreeText(tokensFromFreeText);
			}
			conditionRows.add(conditionRowDTO);
		}
		return conditionRows;
	}

}
