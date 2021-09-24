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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.springframework.stereotype.Service;

@Service
public class ApmsFactMapper extends AbstractFactMapper {

    
    private static Map<String,String> gpaMap = new HashMap<String,String>() {{
        put("a", "4.0");
        put("b", "3.99");
        put("c", "3.89");
        put("d", "3.79");
        put("e", "3.69");
        put("f", "3.59");
        put("g", "3.49");
        put("h", "3.39");
        put("i", "3.29");
        put("j", "3.19");
        put("k", "3.09");
        put("l", "2.99");
        put("m", "2.89");
        put("n", "2.79");
        put("o", "2.69");
        put("p", "2.59");
        put("q", "2.49");
        put("r", "2.39"); 
        put("s", "2.29");
        put("t", "2.19");
        put("u", "2.09");
        put("v", "1.99");
        put("w", "1.89");
        put("x", "1.79");
        put("y", "1.69");
        put("z", "1.59");
        put("aa", "1.49");
        put("bb", "1.39");
        put("cc", "1.29");
        put("dd", "1.19");
        put("ee", "1.09");
        put("ff", ".99");
        put("gg", ".89");
        put("hh", "0.79");
        put("ii", "0.69");
        put("jj", "0.59");
        put("kk", "0.49");
        put("ll", "0.39");
        put("mm", "0.29");
        put("nn", "0.19");
        put("oo", "0.09");
    }};
    
    private static Map<String,String> gradeMap = new HashMap<String,String>() {{
        put("a", "4.0");
        put("b", "3.7");
        put("c", "3.3");
        put("d", "3.0");
        put("e", "2.7");
        put("f", "2.3");
        put("g", "2.0");
        put("h", "1.7");
        put("i", "1.3");
        put("j", "1.0");
        put("k", "0.0");
        put("l", "0.5");
        put("m", "0.5");
    }};
    
    
    private static Map<String,String> highSchoolStatus = new HashMap<String,String>() {{
        put("a", "12");
        put("b", "11");
        put("c", "11");
        put("d", "11");
        put("e", "10");
        put("f", "12");
    }};

    private static Map<String,String> mathMap = new HashMap<String,String>() {{
        put("a", "pre_alg" );
        put("b", "alg_i" );
        put("c", "int_mat_1" ); //Integrated Math 1
        put("d", "int_mat_2" ); //Integrated Math 2
        put("e", "geo" );
        put("f", "alg_ii" );
        put("g", "int_mat_3" ); //Integrated Math 3
        put("h", "stat" );
        put("i", "int_mat_4"); //Integrated Math 4
        put("j", "trig" );
        put("k", "pre_calc" );
        put("l", "calc" );
        put("m", null );
    }};
    
    

    
    
    @Override
    public Map<String, Fact> mapFacts(Map<String, Fact> facts) {
       setMatriculation(facts);
       setCumGpa(facts);
       setEnglish(facts);
       setMath(facts);
       return facts;
    }
    
    protected void setCumGpa(Map<String,Fact> facts) {
        String answer = getValue( "EP03", facts);
        if(StringUtils.isNotBlank(answer)) {
            String gpa  = gpaMap.get(answer);
            if(StringUtils.isNotBlank(gpa)) {
                addFact("gpa_cum",  gpa, facts);
            }
        }
    }
    
    protected void setEnglish(Map<String,Fact> facts) {
        String highestEnglishClass  = getValue( "EP04", facts);
      
        String englishGradeAns = getValue( "EP05", facts);
        
        if(StringUtils.isNotBlank(englishGradeAns)) {
            String englishGrade = gradeMap.get(englishGradeAns);
            if("defgh".contains(highestEnglishClass.toLowerCase())) {
                if(highestEnglishClass.equalsIgnoreCase("d") || highestEnglishClass.equalsIgnoreCase("e") ) {
                    addFact("english_ap_under_class",  englishGrade, facts);
                }
                addFact("english_under_class",  englishGrade, facts);
                return;
            }
           
            if(highestEnglishClass.equalsIgnoreCase("a") || highestEnglishClass.equalsIgnoreCase("b") || highestEnglishClass.equalsIgnoreCase("e")) {
                    addFact("english_ap",  englishGrade, facts);
            }
            addFact("english",  englishGrade, facts);
        }
   }
    
    protected void setMath(Map<String,Fact> facts) {
        String highestMathClass = getValue( "EP06", facts);
        String highestMathGradeAns = getValue( "EP07", facts);
        String highestMathPassedClass = getValue( "EP08", facts);
        String highestMathPassedGradeAns = getValue( "EP09", facts);
        if(StringUtils.isNotBlank(highestMathPassedClass) && mathMap.containsKey(highestMathPassedClass)) {
            String mathGrade = gradeMap.get(highestMathPassedGradeAns);
            addFact(mathMap.get(highestMathPassedClass),  mathGrade, facts);
            setIntegrateMathValues(facts, mathMap.get(highestMathPassedClass), mathGrade);
        } else if (StringUtils.isNotBlank(highestMathClass) && mathMap.containsKey(highestMathClass)) {
            String mathGrade = gradeMap.get(highestMathGradeAns);
           // if(StringUtils.isNotBlank(mathGrade) &&  if(mathClass.equals("int_mat_1")) {Double.parseDouble(mathGrade) > 1.0)
                addFact(mathMap.get(highestMathClass),  mathGrade, facts);
                setIntegrateMathValues(facts, mathMap.get(highestMathClass), mathGrade);
        }
    }
    
    private void setIntegrateMathValues(Map<String,Fact> facts, String mathClass, String mathGrade) {
        if(mathClass == null) {
               return;
        }
        if(mathClass.equals("int_mat_1")) {
            addFact("alg_i",  mathGrade, facts);
        } else if(mathClass.equals("int_mat_2")) {
            addFact("geo",  mathGrade, facts);
        } else if(mathClass.equals("int_mat_3")) {
            addFact("alg_ii",  mathGrade, facts);
        } else if(mathClass.equals("int_mat_4")) {
            addFact("pre_calc",  mathGrade, facts);
            addFact("stat",  mathGrade, facts);
        }
    }
    
    protected void setMatriculation(Map<String,Fact> facts) {
        String  countryOfOrigin  = getValue( "EP01", facts);
        if("a".equals(countryOfOrigin)) {
            String  matriculation  = getValue( "EP02", facts);
            String gradeLevel = highSchoolStatus.get(matriculation);
            addFact("grade_level",  gradeLevel, facts);
        }
    }
    
    @Override
    public String getSource() {
        return "APMS";
    }

    @Override
    public String getSourceType() {
        return "Self Reported";
    }

    @Override
    public Map<String, String> getGradeMap() {
        return gpaMap;
    }

    @Override
    public Map<String, String> getMathMap() {
        return mathMap;
    }
}
