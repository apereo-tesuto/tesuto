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
package org.ccctc.droolseditor.controllers;

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolsdb.services.IEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * As of v2.0.0 of the drools-engine and drools-common libraries, "applications" have been renamed/refactored as "engines"
 */
@RestController
@RequestMapping("/applications")
public class EngineController {
    @Autowired
    IEngineService engineService;

    @RequestMapping(method = RequestMethod.POST)
    public EngineDTO addEngine(@RequestBody EngineDTO engineDTO) {
        engineDTO.adjustValuesForStorage();
        EngineDTO updateEngineDefinition = engineService.save(engineDTO);
        return updateEngineDefinition;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public EngineDTO deleteEngine(@PathVariable("id") String id) {
        EngineDTO engineDTO = engineService.getEngineByName(id);
        engineDTO.adjustValuesForStorage();
        engineDTO.setStatus("inactive");
        return engineService.save(engineDTO);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EngineDTO getEngine(@PathVariable("id") String id) {
        EngineDTO engine = engineService.getEngineByName(id);
        return engine;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<EngineDTO> getEngines(@RequestParam(value = "status", required = false) String status) {
        List<EngineDTO> engineDTOs = new ArrayList<EngineDTO>();
        engineDTOs = engineService.getEnginesByStatus(status);
        return engineDTOs;
    }
}
