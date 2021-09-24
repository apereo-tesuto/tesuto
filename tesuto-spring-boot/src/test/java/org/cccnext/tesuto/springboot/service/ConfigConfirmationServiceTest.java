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
package org.cccnext.tesuto.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/test-context.xml"})
public class ConfigConfirmationServiceTest {

    @Resource(name = "configConfirmationService")
    ConfigConfirmationService service;

    @Test
    public void configConfirmationServiceProperlyCreatesListOfConfigs() {
        String properties = service.logProperties();
        List<String> propertiesList = Arrays.asList(properties.split("\n"));
        propertiesList.replaceAll(String::trim); // get rid of extra whitespace just in case
        assertTrue(propertiesList.size() > 0);
        //TODO Add some sort of additional useful test
    }

    @Test
    public void configConfirmationServiceTruncatesLongPasswordProperties() {
        String propertyName = "some.property.name.with.a.password";
        String propertyValue = "luggage12345";
        String expectedResult = "some.property.name.with.a.password: lug \n";
        String result = service.sensitiveData(propertyName, propertyValue);
        assertEquals(expectedResult, result);
    }

    @Test
    public void configConfirmationServiceDoesntSetShortPasswords() {
        String propertyName = "some.short.password";
        String propertyValue = "123";
        String expectedResult = "some.short.password: NOT SET \n";
        String result = service.sensitiveData(propertyName, propertyValue);
        assertEquals(expectedResult, result);
    }
}
