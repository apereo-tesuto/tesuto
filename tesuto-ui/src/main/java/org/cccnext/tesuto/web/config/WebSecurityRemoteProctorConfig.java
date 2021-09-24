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
package org.cccnext.tesuto.web.config;

import org.cccnext.tesuto.activation.NilAuthenticationManager;
import org.cccnext.tesuto.activation.RemoteProctorAuthenticationFilter;
import org.cccnext.tesuto.springboot.ServiceWebSecurityConfigurer;
import org.cccnext.tesuto.web.security.NoAuthenticationEntryPoint;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

//@Service
public class WebSecurityRemoteProctorConfig implements ServiceWebSecurityConfigurer  {

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(noAuthenticationEntryPoint()).and()
				.antMatcher("/service/v1/remote-proctor/**")
				.addFilterAfter(remoteProctorAuthorizationFilter(), BasicAuthenticationFilter.class);
	}

	RemoteProctorAuthenticationFilter remoteProctorAuthorizationFilter() {
		RemoteProctorAuthenticationFilter filter = new RemoteProctorAuthenticationFilter();
		filter.setAuthenticationManager(nilAuthenticationManager());
		return filter;
	}

	private NilAuthenticationManager nilAuthenticationManager() {
		return new NilAuthenticationManager();
	}

	private NoAuthenticationEntryPoint noAuthenticationEntryPoint() {
		return new NoAuthenticationEntryPoint();
	}

	@Override
	public Integer getOrder() {
		return 31;
	}

}
