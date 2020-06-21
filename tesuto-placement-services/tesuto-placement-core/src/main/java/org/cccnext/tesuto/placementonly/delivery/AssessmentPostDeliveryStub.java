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
package org.cccnext.tesuto.placementonly.delivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.cccnext.tesuto.client.AssessmentPostDeliveryClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

// AssessmentPostDeliveryStub should be deactivated if assessments are used for placements in the future.
@Service
public class AssessmentPostDeliveryStub extends AssessmentPostDeliveryClient {

	@Override
	public void requestPlacements(String cccid) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public void requestPlacements(String cccid, String collegeId) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public void requestPlacements(String cccid, String collegeId, String subjectAreaName) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		List<RestClientTestResult> results = new ArrayList<>();
		UriComponentsBuilder builder = this.endpointBuilder();
		RestClientTestResult result = new RestClientTestResult(AssessmentPostDeliveryStub.class,
				"not implemented due to assessments not used for placement", builder, HttpMethod.TRACE);
		result.complete(HttpStatus.I_AM_A_TEAPOT.name());
		results.add(result);
		return results;
	}
}
