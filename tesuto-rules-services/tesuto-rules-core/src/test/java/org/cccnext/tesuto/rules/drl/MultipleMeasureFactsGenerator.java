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

import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.addMathRanking;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.view.BasicStudentView;
import org.ccctc.common.droolscommon.RulesAction;

public class MultipleMeasureFactsGenerator {

	public static final String RULES_DIRECTORY = "src/test/resources/org/cccnext/tesuto/rules/";
	public static final String TEST_FACTS_DIRECTORY = "src/test/resources/org/cccnext/tesuto/facts/";

	public static Pair<List<Map<String, Object>>, List<PlacementComponentActionResult>> getTestFactsSet(String mmfacts) throws IOException {
		Reader in = new StringReader(mmfacts);
		CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
		Iterator<CSVRecord> rows = parser.getRecords().iterator();
		CSVRecord columnType = rows.next();
		CSVRecord headersRecord = rows.next();
		List<Map<String, Object>> factsSet = new ArrayList<>();
		List<PlacementComponentActionResult> placements = new ArrayList<>();
		while (rows.hasNext()) {
			Map<String, Object> facts = initFacts();
			addStudent(facts);
			placements.add(addVariableSet(facts, columnType.iterator(), headersRecord.iterator(), rows.next()));
			factsSet.add(facts);
		}
		parser.close();
		return Pair.of(factsSet, placements);
	}

	public static Map<String, Object> getTestFacts(String mmfacts) throws IOException {
		Map<String, Object> facts = initFacts();
		addStudent(facts);
		addVariableSet(facts, mmfacts);
		return facts;
	}

	public static Map<String, Object> initFacts() {
		Map<String, Object> facts = new HashMap<String, Object>();
		facts.put("actions", new ArrayList<RulesAction>());
		return facts;
	}

	private static void addVariableSet(Map<String, Object> facts, String mmfacts) throws IOException {
		VariableSet variableSet = new VariableSet();
		variableSet.setId("multipleMeasureVariableSetId");
		variableSet.setFacts(generateFactMap(mmfacts));
		facts.put("MULTIPLE_MEASURE_VARIABLE_SET", variableSet);
	}
	
	public static List<PlacementComponentActionResult> generateResults(String mmfacts) throws IOException {
		Reader in = new StringReader(mmfacts);
		CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
		Iterator<CSVRecord> iterator = parser.getRecords().iterator();
		CSVRecord headers = iterator.next();
		List<PlacementComponentActionResult>  results = new ArrayList<>();
		while (iterator.hasNext()) {
			Iterator<String> values = iterator.next().iterator();
			while (values.hasNext()) {
				results.add(createPlacement(values));
			}
		}
		parser.close();
		return results;
	}
	
	private static PlacementComponentActionResult createPlacement(Iterator<String> values) {
		PlacementComponentActionResult placement = new PlacementComponentActionResult();
		placement.setCccid(values.next());
		placement.setCollegeId(values.next());
		placement.setCompetencyMapDiscipline(values.next());
		placement.setSubjectArea(values.next());
		String programs = values.next();
		if(StringUtils.isNotBlank(programs)) {
			placement.addPrograms(new HashSet(Arrays.asList(programs.split(":"))));
		}
		
		placement.setTransferLevel(values.next());
		placement.setLevelsBelowTransfer(Integer.parseInt(values.next()));
		
		placement.setRuleSetId(values.next());
		placement.setMultipleMeasureVariableSetId("multipleMeasureVariableSetId");
		return placement;
	}
	
