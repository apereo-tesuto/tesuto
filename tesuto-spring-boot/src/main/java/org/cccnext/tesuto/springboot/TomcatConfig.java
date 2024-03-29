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

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {


	@Value("${http.port}")
	Integer httpPort;
	
	@Value("${server.port}") 
	int httpSSLPort;
	
	@Value("${server.servlet.context-path}") 
	String contextPath;
	
	@Value("${server.ssl.key-store}") 
	String sslKeystore;
	@Value("${server.ssl.key-store-password}") 
	String sslKeystorePassword;
	@Value("${server.ssl.enabled}") 
	Boolean sslEnabled;


	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory() throws Exception {
		TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
		if(httpPort != null || httpPort > 0) {
			Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
			connector.setPort(httpPort);
			tomcatServletWebServerFactory.addAdditionalTomcatConnectors(connector);
		}
		
		tomcatServletWebServerFactory.setPort(httpSSLPort);
		Ssl ssl =new Ssl();
		ssl.setEnabled(sslEnabled);
		ssl.setKeyPassword(sslKeystorePassword);
		ssl.setKeyStore(sslKeystore);
		tomcatServletWebServerFactory.setSsl(ssl);
		tomcatServletWebServerFactory.setContextPath(contextPath);
		
		return tomcatServletWebServerFactory;
	}


}
