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
package org.cccnext.tesuto.content.assembler.item.metadata;

import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.CompetencyRefItemMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 1/22/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetencyItemMetadataDtoAssemblerImplTest {

    @Autowired
    CompetencyRefItemMetadataAssembler competencyRefItemMetadataAssembler;

    public static CompetencyRefItemMetadata getCompetencyRefItemMetadata() {
        CompetencyRefItemMetadata competencyRefItemMetadata = new CompetencyRefItemMetadata();
        competencyRefItemMetadata.setMapDiscipline("Map Discipline");
        competencyRefItemMetadata.setCompetencyMapDiscipline("Competency Map Discipline");
        competencyRefItemMetadata.setCompetencyId("Competency Id");
        competencyRefItemMetadata.setCompetencyRefId("Competency Ref Id");
        return competencyRefItemMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        CompetencyRefItemMetadata competencyRefItemMetadata = getCompetencyRefItemMetadata();
        CompetencyRefItemMetadataDto competencyRefItemMetadataDtoAssembled = competencyRefItemMetadataAssembler
                .assembleDto(competencyRefItemMetadata);
        CompetencyRefItemMetadata competencyRefItemMetadataDisassembled = competencyRefItemMetadataAssembler
                .disassembleDto(competencyRefItemMetadataDtoAssembled);
        assertEquals("CompetencyRefItemMetadata incorrectly assembled, dissassembled", competencyRefItemMetadata,
                competencyRefItemMetadataDisassembled);
    }
}
