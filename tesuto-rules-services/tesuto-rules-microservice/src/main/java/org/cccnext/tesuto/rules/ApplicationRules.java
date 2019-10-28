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
package org.cccnext.tesuto.rules;

import java.io.IOException;

import org.cccnext.tesuto.rules.qa.OnboardCollegeService;
import org.cccnext.tesuto.springboot.PropertyFilePatternRegisteringListener;


import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;	
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages={"org.cccnext.tesuto.admin.client",
		"org.cccnext.tesuto.rules"}
,excludeFilters= {@ComponentScan.Filter(type=FilterType.REGEX,pattern="org.cccnext.tesuto.rules.client.*")})
@EnableAutoConfiguration(exclude = {ContextStackAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}) 
@ImportResource(locations = {"application-context.xml"})
public class ApplicationRules  {

	public static void main(String[] args) {
	    
	    try {
	    	ConfigurableApplicationContext context = run(args);
			context.getBean(OnboardCollegeService.class).init();
		} catch (Exception exception) {
		    log.error("Failed Spectacularly for the following reasons!!!", exception);

		} 
	    log.info("Rules service initialized. Rules will be loaded dynamically as requests are received.");

	}
	
	public static ConfigurableApplicationContext run(String[] args) throws Exception {
    	return new SpringApplicationBuilder(ApplicationRules.class)
		.listeners(new PropertyFilePatternRegisteringListener())
		.registerShutdownHook(true)
		.build()
		.run(args);
    }

}
