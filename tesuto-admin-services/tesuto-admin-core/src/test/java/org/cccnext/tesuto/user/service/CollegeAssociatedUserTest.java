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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.stub.StudentCollegeAffiliationReaderStub;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class CollegeAssociatedUserTest {

    @Resource(name = "studentService")
    StudentServiceImpl studentService;

    MockRestServiceServer mockServer;
    
    @Autowired
    CollegeAssociatedUserService service;
    
    @Autowired
    UserAccountService userAccountService;

    @Autowired
    StudentCollegeAffiliationReaderStub studentAffiliation;
        
    @Autowired
    UserGenerator generator;
    
    StudentViewDto student;
    
    @Before
    public void setup() {
        RestTemplate restTemplate = (RestTemplate) studentService.getRestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        student = getExpectedStudent();
    }

    private StudentCollegeAffiliationDto createAffiliation() {
        String eppn = TesutoUtils.newId();
        String cccId = TesutoUtils.newId();
        String misCode = TesutoUtils.newId();
        String authSource = TesutoUtils.newId();
        studentAffiliation.createIfNotExists(eppn, cccId, misCode, authSource);
        StudentCollegeAffiliationDto dto = new StudentCollegeAffiliationDto();
        dto.setEppn(eppn);
        dto.setStudentCccId(cccId);
        dto.setMisCode(misCode);
        dto.setAuthSource(authSource);
        return dto;
    }

    @Test
    public void testStudentCollegeAffiliationsAreAdded() throws Exception {
        StudentCollegeAffiliationDto dto = createAffiliation();
        StudentViewDto student = getExpectedStudent();
        student.setCollegeStatuses(null);
        mockServer.expect(method(HttpMethod.POST)).andRespond(withSuccess(getExpectedStudentJson(student), MediaType.APPLICATION_JSON));
        
        CollegeAssociatedUser found = service.getCollegeAssociatedUser(dto.getStudentCccId());
        Assert.assertNotNull(found);
        Assert.assertTrue(found.getCollegeMiscodes().contains(dto.getMisCode()));
        Assert.assertEquals(1, found.getCollegeMiscodes().size());
    }
    
    @Test
    public void testStudentCollegesAreAdded() throws Exception {
        createAffiliation();
        StudentViewDto student = getExpectedStudent();
        mockServer.expect(method(HttpMethod.POST)).andRespond(withSuccess(getExpectedStudentJson(student), MediaType.APPLICATION_JSON));
        CollegeAssociatedUser found = service.getCollegeAssociatedUser(student.getCccid());
        
        Assert.assertNotNull(found);
        Assert.assertEquals(student.getCccid(), found.getUsername());
        Assert.assertTrue(found.getCollegeMiscodes().containsAll(student.getCollegeStatuses().keySet()));
        Assert.assertEquals(student.getCollegeStatuses().size(), found.getCollegeMiscodes().size());
    }
    
    
    @Test
    public void testStudentCollegesAndAfiliationsAreAdded() throws Exception {
        StudentCollegeAffiliationDto dto = createAffiliation();
        StudentViewDto student = getExpectedStudent();
        student.setCccid(dto.getStudentCccId());
        mockServer.expect(method(HttpMethod.POST)).andRespond(withSuccess(getExpectedStudentJson(student), MediaType.APPLICATION_JSON));

        CollegeAssociatedUser found = service.getCollegeAssociatedUser(student.getCccid());
        Assert.assertNotNull(found);
        Assert.assertEquals(dto.getStudentCccId(), found.getUsername());
        Assert.assertTrue(found.getCollegeMiscodes().contains(dto.getMisCode()));
        Assert.assertTrue(found.getCollegeMiscodes().containsAll(student.getCollegeStatuses().keySet()));
    }
    
    @Test
    public void testUserAccountCollegesAreAdded() throws Exception {
        String userId = generator.createStudent2UserAccount();
        UserAccountDto userAccount = userAccountService.getUserAccount(userId);
        CollegeAssociatedUser found = service.getCollegeAssociatedUser(userAccount.getUsername());
        Assert.assertNotNull(found);
        Assert.assertTrue(found.getCollegeMiscodes().containsAll(userAccount.getCollegeIds()));
    }


    private StudentViewDto getExpectedStudent() {
        StudentViewDto studentViewDto = new StudentViewDto();
        studentViewDto.setCccid("AAA4315");
        studentViewDto.setCccId("AAA4315");
        studentViewDto.setFirstName("Sammy");
        studentViewDto.setLastName("Samuelson");
        studentViewDto.setMiddleName("Samurai");
        studentViewDto.setDisplayName("Sammy Samurai Samuelson");
        Map<String,Integer> collegeStatuses = new HashMap<>();
        collegeStatuses.put("ZZ1", 1);
        collegeStatuses.put("ZZ2", 1);
        studentViewDto.setCollegeStatuses(collegeStatuses);
        studentViewDto.cleanFields();
        return studentViewDto;
    }
    
    private String getCollegesAsJson(StudentViewDto studentViewDto) {
        String quote ="\"";
        StringBuilder builder = new StringBuilder("[");
        studentViewDto.getCollegeStatuses().keySet().forEach(k -> builder.append(quote)
                .append(k).append(quote).append(","));
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]");
        return builder.toString();
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
