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

import java.util.Map;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;

public class CalpasFactMapper implements FactMapper {

    @Override
    public Map<String, Fact> mapFacts(Map<String, Fact> facts) {
        return facts;
    }

    @Override
    public String getSource() {
       
        return "Calpass";
    }

    @Override
    public String getSourceType() {
        return "Verified";
    }

    @Override
    public Map<String, String> getGradeMap() {
        //Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getMathMap() {
        //Auto-generated method stub
        return null;
    }

}