	private static PlacementComponentActionResult createPlacement(Iterator<String> headers, Iterator<String> values) {
		PlacementComponentActionResult placement = new PlacementComponentActionResult();
		placement.setMultipleMeasureVariableSetId("multipleMeasureVariableSetId");
		while(headers.hasNext()) {
			switch(headers.next()) {
				case "cccid":
					placement.setCccid(values.next());
				break;
				case "miscode":
					placement.setCollegeId(values.next());
				break;
				case "competencymapdiscipline":
					placement.setCompetencyMapDiscipline(values.next());
				break;
				case "subjectArea":
					placement.setSubjectArea(values.next());
				break;
				case "program":
					String programs = values.next();
					if(StringUtils.isNotBlank(programs)) {
						placement.addPrograms(new HashSet(Arrays.asList(programs.split(":"))));
					}
				break;
				case "transferLevel":
					placement.setTransferLevel(values.next());
				break;
				case "transferLevelIndex":
					placement.setLevelsBelowTransfer(Integer.parseInt(values.next()));
				break;
				case "levelsBelowTransfer":
                    placement.setLevelsBelowTransfer(Integer.parseInt(values.next()));
                break;
				case "rowNumber":
					placement.setRowNumber(values.next());
				break;
				case "ruleId":
					placement.setRuleId(values.next());
				break;
				case "ruleSetId":
					placement.setRuleSetId(values.next());
				break;
				case "ruleSetRowId":
					placement.setRuleSetRowId(values.next());
				break;
				case "multipleMeasureVariableSetId":
					placement.setMultipleMeasureVariableSetId(values.next());
				break;
				default:
					values.next();
				break;
			}
		}
		return placement;
	}

	private static PlacementComponentActionResult addVariableSet(Map<String, Object> facts,  Iterator<String> columnType,  Iterator<String> headers, CSVRecord mmfacts)
			throws IOException {
		VariableSet variableSet = new VariableSet();
		variableSet.setId("multipleMeasureVariableSetId");
		Pair<Map<String,Fact>, PlacementComponentActionResult> pair = generateFactMap(columnType, headers, mmfacts);
		variableSet.setFacts(pair.getLeft());
		facts.put("MULTIPLE_MEASURE_VARIABLE_SET", variableSet);
		return pair.getRight();
	}
	
	public static Map<String, Fact> generateFactMap(String mmfacts) throws IOException {
		Reader in = new StringReader(mmfacts);
		CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
		Iterator<CSVRecord> iterator = parser.getRecords().iterator();
		Map<String, Fact> mmfactsMap = new HashMap<String, Fact>();
		while (iterator.hasNext()) {
			Iterator<String> values = iterator.next().iterator();
			while (values.hasNext()) {
				addFact(mmfactsMap, values.next(), values.next());
			}
		}
		parser.close();
		addMathRanking(mmfactsMap);
		return mmfactsMap;
	}

	public static Pair<Map<String, Fact>, PlacementComponentActionResult> generateFactMap(Iterator<String> columnType, Iterator<String> headers, CSVRecord mmfacts)
			throws IOException {

		Map<String, Fact> mmfactsMap = new HashMap<String, Fact>();
			Iterator<String> values = mmfacts.iterator();
			while (values.hasNext()) {
				if(columnType.next().equals("fact")) {
					addFact(mmfactsMap, headers.next(), values.next());
				} else {
					PlacementComponentActionResult placement = createPlacement(headers, values);
					addMathRanking(mmfactsMap);
					return Pair.of(mmfactsMap, placement) ;
				}
			}
		return null;
	}

	private static Map<String, Fact> addFact(Map<String, Fact> facts, String name, String value) {
		Fact fact = new Fact();
		fact.setSource("AccessTest");
		fact.setSourceDate(new Date());
		fact.setSourceType("TEST");
		fact.setName(name);
		fact.setValue(value);
		if (facts == null) {
			facts = new HashMap<String, Fact>();
		}
		facts.put(name, fact);
		return facts;
	}

	private static void addStudent(Map<String, Object> facts) {
		facts.put("cccid", "A123456");
		BasicStudentView student = new BasicStudentView();
		student.setCccid("A123456");
		Map<String, Integer> collegeStatuses = new HashMap<>();
		student.setCollegeStatuses(collegeStatuses);
		collegeStatuses.put("${miscode}", 1);
		facts.put("STUDENT_VIEW", student);
	}
}
