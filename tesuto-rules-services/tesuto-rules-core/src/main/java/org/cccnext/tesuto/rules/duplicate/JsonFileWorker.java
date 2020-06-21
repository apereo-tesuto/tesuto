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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.amazonaws.dynamodb.bootstrap.AbstractLogConsumer;
import com.amazonaws.dynamodb.bootstrap.AbstractLogProvider;
import com.amazonaws.dynamodb.bootstrap.SegmentedScanResult;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The working reads the json from the files, and transforms the data into scan results,
 * so they can be processed by the provided DynamoDBConsumer and DynamoDBConsumerWorker
 */
public class JsonFileWorker extends AbstractLogProvider {

    private String tableName;
    private JsonParser jsonParser;
    File tableFile;

    public JsonFileWorker(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void pipe(AbstractLogConsumer consumer) throws ExecutionException, InterruptedException {
        try {
            SegmentedScanResult scanResults  = new SegmentedScanResult(buildScanResults(), 1);
            consumer.writeResult(scanResults);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private ScanResult buildScanResults() throws IOException {
        ScanResult scanResult = new ScanResult();
        scanResult.setItems(readItems());
        return scanResult;
    }

    private Collection<Map<String, AttributeValue>> readItems() throws IOException {
        Collection<Map<String, AttributeValue>> items = new LinkedList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode allNodes = objectMapper.readTree(jsonParser);
        for (JsonNode node: allNodes) {
            items.add(parseItem(node, objectMapper));
        }
        return items;
    }

    private Map<String, AttributeValue> parseItem(JsonNode node, ObjectMapper mapper) throws JsonProcessingException, IOException {
        Map<String,AttributeValue> item = new HashMap<>();
        Iterator<Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            String fieldValue = field.getValue().asText();
            AttributeValue v = mapper.readValue(fieldValue, AttributeValue.class);
            item.put(fieldName, v);
        }
        return item;
    }


    public void initWorker() throws JsonParseException, IOException {
        JsonFactory jFac = new JsonFactory();
        File tableFile = new File(tableName + ".json");
        jsonParser = jFac.createParser(tableFile);
    }

    public void closeWorker() throws JsonParseException, IOException {
        jsonParser.close();
    }

}
