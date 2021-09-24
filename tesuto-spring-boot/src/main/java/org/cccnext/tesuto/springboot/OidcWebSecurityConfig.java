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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cccnext.tesuto.springboot.web.security.BCryptPasswordEncoderWrapper;
import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.openidc.SimpleBearerFilter;
import org.ccctc.web.cors.CorsConfigurationSourceImpl;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.UserInfoFetcher;
import org.mitre.openid.connect.client.service.AuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.ClientConfigurationService;
import org.mitre.openid.connect.client.service.IssuerService;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.HybridClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.mitre.openid.connect.model.PendingOIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.web.UserInfoInterceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration
@Order(120)
public class OidcWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${mitre.clientURL}")
    public String clientURL = "";

    @Value("${openid.serverURL:http://mitre.test/f/}")
    public String serverURL = "";

    @Value("${openid.server.privateURL:${openid.serverURL}}")
    public String privateURL = "";

    @Value("${openid.client.secret}")
    private String clientSecret = "";

    @Value("${openid.client.id}")
    private String clientId = "";

    @Value("${net.cccnext.openidc.jwk.url:${openid.serverURL}jwk}")
    private String jwkEndpoint = "";

    @Autowired
    @Lazy
    private SimpleBearerFilter simpleBearerFilter = null;

    @Value("${openid.security.role:ADMIN}")
    private String oauthRole;
    

    private AuthenticationManager authenticationManager;
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                    log.error("commence, sending redirect");
                    httpServletResponse.sendRedirect("/login");
                }
            })
        .and()
            .addFilterBefore(oidcAuthenticationFilter(authenticationManager), BasicAuthenticationFilter.class)
            .addFilterBefore(simpleBearerFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(new CorsFilter(new CorsConfigurationSourceImpl()), BasicAuthenticationFilter.class);
    }
    
    
    public IssuerService getIssuerService() {
        StaticSingleIssuerService is = new StaticSingleIssuerService();
        is.setIssuer(serverURL);
        return is;
    }
    
    
    public UserInfoInterceptor oidcAuthenticationFilter() {
        return new UserInfoInterceptor();
    }

    @Bean
    public OIDCAuthenticationFilter oidcAuthenticationFilter(
            AuthenticationManager authenticationManager    ) {
        OIDCAuthenticationFilter f = new OIDCAuthenticationFilter();
        f.setIssuerService(getIssuerService());
        f.setAuthenticationManager(authenticationManager);
        f.setServerConfigurationService(serverConfigurationService());
        f.setClientConfigurationService(clientConfigurationService());
        f.setAuthRequestUrlBuilder(getAuthRequestURLBuilder());
        return f;
    }

    @Bean
    public OIDCAuthenticationProvider getProvider() {
        final OIDCAuthenticationProvider p = new OIDCAuthenticationProvider();

        //Lambda to match idToken JWT roles to SimpleGrantedAuthorities.
        p.setAuthoritiesMapper((JWT idToken, UserInfo ui) -> {
            log.debug("Attempting to map user authorities: {}, {}", idToken, ui);
            try {
                final List<String> roles = idToken.getJWTClaimsSet().getStringListClaim("roles");
                log.debug("roles from jwt: {}", roles);

                if (roles == null) {
                    log.warn("No roles were returned in JWT.");
                    return null;
                }
                
                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                return authorities;
            } catch (ParseException e) {
                log.error("Could not parse jwt: {}", e);
                e.printStackTrace();
                return null;
            }
        });

        p.setUserInfoFetcher(new UserInfoFetcher() {
            @Override
            public UserInfo loadUserInfo(PendingOIDCAuthenticationToken token) {
                log.debug("Token: {}", token);
                if (token.getIdToken() instanceof SignedJWT) {
                    return new JWTUserIdentity((SignedJWT)token.getIdToken());
                }
                return super.loadUserInfo(token);
            }
        });

        return p;
    }

    public ServerConfigurationService serverConfigurationService() {
        final StaticServerConfigurationService svc = new StaticServerConfigurationService();
        ServerConfiguration cfg = new ServerConfiguration();
        cfg.setIssuer(serverURL);
        cfg.setJwksUri(privateURL + "jwk");
        cfg.setAuthorizationEndpointUri(serverURL + "authorize");
        cfg.setTokenEndpointUri(privateURL + "token");
        cfg.setUserInfoUri(privateURL + "userinfo");
        cfg.setJwksUri(privateURL+"jwk");
        cfg.setIntrospectionEndpointUri(privateURL + "introspect");

        HashMap<String, ServerConfiguration> m  = new HashMap<>();
        m.put(serverURL, cfg);

        svc.setServers(m);

        return svc;
    }

    public AuthRequestUrlBuilder getAuthRequestURLBuilder() {
        return new PlainAuthRequestUrlBuilder();
    }

    public RegisteredClient getLocalClient() {
        RegisteredClient c = new RegisteredClient();
        c.setScope(new HashSet<String>(Arrays.asList("cccid", "openid", "superuser")));
        c.setClientId(clientId);
        c.setClientSecret(clientSecret);
        c.setClientUri(clientURL);
        final HashSet<String> redirects = new HashSet<>();
        redirects.add(clientURL);
        c.setRedirectUris(redirects);
        return c;
    }

    public ClientConfigurationService clientConfigurationService() {
        HybridClientConfigurationService cfg = new HybridClientConfigurationService();
        HashMap<String, RegisteredClient> m = new HashMap<>();
        m.put(serverURL, getLocalClient());
        cfg.setClients(m);
        return cfg;
    }


    
}

