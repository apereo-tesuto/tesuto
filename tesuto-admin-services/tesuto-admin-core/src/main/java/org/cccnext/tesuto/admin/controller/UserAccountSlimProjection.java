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
package org.cccnext.tesuto.admin.controller;

import java.util.Set;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.springframework.beans.factory.annotation.Value;

/**
 * The is a projection of the UserAccountDto. Only the properties defined in the interface will be rendered in the JSON return.
 */
public interface UserAccountSlimProjection {

    String getUserAccountId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getMiddleInitial();
    boolean isEnabled();
	
    Set<CollegeDto> getColleges();
    
    /**
     * The SPEL converts the securityGroupDtos and only returns the list of roles.
     */
    @Value("#{target.securityGroupDtos.![groupName]}")
    Set<String> getRoles();
    String getPrimaryCollegeId();

}
