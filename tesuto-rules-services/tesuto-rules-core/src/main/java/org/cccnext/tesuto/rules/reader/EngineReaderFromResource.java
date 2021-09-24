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
package org.cccnext.tesuto.rules.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.service.impl.DroolsRulesServiceFactoryFromFile;
import org.ccctc.common.droolsengine.dto.EngineConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Primary
public class EngineReaderFromResource extends DroolsRulesServiceFactoryFromFile {

	@Autowired
	private DroolsEngineEnvironmentConfiguration config;

	
	private Log log = LogFactory.getLog(getClass());
	@Override
	public List<EngineConfigDTO> getEngineConfigurations(ArrayList<String> logMessages) {
		ObjectMapper mapper = new ObjectMapper();
		String resourcePath = config.getEngineSourceFilePath() + "/" + config.getEngineSourceFileName();
		log.debug("Attempting to read Engine Configurations from file [" + resourcePath + "]");
        logMessages.add("-- START getEngineConfigurations(): Attempting to read Engine Configurations from file [" + resourcePath + "]");
		List<EngineConfigDTO> engines = new ArrayList<>();
		try {
			ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
			engines = mapper.readValue(resourceResolver.getResource(resourcePath).getInputStream(), new TypeReference<List<EngineConfigDTO>>() {
			});
	        
		} catch (JsonParseException e) {
			log.warn("Engine Configuration [" + resourcePath + "] is invalid JSON at [" + e.getMessage() + "]");
			log.warn("A default engine will be generated at first use");
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.warn("Engine Configuration [" + resourcePath
					+ "] not found, a default engine will be generated at first use");
		}
		engines.forEach(engineConfig -> logMessages.add("------ engineConfigDTO: " + engineConfig.toString()));
		logMessages.add("-- END getEngineConfigurations()");
		return engines;
	}
	
}
