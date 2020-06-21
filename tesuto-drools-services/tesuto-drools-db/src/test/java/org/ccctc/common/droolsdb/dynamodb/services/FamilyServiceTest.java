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
package org.ccctc.common.droolsdb.dynamodb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.amazonaws.services.dynamodbv2.exceptions.DynamoDBLocalServiceException;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class })
public class FamilyServiceTest extends DynamoDBTest {
    private static final String DEFAULT_COLLEGE_ID = "abc";
    private static final String DEFAULT_COLLEGE_NAME = "/sampleCollege.json";
    private static final String DEFAULT_COLLEGE2_NAME = "/sampleCollege2.json";

    @Autowired
    private IFamilyService familyService;

    private DynamoDBProxyServer server;

    @Test
    public void getActiveFamilies() {
        List<FamilyDTO> familyDTOs = familyService.getFamilies(StandardStatus.ACTIVE);
        assertEquals(1, familyDTOs.size());
    }

    @Test
    public void getAllFamilies() {
        List<FamilyDTO> familyDTOs = familyService.getFamilies();
        assertEquals(2, familyDTOs.size());
    }

    @Test
    public void getFamilyByFamilyCodeCode() {
        FamilyDTO familyDTO = familyService.getFamilyByFamilyCode("ZZ1");
        assertEquals(DEFAULT_COLLEGE_ID, familyDTO.getId());
    }

    @Test
    public void getFamilyByMissingFamilyCode() {
        try {
            familyService.getFamilyByFamilyCode("not-found");
            fail("Should fail for unmatched code");
        }
        catch (ObjectNotFoundException e) {
            // success
        }
        catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }

        try {
            familyService.getFamilyByFamilyCode(null);
            fail("Should fail for blank/null code");
        }
        catch (ObjectNotFoundException e) {
            // success
        }
        catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void getFamilyById() {
        FamilyDTO familyDTO = familyService.getFamilyById(DEFAULT_COLLEGE_ID);
        assertEquals(DEFAULT_COLLEGE_ID, familyDTO.getId());
        assertEquals(1, familyDTO.getEngineDTOs().size());
        EngineDTO engineDTO = familyDTO.getEngineDTO("sns-listener");
        assertEquals("sns-listener", engineDTO.getName());
    }

    @Test(expected=ObjectNotFoundException.class)
    public void getFamilyByIdInvalidId() {
        familyService.getFamilyById("not-found");
    }

    @Test(expected=ObjectNotFoundException.class)
    public void getFamilyByIdNullId() {
        familyService.getFamilyById(null);
    }
    
    @Before
    public void setup() {
        try {
            createCollegeTableForTest();
            loadCollege(DEFAULT_COLLEGE_NAME);
            loadCollege(DEFAULT_COLLEGE2_NAME);
        }
        catch (DynamoDBLocalServiceException e) {
            fail("Setup failed:[" + e.getMessage() + "]");
        }
        catch (Exception e) {
            fail("Setup failed:[" + e.getMessage() + "]");
        }
    }

    @After
    public void teardown() {
        if (server != null) {
            try {
                server.stop();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
