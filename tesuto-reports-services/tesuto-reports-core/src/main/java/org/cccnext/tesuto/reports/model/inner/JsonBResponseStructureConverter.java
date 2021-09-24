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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class JsonBResponseStructureConverter implements AttributeConverter<JsonBResponseStructure, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonBResponseStructure JsonBInteraction) {
        try {
            return objectMapper.writeValueAsString(JsonBInteraction);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public JsonBResponseStructure convertToEntityAttribute(String jsonBInteraction) {
        try {
            return objectMapper.readValue(jsonBInteraction, JsonBResponseStructure.class);
        } catch (Exception ex) {
            return null;
        }
    }

}
