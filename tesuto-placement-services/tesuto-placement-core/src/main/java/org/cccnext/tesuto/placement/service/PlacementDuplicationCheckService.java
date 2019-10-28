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
package org.cccnext.tesuto.placement.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.model.MmapPlacementComponent;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PlacementDuplicationCheckService {

    @Autowired
    PlacementComponentService placementComponentService;

    @Autowired
    PlacementService placementService;

    /**
     * Check if the placement already exists with matching versioned subject area, and components
     * @return true if placement already exists.
     */
    public boolean isDuplicateNewPlacement(PlacementViewDto newPlacement) {
        Collection<PlacementViewDto> existingPlacements = placementService.getPlacementsForStudent(newPlacement.getCollegeId(),
                newPlacement.getCccid(), newPlacement.getDisciplineId());
        return existingPlacements.stream().anyMatch(it ->
                    it.getSubjectAreaVersion().equals(newPlacement.getSubjectAreaVersion())
                    && isDuplicatedPlacementComponents(it.getPlacementComponents(),newPlacement.getPlacementComponents()));
    }

    private boolean isDuplicatedPlacementComponents(Collection<PlacementComponentViewDto> cmps1, Collection<PlacementComponentViewDto> comps2){
        return cmps1.stream().map(t -> t.getId()).collect(Collectors.toSet()).equals( comps2.stream().map(t -> t.getId()).collect(Collectors.toSet()));
    }

    /**
     * Check if the placement already exists and if it is assigned.
     * @return true if the assigned placement matches the placement passed in, otherwise false.
     */
    public boolean isDuplicateAssignedPlacement(PlacementViewDto assigedPlacement) {
        PlacementViewDto placementViewDto = placementService.getPlacement(assigedPlacement.getId());
        if (placementViewDto == null) {
            log.error("Tried to assign a placement that does not exists");
            return true;
        }
        return placementViewDto.isAssigned();
    }

    /**
     * Check if the mmap placement component already exists, based on subject area and variable set.
     * @return true if the placement component exists
     */
    public boolean isDuplicateMmapPlacementComponent(MmapPlacementComponent placementComponent) {
        Collection<PlacementComponentViewDto> existingComponents = placementComponentService.getPlacementComponents(
                placementComponent.getCollegeId(), placementComponent.getCccid(), placementComponent.getSubjectAreaId());

        return existingComponents.stream().anyMatch(it ->
                it.getSubjectAreaVersion().equals(placementComponent.getSubjectAreaVersion())
                && StringUtils.equals(it.getMmapVariableSetId(),placementComponent.getMmapVariableSetId()));
    }

    /**
     * Check if the mmap placement component already exists, based on subject area and variable set.
     * @return true if the placement component exists
     */
    public PlacementComponentViewDto firstDuplicateMmapPlacementComponent(MmapPlacementComponent placementComponent) {
        Collection<PlacementComponentViewDto> existingComponents = placementComponentService.getPlacementComponents(
                placementComponent.getCollegeId(), placementComponent.getCccid(), placementComponent.getSubjectAreaId());

        return existingComponents.stream().filter(it ->
                it.getSubjectAreaVersion().equals(placementComponent.getSubjectAreaVersion())
                && StringUtils.equals(it.getMmapVariableSetId(),placementComponent.getMmapVariableSetId())).findFirst().orElse(null);
    }

    /**
     * Check if the assess placement component already exists, based on subject area and assessment attempt.
     * @return true if the placement component exists
     */
    public boolean isDuplicateAssessPlacementComponent(TesutoPlacementComponent placementComponent) {
        Collection<PlacementComponentViewDto> existingComponents = placementComponentService.getPlacementComponents(
                placementComponent.getCollegeId(), placementComponent.getCccid(), placementComponent.getSubjectAreaId());
        return existingComponents.stream().anyMatch(it ->
                    it.getSubjectAreaVersion().equals(placementComponent.getSubjectAreaVersion())
                    && StringUtils.equals(it.getAssessmentSessionId(),placementComponent.getAssessmentSessionId()));
    }
}
