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
package org.ccctc.common.droolscommon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.kie.api.runtime.KieContainer;

public class KieContainerFactoryTest {
    private KieContainerFactory factory = new KieContainerFactory();

    @Test
    public void getContainerFromDrlStringRuleCompiles() {
        StringBuilder builder = new StringBuilder();
        builder.append("package org.ccctc.common\n");
        builder.append("\n");
        builder.append("import java.util.Map\n");
        builder.append("import java.util.List\n");
        builder.append("import java.util.ArrayList\n");
        builder.append("import org.ccctc.common.droolscommon.RulesAction\n");
        builder.append("\n");
        builder.append("rule \"isActiveMilitary\" when\n");
        builder.append("facts : Map()\n");
        builder.append("cccid : String() from facts.get(\"cccid\")\n");
        builder.append("then\n");
        builder.append("List<String> users = new ArrayList<String>();\n");
        builder.append("users.add(cccid);\n");
        builder.append("RulesAction messageAction = new RulesAction(\"MESSAGE\");\n");
        builder.append("messageAction.addActionParameter(\"subject\", \"Welcome Active Duty Military Personnel\");\n");
        builder.append("messageAction.addActionParameter(\"message-body\", \"Thank you for choosing our college\");\n");
        builder.append("messageAction.addActionParameter(\"message-body-html\", \"Thank you for choosing our college\");\n");
        builder.append("messageAction.addActionParameter(\"users\", users);\n");
        builder.append("insert(messageAction);\n");
        builder.append("end\n");
        builder.append("\n");
        builder.append("query \"actions\"\n");
        builder.append("rulesActions : RulesAction();\n");
        builder.append("end\n");
        String drl = builder.toString();
        KieContainer kContainer = factory.createKieContainerFromDrlString(drl);
        assertTrue(kContainer != null);
    }

    @Test
    public void getContainerFromDrlStringRuleCompileFails() {
        StringBuilder builder = new StringBuilder();
        builder.append("package org.ccctc.common\n");
        builder.append("\n");
        builder.append("import java.util.Map\n");
        builder.append("import java.util.List\n");
        builder.append("import java.util.ArrayList\n");
        builder.append("import org.ccctc.common.droolscommon.RulesAction\n");
        builder.append("\n");
        builder.append("blah!"); // this will cause the compile to fail
        String drl = builder.toString();
        try {
            factory.createKieContainerFromDrlString(drl);
            fail("Should throw an IllegalStateException");
        }
        catch (IllegalStateException e) {
            // ignore, should get here
        }
        catch (Exception e) {
            fail("Should throw an IllegalStateException, instead threw [" + e.getMessage() + "]");
        }
    }

    @Test
    public void getContainerFromMavenRepositoryRepoExists() {
        KieContainer kieContainer = factory.createKieContainerFromMavenRepository("org.jasig.portlet.rules", "drools-rules-survey-portlet", "0.0.1");
        assertTrue(kieContainer != null);
    }

    @Test
    public void getContainerFromMavenRepositoryRepoDoesNotExist() {
        KieContainer kieContainer = factory.createKieContainerFromMavenRepository("group", "artifact", "BAD-VERSION-SNAPSHOT");
        assertTrue(kieContainer == null);
    }
}
