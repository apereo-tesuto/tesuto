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


import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "service/v1/placement")
public class PlacementController extends BaseController {

    @Autowired
    private PlacementComponentService placementComponentService;

    @Autowired
    private PlacementService placementService;

    @Autowired
    private PlacementEventService placementEventService;

    /**
     * Trigger the placement event service thru some REST Api call.
     */
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "request", method = RequestMethod.POST)
    public ResponseEntity<?> generatePlacementDecision( @RequestBody PlacementComponentViewDto placementComponent) throws Exception {
        placementEventService.requestPlacementForComponent(placementComponent);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Trigger the placement event service thru some REST Api call.
     */
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "request/{placementComponentId}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> generatePlacementForId( @PathVariable String placementComponentId) throws Exception {
        PlacementComponentViewDto placementComponent = placementComponentService.getPlacementComponent(placementComponentId);
        if (placementComponent != null) {
            // in case we have dirty seed data. we need to set a tracking id.
            if (StringUtils.isBlank( placementComponent.getTrackingId())) {
                placementComponent.setTrackingId(TesutoUtils.newId());
            }
            placementEventService.requestPlacementForComponent(placementComponent);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Trigger the placement event service thru some REST Api call.
     */
    @PreAuthorize("hasAuthority('CREATE_PLACEMENT_DECISION')")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<?> addPlacement( @RequestBody PlacementActionResult placementAction) throws Exception {
        placementEventService.addPlacement(Collections.singleton(placementAction));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * Trigger the placement event service thru some REST Api call.
     */
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "create/oauth2", method = RequestMethod.POST)
    public ResponseEntity<?> addPlacementOauth2( @RequestBody PlacementActionResult placementAction) throws Exception {
        placementEventService.addPlacement(Collections.singleton(placementAction));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * trigger the update assigned placement event from the UI
     */
    @PreAuthorize("hasAnyAuthority('UPDATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "validateAssign", method = RequestMethod.PUT)
    public ResponseEntity<?> validateAssignedPlacement( @RequestBody PlacementActionResult placementAction) throws Exception {
        placementEventService.requestAssignedPlacement(placementAction);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * trigger the update assigned placement event from the UI
     */
    @PreAuthorize("hasAnyAuthority('UPDATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "updateAssign", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> updateAssignedPlacement( @RequestBody PlacementActionResult placementAction) throws Exception {
        placementEventService.assignPlacement(placementAction);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * trigger the update assigned placement event from the UI
     */
    @PreAuthorize("hasAnyAuthority('UPDATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "updateAssign/{placementId}", method = {RequestMethod.PUT})
    public ResponseEntity<?> updateAssignedPlacement( @PathVariable String placementId) throws Exception {
        PlacementViewDto placement = placementService.getPlacement(placementId);
        
        if (placement != null) {
            // in case we have dirty seed data. we need to set a tracking id.
            if (StringUtils.isBlank( placement.getTrackingId())) {
                placement.setTrackingId(TesutoUtils.newId());
            }
            placementEventService.assignPlacement(placement);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PreAuthorize("hasAnyAuthority('UPDATE_PLACEMENT_DECISION', 'API')")
    @RequestMapping(value = "assign", method = RequestMethod.POST)
    public ResponseEntity<?> updateAssignedPlacementPost( @RequestBody PlacementViewDto viewDto) throws Exception {
        
        if (viewDto != null) {
            // in case we have dirty seed data. we need to set a tracking id.
            if (StringUtils.isBlank( viewDto.getTrackingId())) {
                viewDto.setTrackingId(TesutoUtils.newId());
            }
            placementEventService.assignPlacement(viewDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "assign/oauth2", method = RequestMethod.POST)
    public ResponseEntity<?> updateAssignedPlacementPostOauth2( @RequestBody PlacementViewDto viewDto) throws Exception {
        
        return updateAssignedPlacementPost(viewDto);
    }
}
