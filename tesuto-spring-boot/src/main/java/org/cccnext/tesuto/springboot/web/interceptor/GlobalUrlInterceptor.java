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
package org.cccnext.tesuto.springboot.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This class is executed on *every* response as configured, that is *every* controller, as
 * opposed to static resources served through mvc:resources specified in the Spring servlet-config.xml
 * file. Those static resources *do not* hit this interceptor.  It is extremely important
 * that nothing heavy is executed here. The performance hit would be enormous!!
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GlobalUrlInterceptor extends HandlerInterceptorAdapter {

    private String accessControlAllowOrigin;
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Fixes an HTTPS Internet Explorer issue with font-awesome icons.
        response.setHeader("Pragma", "none");
        // TODO: Make this configurable via property injection.  It's a security hole right now.
        response.setHeader("Access-Control-Allow-Origin", accessControlAllowOrigin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "86400");
        super.postHandle(request, response, handler, modelAndView);
    }
    
	public void setAccessControlAllowOrigin(String accessControlAllowOrigin) {
		this.accessControlAllowOrigin = accessControlAllowOrigin;
	}
}
