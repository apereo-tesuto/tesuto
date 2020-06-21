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
package org.cccnext.tesuto.placement.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class PlacementRestClientTest {
	
	@Autowired
	PlacementRestClient client;

	@Test
	public void TestSubjectAreaURI() {
		Integer subjectAreaId = 19;
		Integer subjectAreaVersion = 2;
		String url = client.getVersionSubjectAreaBuilder(subjectAreaId, subjectAreaVersion).toUriString();
		assertEquals("http://localhost:8086/placement-service/service/v1/oauth2/subject-area/"+ subjectAreaId + "/version/" + subjectAreaVersion, url);
	}
	
	@Test
	public void TestCompositePlacementURI() {
		String collegeMisCode = "ZZ1";
		String cccid = "A1234567";
		String url = client.getPlacementComponentsBuilder(collegeMisCode, cccid).toUriString();
		assertEquals("http://localhost:8086/placement-service/service/v1/oauth2/placement-component/" + collegeMisCode + "/cccid/" + cccid, url);
	}
	
	@Test
	public void TestPlacementURI() {
		String collegeMisCode = "ZZ1";
		String cccid = "A1234567";
		String url = client.getPlacementsBuilder(collegeMisCode, cccid).toUriString();
		assertEquals("http://localhost:8086/placement-service/service/v1/oauth2/placements/college/" + collegeMisCode + "/cccid/" + cccid, url);
	}
}
