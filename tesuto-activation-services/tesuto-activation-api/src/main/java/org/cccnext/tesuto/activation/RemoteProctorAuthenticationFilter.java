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
package org.cccnext.tesuto.activation;

import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.activation.service.TestEventService;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bruce on 2/14/17.
 */
public class RemoteProctorAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Autowired TestEventService testEventService;

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String[] uuids = httpServletRequest.getParameterValues("uuid");
        if (uuids == null || uuids.length == 0) {
            return null;
        }
        String uuid = uuids[0];
		TestEvent event = testEventService.findByUuid(uuid);
		if (event == null) {
			return null;
		}
        UserAccountDto user = testEventService.createRemoteProctorFromTestEvent(uuid, event);
        return new PreAuthenticatedAuthenticationToken(user, uuid, user.getAuthorities());
    }


    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
