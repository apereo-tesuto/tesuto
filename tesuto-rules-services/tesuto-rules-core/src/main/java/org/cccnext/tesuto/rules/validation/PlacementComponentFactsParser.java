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

import static org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys.CCCID_KEY;
import static org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys.MISCODE_KEY;
import static org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys.RULE_SET_ID_KEY;
import static org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys.SUBJECT_AREA_KEY;
import static org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys.TRACKING_ID_KEY;
import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.addMathRanking;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.service.PlacementComponentActionService;
import org.cccnext.tesuto.rules.view.BasicStudentView;
import org.ccctc.common.droolscommon.RulesAction;
import org.springframework.stereotype.Service;

@Service
public class PlacementComponentFactsParser {

    public static String SUBJECT_AREA_TEST_VALUE = "test_subject_area";
    public static String CCC_MIS_CODE_TEST_VALUE = "test_college_id";
    public static String TRACKING_ID_TEST_VALUE = "test_tracking_id";
    public static String RULE_SET_ID_TEST_VALUE = "${rule_set_id}";
    public static String DATA_SOURCE_TEST_VALUE = "validation_source";
    public static String DATA_SOURCE_TYPE_TEST_VALUE = "validation_type";
    public static String DATA_SOURCE_DATE_TEST_VALUE = "2000-01-01";
    private static String COLUMN_TYPE_FACT_KEY = "fact";
    private static String COLUMN_TYPE_RESULT_KEY = "result";
    private static String COLUMN_TYPE_ATTRIBUTE_KEY = "attribute";
    private static String DATA_SOURCE_KEY = "DATA_SOURCE";
    private static String DATA_SOURCE_TYPE_KEY = "DATA_SOURCE_TYPE";
    private static String DATA_SOURCE_DATE_KEY = "DATA_SOURCE_DATE";
    private static String CCCID_TEST_VALUE = "A123456";
    private static String HIGHEST_READING_LEVEL_COURSE_TEST="0";

    public String getEvent() {
        return PlacementComponentActionService.MULTIPLE_MEASURE_PLACEMENT_ACTION_NAME;
    }

    public Pair<List<Map<String, Object>>, List<Object>> getTestFactsExpectedResults(String mmfacts) {
        if (StringUtils.isBlank(mmfacts)) {
            throw new RuntimeException("Validation CSV string was empty, required to perform required validation");
        }
        Reader in = new StringReader(mmfacts);
        List<Map<String, Object>> factsSet = new ArrayList<>();
        List<Object> placements = new ArrayList<>();
        try {
            CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
            Iterator<CSVRecord> rows = parser.getRecords().iterator();
            CSVRecord columnType = rows.next();
            CSVRecord headersRecord = rows.next();

            while (rows.hasNext()) {
                Map<String, Map<String, String>> headersByColumnType = getPropertiesByColumnType(columnType.iterator(),
                        headersRecord.iterator(), rows.next().iterator());
                Map<String, Object> facts = initFacts(headersByColumnType.get(COLUMN_TYPE_FACT_KEY));
                addStudent(facts, headersByColumnType.get(COLUMN_TYPE_FACT_KEY));
                addAttributes(facts,headersByColumnType.get(COLUMN_TYPE_ATTRIBUTE_KEY));
                addVariableSet(facts, headersByColumnType.get(COLUMN_TYPE_FACT_KEY));
                placements.add(getPlacementFromProperty(headersByColumnType.get(COLUMN_TYPE_RESULT_KEY)));
                factsSet.add(facts);
            }
            parser.close();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to parse facts for testing", exception);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to parse facts for testing", exception);
        }

        if (factsSet.isEmpty() && placements.isEmpty()) {
            throw new RuntimeException("No fact sets and no placements results were parsed. ");
        }

