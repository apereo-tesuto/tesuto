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
package org.ccctc.common.droolsdb.dynamodb.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleUtils;
import org.junit.Test;

public class RuleUtilsTest {

    @Test
    public void stringVariable() {
        Map<String, String> variableTypes = new HashMap<String, String>();
        variableTypes.put("sme", "String");
        RuleVariableRowDTO variableRow = new RuleVariableRowDTO();
        variableRow.setName("variableRowName");
        List<RuleVariableDTO> variables = new ArrayList<RuleVariableDTO>();
        RuleVariableDTO variable = new RuleVariableDTO();
        variable.setName("sme");
        variable.setValue("English");
        variables.add(variable);
        variableRow.setVariables(variables);
        String original = "rule \"RuleName\" when\n" +
                "map : Map()\n" +
                "sme : String() from map.get(${sme})\n" +
                "then\n" +
                "end\n";
        String expected = "rule \"RuleName\" when\n" +
                "map : Map()\n" +
                "sme : String() from map.get(\"English\")\n" +
                "then\n" +
                "end\n"; 
        String updated = RuleUtils.generateRuleFromVariables(original, variableTypes, variableRow);
        assertEquals(expected, updated);
    }
    
    @Test
    public void stringNumber() {
        Map<String, String> variableTypes = new HashMap<String, String>();
        variableTypes.put("lower", "Number");
        RuleVariableRowDTO variableRow = new RuleVariableRowDTO();
        variableRow.setName("variableRowName");
        List<RuleVariableDTO> variables = new ArrayList<RuleVariableDTO>();
        RuleVariableDTO variable = new RuleVariableDTO();
        variable.setName("lower");
        variable.setValue("1.0");
        variables.add(variable);
        variableRow.setVariables(variables);
        String original = "rule \"RuleName\" when\n" +
                "map : Map()\n" +
                "sme : Integer(this >= ${lower}) from map.get(\"size\")\n" +
                "then\n" +
                "end\n";
        String expected = "rule \"RuleName\" when\n" +
                "map : Map()\n" +
                "sme : Integer(this >= 1.0) from map.get(\"size\")\n" +
                "then\n" +
                "end\n"; 
        String updated = RuleUtils.generateRuleFromVariables(original, variableTypes, variableRow);
        assertEquals(expected, updated);
    }
}
