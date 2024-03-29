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
package org.cccnext.tesuto.test.util;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SeedDataUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    String localDirectory;

    public static List<Object> readSeedData(String uri, Class objectClass) throws JsonParseException, JsonMappingException, IOException {
        File resources = new File(uri); 
        return objectMapper.readValue(resources, objectMapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) List.class, objectClass));
    }
    
    public static void writeSeedData(String uri, Object[] object) throws JsonParseException, JsonMappingException, IOException {
        File resources = new File(uri); 
        objectMapper.writeValue(resources, object);
    }

}
