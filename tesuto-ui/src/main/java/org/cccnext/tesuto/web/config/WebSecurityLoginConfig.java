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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.util.UrlUtils;
import org.cccnext.tesuto.springboot.audit.TesutoAuthenticationDetailsSource;
import org.cccnext.tesuto.springboot.web.security.BCryptPasswordEncoderWrapper;
import org.cccnext.tesuto.user.service.TesutoUserDetailsService;
import org.cccnext.tesuto.web.security.AuthenticationFailureHandler;
import org.cccnext.tesuto.web.security.BasicAuthenticationEntryPoint;
import org.cccnext.tesuto.web.security.TesutoAccessDeniedHandler;
import org.cccnext.tesuto.web.security.TesutoAuthenticationSuccessHandler;
import org.ccctc.common.commonidentity.openidc.CryptoBearerFilter;
import org.ccctc.common.commonidentity.utils.BearerCSRFRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
public class WebSecurityLoginConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${tesuto.allowed.admin.roles}")
	public List<String> adminRoles = new ArrayList<>();
	
	@Value("${tesuto.login.url}")
	String loginUrl;
	
	@Autowired
	@Qualifier("tesutoAuthenticationSuccessHandler")
	TesutoAuthenticationSuccessHandler successHandler;
	
	@Autowired
	@Qualifier("tesutoAccessDeniedHandler")
	TesutoAccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	UrlUtils urlUtils;
	
	@Autowired
	TesutoAuthenticationDetailsSource authenticationDetailsSource;
	
	@Autowired
	@Qualifier("cryptoBearerFilter")
	CryptoBearerFilter cryptoBearerFilter;
	
	@Autowired
	AuthenticationFailureHandler failureHandler;
	
	@Qualifier("tesutoUserDetailsService")
	@Autowired
	public TesutoUserDetailsService tesutoDetailsService;

	@Autowired
	UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
	
	@Autowired
	private BearerCSRFRequestMatcher bearerCSRFRequestMatcher;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http.sessionManagement()
		     .sessionCreationPolicy(SessionCreationPolicy.NEVER)
		.and().antMatcher("/login/**")
						.addFilterBefore(usernamePasswordAuthenticationFilter, BasicAuthenticationFilter.class).authenticationProvider(authenticationProvider()) 
						.authorizeRequests().antMatchers("/j_spring_security_switch_user").hasAnyAuthority("Admin")
						.antMatchers("/login").permitAll()
						.antMatchers("/logout").permitAll()
						.antMatchers("/error").permitAll()
						.antMatchers("/oauth2/service/**").permitAll()
						.antMatchers("/unauthorized").permitAll()
						.antMatchers("/timedout" ).permitAll()
						.antMatchers("/notfound").permitAll()
						.antMatchers("/ui_lib/**").permitAll()
						.antMatchers("/ui/**").permitAll()
						.antMatchers("/resources/**").permitAll()
						.antMatchers("/swagger/**").authenticated()
						.antMatchers("/preview/assessment/*").permitAll()
						.antMatchers("/service/v1/preview/upload/**").hasAnyAuthority("UPLOAD_PREVIEW_ASSESSMENT_PACKAGE")
						.antMatchers("/service/v1/HealthCheck").permitAll()
						.antMatchers("/preference/**").authenticated()
						.antMatchers("/home").authenticated()
						.antMatchers("/home/**").authenticated()
						.antMatchers("/upload").authenticated()
						.antMatchers("/upload/**").authenticated()
						.antMatchers("/student").authenticated()
						.antMatchers("/student/**").authenticated()
						.antMatchers("/preview/assessment/*").permitAll()
						.antMatchers("/redirect-on-role-change" ).permitAll()
						.antMatchers("/**").authenticated()
						.anyRequest().authenticated()
						.and()
				        .requiresChannel()
				            .antMatchers("/login").requiresSecure()
				            .antMatchers("/preview/assessment/*").requiresSecure()
				            .antMatchers("/home/**").requiresSecure()
				            .antMatchers("/student/**").requiresSecure()
				            .antMatchers("/upload/**").requiresSecure()
				            .antMatchers("/student/**").requiresSecure()
				            .antMatchers("/saml/**").requiresSecure()
				            .antMatchers("/preference/**").requiresSecure()
				            
		.and().exceptionHandling().defaultAuthenticationEntryPointFor(loginUrlauthenticationEntryPoint(), new AntPathRequestMatcher("/**")).accessDeniedHandler(accessDeniedHandler()).and()				
		.addFilterBefore(cryptoBearerFilter, BasicAuthenticationFilter.class);
        
	}
	private AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(tesutoDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public AuthenticationEntryPoint loginUrlauthenticationEntryPoint(){
	    return new BasicAuthenticationEntryPoint("/login");
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoderWrapper();
    }
	
	AccessDeniedHandler accessDeniedHandler() {
		if(adminRoles.isEmpty())
			urlUtils.setAdminRoles(Arrays.asList("LOCAL_ADMIN",
					"PROCTOR",
					"FACULTY",
					"SUPER_ADMIN",
					"PAPER_PENCIL_SCORER",
					"REMOTE_PROCTOR",
					"COUNSELOR",
					"PLACEMENT_READ_ONLY",
					"PLACEMENT_MANAGER",
					"REMOTE_PROCTORING_MANAGER"));
		else
			urlUtils.setAdminRoles(adminRoles);
		if(StringUtils.isBlank(urlUtils.getAdminUrl())){
			urlUtils.setAdminUrl("/home");
		}
		if(StringUtils.isBlank(urlUtils.getStudentUrl())){
			urlUtils.setStudentUrl("/student");
		}
		
		if(StringUtils.isBlank(urlUtils.getUnauthorizedUrl())){
			urlUtils.setUnauthorizedUrl("/error?error=UNAUTHORIZED");
		}
		
		
		return accessDeniedHandler;
	}
    
	
	
	//@Override
	public Integer getOrder() {
		return 20;
	}
}
