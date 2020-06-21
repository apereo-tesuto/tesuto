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


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.cccnext.tesuto.springboot.ServiceWebSecurityConfigurer;
import org.cccnext.tesuto.web.security.TesutoAccessDeniedHandler;
import org.cccnext.tesuto.web.security.SAMLAuthenticationFailureHandler;
import org.cccnext.tesuto.web.security.SAMLAuthenticationSuccessHandler;
import org.ccctc.common.commonidentity.saml.SamlIdentityService;
import org.ccctc.common.commonidentity.utils.BearerCSRFRequestMatcher;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.util.LazyMap;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.trust.httpclient.TLSProtocolSocketFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration("WebSecuritySamlConfig")
@Order(110)
public class WebSecuritySamlConfig extends WebSecurityConfigurerAdapter {
		
	@Value("${saml.key.manager.keystore}")
	String keyManagerStore;
	
	@Value("${saml.key.manager.store.pass}")
	String keyManagerStorePass;
	
	@Value("${saml.key.manager.default.key}")
	String keyManagerDefaultKey;
	
	@Value("${saml.key.manager.default.key.value}")
	String keyManagerDefaultKeyValue;
	
	@Value("${saml.metadata.provider.url}")
	String metaDataProviderUrl;
	
	@Value("${saml.metadata.provider.request.timeout.milliseconds}")
	int timeOutInMilliseconds;
	
	@Value("${saml.metadata.provider.default.idp}")
	String samlMetaDataProviderDefaultIdp;
	
	@Value("${saml.metadata.provider.classpath}")
	String samlMetadataProviderClasspath;
	
	@Autowired
	@Qualifier("samlVelocityEngine")
	VelocityEngine velocityEngine;
	
	@Value("${saml.metadata.provider.local.on}")
	Boolean metaDataProviderLocal;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	@Qualifier("tesutoAccessDeniedHandler")
	TesutoAccessDeniedHandler tesutoDeniedHandler;
	
    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    @Value("${saml.entity.id}")
    private String entityId;

    @Value("${saml.port}")
    private Integer serverPort;
	
 
	private Timer backgroundTaskTimer;
	private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;
	
	@Autowired
	private BearerCSRFRequestMatcher bearerCSRFRequestMatcher;
	
