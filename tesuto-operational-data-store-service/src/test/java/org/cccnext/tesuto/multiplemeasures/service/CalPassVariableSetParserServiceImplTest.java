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
import org.apache.commons.lang3.time.DateUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.service.multiplemeasures.VariableSetParserService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CalPassVariableSetParserServiceImplTest {

    private VariableSetParserService service = new CalPassVariableSetParserServiceImpl();

    @Test
    public void CalPassParserProperlyParsesValidJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> requestDataMap = getValidRequestDataMap();
        Map<String, String> extraRequestDataMap = getExtraRequestDataMap();
        String calPassJson = getValidCalPassJson();
        String expectedSource = "Calpass";
        String expectedSourceType = "Verified";

        Map<String, String> tempMap = new HashMap<>();
        requestDataMap.forEach(tempMap::putIfAbsent);
        extraRequestDataMap.forEach(tempMap::putIfAbsent);

        VariableSet result = service.parseJsonToVariableSet("aMISCode", "someCCCId", calPassJson);

        // Test basic stuff
        Assert.assertNotNull("The variable set id is null!", result.getId());
        Assert.assertEquals("Source is incorrect", expectedSource, result.getSource());
        Assert.assertEquals("Source type is incorrect", expectedSourceType, result.getSourceType());
        Assert.assertTrue("Variable Set was not created today", DateUtils.isSameDay(new Date(), result.getCreateDate()));
        //TODO: Add test here for sourceDate if/when we set it

        Map<String, Fact> resultFacts = result.getFacts();

        // Test that extra stuff isn't in the facts
        Assert.assertEquals("Facts map is the wrong size!", 37, resultFacts.size());
        extraRequestDataMap.forEach((key, value) ->
            Assert.assertTrue(String.format("A fact exists for %s->%s that shouldn't", key, value),
                    resultFacts.keySet().stream().noneMatch(resultKey -> resultKey.equals(key))
        ));

        // Test that request info is in the facts
        requestDataMap.forEach((key, value) ->
            Assert.assertTrue(String.format("A fact does not exist for %s->%s", key, value),
                    resultFacts
                            .keySet()
                            .stream()
                            .anyMatch(resultKey ->
                                    resultFacts.containsKey(key) &&
                                    resultFacts.get(resultKey).getName().equals(key) &&
                                    resultFacts.get(resultKey).getValue().equals(value) &&
                                    resultFacts.get(resultKey).getSource().equals(expectedSource) &&
                                    resultFacts.get(resultKey).getSourceType().equals(expectedSourceType)
                                    //TODO: Add test here for sourceDate if/when we set it
                            )
                    )
        );

        // Test that CalPass values are in the facts
        Map<String, String> calPassJsonMap = mapper.readValue(calPassJson, new TypeReference<HashMap<String, String>>() {});
        calPassJsonMap.forEach((key, value) ->
            Assert.assertTrue(String.format("A fact does not exist for %s->%s", key, value),
                    resultFacts
                            .keySet()
                            .stream()
                            .anyMatch(resultKey ->
                                    resultFacts.containsKey(key) &&
                                    resultFacts.get(resultKey).getName().equals(key) &&
                                    resultFacts.get(resultKey).getValue().equals(value) &&
                                    resultFacts.get(resultKey).getSource().equals(expectedSource) &&
                                    resultFacts.get(resultKey).getSourceType().equals(expectedSourceType)
                                    //TODO: Add test here for sourceDate if/when we set it
                            )
            )
        );
    }

    private Map<String, String> getExtraRequestDataMap() {
        Map<String, String> map = new HashMap<>();

        map.put("someOtherRandomKey", "Isn'tUnitTestingFun?");
        map.put("whatIsThePoint", "youWillSoonSee");

        return map;
    }

    private Map<String, String> getValidRequestDataMap() {
        Map<String, String> map = new HashMap<>();

        map.put("ccc_id", "someCCCId");
        map.put("college_id", "aMISCode");
        //map.put("intersegment_key", "thisIsStringIsLongerThanTheOthersForNoRealReason");

        return map;
    }

    private String getValidCalPassJson() {
        return "{ " +
                "\"englishY\": false, " +
                "\"englishA\": true, " +
                "\"englishB\": true, " +
                "\"englishC\": true, " +
                "\"preAlgebra\": true, " +
                "\"algebraI\": false, " +
                "\"algebraII\": false, " +
                "\"mathGE\": false, " +
                "\"statistics\": false, " +
                "\"collegeAlgebra\": false, " +
                "\"trigonometry\": false, " +
                "\"preCalculus\": false, " +
                "\"calculusI\": false, " +
                "\"readingM_UboundY\": false, " +
                "\"readingY_UboundY\": false, " +
                "\"readingA_UboundY\": true, " +
                "\"readingB_UboundY\": true, " +
                "\"readingC_UboundY\": true, " +
                "\"readingM_UboundA\": false, " +
                "\"readingA_UboundA\": false, " +
                "\"readingB_UboundA\": false, " +
                "\"readingC_UboundA\": false, " +
                "\"readingM_UboundB\": false, " +
                "\"readingB_UboundB\": true, " +
                "\"readingC_UboundB\": false, " +
                "\"eslY_UboundY\": false, " +
                "\"eslA_UboundY\": true, " +
                "\"eslB_UboundY\": true, " +
                "\"eslA_UboundA\": false, " +
                "\"eslB_UboundA\": false, " +
                "\"eslC_UboundA\": true, " +
                "\"eslB_UboundB\": false, " +
                "\"eslC_UboundB\": false, " +
                "\"eslD_UboundB\": true, " +
                "\"isTreatment\": false " +
                "}";
    }
}
