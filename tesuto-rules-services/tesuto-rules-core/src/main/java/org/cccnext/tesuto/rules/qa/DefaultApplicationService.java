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
package org.cccnext.tesuto.rules.qa;

import java.io.IOException;

import org.cccnext.tesuto.qa.QAService;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolsdb.services.IEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultApplicationService implements QAService<EngineDTO> {
    
    @Autowired
    private IEngineService applicationService;
    

    @Override
    public void setDefaults() throws IOException {
        for(EngineDTO application: this.getResources(EngineDTO.class)) {
            try {
                applicationService.getEngineByName(application.getName());
            } catch(ObjectNotFoundException exp) {
                applicationService.save(application);
            }
        }
    }

    @Override
    public String getDirectoryPath() {
        return "classpath:defaults/application";
    }
}
