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
package org.cccnext.tesuto.multiplemeasures.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.service.multiplemeasures.VariableSetParserService;
import org.joda.time.DateTime;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class CalPassVariableSetParserServiceImpl implements VariableSetParserService {

    private static final String CAL_PASS_SOURCE = "Calpass";
    private static final String CAL_PASS_SOURCE_TYPE = "Verified";
    private static final String CCC_ID = "ccc_id";
    private static final String MIS_CODE = "college_id";

    //private static final String INTERSEGMENT_KEY = "intersegment_key";

    @Override
    public VariableSet parseJsonToVariableSet(String misCode, String cccId, String calPassJson) {


        VariableSet calPassVariableSet = new VariableSet();

        calPassVariableSet.setId(UUID.randomUUID().toString());
        calPassVariableSet.setCreateDate(new Date());
        calPassVariableSet.setSource(CAL_PASS_SOURCE);
        calPassVariableSet.setSourceType(CAL_PASS_SOURCE_TYPE);
        calPassVariableSet.setSourceDate(new Date(0L)); // TODO Figure out how to get drools to handle a null value.
        calPassVariableSet.setMisCode(misCode);

        Map<String, Fact> calPassFactsMap = parseCalPassJsonToFactsMap(calPassJson);

        calPassFactsMap.put(CCC_ID, createCalPassFact(CCC_ID, cccId));
        calPassFactsMap.put(MIS_CODE, createCalPassFact(MIS_CODE, misCode));
//        calPassFactsMap.put(INTERSEGMENT_KEY, createCalPassFact(INTERSEGMENT_KEY, requestData.get(INTERSEGMENT_KEY)));

        calPassVariableSet.setFacts(calPassFactsMap);

        return calPassVariableSet;
    }

    /*
    Expects CalPass JSON to be in the format:
    {
        "name1": value1
           ...
        "nameN": valueN
    }
    */
    private Map<String, Fact> parseCalPassJsonToFactsMap(String json) {
        Map<String, Fact> factsMap = new HashMap<>();

        final ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, String> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {});
            jsonMap.forEach((key,value) -> factsMap.put(key, createCalPassFact(key, value)));
        } catch (IOException e) {
            //TODO: Should we do anything else interesting in here? Also, is it safe to log the JSON data?
            log.error("An IOException occurred while attempting to parse the following CalPass JSON data:\n" + json);
        }

        return factsMap;
    }

    private Fact createCalPassFact(String name, String value) {
        Fact fact = new Fact();

        fact.setName(name);
        fact.setValue(value);
        fact.setSource(CAL_PASS_SOURCE);
        fact.setSourceType(CAL_PASS_SOURCE_TYPE);
        fact.setSourceDate(null); // Set this too if we ever set the SourceDate in parseJsonToVariableSet.

        return fact;
    }
}
