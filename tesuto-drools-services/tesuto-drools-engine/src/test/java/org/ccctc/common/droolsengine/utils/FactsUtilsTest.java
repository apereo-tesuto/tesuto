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
package org.ccctc.common.droolsengine.utils;

import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={TestingConfiguration.class})
@ComponentScan
public class FactsUtilsTest {
    
    @Autowired
    private FactsUtils factsUtils;

    @Test
    public void validBuildHeaders() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("oauthToken", "facebearertoken");
        HttpHeaders headers = factsUtils.buildHeaders(facts);
;        assertTrue("header [Content-type] not found", headers.containsKey("Content-type"));
    }
    
    @Test
    public void getOauthTokenValid() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("oauthToken", "dummyToken");
        String oauthToken = factsUtils.getOauthToken(facts);
        assertEquals("retrieve oauthToken does not match expected", "dummyToken", oauthToken);
    }
    
    @Test
    public void getOauthTokenNoToken() {
        Map<String, Object> facts = new HashMap<String, Object>();
        String oauthToken = factsUtils.getOauthToken(facts);
        assertEquals("", oauthToken);    
    }

    @Test
    public void getOauthTokenBlankToken() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("oauthToken", null);
        String oauthToken = factsUtils.getOauthToken(facts);
        assertEquals("", oauthToken);
    }
}
