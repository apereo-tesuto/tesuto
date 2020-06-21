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
package org.cccnext.tesuto.reports.service;

import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.content.model.ScopedIdentifier;


import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DeliveryTestDataService {
    
    @Autowired
    DeliveryTestService service;
    
    @Autowired
    CollegeService collegeService;
    
    @Autowired
    CollegeRepository collegeRepository;
    
    
    public void seedData() throws Exception {
            String simulatedProctor = "multiple@democollege.edu";
            List<String> userIds = Arrays.asList(new String[] {"A123456"
                    , 
                    "A123457", 
                    "A123458", 
                    "A123459", 
                    "A123460",
                    "AAA9643",
                    "AAA8345",
                    "AAA8346",
                    "AAA8347",
                    "AAA8349",
                    "AAA8350",
                    "AAA8351",
                    "AAA8352",
                    "AAA8353",
                    "AAP2174",
                    "AAP1600",
                    "AAP1601",
                    "AAP1602",
                    "AAP1603",
                    "AAP1604",
                    "AAP1605",
                    "AAP1607",
                    "AAP1608"
                    });
            List<String> collegeIds = Arrays.asList(new String[] {"ZZ1", "ZZ2",  "ZZ3"});
            List<ScopedIdentifier> identifiers= Arrays.asList(
                    new ScopedIdentifier[]{new ScopedIdentifier("TEST", "mathsamplefullv1"),
                            new ScopedIdentifier("TEST", "elasamplefullv1")});
            Integer numberOfAttempts = 1;
            Double percentInProgressThreshold = null;
            Integer startDateRangeInDays=1;
            Boolean expireInProgress = false;
            for(String userId:userIds) {
                for(String collegeId:collegeIds) {
                    College college = collegeRepository.findWithTestLocations(collegeId);
                    if(college == null || college.getTestLocations() == null) {
                        log.error("College has no test locations, college id: "  + collegeId);
                        continue;
                    }
                    for(TestLocation testLocation:college.getTestLocations()) {
                    identifiers.forEach(identifier -> service.generateAssessmentSessions(simulatedProctor, 
                            userId,
                            testLocation.getId(),
                            identifier, 
                            numberOfAttempts, 
                            percentInProgressThreshold, 
                            startDateRangeInDays, 
                            expireInProgress));
                    }
                }
            }
        }

}
