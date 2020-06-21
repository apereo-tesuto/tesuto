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

import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;
import org.cccnext.tesuto.content.model.item.metadata.CompetenciesItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.ItemMetadata;
import org.cccnext.tesuto.content.model.item.metadata.ToolsItemMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class ItemMetadataDtoAssemblerImplTest {

    @Autowired
    ItemMetadataDtoAssembler itemMetadataDtoAssembler;

    public static ItemMetadata getMetadata() {
        ItemMetadata itemMetadata = new ItemMetadata();
        itemMetadata.setIdentifier("i001");
        itemMetadata.setItemBankStatusType(ItemBankStatusType.GET_MORE_DATA);
        itemMetadata.setType("itemmetadata");
        itemMetadata.setAuthoringTool("authoringTool");
        itemMetadata.setAuthoringToolVersion("2");
        itemMetadata.setAuthor("LSI");
        itemMetadata.setDifficulty("Really Hard");
        itemMetadata.setCalibratedDifficulty(-400.001);
        itemMetadata.setWeightedCategory("20");
        itemMetadata.setContextual("Context");
        itemMetadata.setTheme("Theme");
        List<String> commonCoreRefs = Arrays.asList("ref2", "ref3", "ref5");
        itemMetadata.setCommonCoreRef(commonCoreRefs);
        itemMetadata.setLexile("Lexile");
        itemMetadata.setPassage("Passage");
        itemMetadata.setPassageType("P_Type");
        CompetenciesItemMetadata competenciesItemMetadata = CompetenciesItemMetadataDtoAssemblerImplTest
                .getCompetenciesItemMetadata();
        itemMetadata.setCompetencies(competenciesItemMetadata);
        ToolsItemMetadata toolsItemMetadata = ToolsItemMetadataDtoAssemblerImplTest.getToolsItemMetadata();
        itemMetadata.setTools(toolsItemMetadata);
        return itemMetadata;
    }

    @Test
    public void testAssembleDisassemble() {
        ItemMetadata itemMetadata = getMetadata();
        ItemMetadataDto itemMetadataDtoAssembled = itemMetadataDtoAssembler.assembleDto(itemMetadata);
        ItemMetadata itemMetadataDisassembled = itemMetadataDtoAssembler.disassembleDto(itemMetadataDtoAssembled);
        assertEquals("ItemMetadata incorrectly assembled, dissassembled", itemMetadata, itemMetadataDisassembled);
    }
}