        if (factsSet.size() != placements.size()) {
            throw new RuntimeException(String.format(
                    "Number of fact sets and number placements results were different so validation will not be true.  "
                            + "Number of Factsets:%d vs Number of Placements: %d", factsSet.size(), placements.size()));
        }
        return Pair.of(factsSet, placements);
    }

    Map<String, Map<String, String>> getPropertiesByColumnType(Iterator<String> columnTypes, Iterator<String> names,
            Iterator<String> facts) {
        Map<String, Map<String, String>> propertiesByColumnType = new HashMap<String, Map<String, String>>();
        while (columnTypes.hasNext()) {
            String columnType = columnTypes.next();
            if (!propertiesByColumnType.containsKey(columnType)) {
                propertiesByColumnType.put(columnType, new HashMap<String, String>());
            }
            if (names.hasNext() && facts.hasNext()) {
                propertiesByColumnType.get(columnType).put(names.next(), facts.next());
            } else {
                throw new RuntimeException(
                        "Unable to parse facts for testing, miss match number of columns do not align and available facts.");
            }
        }
        return propertiesByColumnType;
    }

    public Map<String, Object> initFacts(Map<String, String> properties) {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("actions", new ArrayList<RulesAction>());
        facts.put(RULE_SET_ID_KEY, properties.getOrDefault(RULE_SET_ID_KEY, RULE_SET_ID_TEST_VALUE));
        facts.put(MISCODE_KEY, properties.getOrDefault(MISCODE_KEY, CCC_MIS_CODE_TEST_VALUE));
        facts.put(SUBJECT_AREA_KEY, properties.getOrDefault(SUBJECT_AREA_KEY, SUBJECT_AREA_TEST_VALUE));
        facts.put(TRACKING_ID_KEY, properties.getOrDefault(TRACKING_ID_KEY, TRACKING_ID_TEST_VALUE));
        facts.put(PlacementMapKeys.HIGHEST_READING_LEVEL_COURSE_KEY,properties.getOrDefault(PlacementMapKeys.HIGHEST_READING_LEVEL_COURSE_KEY, HIGHEST_READING_LEVEL_COURSE_TEST));
        return facts;
    }
    
    private void addAttributes(Map<String, Object> facts, Map<String, String> properties) {
        if(properties == null) {
            return;
        }
        properties.keySet().forEach(key -> facts.put(key, properties.get(key)));
    }

    private void addStudent(Map<String, Object> facts, Map<String, String> properties) {
        facts.put(CCCID_KEY, properties.getOrDefault(CCCID_KEY, CCCID_TEST_VALUE));
        BasicStudentView student = new BasicStudentView();
        student.setCccid(facts.get(CCCID_KEY).toString());
        Map<String, Integer> collegeStatuses = new HashMap<>();
        student.setCollegeStatuses(collegeStatuses);
        collegeStatuses.put(properties.getOrDefault(MISCODE_KEY, CCC_MIS_CODE_TEST_VALUE), 1);
        facts.put(PlacementMapKeys.STUDENT_VIEW_KEY, student);
    }

    private PlacementComponentActionResult  getPlacementFromProperty( Map<String, String> properties)
            throws ParseException {
        PlacementComponentActionResult placement = new PlacementComponentActionResult();
        placement.setCccid(properties.getOrDefault(CCCID_KEY, null));
        placement.setCompetencyMapDiscipline(properties.getOrDefault(HeaderNames.COMPETENCY_MAP_DISCIPLINE, null));
        String programs = properties.get(HeaderNames.PROGRAM);
        if (StringUtils.isNotBlank(programs)) {
            placement.addPrograms(new HashSet(Arrays.asList(programs.split(":"))));
        }

        placement.setTransferLevel(properties.getOrDefault(HeaderNames.TREANSFER_LEVEL, null));
        Integer levelsBelow = Integer.parseInt(properties.getOrDefault(HeaderNames.TRANSFER_LEVEL_INDEX, "-100"));
        if (levelsBelow.equals(-100))
            levelsBelow = Integer.parseInt(properties.getOrDefault(HeaderNames.LEVELS_BELOW_TRANSFER, "-100"));
        placement.setLevelsBelowTransfer(levelsBelow.equals(-100) ? null : levelsBelow);
        placement.setRowNumber((properties.getOrDefault(HeaderNames.ROW_NUMBER, null)));
        placement.setRuleId((properties.getOrDefault(HeaderNames.RULE_ID, null)));
        placement.setRuleSetId((properties.getOrDefault(HeaderNames.RULE_SET_ID, null)));
        placement.setRuleSetRowId((properties.getOrDefault(HeaderNames.RULE_SET_ROW_ID, null)));
        placement.setSubjectArea((properties.getOrDefault(HeaderNames.SUBJECT_AREA, null)));
        placement.setCollegeId((properties.getOrDefault(MISCODE_KEY, null)));
        placement.setMultipleMeasureVariableSetId((properties.getOrDefault(
                HeaderNames.MULTIPLE_MEASURE_VARIABLE_SET_ID, null)));
        placement.setTrackingId((properties.getOrDefault(HeaderNames.TRACKING_ID, null)));
        placement.setDataSource((properties.getOrDefault(HeaderNames.SOURCE, null)));
        placement.setDataSourceType((properties.getOrDefault(HeaderNames.SOURCE_TYPE, null)));
        if (properties.containsKey(HeaderNames.SOURCE_Date)
                && StringUtils.isNotBlank(properties.get(HeaderNames.SOURCE_Date))) {
            placement.setDataSourceDate(getDateFormat().parse(properties.get(HeaderNames.SOURCE_Date)));
        } else {
            placement.setDataSourceDate(null);
        }

        if (properties.containsKey(HeaderNames.STAND_ALONE_PLACEMENT)
                && StringUtils.isNotBlank(properties.get(HeaderNames.STAND_ALONE_PLACEMENT))) {
            placement.setStandalonePlacement(Boolean.parseBoolean(properties.get(HeaderNames.STAND_ALONE_PLACEMENT)));
        } else {
            placement.setStandalonePlacement(null);
        }

        if (properties.containsKey(HeaderNames.INSUFFICIENT_DATA)
                && StringUtils.isNotBlank(properties.get(HeaderNames.INSUFFICIENT_DATA))) {
            placement.setInsufficientData(Boolean.parseBoolean(properties.get(HeaderNames.INSUFFICIENT_DATA)));
        } else {
            placement.setInsufficientData(null);
        }

        if(!validateNotAllNull(placement)) {
            throw new RuntimeException(
                    "All values for expected placement component are null. You must validate at least one value. Please checker headers.");
        }
        return placement;
    }

    private void addVariableSet(Map<String, Object> facts, Map<String,String> testFacts) throws IOException, ParseException {
        VariableSet variableSet = new VariableSet();
        variableSet.setId(testFacts.getOrDefault("multipleMeasureVariableSetId)", "multipleMeasureVariableSetId"));
        variableSet.setSource(testFacts.getOrDefault(DATA_SOURCE_KEY, DATA_SOURCE_TEST_VALUE));
        variableSet.setSourceType(testFacts.getOrDefault(DATA_SOURCE_TYPE_KEY, DATA_SOURCE_TYPE_TEST_VALUE));
        variableSet.setSourceDate(getDateFormat().parse(testFacts.getOrDefault(DATA_SOURCE_DATE_KEY, DATA_SOURCE_DATE_TEST_VALUE)));
        Map<String, Fact> mmfactsMap = new HashMap<String, Fact>();
        testFacts.keySet().forEach(k -> addFact(mmfactsMap, k,  testFacts));
        addMathRanking(mmfactsMap);
        variableSet.setFacts(mmfactsMap);
        facts.put(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY, variableSet);
    }

    private SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("YYY-MM-dd");
    }
    private Map<String, Fact> addFact(Map<String, Fact> facts, String key, Map<String, String> testFacts) {
        Fact fact = new Fact();
        fact.setSource(testFacts.getOrDefault(DATA_SOURCE_KEY, DATA_SOURCE_TEST_VALUE));
        fact.setSourceDate(new Date());
        fact.setSourceType(testFacts.getOrDefault(DATA_SOURCE_TYPE_KEY, DATA_SOURCE_TYPE_TEST_VALUE));
        fact.setName(key);
        fact.setValue(testFacts.get(key));
        if (facts == null) {
            facts = new HashMap<String, Fact>();
        }
        facts.put(key, fact);
        return facts;
    }
    
