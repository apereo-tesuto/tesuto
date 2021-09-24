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
package org.ccctc.common.droolsengine.config.family.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.family.AbstractFamilyConfigurator;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Build a family reader that gets config from the local filesystem.
 * Logging for this reader prefixed with "+--+"
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class FamilyConfiguratorFromFile extends AbstractFamilyConfigurator {

    private List<FamilyDTO> getFamilyConfigurations() {
        ObjectMapper mapper = new ObjectMapper();
        String configPath = config.getFamilySourceFilePath() + "/" + config.getFamilySourceFileName();

        Resource resource = new FileSystemResource(configPath);
        List<FamilyDTO> families = new ArrayList<>();
        try {
            families = mapper.readValue(resource.getInputStream(), new TypeReference<List<FamilyDTO>>() {
            });
        }
        catch (JsonParseException e) {
            log.error("+--+" + e.getMessage(), e);
        }
        catch (JsonMappingException e) {
            log.error("+--+" + e.getMessage(), e);
        }
        catch (IOException e) {
            log.error("+--+" + e.getMessage(), e);
        }
        return families;
    }

    @Override
    public List<FamilyDTO> getFamilies(boolean ignoredHere) {
        List<FamilyDTO> families = getFamilyConfigurations();
        return families;
    }

    @Override
    public String getName() {
        return "file";
    }
}
