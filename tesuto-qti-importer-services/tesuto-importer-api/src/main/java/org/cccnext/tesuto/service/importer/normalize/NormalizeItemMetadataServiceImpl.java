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
package org.cccnext.tesuto.service.importer.normalize;

import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 10/5/16.
 */
@Service(value ="normalizeItemMetadataService")
public class NormalizeItemMetadataServiceImpl implements NormalizeItemMetadataService {

    @Override
    public void normalizeItemMetadata(ItemMetadataDto itemMetadataDto) {
        if(itemMetadataDto == null){
            return;
        }
        normalizeCompetencies(itemMetadataDto.getCompetencies());
    }

    private void normalizeCompetencies(CompetenciesItemMetadataDto competenciesItemMetadataDto){
        if(competenciesItemMetadataDto != null){
            normalizeCompetencyRefs(competenciesItemMetadataDto.getCompetencyRef());
        }
    }

    private void normalizeCompetencyRefs(List<CompetencyRefItemMetadataDto> competencyRefItemMetadataDtos){
        competencyRefItemMetadataDtos.stream().forEach(c -> {
            if(c.getCompetencyMapDiscipline() != null) {
                c.setCompetencyMapDiscipline(c.getCompetencyMapDiscipline().toUpperCase());
            }

            if(c.getMapDiscipline() != null){
                c.setMapDiscipline(c.getMapDiscipline().toUpperCase());
            }
        });
    }
}
