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

import java.util.ArrayList;
import java.util.Collection;

import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;

/**
 * Created by bruce on 10/3/16.
 */
public class PlacementNotificationDto {
    private String misCode;
    private String cccId;
	private String eppn;
    private PlacementViewDto placement;
	private Collection<PlacementComponentViewDto> placementComponents = new ArrayList();

    public PlacementNotificationDto() {}

    public PlacementNotificationDto(String misCode, String cccId) {
        this.misCode = misCode;
        this.cccId = cccId;
    }

    public String getMisCode() {
        return misCode;
    }

    public String getCccId() {
        return cccId;
    }

	public String getEppn() {
		return eppn;
	}

	public void setEppn(String eppn) {
		this.eppn = eppn;
	}


	/**
	 * @return the placement
	 */
	public PlacementViewDto getPlacement() {
		return placement;
	}

	/**
	 * @param placement the placement to set
	 */
	public void setPlacement(PlacementViewDto placement) {
		this.placement = placement;
	}

	/**
	 * @return the placementComponents
	 */
	public Collection<PlacementComponentViewDto> getPlacementComponents() {
		return placementComponents;
	}

	/**
	 * @param placementComponents the placementComponents to set
	 */
	public void setPlacementComponents(Collection<PlacementComponentViewDto> placementComponents) {
		this.placementComponents = placementComponents;
	}

	@Override
    public String toString() {
        return "PlacementNotificationDto{" +
                "misCode='" + misCode + '\'' +
                ", cccId='" + cccId + '\'' +
                ", placement=" + placement +
			    ", placementComponents=" + placementComponents +
                '}';
    }
}
