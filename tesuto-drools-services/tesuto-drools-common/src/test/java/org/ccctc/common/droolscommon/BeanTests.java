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

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.DrlDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleStatus;
import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.junit.Assert;
import org.junit.Test;

import com.gtcgroup.testutil.TestUtil;

public class BeanTests {
    @Test
    public void validateAccessors() {
        // model package pojos
        Assert.assertTrue(TestUtil.verifyMutable(new EngineDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new FamilyDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new DrlDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new RuleSetDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new RuleSetRowDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new RuleStatus()));
        Assert.assertTrue(TestUtil.verifyMutable(new RuleVariableDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new RuleVariableRowDTO()));
        Assert.assertTrue(TestUtil.verifyMutable(new StandardStatus()));
        
        // validation package pojos
        Assert.assertTrue(TestUtil.verifyMutable(new DrlValidationData()));
        Assert.assertTrue(TestUtil.verifyMutable(new DrlValidationResults()));
    }
    
    
    //
    // @Test
    // public void testBasicProfileDTOBuiltFromStudent() {
    // BasicProfileDTO basicProfile = new BasicProfileDTO(new Student());
    // Assert.assertEquals("Constructors should have built the same thing", new BasicProfileDTO(), basicProfile);
    // }
    //
    // @Test
    // public void testMessagingBasicProfileBuiltFromStudent() {
    // MessagingBasicProfileDTO mbp = new MessagingBasicProfileDTO(new Student());
    // Assert.assertEquals("Constructors should have built the same thing", new MessagingBasicProfileDTO(), mbp);
    // }
}
