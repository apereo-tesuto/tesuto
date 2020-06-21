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
package org.cccnext.tesuto.springboot.web;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.springframework.security.access.AccessDeniedException;

/**
 * Created by jasonbrown on 8/11/16.
 * @Deprecated Needs to be authed with Open ID.  Extra permissions should be declared through @Preauthorize expressions.
 */
public class AuthorizerService {

    public void authorize(String collegeId, CollegeAssociatedUser user) {
        if (CollectionUtils.isEmpty(user.getCollegeMiscodes()) || !user.getCollegeMiscodes().contains(collegeId)) {
            throw new AccessDeniedException("User cannot access this college resource");
        }
    }
    
   public void authorize(String collegeId, UserAccountDto user) {
        if (CollectionUtils.isEmpty(user.getCollegeIds()) || !user.getCollegeIds().contains(collegeId)) {
            throw new AccessDeniedException("User cannot access this college resource");
        }
    }
}
