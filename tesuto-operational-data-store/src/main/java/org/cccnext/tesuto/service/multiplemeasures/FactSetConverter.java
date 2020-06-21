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
package org.cccnext.tesuto.service.multiplemeasures;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;

import java.util.Set;


/**
 * Created by bruce on 4/13/17.
 */
public class FactSetConverter  implements DynamoDBTypeConverter<String,Set<Fact>> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convert(Set<Fact> facts) {
        try {
            return mapper.writeValueAsString(facts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Fact> unconvert(String json) {
        try {
            return mapper.readValue(json, new TypeReference<Set<Fact>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