boolean validateNotAllNull(PlacementComponentActionResult placement) {
        
        for(Field field: placement.getClass().getDeclaredFields()) {
            field.setAccessible(true);
           
            try {
             
                Object actual = field.get(placement);
                if(actual != null) {
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException exception) {
               throw new RuntimeException("Unable to use reflection to process equivalancy.", exception);
            }
        }
        return false;
    }

    public class HeaderNames {

        static final String MIS_CODE = "miscode";
        static final String COMPETENCY_MAP_DISCIPLINE = "competencymapdiscipline";
        static final String SUBJECT_AREA = "subjectArea";
        static final String TRACKING_ID = "trackingId";
        static final String PROGRAM = "program";
        static final String TREANSFER_LEVEL = "transferLevel";
        static final String TRANSFER_LEVEL_INDEX = "transferLevelIndex";
        static final String ROW_NUMBER = "rowNumber";
        static final String RULE_ID = "ruleId";
        static final String RULE_SET_ID = "ruleSetId";
        static final String RULE_SET_ROW_ID = "ruleSetRowId";
        static final String MULTIPLE_MEASURE_VARIABLE_SET_ID = "multipleMeasureVariableSetId";
        static final String SOURCE = "dataSource";
        static final String SOURCE_TYPE = "dataSourceType";
        static final String SOURCE_Date = "dataSourceDate";

        static final String STAND_ALONE_PLACEMENT = "standAlonePlacement";
        static final String LEVELS_BELOW_TRANSFER = "levelsBelowTransfer";
        static final String INSUFFICIENT_DATA = "insufficientData";
    }
}
