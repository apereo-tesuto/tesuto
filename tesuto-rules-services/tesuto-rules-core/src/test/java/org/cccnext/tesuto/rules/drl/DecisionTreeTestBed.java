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
package org.cccnext.tesuto.rules.drl;

import static org.cccnext.tesuto.rules.drl.KieSessionTestLoader.loadKieSession;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.RULES_DIRECTORY;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.TEST_FACTS_DIRECTORY;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.getTestFactsSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.rules.validation.PlacementComponentError;
import org.ccctc.common.droolscommon.RulesAction;
import org.kie.api.runtime.StatelessKieSession;

import junit.framework.TestCase;

public abstract class DecisionTreeTestBed extends TestCase {
	
	
	public void testRangeOfFacts(String ruleFile, String testfilename, String headerFooterFilename) throws Exception {

		String rule_set_id = UUID.randomUUID().toString();
		String rule_id = UUID.randomUUID().toString();
		String rule_set_row_id = UUID.randomUUID().toString();
		String facts_csv = FileUtils.readFileToString(new File(TEST_FACTS_DIRECTORY + testfilename + "TestFacts.csv"));

		Pair<List<Map<String, Object>>, List<PlacementComponentActionResult>> factsSet = getTestFactsSet(facts_csv);
		Iterator<PlacementComponentActionResult> expectedResults = factsSet.getRight().iterator();

		String csv = FileUtils.readFileToString(new File(RULES_DIRECTORY + ruleFile + ".csv"));
		String rule = FileUtils.readFileToString(new File(RULES_DIRECTORY + ruleFile + ".drt"));
		String rulesetHeader = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + headerFooterFilename + ".header"));
		String rulesetFooter = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + headerFooterFilename + ".footer"));

		List<PlacementComponentError> errors = new ArrayList<>();
		Integer testNumber = 0;
		
		StatelessKieSession kSession = loadKieSession(rulesetHeader, rule_set_id, rule_set_row_id, rule_id, rule, rulesetFooter, csv);
		//kSession.addEventListener(new DebugAgendaEventListener());
		//kSession.addEventListener(new DebugRuleRuntimeEventListener());
		
		for (Map<String, Object> facts : factsSet.getLeft()) {
			PlacementComponentActionResult expectedResult = expectedResults.next();
			expectedResult.setRuleSetId(rule_set_id);
			expectedResult.setRuleSetRowId(rule_set_row_id);
			expectedResult.setRuleId(rule_id);
			kSession.execute(facts);
			List<RulesAction> actions = (List<RulesAction>) facts.get("actions");

			PlacementComponentError placementError = new PlacementComponentError();
			placementError.setTestNumber(++testNumber);
			if(actions.size() != 1) {
				placementError.setExpectedNumberOfActions(1);
				placementError.setNumberOfActions(actions.size());
				errors.add(placementError);
				continue;
			}
			RulesAction action = actions.get(0);
			Map<String, Object> attirbutes = action.getActionParameters();
			if(!"MULTIPLE_MEASURE_PLACEMENT".equals(action.getActionName())) {
				placementError.setActualActionName(action.getActionName());
				placementError.setExpectActionName("MULTIPLE_MEASURE_PLACEMENT");
				errors.add(placementError);
				continue;
			}
					
			Map<String, Map<String, PlacementComponentActionResult>> placements = (Map<String, Map<String, PlacementComponentActionResult>>) attirbutes
					.get("placements");
			
			if(placements.size() != 1) {
				placementError.setExpectedNumberOfPlacements(1);
				placementError.setNumberOfPlacements(placements.size());
				errors.add(placementError);
				continue;
			}

			Map<String,PlacementComponentActionResult> collegePlacements = placements.get("${miscode}");
			if(collegePlacements == null ||  collegePlacements.size() == 0) {
				placementError.setExpectedNumberOfCollegePlacements(1);
				placementError.setNumberOfCollegePlacements(collegePlacements.size());
				errors.add(placementError);
				continue;
			}
			PlacementComponentActionResult placement = collegePlacements.get("${subject_area}");
			if(!placement.equals(expectedResult)) {
				placementError.setActualPlacements(Arrays.asList(placement));
				placementError.setExpectedPlacements(Arrays.asList(expectedResult));
				errors.add(placementError);
			}
		}
		
		assertTrue(errors.toString() ,errors.size() == 0);
	}

}
