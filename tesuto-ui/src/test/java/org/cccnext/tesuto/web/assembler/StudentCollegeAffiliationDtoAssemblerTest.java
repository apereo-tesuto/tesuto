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
package org.cccnext.tesuto.web.assembler;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.web.assembler.StudentCollegeAffiliationDtoAssembler;
import org.cccnext.tesuto.web.model.StudentCollegeAffiliation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-test.xml" })
public class StudentCollegeAffiliationDtoAssemblerTest {

    @Autowired
    StudentCollegeAffiliationDtoAssembler dtoAssembler;

    @Test
    public void testStudentCollegeAffiliationDtoAssemblerProperlyDisassemblesReassembles() throws Exception {
        StudentCollegeAffiliation affiliation = getAffiliation();
        StudentCollegeAffiliationDto dto = dtoAssembler.assembleDto(affiliation);
        StudentCollegeAffiliation result = dtoAssembler.disassembleDto(dto);
        Assert.assertEquals(affiliation, result);
    }

    private StudentCollegeAffiliation getAffiliation() {
        StudentCollegeAffiliation affiliation = new StudentCollegeAffiliation();
        affiliation.setEppn("someEppn@college.edu");
        affiliation.setStudentCccId("AAA123456");
        affiliation.setMisCode("ZZ1");
        affiliation.setLoggedDate(new Date());
        return affiliation;
    }
}
