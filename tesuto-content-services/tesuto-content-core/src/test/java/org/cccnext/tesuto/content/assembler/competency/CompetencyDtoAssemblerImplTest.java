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
package org.cccnext.tesuto.content.assembler.competency;

import static org.cccnext.tesuto.content.assembler.competency.CompetencyMapDtoAssemblerImplTest.generateCompetencyRefs;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.model.competency.Competency;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * Created by jasonbrown on 6/17/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetencyDtoAssemblerImplTest {

    @Autowired
    CompetencyDtoAssembler competencyDtoAssembler;

    public Competency getCompetency(){
        Competency competency = new Competency();
        competency.setVersion(100);
        competency.setDescription("Test description");
        competency.setDiscipline("Test Discipline");
        competency.setSampleItem("<p>here we have some sample question</p>");
        competency.setStudentDescription("The student with see that description, for easy understanding.");
        competency.setId(TesutoUtils.newId());
        competency.setIdentifier("ENG-1001");
        competency.setPublished(true);
        competency.setChildCompetencyRefs(generateCompetencyRefs(Arrays.asList("id-TEST-0001", "id-TEST-0002", "id-TEST-0003")));
        return competency;
    }

    @Test
    public void testAssembleDisassemble(){
        Competency competency = getCompetency();

        CompetencyDto competencyDto = competencyDtoAssembler.assembleDto(competency);
        Competency competencyDisassembled = competencyDtoAssembler.disassembleDto(competencyDto);
        assertEquals("Competency incorrectly assembled, dissassembled", competency, competencyDisassembled);
    }

}
