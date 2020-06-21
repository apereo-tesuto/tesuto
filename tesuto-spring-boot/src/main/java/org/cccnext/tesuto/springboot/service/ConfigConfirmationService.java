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
package org.cccnext.tesuto.springboot.service;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("configConfirmationService")
public class ConfigConfirmationService {

    @Resource(name="applicationProperties")
    private Properties applicationProperties;
    
    @Autowired
    private Environment env;


    public ConfigConfirmationService() {
    }

    public String logProperties() {
        String properties = buildConfigurationProperties();
        log.debug("CONFIGURATION PROPERTIES: \n" + properties);
        return properties;
    }

    private String buildConfigurationProperties() {
        StringBuilder configurations = new StringBuilder();
        Set<String> names = new TreeSet<String>(applicationProperties.stringPropertyNames());
        for(String name:names) {
        	String value = env.getProperty(name);
        	if(name.toLowerCase().contains("password") || name.toLowerCase().contains("secret")) {
        		configurations.append(sensitiveData(name, value));
        	} else {
        		configurations.append(String.format("%s : %s \n", name, value));
        	}
        }
        
        
        return configurations.toString();
    }

    public String sensitiveData(String propertyName, String propertyValue) {
        String configurationValue = "";
        if (propertyValue != null && propertyValue.length() > 5) {
            configurationValue = String.format("%s: %s \n", propertyName, propertyValue.substring(0, 3));
        } else {
            configurationValue = String.format("%s: NOT SET \n", propertyName);
        }
        return configurationValue;
    }

}
