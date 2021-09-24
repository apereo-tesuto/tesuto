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
package org.ccctc.droolseditor;

import org.ccctc.common.commonidentity.openidc.SimpleBearerFilter;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration
@Profile("local")
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigLocal extends WebSecurityConfigurerAdapter {
    @Autowired
    @Lazy
    private OIDCAuthenticationFilter oidcAuthenticationFilter = null;

    @Autowired
    @Lazy
    private SimpleBearerFilter simpleBearerFilter = null;

    @Value("${openid.security.role:ADMIN}")
    private String oauthRole;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                            AuthenticationException e) throws IOException, ServletException {
                log.error("commence, sending redirect");
                httpServletResponse.sendRedirect("/rules-editor/openid_connect_login");
            }
        }).and().csrf().disable().addFilterBefore(oidcAuthenticationFilter, BasicAuthenticationFilter.class)
                        .addFilterBefore(simpleBearerFilter, BasicAuthenticationFilter.class).authorizeRequests()
                        .antMatchers("/vaadinServlet/HEARTBEAT").permitAll()
                        .antMatchers("/openid_connect_login").permitAll()
                        .antMatchers("/healthcheck").permitAll()
                        .anyRequest().hasAnyRole(oauthRole, "ADMIN")
                        .and().logout().permitAll();
    }
}
