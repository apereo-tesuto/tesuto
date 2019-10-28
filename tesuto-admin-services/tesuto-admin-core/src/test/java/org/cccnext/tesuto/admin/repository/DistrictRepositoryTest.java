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
package org.cccnext.tesuto.admin.repository;

import org.cccnext.tesuto.admin.model.District;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional // this makes all test methods transactional!!
public class DistrictRepositoryTest {

    @Resource(name = "districtRepository")
    DistrictRepository repository;

    Set<String> getCollegeIds() {
        Set<String> collegeIds = new HashSet<>();
        collegeIds.add("61");
        collegeIds.add("71");
        collegeIds.add("72");
        collegeIds.add("73");
        collegeIds.add("ZZ1");
        return collegeIds;
    }

    Set<String> getDistrictIds() {
        Set<String> districtIds = new HashSet<>();
        districtIds.add("60");
        districtIds.add("70");
        districtIds.add("ZZ0");
        return districtIds;
    }

    @Test
    public void findAllWithCollegesDoesNotReturnDuplicates() throws Exception {
        Set<String> collegeIds = getCollegeIds();
        Set<String> expectedDistrictIds = getDistrictIds();

        List<District> districts = repository.findAllWithColleges(collegeIds);
        Set<String> districtIds = districts.stream().map(District::getCccId).collect(Collectors.toSet());

        assertTrue(districts.size() == 3);
        assertTrue(districtIds.containsAll(expectedDistrictIds));
    }
}
