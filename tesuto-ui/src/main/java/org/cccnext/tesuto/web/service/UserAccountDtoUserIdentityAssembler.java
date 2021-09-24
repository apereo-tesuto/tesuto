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
package org.cccnext.tesuto.web.service;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.springframework.stereotype.Component;


@Component(value = "userAccountDtoUserIdentityAssembler")
public class UserAccountDtoUserIdentityAssembler {

    public UserAccountDto assembleStudent(UserIdentity userIdentity) {
        if (userIdentity == null) {
            return null;
        }
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUsername(userIdentity.getCccId());
        userAccountDto.setEmailAddress(userIdentity.getEmail());
        userAccountDto.setEnabled(true);
        userAccountDto.setFailedLogins(0);
        userAccountDto.setDisplayName(userIdentity.getDisplayName());
        userAccountDto.setFirstName(userIdentity.getFirstName());
        userAccountDto.setLastName(userIdentity.getLastName());
        userAccountDto.setUserAccountId(userIdentity.getCccId());
        return userAccountDto;
    }
}
