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
package org.cccnext.tesuto.springboot.servlet;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class ProfilesInitializer extends SpringBootServletInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        try {
            String profilesSystemProperties = System.getProperty("spring.profiles.active");
            // If one is provided, don't override it.
            if (profilesSystemProperties != null) {
                return;
            }
            ConfigurableEnvironment configurableEnvironment = ctx.getEnvironment();
            MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
            ResourcePropertySource resourcePropertySource = new ResourcePropertySource("classpath:application.properties");
            mutablePropertySources.addFirst(resourcePropertySource);
            String profiles = (String) resourcePropertySource.getProperty("spring.profiles.active");
            System.out.println("profiles: " + profiles); // The logger is not available at this point.
            configurableEnvironment.addActiveProfile(profiles);
            //configurableEnvironment.setRequiredProperties("spring.session.redis.namespace", "assess");
            System.out.println("mutablePropertySources: " + mutablePropertySources); // The logger is not available at this point.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
