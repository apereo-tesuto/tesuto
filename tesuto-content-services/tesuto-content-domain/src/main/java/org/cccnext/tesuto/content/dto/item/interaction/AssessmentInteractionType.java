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
package org.cccnext.tesuto.content.dto.item.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssessmentInteractionType {

    CHOICE_INTERACTION, INLINE_CHOICE_INTERACTION, EXTENDED_TEXT_INTERACTION, TEXT_ENTRY_INTERACTION, MATCH_INTERACTION, NULL_INTERACTION;

    private static Map<String, AssessmentInteractionType> typeMap = new HashMap<String, AssessmentInteractionType>(3);

    static {
        typeMap.put("choiceInteraction", CHOICE_INTERACTION);
        typeMap.put("inlineChoiceInteraction", INLINE_CHOICE_INTERACTION);
        typeMap.put("extendedTextInteraction", EXTENDED_TEXT_INTERACTION);
        typeMap.put("textEntryInteraction", TEXT_ENTRY_INTERACTION);
        typeMap.put("matchInteraction", MATCH_INTERACTION);
    }

    @JsonCreator
    public static AssessmentInteractionType forValue(String value) {
        return typeMap.get(value);
    }

    @JsonValue
    public String getValue() {
        for (Entry<String, AssessmentInteractionType> entry : typeMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

}
