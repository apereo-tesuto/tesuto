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
package org.cccnext.tesuto.content.assembler.section;

import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.model.shared.AssessmentComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 2/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentComponentDtoAssemblerImplTest {

    @Autowired
    AssessmentComponentDtoAssemblerImpl assessmentComponentDtoAssembler;

    public static List<AssessmentComponent> getAssessmentComponents() {
        List<AssessmentComponent> componentList = new ArrayList<>();
        componentList.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        componentList.add(AssessmentSectionDtoAssemblerImplTest.getAssessmentSection());
        componentList.add(AssessmentItemRefDtoAssemblerImplTest.getAssessmentItemRef());
        return componentList;
    }

    @Test
    public void testAssembleDisassemble() {
        List<AssessmentComponent> assessmentComponents = getAssessmentComponents();
        List<AssessmentComponentDto> assessmentComponentDtos = assessmentComponentDtoAssembler
                .assembleDto(assessmentComponents);
        List<AssessmentComponent> assessmentComponentsDisassembled = assessmentComponentDtoAssembler
                .disassembleDto(assessmentComponentDtos);
        assertEquals("AssessmentComponent incorrectly assembled, dissassembled", assessmentComponents,
                assessmentComponentsDisassembled);
    }
}
