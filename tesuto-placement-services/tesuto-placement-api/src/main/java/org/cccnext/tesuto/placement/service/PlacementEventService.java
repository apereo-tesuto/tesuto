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

import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;

public interface PlacementEventService {
    public Collection<PlacementComponentViewDto> processPlacementEvent(PlacementEventInputDto eventData) throws Exception;
    
    /**
     * One or more placement have been created and we receive a list of action result. We should trigger a new placement assignment.
     */
    public void addPlacement(Collection<PlacementActionResult> placementResults);

    public void requestPlacementForComponent(PlacementComponentViewDto placementComponent);

    public void requestPlacementForActionResult(PlacementActionResult placementActionResult);

    public void requestAssignedPlacement(PlacementActionResult placementActionResult);

    public void requestAssignedPlacement(PlacementViewDto placement);

    public void assignPlacement(PlacementActionResult placementActionResult);

	void assignPlacement(PlacementViewDto placementView);

}
