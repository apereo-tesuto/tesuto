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
package org.cccnext.tesuto.placement.repository.jpa;

import java.util.Collection;

import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.model.PlacementComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// NOTE: Placements are not audited since they are immutable
public interface PlacementComponentRepository  extends JpaRepository<PlacementComponent, String> {

    Collection<PlacementComponent> findByTrackingId(String trackingId);
		Collection<PlacementComponent> findByCollegeIdAndCccid(String collegeId, String cccid);
		Collection<PlacementComponent> findByCollegeIdAndCccidAndSubjectAreaId(String misCode, String cccid, Integer subjectAreaId);
		Collection<PlacementComponent> findByPlacements(Placement placement);

		// TODO: Review query, this needs rework.
		@Query("update PlacementComponent set placements = :placements where id in (:ids)")
		@Modifying
		void linkPlacementToPlacementComponents(@Param("placement") Placement placement, @Param("ids") Collection<String> ids);

		int deleteByCollegeId(String misCode);
}
