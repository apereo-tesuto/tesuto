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
package org.cccnext.tesuto.rules.validation;

import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.MATH_RANKING_KEY;
import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.addMathRanking;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.service.FactMapper;
import org.cccnext.tesuto.rules.view.BasicStudentView;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.exceptions.InvalidRuleSetResults;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.services.DrlSyntaxValidator;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlacementComponentDrlValidationService extends AbstractDrlValidationService {

	@Autowired
	private PlacementComponentFactsParser placementFactsParser;

	@Autowired
	private PlacementComponentValidator placementComponentValidator;

	@Autowired
	private DrlSyntaxValidator syntaxValidator;
	
    @Autowired
    List<FactMapper> factMappers;

	
	public DrlValidationResults validate(DrlValidationData data) {
		DrlValidationResults results = syntaxValidator.validate(data.getDrl());
		
		if(!results.getIsValid() || !data.getCsvValidationRequired()) {
			return results;
		}
		
		 results.setIsValid(false);
		 Pair<List<Map<String,Object>>, List<Object>> factsSetAndResults =  Pair.of(new ArrayList<Map<String,Object>>(), new ArrayList<>());
		 try {
			 factsSetAndResults = placementFactsParser.getTestFactsExpectedResults(data.getValidationCsv());
		 } catch (Exception exception) {
			 Map<String,Object> facts = new HashMap<>();
			 facts.put(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY, new VariableSet());
			 facts.put(PlacementMapKeys.STUDENT_VIEW_KEY, new BasicStudentView());
			 facts.put("actions", new ArrayList<RulesAction>());
			 factsSetAndResults.getLeft().add(facts);
			 results.setExceptionMessage("Validation facts could not be parsed because" + exception.getMessage() + "\n");
			 results.setExceptionTrace(exceptionStacktraceToString(exception));
			 results.setIsValid(false);
			 return results;
		 }
		List<List<RulesAction>> actions = new ArrayList<>();
		 StatelessKieSession kSession = loadKieSession(data.getDrl());
		 try {
		 for(Map<String,Object> facts:factsSetAndResults.getLeft()) {
		     updateVariableSetFacts(facts);
			 kSession.execute(facts);
			 actions.add((List<RulesAction>) facts.get("actions"));
		 }
		 } catch(Exception exception) {
			 results.setIsValid(false);
			 results.setExceptionMessage("Failed on execute \n");
			 results.setExceptionTrace(exceptionStacktraceToString(exception));
			 return results;
		 }
		 try {
			 if(!factsSetAndResults.getRight().isEmpty()) {
				 placementComponentValidator.validateExpectedResults(actions, factsSetAndResults.getRight(), factsSetAndResults.getLeft());
				 results.setIsValid(true);
			 }
		 } catch(InvalidRuleSetResults exception) {
			 results.setExceptionMessage(exception.getMessage());
			 results.getErrors().addAll(exception.getErrors());
			 results.setExceptionTrace(exceptionStacktraceToString(exception));
		 } catch(Exception exception) {
			 results.setExceptionMessage(exception.getMessage());
			 results.setExceptionTrace(exceptionStacktraceToString(exception));
		 }
		 return results;
	}
	
	private void updateVariableSetFacts(Map<String, Object> facts) {
	    VariableSet vs = (VariableSet)facts.get(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY);
	    if(vs == null) {
	        return;
	    }
	    List<FactMapper> filteredFactMappers = factMappers.stream().filter( fm -> fm.getSource().equals(vs.getSource())).collect(Collectors.toList());
        
        if(filteredFactMappers.size() == 1){
            FactMapper factMapper = filteredFactMappers.get(0);
            vs.getFacts().putAll(factMapper.mapFacts(vs.getFacts()));
        }
        if(!vs.getFacts().containsKey(MATH_RANKING_KEY)) {
            addMathRanking(vs.getFacts());
        }
	}

	private List<RulesAction> executeRules(KieContainer kContainer, Map<String, Object> facts) {
		StatelessKieSession kSession = kContainer.newStatelessKieSession();
		List<Command<?>> cmds = new ArrayList<Command<?>>();
		cmds.add(CommandFactory.newInsert(facts, "myfacts"));
		cmds.add(CommandFactory.newFireAllRules());
		cmds.add(CommandFactory.newQuery("actions", "actions"));
		ExecutionResults results = kSession.execute(CommandFactory.newBatchExecution(cmds));

		List<RulesAction> actions = new ArrayList<RulesAction>();
		QueryResults queryResults = (QueryResults) results.getValue("actions");
		for (QueryResultsRow row : queryResults) {
			Object obj = row.get("rulesActions");
			if (obj instanceof RulesAction) {
				RulesAction action = (RulesAction) obj;
				actions.add(action);
			} else {
				fail("[actions] query returned a value that was not a RulesAction");
			}
		}
		return actions;
	}
}
