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
package org.cccnext.tesuto.content.repository.assessment;

import java.util.List;

import javax.annotation.Resource;

import org.cccnext.tesuto.content.model.Assessment;
import org.cccnext.tesuto.content.repository.mongo.AssessmentRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentRepositoryTest {

    @Value(value = "${MONGO_USER}")
    String mongoUser;

    @Value(value = "${MONGO_PASSWORD}")
    String mongoPassword;

    @Value(value = "${MONGO_HOST_1}")
    String mongoHost1;

    @Resource(name = "assessmentRepository")
    AssessmentRepository assessmentRepository;

    public AssessmentRepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        // "namespace" : "DEVELOPER", "identifier" : "a200000000002", "version"
        // : 0
        Assessment assessment = assessmentRepository.findByNamespaceAndIdentifierAndVersion("DEVELOPER",
                "a200000000002", 1);
        if (assessment != null) {
            assessmentRepository.delete(assessment);
        }

        assessment = new Assessment();
        assessment.setNamespace("DEVELOPER");
        assessment.setIdentifier("a200000000002");
        assessment.setVersion(1);
        assessment.setPublished(true);
        assessmentRepository.save(assessment);
    }

    @After
    public void tearDown() throws Exception {
        Assessment assessment = assessmentRepository.findByNamespaceAndIdentifierAndVersion("DEVELOPER",
                "a200000000002", 1);
        if (assessment != null) {
            assessmentRepository.delete(assessment);
        }
    }

    @Test
    public void testFindByNamespaceAndIdentifierAndVersion() throws Exception {
        System.out.println("findByNamespaceAndIdAndVersion");
        Assessment assessment = assessmentRepository.findByNamespaceAndIdentifierAndVersion("DEVELOPER",
                "a200000000002", 1);
        if (assessment != null) {
            System.out.println(assessment.getNamespace() + " " + assessment.getId() + " " + assessment.getVersion()
                    + " " + assessment.isPublished());
        }
    }

    @Test
    public void testFindByNamespaceAndIdentifier() throws Exception {
        System.out.println("findByNamespaceAndId");
        System.out.println("********************************************************************************");
        System.out.println("Mongo Host1: " + mongoHost1);
        System.out.println("Mongo User: " + mongoUser);
        System.out.println("Mongo Password: " + mongoPassword);
        System.out.println("********************************************************************************");
        List<Assessment> assessmentList = assessmentRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc("DEVELOPER", "a200000000002");
        for (Assessment assessment : assessmentList) {
            if (assessment != null) {
                System.out.println(assessment.getNamespace() + " " + assessment.getId() + " " + assessment.getVersion()
                        + " " + assessment.isPublished() + " " + assessment);
            }
        }
    }
}
