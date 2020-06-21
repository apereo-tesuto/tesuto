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
package org.cccnext.tesuto.admin.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;

public class CCCUserUtils {
    
    static public StudentViewDto buildStudentFromUserAccount(UserAccountDto userAccount) {
        if(userAccount == null) {
            return null;
        }
        StudentViewDto studentDto =  new StudentViewDto();
        studentDto.setDisplayName(userAccount.getDisplayName());
        studentDto.setFirstName(userAccount.getFirstName());
        studentDto.setLastName(userAccount.getLastName());
        studentDto.setMiddleName(userAccount.getMiddleInitial());
        studentDto.setPhone(userAccount.getPhoneNumber());
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
}
