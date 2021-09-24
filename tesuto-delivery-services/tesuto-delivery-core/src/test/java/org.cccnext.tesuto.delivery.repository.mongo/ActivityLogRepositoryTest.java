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

import org.cccnext.tesuto.delivery.model.internal.ActivityLog;
import org.cccnext.tesuto.delivery.repository.mongo.ActivityLogRepository;
import org.junit.*;
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
public class ActivityLogRepositoryTest {
    @Resource(name="activityLogRepository")
    ActivityLogRepository activityLogRepository;

    public ActivityLogRepositoryTest () {
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
        System.out.println("Test ActivityLogRepository.findByUserId");
        List<ActivityLog> activityLogList = activityLogRepository.findByUserId("A12345");
    }

    @Test
    public void testFindByAsId() throws Exception {
        System.out.println("Test ActivityLogRepository.findByAsId");
        List<ActivityLog> activityLogList = activityLogRepository.findByAsId("515220b8-eb0e-4248-92fb-f24579fb6567");
    }
}
