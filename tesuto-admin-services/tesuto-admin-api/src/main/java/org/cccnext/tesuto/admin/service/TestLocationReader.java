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
package org.cccnext.tesuto.admin.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;

public interface TestLocationReader {

    public default boolean isUserAssociatedWithTestLocation(UserAccountDto user, String testLocationId) {
        boolean isAssociated = false;
        Set<String> testLocationIds = new HashSet<>();
        for (CollegeDto college : user.getColleges()) {
            for (TestLocationDto testLocation : college.getTestLocations()) {
                testLocationIds.add(testLocation.getId());
            }
        }
        if (testLocationIds.contains(testLocationId)) {
            isAssociated = true;
        }
        return isAssociated;
    }

    public TestLocationDto read(String id);   
    
    public List<TestLocationDto> read();
    
    public default Map<String, TestLocationDto> readMap() {
    	return generateMap(read());
    }
    
    public default Map<String, TestLocationDto> generateMap(List<TestLocationDto> locationDtos) {
    	
        HashMap<String, TestLocationDto> locationDtoHashMap = new HashMap<>();
        locationDtos.forEach(l -> locationDtoHashMap.put(l.getId().toString(), l));
        return locationDtoHashMap;
    }
    
    public default boolean isUserAssociatedWithTestLocationCollege(UserAccountDto user, String testLocationId) {
        TestLocationDto testLocationDto = read(testLocationId);
        String testLocationCollegeId = testLocationDto.getCollegeId();
        Set<String> userColleges = user.getCollegeIds();
        return userColleges.contains(testLocationCollegeId);
    }

}
