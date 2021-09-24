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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolsdb.dynamodb.utils.AbstractMapper;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class DecisionTreeViewMapper extends AbstractMapper<DecisionTreeView, RuleSetRowDTO> {

	@Autowired 
	Mapper mapper;
	
	@Override
	protected DecisionTreeView doMapTo(RuleSetRowDTO ruleSetRow) {
		DecisionTreeView treeView = mapper.map(ruleSetRow, DecisionTreeView.class);
		treeView.setTokenValuesAsCsv(generateCSV(ruleSetRow.getVariableRows()));
		treeView.adjustValuesForUI();
		return treeView;
	}

	@Override
	protected RuleSetRowDTO doMapFrom(DecisionTreeView view) {
		view.adjustValuesForStorage();
		RuleSetRowDTO ruleSetRowDTO = mapper.map(view, RuleSetRowDTO.class);
		ruleSetRowDTO.setVariableRows( generateVariableMaps( view.getTokenValuesAsCsv()));
		return ruleSetRowDTO;
	}
	
	private String generateCSV(List<RuleVariableRowDTO> variables) {
		List<String> names = new ArrayList<>();
		List<Map<String,String>> variableRowMaps = new ArrayList<>();
		for(RuleVariableRowDTO variableRow: variables) {
			Map<String,String> variableRowMap = new HashMap<>();
			variableRowMaps.add(variableRowMap);
			for(RuleVariableDTO variable: variableRow.getVariables()){
				if(!names.contains(variable.getName())) {
					names.add(variable.getName());
				}
				variableRowMap.put(variable.getName(), variable.getValue());
			}
		}
		
		return generateCSV(variableRowMaps, names);
	}
	
	private String generateCSV(List<Map<String,String>>variableRowMaps, List<String> names ) {
		StringBuilder csv = new StringBuilder();
		String separator = "";
		for(String name:names) {
			 csv.append(separator).append(name);
			 separator = ",";
		}
		csv.append("\n");
		
		for(Map<String,String> variableRowMap:variableRowMaps) {
			 separator = "";
			for(String name:names) {
				if(variableRowMap.containsKey(name)) {
					csv.append(separator).append(variableRowMap.get(name));
					separator = ",";
				}else {
					separator = ",";
					csv.append(separator);
				}
			}
			csv.append("\n");
		}
			return csv.toString();
	}
	
	public List<RuleVariableRowDTO> generateVariableMaps(String csv) {
		List<RuleVariableRowDTO> ruleVariableRows = new ArrayList<>();
		if(StringUtils.isBlank(csv)) {
			return ruleVariableRows;
		}
		Reader in = new StringReader(csv);
		try {
			CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
			Iterator<CSVRecord> rows = parser.getRecords().iterator();
			CSVRecord headersRecord = rows.next();

			while (rows.hasNext()) {
				Iterator<String> values = rows.next().iterator();
				Iterator<String> headers = headersRecord.iterator();
				RuleVariableRowDTO variableRow = new RuleVariableRowDTO();
				ruleVariableRows.add(variableRow);
				while(headers.hasNext()) {
					String name = headers.next();
					String value = values.next();
					if(StringUtils.isNotBlank(value)){
						if(name.endsWith("ruleName")) {
							variableRow.setName(name);
							continue;
						}
						RuleVariableDTO variable = new RuleVariableDTO();
						variable.setName(name);
						variable.setValue(value);
						variableRow.getVariables().add(variable);						
					}
				}
			}
			parser.close();
		} catch (IOException exception) {
			throw new RuntimeException("Unable to parse variable maps", exception);
		}
		return ruleVariableRows;
	}
	


}
