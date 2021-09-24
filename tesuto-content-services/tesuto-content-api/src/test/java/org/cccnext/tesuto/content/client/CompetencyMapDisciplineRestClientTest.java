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
package org.cccnext.tesuto.content.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class CompetencyMapDisciplineRestClientTest {

	@Autowired
	CompetencyMapDisciplineRestClient client;
	
	@Test
	public void getDisciplinesURLTest() {
		String competencyMapDiscipline = "MAP";
		String url = client.endpointBuilder(competencyMapDiscipline).toUriString();
		assertEquals("http://localhost:8084/content-service/service/v1/competency-map-discipline/" + competencyMapDiscipline, url);

	}
}
