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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMapDiscipline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 7/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetencyMapDisciplineDtoAssemblerImplTest {

    @Autowired
    CompetencyMapDisciplineDtoAssembler competencyMapDisciplineDtoAssembler;

    public CompetencyMapDiscipline getCompetencyMapDiscipline(){
        CompetencyMapDiscipline competencyMapDiscipline = new CompetencyMapDiscipline();
        competencyMapDiscipline.setId(1000);
        competencyMapDiscipline.setDisciplineName("ENGLISH");
        return competencyMapDiscipline;
    }

    @Test
    public void testAssembleDisassemble(){
        CompetencyMapDiscipline competencyMapDiscipline = getCompetencyMapDiscipline();

        CompetencyMapDisciplineDto competencyMapDisciplineDto = competencyMapDisciplineDtoAssembler.assembleDto(competencyMapDiscipline);
        CompetencyMapDiscipline competencyMapDisciplineDisassembled = competencyMapDisciplineDtoAssembler.disassembleDto(competencyMapDisciplineDto);
        assertEquals("CompetencyMap incorrectly assembled, dissassembled", competencyMapDiscipline, competencyMapDisciplineDisassembled);
    }
}
