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
package org.cccnext.tesuto.admin.assembler;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.model.College;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
/**
 * Created by jasonbrown on 8/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
public class CollegeDtoAssemblerTest {

    @Autowired CollegeDtoAssembler collegeDtoAssembler;

    public static College getCollege(){
        College college = new College();
        college.setCollegeAttribute(CollegeAttributeDtoAssemblerTest.getCollegeAttribute());
        college.setCccId("ccc_id");
        college.setCity("city");
        college.setCreatedDate(new Date());
        college.setEppnSuffix("eppen_suffic");
        college.setLastUpdatedDate(new Date());
        college.setName("name");
        college.setPostalCode("postal_code");
        college.setStreetAddress1("street_address_one");
        college.setStreetAddress2("street_address_two");
        //TODO test district
        //TODO test test location
        college.setUrl("url");
        return college;
    }

    @Test
    public void testAssembleDisassemble() {
        College college = getCollege();

        CollegeDto collegeDtoAssembled = collegeDtoAssembler.assembleDto(college);
        College collegeDisassembled = collegeDtoAssembler.disassembleDto(collegeDtoAssembled);
        assertEquals("CollegeAttribute is not assembled, disassembled correctly", college, collegeDisassembled);
    }}
