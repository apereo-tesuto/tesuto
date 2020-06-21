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

import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.student.PlacementStudentViewDto;
import org.springframework.stereotype.Service;

@Service
public interface PlacementService {

	Placement addPlacement(PlacementViewDto placementViewDto);

	void updateAssignedPlacements(PlacementViewDto placementViewDto);

	Collection<PlacementViewDto> getPlacements(String misCode);

	Collection<PlacementViewDto> getPlacementsForStudent(String collegeMisCode, String userAccountId);

	Collection<PlacementStudentViewDto> getStudentViewPlacementsForStudent(String collegeMisCode, String userAccountId);

	Collection<PlacementViewDto> createPlacementsForStudent(String collegeMisCode, String userAccountId);

	Collection<PlacementViewDto> getPlacementsForStudent(String misCode, String cccid, Integer subjectAreaId);

	PlacementViewDto findNewPlacementForCccid(String cccid);

	int deleteAllPlacementsForCollegeId(String misCode);
	boolean hasPlacementsForCollegeId(String misCollegeCode);

	PlacementViewDto getPlacement(String placementId);

  Collection<PlacementViewDto> updatePlacements(Collection<PlacementViewDto> placements);
}
