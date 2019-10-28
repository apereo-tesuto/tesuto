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
package org.cccnext.tesuto.importer.qti;

import org.cccnext.tesuto.springboot.PropertyFilePatternRegisteringListener;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages={
		"org.cccnext.tesuto.activation.client",
		"org.cccnext.tesuto.admin.client",
		"org.cccnext.tesuto.importer.qti",
		"org.cccnext.tesuto.importer.service",
		"org.cccnext.tesuto.service.importer.normalize",
		"org.cccnext.tesuto.delivery.client",
		"org.cccnext.tesuto.content"},
excludeFilters= {@ComponentScan.Filter(type=FilterType.REGEX,pattern="org.cccnext.tesuto.content.client.*")})
@EnableAutoConfiguration(exclude = {ContextStackAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})  // Necessary to solve an AWS permission issue with Spring Boot wackiness.
@ImportResource(locations = {"application-context.xml"})
@EnableCaching(mode = AdviceMode.ASPECTJ)
@EnableMongoRepositories(basePackages = {"org.cccnext.tesuto.content.repository.mongo"})
public class ApplicationQtiImporter {
    
    public static void main(String[] args) throws Exception {
        log.debug("Application Started");
        run(args);
        log.debug("Application Ended");
    }
    

    public static ConfigurableApplicationContext run(String[] args) throws Exception {
    	return new SpringApplicationBuilder(ApplicationQtiImporter.class)
		.listeners(new PropertyFilePatternRegisteringListener())
		.registerShutdownHook(true)
		.build()
		.run(args);
    }
    
}