	public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
		if(multiThreadedHttpConnectionManager == null)
		this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
		return multiThreadedHttpConnectionManager;
	}
	
	private Timer backgroundTaskTimer() {
		if(backgroundTaskTimer == null)
			this.backgroundTaskTimer = new Timer(true);
		return backgroundTaskTimer;
	}
	
	@PreDestroy
	public void destroy() {
		this.backgroundTaskTimer.purge();
		this.backgroundTaskTimer.cancel();
		this.multiThreadedHttpConnectionManager.shutdown();
	}
	
    /**
     * Defines the web based security configuration.
     * 
     * @param   http It allows configuring web based security for specific http requests.
     * @throws  Exception 
     */
    @Override  
    public void configure(HttpSecurity http) throws Exception {
    	FilterChainProxy samlFilter = samlFilter(authenticationManager);
    	AntPathRequestMatcher samlRequestMatcher = new AntPathRequestMatcher("/saml/**");
    	
        http
        .addFilterAfter(samlFilter, BasicAuthenticationFilter.class)
        .addFilterBefore(samlFilter, CsrfFilter.class)
        .exceptionHandling().defaultAuthenticationEntryPointFor(samlEntryPoint(), samlRequestMatcher)
        .accessDeniedHandler(tesutoDeniedHandler);
        
    }
	
	   // Logger for SAML messages and events
    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }
    
    @Bean
    public KeyManager keyManager() {
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource storeFile = loader
                .getResource(keyManagerStore);
        String storePass = keyManagerStorePass;
        Map<String, String> passwords = new HashMap<String, String>();
        passwords.put(keyManagerDefaultKey, keyManagerDefaultKeyValue);
        String defaultKey = keyManagerDefaultKey;
        return new JKSKeyManager(storeFile, storePass, passwords, defaultKey);
    }
    
 // Entry point to initialize authentication, default values taken from
    // properties file
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }
    
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }
 
    
 // The filter is waiting for connections on URL suffixed with filterSuffix
    // and presents SP metadata there
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }
    
    @Bean
    public ExtendedMetadataDelegate spMetadataProvider() throws ResourceException, MetadataProviderException {
    	ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    	extendedMetadata.setLocal(metaDataProviderLocal);
    	ExtendedMetadataDelegate delegate = new ExtendedMetadataDelegate(resourceBackProvider(), extendedMetadata);
    	return delegate;
    }
    
    
    @Bean
    public ResourceBackedMetadataProvider resourceBackProvider() throws ResourceException, MetadataProviderException {
    	ClasspathResource resource = new ClasspathResource(samlMetadataProviderClasspath);
    	ResourceBackedMetadataProvider provider = new ResourceBackedMetadataProvider(backgroundTaskTimer(),
    			resource);
    	provider.setParserPool(parserPool());
    	return provider;
    }
    
    // IDP Metadata configuration - paths to metadata of IDPs in circle of trust
    // is hereonfig$$EnhancerBySpringCGLIB$
    // Do no forget to call iniitalize method on providers
    @Bean
    @Profile("http-metadata")
    @Qualifier("metadata")
    public CachingMetadataManager metadata() throws MetadataProviderException, ResourceException {
        List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
        providers.add(httpMetaDataProvider());
        providers.add(spMetadataProvider());
        CachingMetadataManager manager = new CachingMetadataManager(providers);
        manager.setDefaultIDP(samlMetaDataProviderDefaultIdp);
        return manager;
    }
    
    @Bean
    @Profile("no-http-metadata")
    @Qualifier("metadata")
    public CachingMetadataManager noHttpMetadata() throws MetadataProviderException, ResourceException {
        List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
        providers.add(noHttpMetadataProvider());
        providers.add(spMetadataProvider());
        CachingMetadataManager manager = new CachingMetadataManager(providers);
        manager.setDefaultIDP(samlMetaDataProviderDefaultIdp);
        return manager;
    }
    
    @Bean
    public SamlIdentityService userDetailsServiceSAML() {
    	return new SamlIdentityService();
    }
    

    @Bean
	@Autowired
    public FilterChainProxy samlFilter(AuthenticationManager authenticationManager) throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<SecurityFilterChain>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/**", "POST"),
        		samlAuthorizeProcessingFilter(authenticationManager)));
        
        return new FilterChainProxy(chains);
    }
    
    @Bean
	public SAMLAuthenticationProvider samlAuthenticationProvider() {
		SAMLAuthenticationProvider provider = new SAMLAuthenticationProvider();
		provider.setUserDetails(userDetailsServiceSAML());
		provider.setForcePrincipalAsString(false);
		return provider;
	}
    
    // Handler deciding where to redirect user after successful login
    @Bean
    public SAMLAuthenticationSuccessHandler successHandlerSAML() {
        return new SAMLAuthenticationSuccessHandler();
    }
    
    /**
     * Sets a custom authentication provider. 
     * 
     * @param   auth SecurityBuilder used to create an AuthenticationManager.
     * @throws  Exception 
     */
    @Autowired
    public void initialize(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(samlAuthenticationProvider());
        auth.getDefaultUserDetailsService();
    }
    
 // Handler deciding where to redirect user after failed login
    @Bean
    public SAMLAuthenticationFailureHandler failureHandlerSAML() {
    	SAMLAuthenticationFailureHandler failureHandler =
    			new SAMLAuthenticationFailureHandler();
    	failureHandler.setTargetUrlOnSamlFailure("/login");
    	return failureHandler;
    }
    
    @Bean
    public SAMLProcessingFilter samlAuthorizeProcessingFilter(AuthenticationManager authenticationManager) throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager);
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successHandlerSAML());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(failureHandlerSAML());
        return samlWebSSOProcessingFilter;
    }
    
    // Processor
   	@Bean
   	public SAMLProcessorImpl processor() throws VelocityException, IOException {
   		Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>();
   		bindings.add(redirectBinding());
   		bindings.add(httpPostBinding());
   		bindings.add(artifactBinding(parserPool(), velocityEngine()));
   		bindings.add(soapBinding());
   		bindings.add(paosBinding());
   		return new SAMLProcessorImpl(bindings);
   	}
   	
    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
    	WebSSOProfileConsumerImpl impl = new WebSSOProfileConsumerImpl();
    	impl.setReleaseDOM(false);
        return impl;
    }
    
    
    
    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }
    
    
    // SAML 2.0 Web SSO profile
    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }
    
    
    // SAML 2.0 Holder-of-Key Web SSO profilesamlAuthenticationProvider
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
        return new WebSSOProfileConsumerHoKImpl();
    }
    
    // SAML 2.0 ECP profile
    @Bean
    public WebSSOProfileECPImpl ecpprofile() {
        return new WebSSOProfileECPImpl();
    }
    
    @Bean
    public HTTPPostBinding httpPostBinding() throws VelocityException, IOException {
    	return new HTTPPostBinding(parserPool(), velocityEngine());
    }
    
    @Bean
    public HTTPRedirectDeflateBinding redirectBinding() {
    	return new HTTPRedirectDeflateBinding(parserPool());
    }
    
 // Bindings
    private ArtifactResolutionProfile artifactResolutionProfile() {
        final ArtifactResolutionProfileImpl artifactResolutionProfile = 
        		new ArtifactResolutionProfileImpl(httpClient());
        artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
        return artifactResolutionProfile;
    }
    
 // Bindings, encoders and decoders used for creating and parsing messages
    @Bean
    public HttpClient httpClient() {
        return new HttpClient(multiThreadedHttpConnectionManager());
    }
    
    @Bean
    public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
        return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
    }
    
    @Bean
    public HTTPSOAP11Binding soapBinding() {
        return new HTTPSOAP11Binding(parserPool());
    }
    
    @Bean
    public HTTPPAOS11Binding paosBinding() {
    	return new HTTPPAOS11Binding(parserPool());
    }
    
    // Initialization of OpenSAML library
    @Bean
    public static SAMLBootstrap sAMLBootstrap() {
        return new SAMLBootstrap();
    }
    
    // XML parser pool needed for OpenSAML parsing
    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
    	StaticBasicParserPool pool = new StaticBasicParserPool();
    	Map<String,Boolean> features = new LazyMap<String,Boolean>();
    	features.put("http://apache.org/xml/features/dom/defer-node-expansion", false);
    	pool.setBuilderFeatures(features);
        return pool;
    }
    
    @Bean
	@Profile("http-metadata")
	@Qualifier("idp-ssocircle")
	public HTTPMetadataProvider httpMetaDataProvider()
			throws MetadataProviderException {
		String idpSSOCircleMetadataURL = metaDataProviderUrl;
		HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(
				this.backgroundTaskTimer(), httpClient(), idpSSOCircleMetadataURL);
		httpMetadataProvider.setParserPool(parserPool());
		
		backgroundTaskTimer.purge();
		return httpMetadataProvider;
	}
    
    @Bean
   	@Profile("no-http-metadata")
   	@Qualifier("idp-ssocircle")
   	public ResourceBackedMetadataProvider noHttpMetadataProvider()
   			throws MetadataProviderException, ResourceException {
   		return noHttpResourceBackProvider();
   	}
    
    
    public ResourceBackedMetadataProvider noHttpResourceBackProvider() throws ResourceException, MetadataProviderException {
    	ClasspathResource resource = new ClasspathResource(samlMetadataProviderClasspath);
    	ResourceBackedMetadataProvider provider = new ResourceBackedMetadataProvider(backgroundTaskTimer(),
    			resource);
    	provider.setParserPool(parserPool());
    	return provider;
    }
    
    // Initialization of the velocity engine
   // @Bean
    public VelocityEngine velocityEngine() {
        return velocityEngine;
    }
    

 


	//@Override
	public Integer getOrder() {
		return 10;
	} 
	
    private URL getUrlFromEntityId()  {
        if (entityId == null) {
            throw new IllegalArgumentException("entity.id property is not defined");
        }
        try {
            return new URL(entityId);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("entity.id property " + entityId +  " is  invalid. It must be a valid URL");
        }

    }
    
    @Bean
    public SAMLContextProviderImpl contextProvider() {
        if (isSecureEntityId()) {
            SAMLContextProviderLB cp = new SAMLContextProviderLB();
            cp.setScheme("https");
            cp.setServerName(getEntityIdServerName());
            cp.setServerPort(serverPort);
            cp.setIncludeServerPortInRequestURL(true);
            cp.setContextPath(serverContextPath);
            log.info("Setting Load Balancer Context provider for server: " + getEntityIdServerName() + " context: " + serverContextPath);
            return cp;
        } else {
            log.info("Using standard Context Provider (not behind load balancer");
            return new SAMLContextProviderImpl();
        }
    }
    
    /* Save for later? -Parker Neff and Scott Smith
    */

    private boolean isSecureEntityId() {
        return ("https".equals(getUrlFromEntityId().getProtocol()));
    }
    private String getEntityIdServerName() {
        return (getUrlFromEntityId().getHost());
    }
	
 ////////////////////////////////////////////////////////////////
	
     /*

    
   
    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }
 
 
    @Bean
    public SingleLogoutProfile logoutprofile() {
        return new SingleLogoutProfileImpl();
    }
 
    
    // Setup TLS Socket Factory
    @Bean
    public TLSProtocolConfigurer tlsProtocolConfigurer() {
    	return new TLSProtocolConfigurer();
    }
    
    @Bean
    public ProtocolSocketFactory socketFactory() {
        return new TLSProtocolSocketFactory(keyManager(), null, "default");
    }

    @Bean
    public Protocol socketFactoryProtocol() {
        return new Protocol("https", socketFactory(), 443);
    }

    @Bean
    public MethodInvokingFactoryBean socketFactoryInitialization() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(Protocol.class);
        methodInvokingFactoryBean.setTargetMethod("registerProtocol");
        Object[] args = {"https", socketFactoryProtocol()};
        methodInvokingFactoryBean.setArguments(args);
        return methodInvokingFactoryBean;
    }
    

    
    
    // Setup advanced info about metadata
    @Bean
    public ExtendedMetadata extendedMetadata() {
    	ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    	extendedMetadata.setIdpDiscoveryEnabled(true); 
    	extendedMetadata.setSignMetadata(false);
    	extendedMetadata.setEcpEnabled(true);
    	return extendedMetadata;
    }
    
    // IDP Discovery Service
    @Bean
    public SAMLDiscovery samlIDPDiscovery() {
        SAMLDiscovery idpDiscovery = new SAMLDiscovery();
        idpDiscovery.setIdpSelectionPath("/saml/idpSelection");
        return idpDiscovery;
    }
    
	
 
  
 
    // Filter automatically generates default SP metadata
    @Bean
    public MetadataGenerator metadataGenerator() {
        MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setEntityId("com:vdenotaris:spring:sp");
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager()); 
        return metadataGenerator;
    }
     
    @Bean
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
        SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
        samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(successHandlerSAML());
        samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager);
        samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(failureHandlerSAML());
        return samlWebSSOHoKProcessingFilter;
    }
      
     
    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }
     
    // Handler for successful logout
    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
        successLogoutHandler.setDefaultTargetUrl("/");
        return successLogoutHandler;
    }
     
    // Logout handler terminating local session
    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler = 
        		new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }
 
    // Filter processing incoming logout messages
    // First argument determines URL user will be redirected to after successful
    // global logout
    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(),
                logoutHandler());
    }
     
    // Overrides default logout processing filter with the one processing SAML
    // messages
    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successLogoutHandler(),
                new LogoutHandler[] { logoutHandler() },
                new LogoutHandler[] { logoutHandler() });
    }
	
    
 
 */


}
