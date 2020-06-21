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

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
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

@Controller
// NOTE there is a potential conflict with the CollegeController as it has the same root
@RequestMapping(value = "service/v1/colleges")
public class PlacementComponentController extends BaseController {

	@Autowired
	private PlacementComponentService placementComponentService;

	/**
	 * return the all placement components for the given college MIS Code and CCCID
	 * @param collegeMisCode
	 * @param cccid
	 * @return List of PlacementComponentViewDtos
	 */
	@PreAuthorize("hasAuthority('VIEW_PLACEMENT_DECISION')")
	@RequestMapping(value = "/{college-miscode}/cccid/{cccid}/placement-components", method = RequestMethod.GET)
	public ResponseEntity<?> viewPlacementComponents(@PathVariable("college-miscode") String collegeMisCode, @PathVariable(value="cccid") String cccid) {
		if (!userIsAffiliated(collegeMisCode)) {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}

		Collection<PlacementComponentViewDto> placementComponents;
		placementComponents = placementComponentService.getPlacementComponents(collegeMisCode, cccid);

		if (CollectionUtils.isEmpty(placementComponents)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(placementComponents, HttpStatus.OK);
	}
}
