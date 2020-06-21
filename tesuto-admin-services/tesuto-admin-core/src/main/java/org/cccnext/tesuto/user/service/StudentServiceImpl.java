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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.util.CCCUserUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.user.assembler.UserAccountDtoAssembler;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "studentService")
public class StudentServiceImpl implements StudentService {

    private static final String SIMPLE_SEARCH = "simpleSearch";

    @Value("${STUDENT_PROFILE_URL}")
    public String studentProfileUrl;

    @Autowired
    RestOperations restTemplate;
    
    @Autowired
    UserAccountSearchService userAccounSearchService;

    @Autowired
    private UserAccountDtoAssembler userAccountDtoAssembler;

    @Autowired Mapper mapper;

    public RestOperations getRestTemplate() {
        return restTemplate;
    }

    private String getBaseUrl() {
        return studentProfileUrl;
    }

    public String getSimpleSearchUrl() {
        return getBaseUrl() + SIMPLE_SEARCH;
    }

    @Override
    public StudentViewDto findByCccid(String cccid) {
        return findByCccid( cccid, null);
    }

    @Override
    public StudentViewDto findByCccid(String cccid, Set<String> collegeFilter) {
        if (StringUtils.isEmpty(cccid)) {
            return null;
        }

        log.debug(String.format("Attempting to find Student by cccid:%s", cccid));

        StudentSearchForm studentSearchForm = new StudentSearchForm();
        ArrayList<String> ccids = new ArrayList<>();
        ccids.add(cccid);
        studentSearchForm.setCccids(ccids);
        List<StudentViewDto> foundStudents = findBySearchForm(studentSearchForm, collegeFilter);
        
        if(CollectionUtils.isEmpty(foundStudents)) {
            log.error(String.format("cccid returned no one for cccid: %s", cccid));
            throw new NonUniqueResultException("cccid did not find a user");
        }
        if (foundStudents.size() > 1) {
            log.error(String.format("cccid returned more than one user for cccid: %s", cccid));
            throw new NonUniqueResultException("cccid returned more than one user");
        }
        return foundStudents.get(0);

    }

    @Override
    public List<StudentViewDto> findBySearchForm(StudentSearchForm studentSearchForm, Set<String> collegeFilter) {
        studentSearchForm.cleanForm();
        if (!studentSearchForm.isValidForSearch()) {
            throw new IllegalArgumentException("firstName or lastName cannot be null");
        }

        return postBySearchFormInternal(studentSearchForm, collegeFilter);
    }

    /**
     * The collegeFilter values do not figure into the caching calls.  They are not used in the rest call out to the
     * Student Profile Service.  Currently on the CCC ID is included in the cache key.
     *
     * @param studentSearchForm
     * @param collegeFilter
     * @return
     */
    /* If either the phone number or email fields are populated in the student search form object, the presence of those
     * values cause caching to be excluded.  This means searches with those values will always result in a call out to
     * the Student Profile service.
     *
     * Right now we're only caching CCC IDs, so this is disabled. -scott smith
     */
    /*
    @Cacheable(cacheNames = "StudentService", key="#studentSearchForm",
            condition = "(#studentSearchForm.phone == null || #studentSearchForm.phone.length() < 1) " +
                    "&& (#studentSearchForm.email == null || #studentSearchForm.email.length() < 1)")
    */
    @Cacheable(cacheNames = "StudentService", key="#studentSearchForm",
            condition = "#studentSearchForm.cccids != null && #studentSearchForm.cccids.size() > 1")
    public List<StudentViewDto> postBySearchFormInternal(StudentSearchForm studentSearchForm, Set<String> collegeFilter) {
        HttpEntity<StudentSearchForm> entity = new HttpEntity<StudentSearchForm>(studentSearchForm.cleanForm(),
                createHeaders());

        List<StudentViewDto> foundAndFilteredUsers = new ArrayList<>();
        if(StringUtils.isNotEmpty(getBaseUrl())){
            /* If restTemplate throws an exception, search stops.
             * Locally baseUrl must be empty, if in AWS studentSearchProfile must be working 
             * If something is wrong with student search profile, notification is given to the client */
            StudentViewDto[] foundStudents = (StudentViewDto[]) restTemplate.postForObject(getSimpleSearchUrl(), entity,
                    StudentViewDto[].class);
            if(ArrayUtils.isNotEmpty(foundStudents)) {
                for(StudentViewDto foundStudent:foundStudents){
                    cleanAndRemoveColleges(foundStudent, collegeFilter);
                }
                foundAndFilteredUsers.addAll(Arrays.asList(foundStudents));
            }
        }
        
        List<StudentViewDto> foundUsers = getStudentsFromUserAccount(studentSearchForm, foundAndFilteredUsers
                .stream()
                .map(u -> u.getCccid().toLowerCase())
                .collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(foundUsers)) {
            foundAndFilteredUsers.addAll(foundUsers);
        }
        if(CollectionUtils.isNotEmpty(foundAndFilteredUsers)) {
            for(StudentViewDto foundStudent:foundAndFilteredUsers){
                cleanAndRemoveColleges(foundStudent, collegeFilter);
            }
        }
        return foundAndFilteredUsers;
    }
    
