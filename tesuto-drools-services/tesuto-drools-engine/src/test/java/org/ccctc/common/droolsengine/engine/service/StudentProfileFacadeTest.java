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
package org.ccctc.common.droolsengine.engine.service;

import static org.junit.Assert.assertEquals;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

public class StudentProfileFacadeTest {

    @Test
    public void getProfileServiceURI() {
        StudentProfileFacade facade = new StudentProfileFacade();
        MockEnvironment env = new MockEnvironment();
        DroolsEngineEnvironmentConfiguration config = new DroolsEngineEnvironmentConfiguration();
        facade.setConfiguration(config);
        config.setEnvironment(env);
        
        env.setProperty("PROFILE_URL", "https://localhost:8080/student_profile/v1/users");
        String uri = facade.getProfileServiceURI() + "/1234";
        assertEquals(uri, "https://localhost:8080/student_profile/v1/users/1234");

        env.setProperty("PROFILE_URL",  "https://localhost:8080/student_profile/v1/users");
        String uri2 = facade.getProfileServiceURI() + "/2345";
        assertEquals(uri2, "https://localhost:8080/student_profile/v1/users/2345");
    }    
}
