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
package org.cccnext.tesuto.rules.duplicate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The consumer worker read the scan results from the table scan and writes them to a
 * formatted JSON file.
 */
public class JsonFileConsumerWorker implements Callable<Void> {
    private JsonGenerator jsonGenerator;
    private List<Map<String, AttributeValue>> items;

    public JsonFileConsumerWorker(JsonGenerator jsonGenerator, List<Map<String, AttributeValue>> items) {
        this.jsonGenerator = jsonGenerator;
        this.items = items;
    }

    @Override
    public Void call() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        for (Map<String, AttributeValue> item: items) {
            jsonGenerator.writeStartObject();
            for (Entry<String,AttributeValue> entry: item.entrySet()) {
                jsonGenerator.writeStringField(entry.getKey(), mapper.writeValueAsString(entry.getValue()));
            }
            jsonGenerator.writeEndObject();
            jsonGenerator.flush();
        }
        return null;
    }

}
