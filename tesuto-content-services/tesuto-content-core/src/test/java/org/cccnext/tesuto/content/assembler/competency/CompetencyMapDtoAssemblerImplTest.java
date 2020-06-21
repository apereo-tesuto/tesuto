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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMap;
import org.cccnext.tesuto.content.model.competency.CompetencyRef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
/**
 * Created by jasonbrown on 6/17/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetencyMapDtoAssemblerImplTest {

    @Autowired
    CompetencyMapDtoAssembler competencyMapDtoAssembler;

    public CompetencyMap getCompetencyMap(){
        CompetencyMap competencyMap = new CompetencyMap();

        competencyMap.setVersion(4);
        competencyMap.setIdentifier("Identifier");
        
        
        competencyMap.setCompetencyRefs(generateCompetencyRefs(Arrays.asList("id-TEST-0001", "id-TEST-0002", "id-TEST-0003")));
        competencyMap.setDiscipline("MATH");
        competencyMap.setTitle("TEST | TITLE");
        return competencyMap;
    }

    @Test
    public void testAssembleDisassemble(){
        CompetencyMap competencyMap = getCompetencyMap();

        CompetencyMapDto competencyMapDto = competencyMapDtoAssembler.assembleDto(competencyMap);
        CompetencyMap competencyMapDisassembled = competencyMapDtoAssembler.disassembleDto(competencyMapDto);
        assertEquals("CompetencyMap incorrectly assembled, dissassembled", competencyMap, competencyMapDisassembled);
    }
    
    public static List<CompetencyRef> generateCompetencyRefs(List<String> identifiers) {
        List<CompetencyRef> competenceRefs = new ArrayList<CompetencyRef>();
        for(String identifier:identifiers) {
            CompetencyRef ref = new CompetencyRef();
            ref.setCompetencyIdentifier(identifier);
            ref.setVersion(1);
            ref.setDiscipline("discipline");
            competenceRefs.add(ref);
        }
        return competenceRefs;
    }

}
