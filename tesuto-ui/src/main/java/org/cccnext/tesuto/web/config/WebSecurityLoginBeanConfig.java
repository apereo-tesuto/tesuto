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

import org.cccnext.tesuto.springboot.audit.TesutoAuthenticationDetailsSource;
import org.cccnext.tesuto.user.service.TesutoUserDetailsService;
import org.cccnext.tesuto.web.security.AuthenticationFailureHandler;
import org.cccnext.tesuto.web.security.TesutoAccessDeniedHandler;
import org.cccnext.tesuto.web.security.TesutoAuthenticationSuccessHandler;
import org.ccctc.common.commonidentity.utils.BearerCSRFRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class WebSecurityLoginBeanConfig {

	SecurityContextPersistenceFilter filter;
	
	@Qualifier("tesutoUserDetailsService")
	@Autowired
	public TesutoUserDetailsService tesutoDetailsService;
	
	@Autowired
	@Qualifier("tesutoAuthenticationSuccessHandler")
	TesutoAuthenticationSuccessHandler successHandler;
	
	@Autowired
	@Qualifier("tesutoAccessDeniedHandler")
	TesutoAccessDeniedHandler accessDeniedHandler;
	

	@Bean
	AuthenticationFailureHandler failureHandler() {
		AuthenticationFailureHandler failureHandler = new AuthenticationFailureHandler();
		failureHandler.setDefaultFailureUrl("/logout");
		return failureHandler;	}


	@Bean
	TesutoAuthenticationDetailsSource authenticationDetailsSource() {
		return new TesutoAuthenticationDetailsSource();
	}
	
	@Bean
	BearerCSRFRequestMatcher  bearerCSRFRequestMatcher() {
		return new BearerCSRFRequestMatcher();
	}
	
	
	
	
	@Autowired
    public void initialize(AuthenticationManagerBuilder builder) {
		builder.authenticationProvider(authenticationProvider() );
    }
	
	
	private AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(tesutoDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
	
	@Bean
	@Autowired
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
		UsernamePasswordAuthenticationFilter filter= new UsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationFailureHandler(failureHandler());
		filter.setAuthenticationSuccessHandler(successHandler);
		filter.setUsernameParameter("j_username");
		filter.setPasswordParameter("j_password");
		filter.setFilterProcessesUrl("/login");
		filter.setAuthenticationDetailsSource(authenticationDetailsSource());
		RequestMatcher requestMatcher = new AntPathRequestMatcher("/login", "POST");
		filter.setRequiresAuthenticationRequestMatcher(requestMatcher);
		return filter;
	}


}
