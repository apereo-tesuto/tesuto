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

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ApmsErrorHeaders implements ErrorHeaders {

    @Override
    public String getDataSource() {
        return "APMS";
    }

    @Override
    public String getDataType() {
        return "Self Reported";
    }

   @Override
    public List<String> getParameterNames() {
        List<String> headers = Arrays.asList(new String[] { "cccid", "EP01", "EP02", "EP03", "EP04", "EP05", "EP06",
                "EP07", "EP08", "EP09", "grade_level", "gpa_cum", "math_ranking", "calc", "pre_calc", "trig", "geo",
                "stat", "alg_ii", "alg_i", "int_mat_1", "int_mat_2", "int_mat_3", "int_mat_4", "english_ap", "english",
                "english_under_class", "english_ap_under_class", "transferLevel", "programs", "standalonePlacement",
                "insufficientData", "rowNumber", "levelsBelowTransfer", "ruleSetId", "ruleId", "ruleSetRowId",
                "DATA_SOURCE", "DATA_SOURCE_TYPE", "trackingId", "test number", "serialVersionUID", "collegeId",
                "subjectArea", "multipleMeasureVariableSetId", "competencyMapDiscipline", "dataSource",
                "dataSourceDate", "dataSourceType" });
        return headers;
    }
    

}
