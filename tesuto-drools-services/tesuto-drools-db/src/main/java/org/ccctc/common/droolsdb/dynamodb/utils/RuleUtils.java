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

import java.util.Map;

import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;

public class RuleUtils {

    public static String generateRuleFromVariables(String rule, 
                Map<String, String> variableTypes, 
                RuleVariableRowDTO variableRow) {
        for (RuleVariableDTO variable : variableRow.getVariables()) {
            String replacement = variable.getValue();
            String dataType = variableTypes.get(variable.getName());
            if ("String".equals(dataType)) {
                replacement = "\"" + replacement + "\"";
            }
            rule = rule.replace("${" + variable.getName() + "}", replacement);
        }
        return rule;
    }
}
