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
package org.cccnext.tesuto.admin.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.cccnext.tesuto.user.service.UserGenerator.PROCTOR_USER_NAME;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationAssessmentDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.cccnext.tesuto.user.service.UserGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class TestLocationServiceImplTest {

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    TestLocationService testLocationService;
    
    @Autowired
    TestLocationAssessmentService testLocationAssessmentService;

    @Autowired
    UserGenerator generator;

    @Before
    public void setup() {
        
        generator.createProctorUserAccount("UNIT_TEST_AUTHOR");
        generator.createDeletedStudentUserAccount();
    }
    
    @Test
    public void testCreateTestIsRead() {
    	TestLocationDto testLocation = testLocationService.create(createTestLocationDto());
    	TestLocationDto found = testLocationService.read(testLocation.getId());
    	assertEquals(testLocation,found);
    }

    @Test
    public void testIsUserAssociatedWithTestLocationReturnsTrueForPositiveTest() {
        UserAccountDto user = userAccountService.getUserAccountByUsername(PROCTOR_USER_NAME);

        boolean isAssociated = testLocationService.isUserAssociatedWithTestLocation(user, "ZZ1");

        assertTrue(isAssociated);
    }

    @Test
    public void testIsUserAssociatedWithTestLocationReturnsFalseForNegativeTest() {
        UserAccountDto user = userAccountService.getUserAccountByUsername(PROCTOR_USER_NAME);

        boolean isAssociated = testLocationService.isUserAssociatedWithTestLocation(user, "51");

        assertFalse(isAssociated);
    }

    @Test
    public void testEditTestLocationDoesNotWipeUserAssociations() {
        int expectedNumberOfAccounts = userAccountService.getUsersByTestLocation("ZZ2").size();
        TestLocationDto testLocationDto = testLocationService.read("ZZ2");
        testLocationDto.setName(testLocationDto.getName() + " EDITED");

        testLocationService.editTestLocationWithAssessments(testLocationDto.getId(), testLocationDto, null);
        int actualNumberOfAccounts = userAccountService.getUsersByTestLocation("ZZ2").size();

        assertEquals(expectedNumberOfAccounts, actualNumberOfAccounts);
    }

    @Test
    public void testEditTestLocationWithAssessmentsProperlyEditsLocations() {
        String expectedName = "Modified Location";
        Integer expectedCapacity = 321;
        boolean expectedEnabled = false;

        TestLocationDto testLocationDto = createTestLocationDto();
        TestLocationDto createdTestLocationDto = testLocationService.create(testLocationDto);

        testLocationDto.setName(expectedName);
        testLocationDto.setCapacity(expectedCapacity);
        testLocationDto.setEnabled(expectedEnabled);

        TestLocationDto result = testLocationService.editTestLocationWithAssessments(createdTestLocationDto.getId(), testLocationDto, null);

        assertEquals(expectedName, result.getName());
        assertEquals(expectedCapacity, result.getCapacity());
        assertEquals(createdTestLocationDto.getId(), result.getId());
        assertFalse(result.isEnabled());
    }

    @Test
    public void testEditTestLocationWithAssessmentsHandlesUpdatingAssessmentsProperly() {
        TestLocationDto testLocationDto = createTestLocationDto();
        Set<ScopedIdentifierDto> originalAssessments = createAssessmentViewDtos();
        TestLocationDto persistedTestLocationDto = testLocationService.createTestLocationWithAssessments(testLocationDto, originalAssessments);
        Set<ScopedIdentifierDto> scopedIdentifiers = createNewAssessmentViewDtos();
        TestLocationDto editedTestLocationDto = testLocationService.editTestLocationWithAssessments(persistedTestLocationDto.getId(), persistedTestLocationDto, scopedIdentifiers);
        Set<TestLocationAssessmentDto> actualAssessments = testLocationAssessmentService.getByTestLocation(editedTestLocationDto.getId());
        
        assertEquals(scopedIdentifiers.size(), actualAssessments.size());
        assertTrue(convertToTLA(persistedTestLocationDto,scopedIdentifiers).containsAll(actualAssessments));
    }

    @Test
    @Ignore //TODO need to get this specific funtionality by utilizing the service in content!!!
    public void testGetByTestLocationSkipsUnpublishedAssessments() {
        Set<ScopedIdentifierDto> assessments = new HashSet<>();
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12345"));
        TestLocationDto testLocationDto = createTestLocationDto();
        TestLocationDto persistedTestLocationDto = testLocationService.createTestLocationWithAssessments(testLocationDto, assessments);

        Set<TestLocationAssessmentDto> oneAssessment = testLocationAssessmentService.getByTestLocation(persistedTestLocationDto.getId());
        assertEquals(1, oneAssessment.size());
        assertEquals("assessment12345", oneAssessment.iterator().next().getAssessmentIdentifier());
        assertEquals("TEST", oneAssessment.iterator().next().getAssessmentNamespace());

        Set<TestLocationAssessmentDto> noAssessments = testLocationAssessmentService.getByTestLocation(persistedTestLocationDto.getId());
        assertEquals(0, noAssessments.size());
    }

    @Test
    public void testEnableTestLocationProperlySetsEnabledStatus() {
        TestLocationDto testLocationDto = createTestLocationDto();
        TestLocationDto createdTestLocation = testLocationService.create(testLocationDto);
        testLocationService.enableTestLocation(createdTestLocation.getId(), false);
        TestLocationDto result = testLocationService.read(createdTestLocation.getId());
        assertFalse(result.isEnabled());
        testLocationService.enableTestLocation(createdTestLocation.getId(), true);
        result = testLocationService.read(createdTestLocation.getId());
        assertTrue(result.isEnabled());
    }
    
    private Set<TestLocationAssessmentDto> convertToTLA(TestLocationDto testLocation, Set<ScopedIdentifierDto> scopedIdentifiers) {
    	Set<TestLocationAssessmentDto> tlas = new HashSet<>();
    	for(ScopedIdentifierDto si:scopedIdentifiers) {
    		TestLocationAssessmentDto tla = new TestLocationAssessmentDto();
    		tla.setTestLocationId(testLocation.getId());
    		tla.setAssessmentIdentifier(si.getIdentifier());
    		tla.setAssessmentNamespace(si.getNamespace());
    		tlas.add(tla);
    	}
    	return tlas;
    }

    private Set<ScopedIdentifierDto> createAssessmentViewDtos() {
        Set<ScopedIdentifierDto> assessments = new HashSet<>();
;
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12345"));
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12346"));
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12347"));
        return assessments;
    }

    private Set<ScopedIdentifierDto> createNewAssessmentViewDtos() {
        Set<ScopedIdentifierDto> assessments = new HashSet<>();
    
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12348"));
        assessments.add(new ScopedIdentifierDto("TEST", "assessment12345"));
        return assessments;
    }

    private TestLocationDto createTestLocationDto() {
        TestLocationDto testLocationDto = new TestLocationDto();
        testLocationDto.setEnabled(true);
        testLocationDto.setCollegeId("ZZ1");
        testLocationDto.setCapacity(123);
        testLocationDto.setCreatedOnDate(new Date());
        testLocationDto.setName("Test Location");
        testLocationDto.setLocationType("ON_SITE");
        testLocationDto.setLocationStatus("ACTIVE");
        return testLocationDto;
    }
}
