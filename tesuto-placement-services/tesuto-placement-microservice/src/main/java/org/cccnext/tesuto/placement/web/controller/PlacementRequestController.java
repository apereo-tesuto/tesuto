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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.client.AssessmentPostDeliveryClient;
import org.cccnext.tesuto.placement.service.MultipleMeasurePlacementRequestService;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("service/v1/placement-request")
//NOTE: If component-type is null they assessmntPostDeliverClient will not be called, may want to change for assessments
public class PlacementRequestController  extends BaseController {

    @Autowired
    MultipleMeasurePlacementRequestService multipleMeasurePlacementRequestService;

    @Autowired
    AssessmentPostDeliveryClient assessmentPostDeliveryClient;

    @Autowired
    PlacementComponentService placementComponentService;
    
    @Autowired
    PlacementEventService placementEventService;
    
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION','API')")
    @RequestMapping(value = "cccid/{cccid}", method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacements(@PathVariable("cccid") String cccid, 
            @RequestParam(defaultValue = "true") boolean newPlacementsOnly, @RequestParam(name="component-type", required=false) String componentType){
        
        if (StringUtils.isBlank(componentType)  || StringUtils.equalsIgnoreCase(componentType, "mmap")) {
            multipleMeasurePlacementRequestService.requestPlacements(cccid, newPlacementsOnly);
        }
        if (StringUtils.equalsIgnoreCase(componentType, "tesuto")) {
            assessmentPostDeliveryClient.requestPlacements(cccid);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION','API')")
    @RequestMapping(value = "colleges/{college-miscode}/cccid/{cccid}", method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacements( @PathVariable("college-miscode") String collegeId, @PathVariable("cccid") String cccid, 
            @RequestParam(defaultValue = "true") boolean newPlacementsOnly, @RequestParam(name="component-type", required=false) String componentType){
        
        if (StringUtils.isBlank(componentType)  || StringUtils.equalsIgnoreCase(componentType, "mmap")) {
            multipleMeasurePlacementRequestService.requestPlacements(collegeId, cccid, newPlacementsOnly);
        }
        if (StringUtils.equalsIgnoreCase(componentType, "tesuto")) {
            assessmentPostDeliveryClient.requestPlacements(cccid, collegeId);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION','API')")
    @RequestMapping(value = "colleges/{college-miscode}/cccid/{cccid}/{subjectArea}", method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacements( @PathVariable("college-miscode") String collegeId, @PathVariable("cccid") String cccid, @PathVariable("subjectArea") String subjectAreaName, 
            @RequestParam(defaultValue = "true") boolean newPlacementsOnly, @RequestParam(name="component-type", required=false) String componentType){

        if (StringUtils.isBlank(componentType)  || StringUtils.equalsIgnoreCase(componentType, "mmap")) {
            multipleMeasurePlacementRequestService.requestPlacements(collegeId, cccid, subjectAreaName, newPlacementsOnly);
        }
        if (StringUtils.equalsIgnoreCase(componentType, "tesuto")) {
            assessmentPostDeliveryClient.requestPlacements(cccid, collegeId, subjectAreaName);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION','API')")
    @RequestMapping(value = "{placement-component-id}", method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacements( @PathVariable("placement-component-id") String placementComponentId, 
            @RequestParam(defaultValue = "true") boolean newPlacementsOnly) {

        PlacementComponentViewDto placementComponent = placementComponentService.getPlacementComponent(placementComponentId);
        placementEventService.requestPlacementForComponent(placementComponent);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
