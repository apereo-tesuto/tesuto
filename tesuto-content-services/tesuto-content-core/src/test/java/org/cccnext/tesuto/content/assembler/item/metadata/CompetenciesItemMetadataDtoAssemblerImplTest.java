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

import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.model.item.metadata.CompetenciesItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.CompetencyRefItemMetadata;
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
 * Created by jasonbrown on 1/22/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class CompetenciesItemMetadataDtoAssemblerImplTest {

    @Autowired
    CompetenciesItemMetadataDtoAssembler competenciesItemMetadataDtoAssembler;

    public static CompetenciesItemMetadata getCompetenciesItemMetadata() {
        CompetenciesItemMetadata competenciesItemMetadata = new CompetenciesItemMetadata();
        List<String> skippedCompetencies = Arrays.asList("skip-q", "skip-c", "skip-a");
        List<String> skippedCompetencyRefIds = Arrays.asList("refid-a", "refid-b", "refid-c");
        competenciesItemMetadata.setSkippedCompetency(skippedCompetencies);
        competenciesItemMetadata.setSkippedCompetencyRefId(skippedCompetencyRefIds);
        List<CompetencyRefItemMetadata> competencyRefItemMetadataList = new ArrayList<>(1);
        competencyRefItemMetadataList.add(CompetencyItemMetadataDtoAssemblerImplTest.getCompetencyRefItemMetadata());
        competenciesItemMetadata.setCompetencyRef(competencyRefItemMetadataList);
        return competenciesItemMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        CompetenciesItemMetadata competenciesItemMetadata = getCompetenciesItemMetadata();
        CompetenciesItemMetadataDto competenciesItemMetadataDtoAssembled = competenciesItemMetadataDtoAssembler
                .assembleDto(competenciesItemMetadata);
        CompetenciesItemMetadata competenciesItemMetadataDisassembled = competenciesItemMetadataDtoAssembler
                .disassembleDto(competenciesItemMetadataDtoAssembled);
        assertEquals("CompetenciesItemMetadata incorrectly assembled, dissassembled", competenciesItemMetadata,
                competenciesItemMetadataDisassembled);
    }
}
