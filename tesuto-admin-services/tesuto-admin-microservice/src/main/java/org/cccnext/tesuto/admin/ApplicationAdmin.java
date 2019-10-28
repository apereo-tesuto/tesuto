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
package org.cccnext.tesuto.admin;

import org.cccnext.tesuto.admin.qa.UserAccountResourceService;
import org.cccnext.tesuto.springboot.PropertyFilePatternRegisteringListener;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@SpringBootApplication
@ImportResource(value = { "application-context.xml" })
@ComponentScan(basePackages={"org.cccnext.tesuto.admin","org.cccnext.tesuto.domain.util",
		"org.cccnext.tesuto.user", "org.cccnext.tesuto.content.client", "org.cccnext.tesuto.client"}
,excludeFilters= {@ComponentScan.Filter(type=FilterType.REGEX,pattern="org.cccnext.tesuto.admin.client.*")})
@EnableAutoConfiguration(exclude = {ContextStackAutoConfiguration.class,ServletWebServerFactoryAutoConfiguration.class})  // Necessary to solve an AWS permission issue with Spring Boot wackiness.
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class ApplicationAdmin {

    
    public static void main(String[] args) throws Exception {
        log.debug("Application Started");
        init(run(args));
        log.debug("Application Ended");
    }
    
    
    public static ConfigurableApplicationContext run(String[] args) throws Exception {
    	return new SpringApplicationBuilder(ApplicationAdmin.class)
		.listeners(new PropertyFilePatternRegisteringListener())
		.registerShutdownHook(true)
		.build()
		.run(args);
    }
    public static void init(ConfigurableApplicationContext context) throws Exception {
        context.getBean(UserAccountResourceService.class).init(); 
	    log.info("User Account Resouce Service has been initialized. If as requested, nominal users have been loaded.");
    }
    
}
