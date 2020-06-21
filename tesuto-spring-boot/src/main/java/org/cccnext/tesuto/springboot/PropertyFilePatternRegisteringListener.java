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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;

public class PropertyFilePatternRegisteringListener
		implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public static final String PROPERTY_FILE_PREFIX = "application";

	private static final String FILE_SUFFIX = ".properties";

	private static final String COMMON_FILE = "common-application.properties";

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment environment = event.getEnvironment();
		try {
			loadCmdLineProperties(environment);
			loadActiveProfileProperties(environment);
			loadProfileProperties(environment, environment.getDefaultProfiles());
			loadDefaultApplicationProperties(environment);

		} catch (IOException ex) {
			throw new IllegalStateException("Unable to load configuration file", ex);
		}
	}

	private void loadCmdLineProperties(ConfigurableEnvironment environment) throws IOException {
		String configlocation = environment.getProperty("spring.config.location");
		if(StringUtils.isBlank(configlocation))
			return;
		FileSystemResource fileResource = new FileSystemResource(configlocation);
		if (fileResource.exists()) {
			environment.getPropertySources().addFirst(new ResourcePropertySource(fileResource));
		}
	}

	private void loadActiveProfileProperties(ConfigurableEnvironment environment) throws IOException {
		String activeProfiles = environment.getProperty("spring.profiles.active");
		if (StringUtils.isNotBlank(activeProfiles)) {
			environment.setActiveProfiles(activeProfiles.split(","));
			loadProfileProperties(environment, activeProfiles.split(","));
		}
	}

	private void loadProfileProperties(ConfigurableEnvironment environment, String[] profiles) throws IOException {
		for (String activeProfile : profiles) {
			addClassPathFileToEnvironment(environment, PROPERTY_FILE_PREFIX + "-" + activeProfile + FILE_SUFFIX);
		}
	}

	private void loadDefaultApplicationProperties(ConfigurableEnvironment environment) throws IOException {
		addClassPathFileToEnvironment(environment, COMMON_FILE);
		addClassPathFileToEnvironment(environment, PROPERTY_FILE_PREFIX + FILE_SUFFIX);
	}

	private void addClassPathFileToEnvironment(ConfigurableEnvironment environment, String file) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(file);
		if (classPathResource.exists()) {
			environment.getPropertySources().addFirst(new ResourcePropertySource(classPathResource));
		}
	}
}
