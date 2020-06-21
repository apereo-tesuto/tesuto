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
package org.cccnext.tesuto.content.repository.item;

import java.util.List;

import javax.annotation.Resource;

import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.cccnext.tesuto.content.repository.mongo.AssessmentItemRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.MongoException;

/**
 * Important! These are integration tests. They connect with a Mongo data store
 * and they are expecting values to be in there! These will fail without those
 * values.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentItemRepositoryTest {

    @Resource(name = "assessmentItemRepository")
    AssessmentItemRepository assessmentItemRepository;

    public AssessmentItemRepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        AssessmentItem assessmentItem = assessmentItemRepository.findByNamespaceAndIdentifierAndVersion("LSI",
                "i100000000001", 1);
        if (assessmentItem != null) {
            assessmentItemRepository.delete(assessmentItem);
        }

        assessmentItem = new AssessmentItem();
        assessmentItem.setNamespace("LSI");
        assessmentItem.setIdentifier("i100000000001");
        assessmentItem.setVersion(1);
        assessmentItem.setPublished(true);
        assessmentItemRepository.save(assessmentItem);

        assessmentItem = new AssessmentItem();
        assessmentItem.setNamespace("LSI");
        assessmentItem.setIdentifier("i100000000001");
        assessmentItem.setVersion(2);
        assessmentItem.setPublished(true);
        assessmentItemRepository.save(assessmentItem);
    }

    @After
    public void tearDown() {
        List<AssessmentItem> assessmentItemList = assessmentItemRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc("LSI", "i100000000001");
        for (AssessmentItem assessmentItem : assessmentItemList) {
            if (assessmentItem != null) {
                assessmentItemRepository.delete(assessmentItem);
            }
        }
    }

    /**
     * Test of testFindByNamespaceAndIdAndVersion method, of class
     * AssessmentItemRepository.
     */
    @Test
    public void testFindByNamespaceAndIdAndVersion() throws Exception {
        System.out.println("findByNamespaceAndIdAndVersion");
        AssessmentItem assessmentItem = assessmentItemRepository.findByNamespaceAndIdentifierAndVersion("LSI",
                "i100000000001", 1);
        if (assessmentItem != null) {
            System.out.println(
                    assessmentItem.getNamespace() + " " + assessmentItem.getId() + " " + assessmentItem.getVersion()
                            + " " + assessmentItem.isPublished() + " " + assessmentItem.getBody());
        }
    }

    /**
     * Test of testFindByNamespaceAndId method, of class
     * AssessmentItemRepository.
     */
    @Test
    public void testFindByNamespaceAndId() throws Exception {
        System.out.println("findByNamespaceAndId");
        List<AssessmentItem> assessmentItemList = assessmentItemRepository
                .findByNamespaceAndIdentifierOrderByVersionDesc("LSI", "i100000000001");
        for (AssessmentItem assessmentItem : assessmentItemList) {
            if (assessmentItem != null) {
                System.out.println(
                        assessmentItem.getNamespace() + " " + assessmentItem.getId() + " " + assessmentItem.getVersion()
                                + " " + assessmentItem.isPublished() + " " + assessmentItem.getBody());
            }
        }
        // This illustrates that the first item inserted with a given namespace,
        // identifier, and version wins. This will not
        // allow an insertion. If the user retries, it will get the next version
        // number, be unique and work. With this
        // things will at least be consistent.
        AssessmentItem assessmentItem = new AssessmentItem();
        assessmentItem.setNamespace("LSI");
        assessmentItem.setIdentifier("i100000000001");
        assessmentItem.setVersion(3); // Switching this value to a 3 will allow
                                      // the insert to happen, a 2 will throw an
                                      // exception.
        assessmentItem.setPublished(true);
        assessmentItem.setBody("overwrite?");

        System.out.println("ID before insert: " + assessmentItem.getId());
        assessmentItemRepository.save(assessmentItem); // Set breakpoint after
                                                       // this line and check
                                                       // Mongo to verify.
        System.out.println("ID after insert: " + assessmentItem.getId());
        AssessmentItem assessmentItemCheck = assessmentItemRepository.findByNamespaceAndIdentifierAndVersion(
                assessmentItem.getNamespace(), assessmentItem.getIdentifier(), assessmentItem.getVersion());
        if (!assessmentItem.getId().equals(assessmentItemCheck.getId())) {
            // The insert did not happen because of a unique constraint
            // violation!
            throw new MongoException(
                    "A very rare race condition just happened.  2 AssessmentItems were uploaded with the "
                            + "same author namespace, identifier, and version.  The first upload is kept.  The second one causes "
                            + "this error because the version is the same.  Retrying this upload will cause a version bump and update "
                            + "the conflicting content.  This is a content authoring conflict that needs to be "
                            + "resolved from a content consistency standpoint.  There is likely something being updated unexpectedly with this import.");
        }
        System.out.println("AssessmentItem.getId(): " + assessmentItem.getId());
        System.out.println("AssessmentItemCheck.getId(): " + assessmentItemCheck.getId());
    }
}
