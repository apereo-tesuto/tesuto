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
package org.cccnext.tesuto.admin.qa;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.service.TestLocationAssessmentService;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.cccnext.tesuto.qa.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestLocationQAService implements QAService<TestLocationDto> {

	static String  TEST_LOCATION_URI = "classpath:qa/test_locations";
	
    @Autowired
    TestLocationService testLocationService;
    

    
    @Autowired
    TestLocationAssessmentService testLocationAssessmentService;
    
	@Override
	public String getDirectoryPath() {
		return TEST_LOCATION_URI;
	}
	
    private static List<ScopedIdentifierDto> identifiers= Arrays.asList(
            new ScopedIdentifierDto[]{new ScopedIdentifierDto("TEST", "mathsamplefullv1"),
                    new ScopedIdentifierDto("TEST", "elasamplefullv1")});

	@Override
	public void setDefaults() throws IOException {
		 Set<TestLocationDto> testLocations = getResources(TestLocationDto.class);
	        for(TestLocationDto testLocation:testLocations) {
	            String testLocationId = testLocationService.upsert(testLocation).getId();
	            for(ScopedIdentifierDto identifier:identifiers) {
	                testLocationAssessmentService.create(testLocationId, identifier);
	            }
	        }
		
	}

}
