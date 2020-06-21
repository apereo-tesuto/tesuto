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

import junit.framework.TestCase;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruce on 7/6/17.
 */
public class MultipleMeasureFactMapperTest extends TestCase {


    private Fact createFact(String name, String value) {
        Fact fact = new Fact();
        fact.setName(name);
        fact.setValue(value);
        fact.setSourceDate(new Date(0l));
        fact.setSource("CCCApply-Mapping");
        fact.setSourceType("Self Reported-Mapping");
        return fact;
    }

    private void addFact(Map<String,Fact> facts, String name, String value) {
        facts.put(name, createFact(name, value));
    }

    @Test
    public void testFactMapper() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "grade_point_average", "2.5");
        addFact(facts,  "highest_english_grade", "B+");
        addFact(facts,  "highest_english_course", "1");
        addFact(facts,  "highest_math_taken_grade", "D");
        addFact(facts,  "highest_math_course_taken", "8");
        addFact(facts, "highest_math_passed_grade", "A-");
        addFact(facts, "highest_math_course_passed", "6");
        addFact(facts, "total nonsense", "x");

        Map<String,Fact> expected = new HashMap<>();
        addFact(expected, "gpa_cum", "2.5");
        addFact(expected, "english_ap", "3.3");
        addFact(expected, "english", "3.3");
        addFact(expected, "alg_ii", "3.7");
        addFact(expected, "stat", "1.0");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        
        assertEquals(expected.size(),newFacts.size());
        for(String key:expected.keySet()) {
            assertEquals(expected.get(key), newFacts.get(key));
        }
        assertEquals(expected, newFacts);
    }

    @Test
    public void testFactMapperWithoutAP() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "grade_point_average", "2.5");
        addFact(facts,  "highest_english_grade", "B+");
        addFact(facts,  "highest_english_course", "3");
        addFact(facts,  "highest_math_taken_grade", "D");
        addFact(facts,  "highest_math_course_taken", "8");
        addFact(facts, "highest_math_passed_grade", "A-");
        addFact(facts, "highest_math_course_passed", "6");
        addFact(facts, "total nonsense", "x");

        Map<String,Fact> expected = new HashMap<>();
        addFact(expected, "gpa_cum", "2.5");
        addFact(expected, "english", "3.3");
        addFact(expected, "alg_ii", "3.7");
        addFact(expected, "stat", "1.0");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);

        assertEquals(expected, newFacts);
    }

    @Test
    public void testFactMapperWithGradeLevel11() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "1");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertEquals("11", ApplyFactMapper.getValue("grade_level", newFacts));
    }


    @Test
    public void testFactMapperWithGradeLevel12() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "4");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertEquals("12", ApplyFactMapper.getValue("grade_level", newFacts));
    }

    @Test
    public void testFactMapperWithNoGradeLevel() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_edu_level", "4");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertTrue(newFacts.isEmpty());
    }

    @Test
    public void testFactMapperWithNoGradeLevel2() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "4");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "400000000000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertTrue(newFacts.isEmpty());
    }

    @Test
    public void testFactMapperWithNoGradeLevel3() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "2");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertTrue(newFacts.isEmpty());
    }

    @Test
    public void testFactMapperWithNoGradeLevel4() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", null);
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "1");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertTrue(newFacts.isEmpty());
    }

    @Test
    public void testFactMapperWithNoGradeLevel5() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "1");
        addFact(facts, "hs_comp_date", "1000");
        addFact(facts, "rdd", "2000");

        Map<String,Fact> newFacts = mapper.mapFacts(facts);
        assertTrue(newFacts.isEmpty());
    }

    @Test
    public void testFactMapperWithNoGradeLevel6() {
        ApplyFactMapper mapper = new ApplyFactMapper();
        Map<String,Fact> facts = new HashMap<>();
        addFact(facts, "completed_eleventh_grade", "true");
        addFact(facts, "hs_country", "US");
        addFact(facts, "hs_edu_level", "4");
        addFact(facts, "rdd", "2000");
        try {
            Map<String, Fact> newFacts = mapper.mapFacts(facts);
            assertTrue(newFacts.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
