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
package org.cccnext.tesuto.delivery.repository.mongo;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class AssessmentSessionRepositoryTest {
    @Resource(name="assessmentSessionRepository")
    AssessmentSessionRepository assessmentSessionRepository;

    public AssessmentSessionRepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFindByUserId() throws Exception {
        List<AssessmentSession> assessmentSessionList = assessmentSessionRepository.findByUserIdIgnoreCase("A12345");
    }

    @Test
    public void testFindByAssessmentSessionId() throws Exception {
        List<AssessmentSession> assessmentSessionList = assessmentSessionRepository.findByAssessmentSessionId("4c7f2b24-f0b0-4f25-99a1-db10eede8d15");
    }
}
