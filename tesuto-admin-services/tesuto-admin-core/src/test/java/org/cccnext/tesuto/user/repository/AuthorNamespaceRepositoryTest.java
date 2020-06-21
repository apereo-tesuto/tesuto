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
package org.cccnext.tesuto.user.repository;

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.assembler.UserAccountDtoAssemblerImpl;
import org.cccnext.tesuto.user.model.AuthorNamespace;
import org.cccnext.tesuto.user.service.UserGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Important! These are integration tests. They connect with the database and
 * they are expecting values to be in there! These will fail without those
 * values.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional // this makes all test methods transactional!!
public class AuthorNamespaceRepositoryTest {

    @Resource(name = "authorNamespaceRepository")
    AuthorNamespaceRepository authorNamespaceRepository;

    @Autowired
    UserGenerator generator;

    @Autowired
    UserAccountRepository userAccountRepository;


    public AuthorNamespaceRepositoryTest() {
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

    /**
     * Test of findByUsername method, of class UserAccountRepository.
     */
    @Test
    public void testFindByAuthorNamespace() throws Exception {
    	String namespace = "AUTHOR_NAMESPACE_UNIT_TEST";
    	generator.createAuthorNamespace(namespace);
        System.out.println("findByAuthorNamespace");
        AuthorNamespace result = authorNamespaceRepository.findByNamespace(namespace);
        assertEquals(namespace, result.getNamespace());
        System.out.println("Expected Result: " + namespace);
        System.out.println("Actual Result: " + result.getNamespace());
    }

    @Test
    public void testNamespaceUserAccountListContainsProperUsers() throws Exception {
    	String namespace = "AUTHOR_NAMESPACE_UNIT_TEST";
    	generator.createAuthorNamespace(namespace);
       
    	AuthorNamespace authorNamespace = authorNamespaceRepository.findByNamespace(namespace);
        String newProctorId = generator.createProctorUserAccount(namespace);
        authorNamespace = authorNamespaceRepository.findByNamespace(namespace);
        int afterUserCount = authorNamespace.getUserAccountList().size();
        assertTrue("Creating a new developer account should increase namespace user account by one",
                afterUserCount == 1);
        assertTrue("Namespace user account list should contain newly created user",
                authorNamespace.getUserAccountList().contains(userAccountRepository.findByUserAccountId(newProctorId)));
    }
}
