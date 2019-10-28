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
public class CalPasErrorHeaders implements ErrorHeaders {

    @Override
    public String getDataSource() {
        return "CalPass";
    }

    @Override
    public String getDataType() {
        return "Verified";
    }

    @Override
    public List<String> getParameterNames() {
        List<String> headers = Arrays.asList(new String[] { "cccid","interSegmentKey", "englishY", "englishA", "englishB", "englishC",
                "preAlgebra", "algebraI", "algebraII", "mathGE", "statistics", "collegeAlgebra",
                "trigonometry", "preCalculus", "calculusI", "readingM_UboundY", 
                "readingY_UboundY", "readingA_UboundY", "readingB_UboundY", 
                "readingC_UboundY", "readingM_UboundA", "readingA_UboundA",
                "readingB_UboundA", "readingC_UboundA", "readingM_UboundB", 
                "readingB_UboundB", "readingC_UboundB",
                "eslY_UboundY", "eslA_UboundY", "eslB_UboundY", 
                "eslA_UboundA", "eslB_UboundA", "eslC_UboundA",
                "eslB_UboundB", "eslC_UboundB", 
                "eslD_UboundB", "isTreatment",
                "transferLevel", "programs", "standalonePlacement",
                "insufficientData", "rowNumber", "levelsBelowTransfer", "ruleSetId", "ruleId", "ruleSetRowId",
                "DATA_SOURCE", "DATA_SOURCE_TYPE", "trackingId", "test number", "serialVersionUID", "collegeId",
                "subjectArea", "multipleMeasureVariableSetId", "competencyMapDiscipline", "dataSource",
                "dataSourceDate", "dataSourceType"});
        return headers;
    }

}
