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
package org.cccnext.tesuto.user.student;

import static org.cccnext.tesuto.user.service.UserGenerator.PROCTOR_USER_NAME;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_FIRST_NAME_1;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_LAST_NAME_1;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_USER_NAME_1;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_USER_NAME_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NonUniqueResultException;

import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.util.CCCUserUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.user.service.StudentServiceImpl;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.cccnext.tesuto.user.service.UserGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
public class StudentServiceTest {

    @Resource(name = "studentService")
    StudentServiceImpl studentService;

    MockRestServiceServer mockServer;

    @Autowired
    UserGenerator userGenerator;
    
    @Autowired
    UserAccountService userAccountService;
    
    @Before
    public void setup() {
        RestTemplate restTemplate = (RestTemplate) studentService.getRestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    /**
     * StudentServiceTest will *not* pass with an empty URL the test application.properties.  There would need to be
     * another test created to test the local lookup code. A different execution is caused by changing these properties.
     * So this code is not actually hitting the CI Mitre instance as it appears because a mock server for the Student
     * Profile Service is being wired in.
	 *
     * @throws Exception
     */
    @Test
    public void testFindStudentByCCCID() throws Exception {
        StudentViewDto expectedStudentViewDto = getExpectedStudent();
        String cccid = "AAA4315";

        addExpectPost(mockServer).andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        StudentViewDto result = studentService.findByCccid(cccid);
        mockServer.verify();

        assertEquals("Get Student by CCCID", expectedStudentViewDto, result);

    }

    @Test
    public void testFindStudentByCCCIDGiveALowerCaseCCCIDWillOnlySearchForUpperCase() throws Exception {
        StudentViewDto expectedStudentViewDto = getExpectedStudent();
        String cccid = "aaa4315";

        addExpectPost(mockServer)
                .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        StudentViewDto result = studentService.findByCccid(cccid);

        mockServer.verify();
        assertEquals("Get Student by CCCID", expectedStudentViewDto, result);

    }

    @Test
    public void testFindStudentByCCCIDGiveMixedCaseCCCIDWillOnlySearchForUpperCase() throws Exception {
        StudentViewDto expectedStudentViewDto = getExpectedStudent();
        String cccid = "aAa4315";

        addExpectPost(mockServer)
                .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        StudentViewDto result = studentService.findByCccid(cccid);

        mockServer.verify();
        assertEquals("Get Student by CCCID", expectedStudentViewDto, result);

    }

    @Test
    public void testFindStudentBySearchForm() throws Exception {
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        studentSearchForm.setFirstName("Sammy");
        studentSearchForm.setLastName("Samuelson");
        studentSearchForm.setMiddleName("S");
       
        Set<String> misCodes = new HashSet<String>();
        misCodes.add("misCode1");
        studentSearchForm.setMisCodes(misCodes);
        StudentViewDto expectedStudentViewDto = getExpectedStudent();

        addExpectPost(mockServer)
                .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        List<StudentViewDto> result = studentService.findBySearchForm(studentSearchForm, misCodes);
        mockServer.verify();
        assertEquals("Number of Found Students:", 1, result.size());
        assertEquals("Get Student by Search Form", expectedStudentViewDto, result.get(0));
        System.out.println("Age: " + result.get(0).getAge());
    }

    @Test
    public void testFindStudentsBySearchFrom() throws Exception {
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        List<String> cccids = new ArrayList<String>();
        cccids.add("AAA4315");
        cccids.add("A12345");
        cccids.add("AAA4316");

        studentSearchForm.setCccids(cccids);

        StudentViewDto expectedStudentViewDto = getExpectedStudent();

        addExpectPost(mockServer)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"middleName\":null,\"firstName\":null,\"lastName\":null,\"phone\":null,\"email\":null,\"age\":null,\"misCodes\":null,\"cccids\":[\"AAA4315\",\"A12345\",\"AAA4316\"]}"))
                .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        List<StudentViewDto> results = studentService.findBySearchForm(studentSearchForm, null);

        mockServer.verify();

        assertEquals("Number of Found Students:", 1, results.size());

