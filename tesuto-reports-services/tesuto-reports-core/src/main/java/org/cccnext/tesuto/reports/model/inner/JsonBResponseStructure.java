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
package org.cccnext.tesuto.reports.model.inner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonBResponseStructure implements Serializable {

    private static final long serialVersionUID = -7919812592849113312L;

    // interationIdentiferi:valueIdentfier,godelInteractionValue
    Map<String, Integer> orderMap;

    List<String> valueOrder;

    Map<String, Character> singleCharacterResponses;

    public JsonBResponseStructure() {
        orderMap = new HashMap<String, Integer>();
        valueOrder = new ArrayList<String>();
        singleCharacterResponses = new HashMap<String, Character>();
    }

    public Map<String, Integer> getOrderMap() {
        return orderMap;
    }

    public void orderMap(Map<String, Integer> valueGodels) {
        this.orderMap = valueGodels;
    }

    public List<String> getValueOrder() {
        return valueOrder;
    }

    public void setValueOrder(List<String> valueOrder) {
        this.valueOrder = valueOrder;
    }

    public Map<String, Character> getSingleCharacterResponses() {
        return singleCharacterResponses;
    }

    public void setSingleCharacterResponses(Map<String, Character> singleCharacter) {
        this.singleCharacterResponses = singleCharacter;
    }

}
