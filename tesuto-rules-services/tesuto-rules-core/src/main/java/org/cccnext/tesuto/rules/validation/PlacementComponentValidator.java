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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.service.PlacementComponentActionService;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.exceptions.InvalidRuleSetResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PlacementComponentValidator {
    
    @Autowired
    List<ErrorHeaders>  errorHeaders;

	public String getEvent() {
		return PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME;
	}
	
	public void validateExpectedResults(List<List<RulesAction>> actionsSet, List<Object> expectedResults, List<Map<String,Object>> facts) {
		int index = 0;
		int testNumber = 0;
		List<PlacementComponentError> errors = new ArrayList<>();
		for (List<RulesAction> actions : actionsSet) {
			PlacementComponentActionResult expectedResult = (PlacementComponentActionResult)expectedResults.get(index++);
			PlacementComponentError placementError = new PlacementComponentError();
			placementError.setTestNumber(++testNumber);
			if (actions.size() != 1) {
				placementError.setExpectedNumberOfActions(1);
				placementError.setNumberOfActions(actions.size());
				errors.add(placementError);
				continue;
			}
			RulesAction action = actions.get(0);
			Map<String, Object> attirbutes = action.getActionParameters();
			if (!PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME.equals(action.getActionName())) {
				placementError.setActualActionName(action.getActionName());
				placementError.setExpectActionName(PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME);
				errors.add(placementError);
				continue;
			}

			Map<String, Map<String, PlacementComponentActionResult>> placements = (Map<String, Map<String, PlacementComponentActionResult>>) attirbutes
					.get("placements");

			if (placements.size() != 1) {
				placementError.setExpectedNumberOfPlacements(1);
				placementError.setNumberOfPlacements(placements != null ? placements.size() : 0);
				errors.add(placementError);
				continue;
			}

			Map<String, PlacementComponentActionResult> collegePlacements = placements.get("test_college_id");
			if (collegePlacements == null || collegePlacements.size() == 0) {
				placementError.setExpectedNumberOfCollegePlacements(1);
				placementError.setNumberOfCollegePlacements(0);
				errors.add(placementError);
				continue;
			}
			PlacementComponentActionResult placement = collegePlacements.get("test_subject_area");
			String fieldErrors = testValidationEquivalance(placement, expectedResult, false);
			if (StringUtils.isNotBlank(fieldErrors)) {
				placementError.setActualPlacements(Arrays.asList(placement));
				placementError.setExpectedPlacements(Arrays.asList(expectedResult));
				placementError.setFieldErrors(fieldErrors);
				errors.add(placementError);
			}
		}
		if(errors.size() != 0) {
		    List<String> factNames = getParameterNames(errors, facts);	
			List<String> ers = errors.stream().map(e -> e.fieldErrors(factNames, 
			      ( (VariableSet)facts.get(e.getTestNumber() - 1).get(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY)).getFacts())).filter(str -> StringUtils.isNotBlank(str)).collect(Collectors.toList());
			if(ers.size() > 0 ) {
			    ers.add(0, StringUtils.join(factNames,",") + ",test numbers\n");
			    ers.add(0, "\n(If different values you will see expected value:: actual value)\nTotal Errors:" + errors.size()  + "\n");
			}
			List<String> actionErs = errors.stream().map(e -> e.actionErrors(factNames, 
                    ( (VariableSet)facts.get(e.getTestNumber() - 1).get(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY)).getFacts()))
                    .filter(str -> StringUtils.isNotBlank(str)).collect(Collectors.toList());
			if(actionErs.size() > 0) {
			    ers.add(ErrorHeaders.actionHeaders(factNames));
			    ers.addAll(actionErs);
			}
			StringBuilder results = new StringBuilder();
			ers.forEach(er -> results.append(er));
			throw new InvalidRuleSetResults("\nUnable to validate placements Total of " + errors.size() + " Errors found", "", Arrays.asList(results.toString()));
		}
	}
	
	List<String>  getParameterNames(List<PlacementComponentError> errors, List<Map<String,Object>> facts) {
	    List<String> factNames = new ArrayList<>();
	  errors.forEach(er -> getFactNames(factNames,  
	          ( (VariableSet)facts.get(er.getTestNumber() - 1).get(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY))));
	  return factNames;
	}

	
	private List<String> getErrorHeaders(String dataSource, String dataType, Collection<Fact> facts) {
	    List<ErrorHeaders> matches = errorHeaders.stream().filter(eh -> eh.getDataSource().equalsIgnoreCase(dataSource) 
	            && eh.getDataType().equalsIgnoreCase(dataType)).collect(Collectors.toList());
	    if(matches.isEmpty()) {
	        return ErrorHeaders.getDefaultParameterNames(facts);
	    }
	    return matches.get(0).getParameterNames();
	}
	
    private void  getFactNames(List<String> names, VariableSet variableSet) {
        List<String> prameterNames = getErrorHeaders(variableSet.getSource(), variableSet.getSourceType(), variableSet.getFacts().values());
        for(String parameterName:prameterNames) {
            if(!names.contains(parameterName)) {
                names.add(parameterName);
            }
        }
    }
	
	String testValidationEquivalance(PlacementComponentActionResult placement, PlacementComponentActionResult expectedPlacement, Boolean allFieldsRequired) {
	    String errors="";
	    for(Field field: placement.getClass().getDeclaredFields()) {
	        field.setAccessible(true);
	       
	        try {
	            Object expected = field.get(expectedPlacement);
	            if(!allFieldsRequired && expected == null) continue;
	            
	            Object actual = field.get(placement);
	            if(field.getName().equals("programs") && !valuesEquivalent(expected,  actual)) {
	                   Set<String> actualPrograms = (Set<String>)actual;
	                   Set<String> expectedPrograms = (Set<String>)expected;
	                   if(actualPrograms.size() == expectedPrograms.size() ) {
	                       errors += field.getName() + " :" +expected + " ::" + actual + ",";
	                       continue;
	                   }
	                   if( !actualPrograms.containsAll(expectedPrograms)){
                           errors += field.getName() + " :" +expected + " ::" + actual + ",";
                           continue;
                       }
	                    if(maxProgram(actualPrograms) > maxProgram(expectedPrograms)){
	                           errors += field.getName() + " :" +expected + " ::" + actual  + ",";
	                           continue;
	                     }
	                  continue;
	            }
	            if(!valuesEquivalent(expected,  actual)) {
	                errors += field.getName() + " :" +expected + " ::" + actual  + ",";
	                continue;
	            }
            } catch (IllegalArgumentException | IllegalAccessException exception) {
               throw new RuntimeException("Unable to use reflection to process equivalancy.", exception);
            }
	    }
	    return  errors;
	}
	
	private boolean valuesEquivalent(Object expected, Object actual) {
        if(expected == actual || (expected != null && expected.equals(actual))) {
            return true;
        }
        return false;
    }
	
	   private boolean valuesEquivalent(String expected, String actual) {
	        if(StringUtils.isBlank(expected) && StringUtils.isBlank(actual) || expected == actual || (expected != null && expected.equals(actual))) {
	            return true;
	        }
	        return false;
	    }
	   
	   private Integer maxProgram(Set<String> programs) {
	       Integer max = 0;
	       for(String program:programs) {
	           Integer programValue = programsMap.get(program);
	           if(programValue != null && programValue > max)
	               max = programValue;
	       }
	       return max;
	   }
	     
	    private static Map<String,Integer> programsMap = new HashMap<String,Integer>() {{
	        put("pre_alg", 1 );
	        put("alg_i",2 );
	        put( "geo",3 ); //Integrated Math 2
	        put("alg_ii",4 );
	        put( "alg_ii",5 ); //Integrated Math 3
	        put( "stat",6 );
	        put("trig" ,7);
	        put("pre_calc",8 );
	        put( "calc",9 );
	    }};
}
