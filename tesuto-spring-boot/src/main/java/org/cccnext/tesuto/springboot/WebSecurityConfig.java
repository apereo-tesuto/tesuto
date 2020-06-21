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
package org.cccnext.tesuto.springboot;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled = true, proxyTargetClass = true)
@Order(90)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${csfr.ignore.ant.mathchers:}")
	String[] csrfAntMatchers;
	
	@Autowired(required=false)
	List<ServiceWebSecurityConfigurer> configurers;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if(csrfAntMatchers == null || csrfAntMatchers.length == 0) {
			csrfAntMatchers = new String[] {"/saml/**","/**/oauth2/**","/**/placement-request/**","/**/oauth2", "/**/students/search", "/**/students/search/**", "/rules-service/**"};
		}
		http.csrf().ignoringAntMatchers(csrfAntMatchers);
		if(configurers == null)
			return;	
		for(ServiceWebSecurityConfigurer  c: configurers.stream().sorted(Comparator.comparing(ServiceWebSecurityConfigurer::getOrder)).collect(Collectors.toList())) {
				c.configureHttpSecurity(http);
		}
	}
	

}
