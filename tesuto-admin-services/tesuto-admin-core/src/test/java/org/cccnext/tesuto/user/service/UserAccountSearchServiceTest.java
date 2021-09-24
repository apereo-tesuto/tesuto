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
package org.cccnext.tesuto.user.service;

import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_USER_NAME_3;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.user.model.UserAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_FIRST_NAME_1;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_LAST_NAME_1;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class UserAccountSearchServiceTest {

    @Autowired
    UserAccountSearchService service;
    
    @Autowired
    UserGenerator generator;

    @Before
    public void setUp() {
       
        generator.createStudent1UserAccount();
        generator.createDeletedStudentUserAccount();
        
    }
    @Test
    public void searchByFirstName(){
        String firstName = STUDENT_FIRST_NAME_1.toLowerCase();
        SearchParameters parameters = new SearchParameters();
        parameters.setFirstName(firstName);
        Collection<UserAccount> users = service.search(parameters);
        assertTrue("Search should return at least one user "+firstName, users.size() >=1);
        assertTrue("Every returned user should be named "+ firstName,
                users.stream().allMatch(u -> firstName.equalsIgnoreCase(u.getFirstName()))
        );
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void searchByLastName(){
        String lastName = STUDENT_LAST_NAME_1.toLowerCase();
        SearchParameters parameters = new SearchParameters();
        parameters.setLastName(lastName);
        Collection<UserAccount> users = service.search(parameters);
        assertTrue("Search should return at least one user "+lastName, users.size() >=1);
        assertTrue("Every returned user should be named "+ lastName,
                users.stream().allMatch(u -> lastName.equalsIgnoreCase(u.getLastName()))
        );
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void searchByColleges() {
        String goodCollegeId = "ZZ2";
        Set<String> collegeIds = new HashSet<>();
        collegeIds.addAll(Arrays.asList(goodCollegeId, "JUNK"));
        SearchParameters parameters = new SearchParameters();
        parameters.setCollegeIds(collegeIds);
        Collection<UserAccount> users = service.search(parameters);
        assertTrue("Search should return at least one user ", users.size() >=1);
        for(UserAccount user:users) {
            assertTrue("User does not have expected college " + user.getUsername() + " has colleges: " + user.getCollegeIds().toString(),user.getCollegeIds().contains(goodCollegeId));
        }
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void searchByFirstAndLastName() {
        String firstName = STUDENT_FIRST_NAME_1.toLowerCase();
        String lastName = STUDENT_LAST_NAME_1.toLowerCase();
        SearchParameters parameters = new SearchParameters();
        parameters.setFirstName(firstName);
        parameters.setLastName(lastName);
        Collection<UserAccount> users = service.search(parameters);
        assertTrue("Search should return at least one user "+firstName, users.size() >=1);
        assertTrue("Every returned user should have first name  "+ firstName,
                users.stream().allMatch(u -> firstName.equalsIgnoreCase(u.getFirstName()))
        );
        assertTrue("Every returned user should have last name  "+ lastName,
                users.stream().allMatch(u -> lastName.equalsIgnoreCase(u.getLastName()))
        );
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void searchByNameAndColleges() {
        String lastName = STUDENT_LAST_NAME_1.toLowerCase();
        String goodCollegeId = "ZZ2";
        Set<String> collegeIds = new HashSet<>();
        collegeIds.addAll(Arrays.asList(goodCollegeId, "JUNK"));
        SearchParameters parameters = new SearchParameters();
        parameters.setLastName(lastName);
        parameters.setCollegeIds(collegeIds);
        Collection<UserAccount> users = service.search(parameters);
        assertTrue("Search should return at least one user", users.size() >=1);
        assertTrue("Every returned user should be named "+ lastName,
                users.stream().allMatch(u -> lastName.equalsIgnoreCase(u.getLastName()))
        );
        assertTrue(users.stream().allMatch(u -> u.getCollegeIds().contains(goodCollegeId)));
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void searchForDeletedUserReturnsNoResults() {
        Set<String> usernames = new HashSet<>();
        usernames.add(STUDENT_USER_NAME_3);
        SearchParameters parameters = new SearchParameters();
        parameters.setUsernames(usernames);
        Assert.assertEquals(0, service.search(parameters).size());
    }

    @Test
    public void emptySearch() {
        SearchParameters parameters = new SearchParameters();
        Assert.assertEquals(0, service.search(parameters).size());
    }
}
