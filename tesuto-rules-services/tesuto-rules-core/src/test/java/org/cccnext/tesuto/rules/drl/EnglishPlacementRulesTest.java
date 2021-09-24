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
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.getTestFacts;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.initFacts;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.ccctc.common.droolscommon.RulesAction;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.StatelessKieSession;


public class EnglishPlacementRulesTest extends DecisionTreeTestBed {

	private static final String TEST_FILE_NAME = "EnglishPlacement";

	private static final String HEADER_FOOTER_FILE_NAME = "MultipleMeasurePlacement";

	@Before
	public void setUp() {

	}

	@Test
	public void testRangeOfFacts() throws Exception {
		testRangeOfFacts( TEST_FILE_NAME, TEST_FILE_NAME,  HEADER_FOOTER_FILE_NAME);
	}

	@Test
	public void testUnquotedFacts() throws Exception {
		Map<String, Object> facts = getTestFacts(
				"cst,290,grade_level,11,gpa_cum,3.25,ap_any,true,english,3.5,english_ap,3.5,"
						+ "pre_alg,,pre_alg_ap,,alg_i,3,alg_i_ap,,alg_ii,2.5,geo,3,geo_ap,,trig_ap,,pre_calc,,calc,,calc_ap,,stat,,stat_ap,");

		String csv = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".csv"));
		String rule = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".drt"));
		String rulesetHeader = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".header"));
		String rulesetFooter = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".footer"));

		StatelessKieSession kSession = loadKieSession(rulesetHeader, rule, rulesetFooter, csv);

		//kSession.addEventListener(new DebugAgendaEventListener());
		//kSession.addEventListener(new DebugRuleRuntimeEventListener());
		kSession.execute(facts);
		List<RulesAction> actions = (List<RulesAction>) facts.get("actions");

		assertEquals(1, actions.size());
		RulesAction action = actions.get(0);
		Map<String, Object> attirbutes = action.getActionParameters();
		assertEquals("Action should equal MULTIPLE_MEASURE_PLACEMENT", "MULTIPLE_MEASURE_PLACEMENT",
				action.getActionName());
		Map<String, Map<String, PlacementComponentActionResult>> placements = (Map<String, Map<String, PlacementComponentActionResult>>) attirbutes
				.get("placements");

		PlacementComponentActionResult placement = placements.get("${miscode}").get("${subject_area}");
		assertEquals("Action should equal transfer level", (Integer) 0, placement.getLevelsBelowTransfer());
	}

	@Test
	public void testPlacementGenerated() throws Exception {
		Map<String, Object> facts = getTestFacts(
				"\"cst\",\"290\",\"grade_level\",\"11\",\"gpa_cum\",\"3.25\",\"ap_any\",\"true\",\"english\",\"3.5\",\"english_ap\",\"3.5\","
						+ "\"pre_alg\",\"\",\"pre_alg_ap\",\"\",\"alg_i\",\"3\",\"alg_i_ap\",\"\",\"alg_ii\",\"2.5\",\"geo\",\"3\","
						+ "\"geo_ap\",\"\",\"trig_ap\",\"\",\"pre_calc\",\"\",\"calc\",\"\",\"calc_ap\",\"\",\"stat\",\"\",\"stat_ap\",\"\"");

		String csv = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".csv"));
		String rule = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".drt"));
		String rulesetHeader = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".header"));
		String rulesetFooter = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".footer"));

		StatelessKieSession kSession = loadKieSession(rulesetHeader, rule, rulesetFooter, csv);

		//kSession.addEventListener(new DebugAgendaEventListener());
		//kSession.addEventListener(new DebugRuleRuntimeEventListener());
		kSession.execute(facts);
		List<RulesAction> actions = (List<RulesAction>) facts.get("actions");

		assertEquals(1, actions.size());
		RulesAction action = actions.get(0);
		Map<String, Object> attirbutes = action.getActionParameters();
		assertEquals("Action should equal MULTIPLE_MEASURE_PLACEMENT", "MULTIPLE_MEASURE_PLACEMENT",
				action.getActionName());
		Map<String, Map<String, PlacementComponentActionResult>> placements = (Map<String, Map<String, PlacementComponentActionResult>>) attirbutes
				.get("placements");

		PlacementComponentActionResult placement = placements.get("${miscode}").get("${subject_area}");
		assertEquals("Action should equal transfer level", (Integer) 0, placement.getLevelsBelowTransfer());
	}

	@Test
	public void testNoMatches() throws Exception {
		Map<String, Object> facts = initFacts();

		String csv = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".csv"));
		String rule = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".drt"));
		String rulesetHeader = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".header"));
		String rulesetFooter = FileUtils
				.readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".footer"));

		StatelessKieSession kSession = loadKieSession(rulesetHeader, rule, rulesetFooter, csv);

		kSession.execute(facts);

		List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
		assertEquals(0, actions.size());
	}
	


}
