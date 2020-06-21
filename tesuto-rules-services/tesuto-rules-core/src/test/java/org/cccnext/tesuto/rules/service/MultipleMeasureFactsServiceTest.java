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
package org.cccnext.tesuto.rules.service;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.function.DrlFunctions;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.APPLY_SOURCE;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.VERIFIED;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.SELF_REPORTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bruce on 7/12/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-application-context.xml")
public class MultipleMeasureFactsServiceTest {

    @Autowired MultipleMeasureFactService service;
    @Autowired OperationalDataStoreService ods;

    @Before
    public void setup() throws Exception {
        ods.createStudentTable();
        ods.createVariableSetTable();
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Integer randomInteger() {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(5000);
    }

    private Fact createFact(String name, String value) {
        Fact fact = new Fact();
        fact.setName(name);
        fact.setValue(value);
        fact.setSource(randomString());
        fact.setSourceDate(new Date());
        fact.setSourceType(randomString());
        return fact;
    }

    private Map<String,Object> initFacts() {
        String cccId = randomString();
        String misCode = randomString();
        return new HashMap<String,Object>() {{
            put(PlacementMapKeys.CCCID_KEY, cccId);
            put(PlacementMapKeys.MISCODE_KEY, misCode);
            put(PlacementMapKeys.EVENT_TRIGGER_KEY, PlacementMapKeys.EVENT_TRIGGER_COMPONENT);
            put(PlacementMapKeys.RULE_SET_ID_KEY, randomInteger());
        }};
    }

    private Student createStudent(Map<String,Object> facts) {
        String cccId = (String)facts.get(PlacementMapKeys.CCCID_KEY);
        Student student = new Student();
        student.setCccId(cccId);
        student.setSsId(randomString());
        ods.createStudent(student);
        return student;
    }

    @Test
    public void errorIfNoCccId() {
        Map<String, Object> facts = initFacts();
        facts.remove(PlacementMapKeys.CCCID_KEY);
        List<String> errors = service.processFacts(facts);
        assertFalse(facts.containsKey(MultipleMeasureFactService.NAME));
        assertEquals(1, errors.size());
    }

    @Test
    public void errorIfNoValidVariableSets() {
        Map<String, Object> facts = initFacts();
        List<String> errors = service.processFacts(facts);
        assertFalse(facts.containsKey(MultipleMeasureFactService.NAME));
        assertEquals(1, errors.size());
    }

    @Test
    public void unverifiedVariableSetWithOptIn() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        VariableSet variableSet = buildSelfReportedVariableSet(student.getCccId(), misCode);
        ods.addVariableSet(student.getCccId(), variableSet);
        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(variableSet.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }

    @Test
    public void unverifiedVariableSetWithOptInAndNullMisCode() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        VariableSet variableSet = buildSelfReportedVariableSet(student.getCccId(), null);
        ods.addVariableSet(student.getCccId(), variableSet);
        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(variableSet.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }


    @Test
    public void unverifiedVariableSetsWithOptOut() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "false");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        VariableSet variableSet =buildSelfReportedVariableSet(student.getCccId(), misCode);
        ods.addVariableSet(student.getCccId(), variableSet);
        buildSelfReportedVariableSet(student.getCccId(), null);
        List<String> errors = service.processFacts(facts);
        assertEquals(1, errors.size());
        assertFalse(facts.containsKey(MultipleMeasureFactService.NAME));
    }

    @Test
    public void optInDefaultsToFalse() {
        Map<String, Object> facts = initFacts();
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        createSelfReportedVariableSet(student.getCccId(), misCode);
        createSelfReportedVariableSet(student.getCccId(), null);
        List<String> errors = service.processFacts(facts);
        assertEquals(1, errors.size());
        assertFalse(facts.containsKey(MultipleMeasureFactService.NAME));
    }

    private VariableSet addVariableSet(String cccId, String misCode, String sourceType, long delta) {
        VariableSet vs = buildVerifiedVariableSet(cccId, misCode);
        vs.setSourceType(sourceType);
        vs.setSourceDate(new Date(System.currentTimeMillis() - delta));
        vs.getFacts().put("verified", createFact("verified", sourceType));
        ods.addVariableSet(cccId, vs);
        return vs;
    }


    @Test
    public void mostRecentVerifiedVariableSetSelected() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();

        addVariableSet(cccId, misCode, VERIFIED, 20000l);
        VariableSet vs = addVariableSet(cccId, misCode, VERIFIED, 10000l);
        addVariableSet(cccId, misCode, SELF_REPORTED, 30000l);
        addVariableSet(cccId, misCode, SELF_REPORTED, 0l);

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(vs.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }

    @Test
    public void mostRecentSelfReportedVariableSetSelectedInAbsenceOfVerifiedData() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();
        addVariableSet(cccId, misCode, SELF_REPORTED, 20000l);
        addVariableSet(cccId, misCode, SELF_REPORTED, 10000l);
        VariableSet vs = addVariableSet(cccId, misCode, SELF_REPORTED, 0l);
        addVariableSet(cccId, misCode, SELF_REPORTED, 30000l);

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(vs.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }

    @Test
    public void onlyValidVariableSetSelected() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "false");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();

        addVariableSet(cccId, randomString(), VERIFIED, 0l);
        VariableSet vs = addVariableSet(cccId, misCode, VERIFIED, 10000l);
        addVariableSet(cccId, null, SELF_REPORTED, 0l);
        addVariableSet(cccId, misCode, SELF_REPORTED, 20000l);

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(vs.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }


    @Test
    public void factsAreMappedIfNecessary() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();
        VariableSet vs = createSelfReportedVariableSet(cccId, misCode);
        vs.setSourceDate(new Date());
        Fact fact = createFact("highest_math_grade_taken", "A");
        vs.getFacts().put(fact.getName(), fact);
        ods.addVariableSet(cccId, vs);
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> mapped = mapper.mapFacts(vs.getFacts());

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        Map<String,Fact> generatedFacts =  ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts();
        //To support potential logic values are added to the already existent facts
        mapped.keySet().forEach(key -> assertTrue(generatedFacts.containsKey(key)));
    }

    @Test
    public void factsAreNotMappedIfNotFromApply() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();
        VariableSet vs = buildSelfReportedVariableSet(cccId, misCode);
        vs.setSource("Some Other Source");
        vs.setSourceDate(new Date());
        Fact fact = createFact("highest_math_grade_taken", "A");
        vs.getFacts().put(fact.getName(), fact);
        ods.addVariableSet(cccId, vs);

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(vs.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }

    @Test
    public void factsAreNotMappedIfVerified() {
        Map<String, Object> facts = initFacts();
        facts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(facts);
        String misCode = (String)facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();
        VariableSet vs = buildVerifiedVariableSet(cccId, misCode);
        vs.setSource(APPLY_SOURCE);
        vs.setSourceType(VERIFIED);
        vs.setSourceDate(new Date());
        Fact fact = createFact("highest_math_grade_taken", "A");
        vs.getFacts().put(fact.getName(), fact);
        ods.addVariableSet(cccId, vs);

        List<String> errors = service.processFacts(facts);
        assertEquals(0, errors.size());
        assertEquals(vs.getFacts(), ((VariableSet)facts.get(MultipleMeasureFactService.NAME)).getFacts());
    }
    
    @Test
    public void verifyApplySelfToCalPasConversion() {
        
        Map<String, Object> ruleEngineFacts = initFacts();
        ruleEngineFacts.put(PlacementMapKeys.OPT_IN_KEY, "true");
        Student student = createStudent(ruleEngineFacts);
        String misCode = (String)ruleEngineFacts.get(PlacementMapKeys.MISCODE_KEY);
        String cccId = student.getCccId();
        VariableSet variableSet = buildSelfReportedVariableSet(cccId, misCode);
        Map<String,Fact> facts = new HashMap<String,Fact>();
        addSelfReportedFact(facts,  "rdd", "1485331200000");
        addSelfReportedFact(facts,  "highest_math_taken_grade", "A");
        addSelfReportedFact(facts,  "highest_math_course_taken", "12");
        addSelfReportedFact(facts,  "hs_comp_date", "1451677432408");
        addSelfReportedFact(facts,  "highest_english_grade", "A");
        addSelfReportedFact(facts,  "highest_english_course", "1");
        addSelfReportedFact(facts,  "grade_point_average", "4.00");
        addSelfReportedFact(facts,  "ccc_id", "AAK2923");
        addSelfReportedFact(facts,  "hs_edu_level", "3");
        addSelfReportedFact(facts,  "hs_country", "US");
        variableSet.setFacts(facts);
        ods.addVariableSet(cccId, variableSet);
        
        List<String> errors = service.processFacts(ruleEngineFacts);
        
        assertEquals(0, errors.size());
       VariableSet created = (VariableSet) ruleEngineFacts.get(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY);
        Double mathRanking = DrlFunctions.parseFactToDouble(created.getFacts().get("math_ranking"));
        assertEquals((Double)6.4,mathRanking);
        
        Double englishGpa = DrlFunctions.parseFactToDouble(created.getFacts().get("english_ap"));
        assertEquals((Double)4.0,englishGpa);
          
    }
    
    private void addSelfReportedFact(Map<String,Fact> facts, String name, String value) {
        Fact fact = new Fact();
        fact.setName(name);
        fact.setValue(value);
        fact.setSource(APPLY_SOURCE);
        fact.setSourceType( SELF_REPORTED);
        fact.setSourceDate(new Date());
        facts.put(name, fact);
    }
    
    private VariableSet buildSelfReportedVariableSet(String cccId, String misCode)  {
        VariableSet vs = buildVariableSet(cccId, misCode);
        vs.setSourceType(SELF_REPORTED);
        vs.setSource(APPLY_SOURCE);
        return vs;
    }
    
    private VariableSet createSelfReportedVariableSet(String cccId, String misCode)  {
        VariableSet vs = buildSelfReportedVariableSet(cccId, misCode);
        ods.addVariableSet(cccId, vs);
        return vs;
    }

    private VariableSet buildVerifiedVariableSet(String cccId, String misCode) {
        VariableSet vs = buildVariableSet(cccId, misCode);
        vs.setSourceType(VERIFIED);
        return vs;
    }
    
    private VariableSet buildVariableSet(String cccId, String misCode) {
        String id = randomString();
        VariableSet vs = new VariableSet() {{
            setId(id);
            setMisCode(misCode);
            setCreateDate(new Date());
            setSource("Test");
            setSourceDate(new Date());
            setFacts(new HashMap<>());
        }};
        vs.getFacts().put("id", createFact("id", id));
        return vs;
    }

}
