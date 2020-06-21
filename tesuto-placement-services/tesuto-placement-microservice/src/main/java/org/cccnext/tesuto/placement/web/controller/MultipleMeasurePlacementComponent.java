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
package org.cccnext.tesuto.placement.web.controller;

import java.io.IOException;

import javax.script.ScriptException;

import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.placement.service.MultipleMeasurePlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping(value = "service/v1/placement/multiplemeasure")
public class MultipleMeasurePlacementComponent extends BaseController {

    @Autowired
    MultipleMeasurePlacementComponentService placementComponentService;
    
    @Autowired
    PlacementEventService placementEventService;
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "oauth2", method = RequestMethod.POST)
    public ResponseEntity<?> createPlacementComponent(@RequestBody PlacementComponentActionResult placementData) throws JsonProcessingException, IOException, ScriptException {
        PlacementComponentViewDto placementComponent = placementComponentService.addPlacementComponent(placementData);
        if(placementComponent == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        placementEventService.requestPlacementForComponent(placementComponent);
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
