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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.service.IDroolsRulesServiceFactory;
import org.ccctc.common.droolsengine.config.service.impl.ServiceConfiguratorFactoryImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class EngineReaderFactoryResource extends ServiceConfiguratorFactoryImpl implements InitializingBean {
    private Log log = LogFactory.getLog(getClass());


    private final Map<String, IDroolsRulesServiceFactory> readers = new HashMap<>();

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;
    
    @Autowired
    private EngineReaderFromResource engineReaderFromResource;
    
    
	@Override
	public IDroolsRulesServiceFactory getDroolsRulesServiceFactory() {
		IDroolsRulesServiceFactory reader = readers.get(config.getEngineSource());
        if (reader != null) {
            log.debug("Found EngineReader [" + config.getEngineSource() + "], returning :[" + reader.getClass() + "]");
            return reader;
        }
        
        if ("resource".equals(config.getEngineSource())) {
            EngineReaderFromResource readerFromResource = engineReaderFromResource;
            ArrayList<String> logMessages = new ArrayList<>();
            readerFromResource.init(logMessages);
            readers.put(config.getEngineSource(), readerFromResource);
            reader = readerFromResource;
        } else {
            reader = super.getDroolsRulesServiceFactory();
        }
        log.debug("returning Reader:[" + reader.getClass() + "]");
        return reader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		getDroolsRulesServiceFactory();
	}

}
