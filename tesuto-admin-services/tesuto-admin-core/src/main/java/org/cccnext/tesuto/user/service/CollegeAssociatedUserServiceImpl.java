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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader;
import org.cccnext.tesuto.user.service.CollegeAssociatedUserService;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollegeAssociatedUserServiceImpl implements CollegeAssociatedUserService {

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    StudentService studentService;

    @Autowired
    StudentCollegeAffiliationReader affiliationService;

    @Override
    public CollegeAssociatedUser getCollegeAssociatedUser(String username) {
        UserAccountDto user = userAccountService.getUserAccountByUsername(username);
        if (user != null) {
            CollegeAssociatedUser associatedUser = new CollegeAssociatedUser();
            associatedUser.setCollegeMiscodes(user.getCollegeIds());
            associatedUser.setUsername(username);
            return associatedUser;
        } else {
            Set<String> collegeIds = new HashSet<>();
            StudentViewDto student = null;
            try {
            	student = studentService.findByCccid(username);
            } catch(Exception ex) {
            	return null;
            }
            if(student != null && student.getCollegeStatuses() != null && !student.getCollegeStatuses().isEmpty()) {
                 collegeIds.addAll(student.getCollegeStatuses().keySet());
            }
            List<StudentCollegeAffiliationDto> affiliations = affiliationService.findByStudentCccId(username);
            if(CollectionUtils.isNotEmpty(affiliations)) {
                collegeIds.addAll(affiliations.stream().map(sa -> sa.getMisCode())
                        .collect(Collectors.toSet()));
            }
            CollegeAssociatedUser associatedUser = new CollegeAssociatedUser();
            associatedUser.setCollegeMiscodes(collegeIds);
            associatedUser.setUsername(username);
            return associatedUser;
        }
    }

    @Override
    public Boolean isAssociatedToCollege(String username, String cccCollegeMisCode) {
        CollegeAssociatedUser associatedUser = getCollegeAssociatedUser(username);
        if(associatedUser == null) {
        	return new Boolean(false);
        }
        return new Boolean(associatedUser.getCollegeMiscodes().contains(cccCollegeMisCode));
    }

}