        for (StudentViewDto result : results) {
            assertTrue("Found results do not match expected missing found result:" + result.getCccid(),
                    cccids.contains(result.getCccid()));
        }
    }

    @Test
    public void testFindStudentsBySearchFromGiveCCCIDSwithLowerCaseWillConvertToUpperCaseForRequestToService()
            throws Exception {
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        List<String> cccids = new ArrayList<String>();
        List<String> expectedCccids = new ArrayList<>();

        cccids.add("aaa4315");
        cccids.add("a12345");
        cccids.add("AaA4316");

        for (String cccid : cccids) {
            expectedCccids.add(cccid.toUpperCase());
        }

        studentSearchForm.setCccids(cccids);

        StudentViewDto expectedStudentViewDto = getExpectedStudent();

        addExpectPost(mockServer)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"middleName\":null,\"firstName\":null,\"lastName\":null,\"phone\":null,\"email\":null,\"age\":null,\"misCodes\":null,\"cccids\":[\"AAA4315\",\"A12345\",\"AAA4316\"]}"))
                .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        List<StudentViewDto> results = studentService.findBySearchForm(studentSearchForm, null);

        mockServer.verify();

        assertEquals("Number of Found Students:", 1, results.size());

        for (StudentViewDto result : results) {
            assertTrue("Found results do not match expected missing found result:" + result.getCccid(),
                    expectedCccids.contains(result.getCccid()));
        }
    }
    

    @Test
    public void testStudentClearColleges() {
        StudentViewDto expectedStudentViewDto = getExpectedStudent();
        Map<String,Integer> collegeStatuses = new HashMap<>();
        List<String> cccids = new ArrayList<String>();
        
        collegeStatuses.put("College1", 1);
        collegeStatuses.put("College2", 1);
        collegeStatuses.put("College3", 1);
        expectedStudentViewDto.setCollegeStatuses(collegeStatuses);
        Set<String> allowedColleges = new HashSet<>();
        allowedColleges.add("College1");
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        
        cccids.add("aaa4315");
        cccids.add("a12345");
        cccids.add("AaA4316");
        
        studentSearchForm.setCccids(cccids);
        
        addExpectPost(mockServer)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("{\"middleName\":null,\"firstName\":null,\"lastName\":null,\"phone\":null,\"email\":null,\"age\":null,\"misCodes\":[\"College1\"],\"cccids\":[\"AAA4315\",\"A12345\",\"AAA4316\"]}"))
        .andRespond(withSuccess(getExpectedStudentJson(expectedStudentViewDto), MediaType.APPLICATION_JSON));

        studentSearchForm.setMisCodes(allowedColleges);
        List<StudentViewDto> results = studentService.findBySearchForm(studentSearchForm, allowedColleges);
        
        assertEquals("Number of Found Students:", 1, results.size());

        for (StudentViewDto result : results) {
            assertEquals(1, result.getCollegeStatuses().size());
            assertTrue("College Filter is not working correctly", result.getCollegeStatuses().containsKey("College1"));
        }
    }
    
    @Test
    @Transactional
    public void testUserAccountStudentSearchFindStudentByCCCID() {
        addExpectPost(mockServer).andRespond(withSuccess()); 
        
        String userAccountId = userGenerator.createStudent1UserAccount();
        StudentViewDto expectedStudent = CCCUserUtils.buildStudentFromUserAccount(userAccountService.getUserAccountByUserAccountId(userAccountId));
             
        StudentViewDto foundStudent = studentService.findByCccid(STUDENT_USER_NAME_1);
        
        assertEquals("Found Student is not expected student, expected:%s, found:%s", expectedStudent,foundStudent);
    }
    
    @Test
    @Transactional
    public void testUserAccountStudentSearchFindStudentByFirstLast() {
        addExpectPost(mockServer).andRespond(withSuccess());  
        String userAccountId = userGenerator.createStudent1UserAccount();
        StudentViewDto expectedStudent = CCCUserUtils.buildStudentFromUserAccount(userAccountService.getUserAccountByUserAccountId(userAccountId));
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        studentSearchForm.setFirstName(STUDENT_FIRST_NAME_1);
        studentSearchForm.setLastName(STUDENT_LAST_NAME_1);
        List<StudentViewDto> foundStudents = studentService.findBySearchForm(studentSearchForm, null);
        
        assertEquals("Found Students expected to be 1", 1, foundStudents.size());
        
        assertEquals(String.format("Found Student expected: %s found: %s :", studentSearchForm, foundStudents.get(0)), expectedStudent, foundStudents.get(0));
    }
    
    @Test
    @Transactional
    public void testUserAccountStudentSearchFindStudentsByCCCIDs() {
        addExpectPost(mockServer).andRespond(withSuccess()); 
        String userAccount1Id = userGenerator.createStudent1UserAccount();
        String userAccount2Id = userGenerator.createStudent2UserAccount();
        
        StudentSearchForm studentSearchForm = new StudentSearchForm();
        
        studentSearchForm.setCccids(Arrays.asList(new String[] {STUDENT_USER_NAME_1, STUDENT_USER_NAME_2 }));
        
        StudentViewDto expectedStudent1 = CCCUserUtils.buildStudentFromUserAccount(userAccountService.getUserAccountByUserAccountId(userAccount1Id));
        StudentViewDto expectedStudent2 = CCCUserUtils.buildStudentFromUserAccount(userAccountService.getUserAccountByUserAccountId(userAccount2Id));
        List<StudentViewDto> expectedStudents = Arrays.asList(new StudentViewDto[] {expectedStudent1,expectedStudent2});
        List<StudentViewDto> foundStudents = studentService.findBySearchForm(studentSearchForm, null);
        
        assertEquals("Found Students expected to be 2", 2,foundStudents.size());
        
        assertTrue(expectedStudents.contains(foundStudents.get(0)));
        assertTrue(expectedStudents.contains(foundStudents.get(1)));
        
    }    
    
    @Test(expected=NonUniqueResultException.class)
    @Transactional
    public void testUserAccountStudentSearchDoNotFindProctorByCCCID() {
        addExpectPost(mockServer).andRespond(withSuccess()); 
        
        userGenerator.createProctorUserAccount("UNIT_TEST_AUTHOR");
        studentService.findByCccid(PROCTOR_USER_NAME);
    }
    
    
    private ResponseActions addExpectPost(MockRestServiceServer mockServer) {
        return mockServer.expect(requestTo(studentService.getSimpleSearchUrl())).andExpect(method(HttpMethod.POST));
    }

    private StudentViewDto getExpectedStudent() {
        StudentViewDto studentViewDto = new StudentViewDto();
        studentViewDto.setCccid("AAA4315");
        studentViewDto.setCccId("AAA4315");
        studentViewDto.setFirstName("Sammy");
        studentViewDto.setLastName("Samuelson");
        studentViewDto.setMiddleName("Samurai");
        studentViewDto.setDisplayName("Sammy Samurai Samuelson");
        studentViewDto.cleanFields();
        return studentViewDto;
    }

    private String getExpectedStudentJson(StudentViewDto studentViewDto) {
        StringBuilder builder = new StringBuilder("[{\"");
        builder.append("cccid").append("\":\"").append(studentViewDto.getCccid()).append("\",\"").append("cccId")
                .append("\":\"").append(studentViewDto.getCccid()).append("\",\"").append("firstName").append("\":\"")
                .append(studentViewDto.getFirstName()).append("\",\"").append("lastName").append("\":\"")
                .append(studentViewDto.getLastName()).append("\",\"").append("middleName").append("\":\"")
                .append(studentViewDto.getMiddleName()).append("\",\"").append("age").append("\":\"")
                .append(studentViewDto.getAge());
        if(studentViewDto.getCollegeStatuses() != null && studentViewDto.getCollegeStatuses().size()> 0) {
            builder.append("\",\"collegeStatuses\":{");
            
            for(String key:studentViewDto.getCollegeStatuses().keySet()) {
                builder.append("\"").append(key).append("\":").append(studentViewDto.getCollegeStatuses().get(key)).append(",");
            }
            builder.delete(builder.length() - 1, builder.length());
            builder.append("}}]");
        } else {
            builder.append("\"}]");
        }
        return builder.toString();
    }
}
