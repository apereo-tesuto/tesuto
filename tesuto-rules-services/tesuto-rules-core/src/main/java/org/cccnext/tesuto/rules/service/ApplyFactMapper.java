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
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by bruce on 7/3/17.
 */
@Service
public class ApplyFactMapper extends  AbstractFactMapper {

    private static Map<String,String> gradeMap = new HashMap<String,String>() {{
        put("A", "4.0");
        put("A-", "3.7");
        put("B+", "3.3");
        put("B", "3.0");
        put("B-", "2.7");
        put("C+", "2.3");
        put("C", "2.0");
        put("C-", "1.7");
        put("D+", "1.3");
        put("D", "1.0");
        put("D-", "0.7");
        put("F", "0.0");
        put("P", "2.9");
        put("NP", "0.5");
        put("ON", "0.5");
    }};

    private static Map<String,String> mathMap = new HashMap<String,String>() {{
        put("1", "pre_alg" );
        put("2", "alg_i" );
        put("3", "alg_i" ); //Integrated Math 1
        put("4", "geo" ); //Integrated Math 2
        put("5", "geo" );
        put("6", "alg_ii" );
        put("7", "alg_ii" ); //Integrated Math 3
        put("8", "stat" );
        put("9", "pre_calc"); //Integrated Math 4
        put("10", "trig" );
        put("11", "pre_calc" );
        put("12", "calc" );
    }};
    
    public Map<String,String> getGradeMap() {
        return gradeMap;
    }

    public Map<String,String> getMathMap() {
        return mathMap;
    }

    public Map<String,Fact> mapFacts(Map<String,Fact> facts) {

        Map<String,Fact> newFacts = new HashMap<>();

        copy("grade_point_average", facts, newFacts, (fact -> fact.setName("gpa_cum")));
        copy("highest_english_grade", facts, newFacts, (fact -> {
            fact.setName("english");
            String grade = gradeMap.get(fact.getValue());
            fact.setValue(grade);
            String highestEnglishCourse = getValue("highest_english_course", facts);
            if ("1".equals(highestEnglishCourse) || "4".equals(highestEnglishCourse)) {
                addFact("english_ap", grade, newFacts);
            }
        }));

        Fact mathGradeTaken = facts.get("highest_math_taken_grade");
        if (mathGradeTaken != null) {
            copy("highest_math_course_taken", facts, newFacts, (fact -> mathTransform(fact, mathGradeTaken)));
        }

        Fact mathGradePassed = facts.get("highest_math_passed_grade");
        if (mathGradePassed != null) {
            copy("highest_math_course_passed", facts, newFacts, (fact -> mathTransform(fact, mathGradePassed)));
        }

        if ("US".equals(getValue("hs_country", facts))) {
            String hsLevel = getValue("hs_edu_level", facts);
            boolean completed11 = getCompleted11th(facts);
            if (completed11 && "1".equals(hsLevel)) {
                addFact("grade_level", "11", newFacts);
            } else if ("3".equals(hsLevel) || "4".equals(hsLevel) || "5".equals(hsLevel)) {
                Calendar completionDate = getDate("hs_comp_date", facts);
                Calendar rdd = getDate("rdd", facts);
                if (completionDate != null && rdd != null) {
                    rdd.add(Calendar.YEAR, -10);
                    if (!completionDate.before(rdd)) {
                        addFact("grade_level", "12", newFacts);
                    }
                }
            }
        }

        return newFacts;
    }
    
    public String getSource() {
        return "CCCApply";
    }
    
    public String getSourceType() {
        return "Self Reported";
    }
}