    private void cleanAndRemoveColleges(StudentViewDto foundStudent, Set<String> allowedColleges) {
        foundStudent.cleanFields();
        if(CollectionUtils.isEmpty(allowedColleges)) {
            return;
        }
        Set<String> misCodesToRemove = new HashSet<String>();
        for(String misCode :foundStudent.getCollegeStatuses().keySet()) {
            if(!allowedColleges.contains(misCode)){
                misCodesToRemove.add(misCode);
            }
        }
        if(!misCodesToRemove.isEmpty()) {
            for(String misCodeToRemove:misCodesToRemove) {
                foundStudent.getCollegeStatuses().remove(misCodeToRemove);
            }
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public class UserIdentityWithPermissions extends UserIdentity {

        private static final long serialVersionUID = 1L;

        private List<String> permissions = Arrays.asList("ADMIN", "PROCTOR");

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
    
    private List<StudentViewDto> getStudentsFromUserAccount(StudentSearchForm studentSearchForm, List<String> cccids) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setCollegeIds(studentSearchForm.getMisCodes());
        List<String> userNamesToBeFound = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(studentSearchForm.getCccids())) {
            userNamesToBeFound = studentSearchForm.getCccids().stream().filter(cccid -> !cccids.contains(cccid.toLowerCase()) )
                    .map(cccid -> cccid.toLowerCase()).collect(Collectors.toList());
        }

        if(CollectionUtils.isNotEmpty(userNamesToBeFound)) {
            searchParameters.setUsernames(new HashSet<String>(userNamesToBeFound));
        }
        searchParameters.setFirstName(studentSearchForm.getFirstName());
        searchParameters.setLastName(studentSearchForm.getLastName());
        if(isValidSearchParameters(searchParameters)) {
            List<UserAccountDto> userAccounts = userAccountDtoAssembler.assembleDto(userAccounSearchService.search(searchParameters));
            if(userAccounts != null) {
                return userAccounts.stream().filter(ua -> (isStudent(ua)
                        && CollectionUtils.isEmpty(cccids)) ||
                        (isStudent(ua) && !cccids.contains(ua.getUsername().toLowerCase())))
                        .map(ua -> CCCUserUtils.buildStudentFromUserAccount(ua)).collect(Collectors.toList());
            }
        }
        return null;
    }
    
    private Boolean isStudent(UserAccountDto userAccount) {
        return userAccount.getSecurityGroupDtos().stream().anyMatch(ua -> ua.getGroupName().equals("STUDENT"));
    }
    
    private Boolean isValidSearchParameters(SearchParameters searchParameters) {
        if(StringUtils.isNotBlank(searchParameters.getFirstName())
                && StringUtils.isNotBlank(searchParameters.getLastName())) {
            return true;
        }
        
        if(CollectionUtils.isNotEmpty(searchParameters.getUsernames())) {
            return true;
        }
        
        return false;
    }

    public StudentViewDto buildStudentFromUserAccount(UserAccountDto userAccount) {
        StudentViewDto studentDto =  mapper.map(userAccount, StudentViewDto.class);
        studentDto.setCccId(userAccount.getUsername());
        studentDto.setEmail(userAccount.getEmailAddress());
        studentDto.setPhone(userAccount.getPhoneNumber());

        if (CollectionUtils.isNotEmpty(userAccount.getColleges())) {
            Map<String, Integer> collegeStatuses = new HashMap<String, Integer>();
            userAccount.getColleges().forEach(col -> collegeStatuses.put(col.getCccId(), 1));
            studentDto.setCollegeStatuses(collegeStatuses);
        }
        studentDto.cleanFields();
        return studentDto;
    }

    /**
     * Similar code to what is in the StudentRestClient in the placement microservice.
     * Consider extracting to a common place.
     *
     * @param cccid
     * @return
     */
    @Override
    public Set<String> getCollegesAppliedTo(String cccid) {
    	if(StringUtils.isNotEmpty(getBaseUrl())){
    		String collegeUri = String.format("%s/%s/appliedcolleges", studentProfileUrl, cccid);
        try {
            
            String[] colleges = restTemplate.getForObject(collegeUri, String[].class);
            return  Arrays.stream(colleges).collect(Collectors.toSet());
        } catch (HttpClientErrorException ex) {
            log.error(
                    "Unable to find colleges through service " + collegeUri + ", apparently failure caused by http error. Service may be down.",
                    ex);
        } catch (Exception ex) {
            log.error("Error caused by service. Service may be down.", ex);
        }
    	}
    	StudentViewDto student = findByCccid(cccid, null);
    	
    	if(student != null) {
    		Map<String,Integer> statuses = student.getCollegeStatuses();
    		return statuses.keySet().stream().filter(k -> statuses.get(k).equals(new Integer(1))).collect(Collectors.toSet());
    	}
    	
        return null;
    }

	@Override
	public StudentViewDto getStudentById(String cccid) {
		return this.findByCccid(cccid);
	}
}
