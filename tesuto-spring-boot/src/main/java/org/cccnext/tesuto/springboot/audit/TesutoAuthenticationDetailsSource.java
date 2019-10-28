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
package org.cccnext.tesuto.springboot.audit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * This is here to satisfy a serialization error in the placement module.
 * This is not the best location for it but it does satisfy the runtime dependency.
 * I would expect this to change at some point when we are not sharing the sessions
 * across the 2 services. -scott smith
 */
public class TesutoAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, TesutoAuthenticationDetails> {

    @Override
    public TesutoAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new TesutoAuthenticationDetails(request);
    }

}
