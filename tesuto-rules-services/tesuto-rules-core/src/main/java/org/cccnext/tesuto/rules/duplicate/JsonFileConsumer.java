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
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.amazonaws.dynamodb.bootstrap.AbstractLogConsumer;
import com.amazonaws.dynamodb.bootstrap.SegmentedScanResult;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * This consumer is setting up the processing of the scan results by passing them to the JsonFileConsumerWorker
 */
public class JsonFileConsumer extends AbstractLogConsumer  {

    private String tableName;
    private File tableFile;
    private JsonGenerator jsonGenerator;

    public JsonFileConsumer(String tableName, ExecutorService exec) throws IOException {
        this.tableName = tableName;
        super.threadPool = exec;
        super.exec = new ExecutorCompletionService<Void>(threadPool);
    }

    @Override
    public Future<Void> writeResult(SegmentedScanResult result) {
        Future<Void> jobSubmission = null;
        jobSubmission = exec.submit(new JsonFileConsumerWorker(jsonGenerator, result.getScanResult().getItems()));
        return jobSubmission;
    }

    public void initFile() throws IOException {
        tableFile = new File(tableName + ".json");
        JsonFactory jFac = new JsonFactory();
        jsonGenerator = jFac.createGenerator(tableFile, JsonEncoding.UTF8);
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartArray();
    }

    public void closeFile() throws IOException {
        jsonGenerator.writeEndArray();
        jsonGenerator.close();
    }

}
