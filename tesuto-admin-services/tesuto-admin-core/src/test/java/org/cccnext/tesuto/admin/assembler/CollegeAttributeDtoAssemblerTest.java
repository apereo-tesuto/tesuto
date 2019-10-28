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

import org.cccnext.tesuto.admin.dto.CollegeAttributeDto;
import org.cccnext.tesuto.admin.model.CollegeAttribute;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 8/1/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
public class CollegeAttributeDtoAssemblerTest {


    @Autowired
    CollegeAttributeAssembler collegeAttributeAssembler;

    public static CollegeAttribute getCollegeAttribute(){
        CollegeAttribute collegeAttribute = new CollegeAttribute();
        collegeAttribute.setEnglishPlacementOption("ENGLISH");
        collegeAttribute.setEslPlacementOption("ESL");
        collegeAttribute.setCollegeId("college_id_test");
        return collegeAttribute;
    }

    @Test
    public void testAssembleDisassemble() {
        CollegeAttribute collegeAttribute = getCollegeAttribute();

        CollegeAttributeDto collegeAttributeDtoAssembled = collegeAttributeAssembler.assembleDto(collegeAttribute);
        CollegeAttribute collegeAttributeDisassembled = collegeAttributeAssembler.disassembleDto(collegeAttributeDtoAssembled);
        assertEquals("CollegeAttribute is not assembled, disassembled correctly", collegeAttribute, collegeAttributeDisassembled);
    }
}
