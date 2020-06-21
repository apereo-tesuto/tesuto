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
package org.cccnext.tesuto.web;

import org.cccnext.tesuto.springboot.PropertyFilePatternRegisteringListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.aws.autoconfigure.cache.ElastiCacheAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, ElastiCacheAutoConfiguration.class, ContextStackAutoConfiguration.class})
@ImportResource(locations = {"classpath:META-INF/resources/WEB-INF/config/root-context.xml"})
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class ApplicationUI {

	@Autowired
	Environment environment;
	    
    public static void main(String[] args) throws Exception {
        log.debug("Application Started");
        run(args);
        log.debug("Application Ended");
    }
    
    public static void run(String[] args) throws Exception {
    	new SpringApplicationBuilder(ApplicationUI.class)
		.listeners(new PropertyFilePatternRegisteringListener())
		.build()
		.run(args);
    }
}
