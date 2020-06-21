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

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.assembler.exception.InvalidCompetencyRefException;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.content.viewdto.CompetencyMapViewDto;
import org.cccnext.tesuto.content.viewdto.CompetencyViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jasonbrown on 6/23/16.
 */
@Service
public class CompetencyAssemblyService {

    @Autowired
    CompetencyService competencyService;

    @Autowired
    CompetencyMapService competencyMapService;




    public CompetencyMapViewDto assembleCompetencyMapViewDto(CompetencyMapDto competencyMapDto){
        if(competencyMapDto == null){
            return null;
        }

        CompetencyMapViewDto competencyMapViewDto = new CompetencyMapViewDto();
        competencyMapViewDto.setIdentifier(competencyMapDto.getIdentifier());
        competencyMapViewDto.setVersion(competencyMapDto.getVersion());
        competencyMapViewDto.setPublished(competencyMapDto.isPublished());
        competencyMapViewDto.setDiscipline(competencyMapDto.getDiscipline());
        competencyMapViewDto.setTitle(competencyMapDto.getTitle());
        List<CompetencyViewDto> childCompetencyViewDtos = getChildCompetencyViewDtos(competencyMapDto.getDiscipline(), competencyMapDto.getCompetencyRefs());
        competencyMapViewDto.setChildCompetencyViewDtos(childCompetencyViewDtos);
        return competencyMapViewDto;
    }

    private List<CompetencyViewDto> getChildCompetencyViewDtos(String discipline, List<CompetencyRefDto> refs){
        List<CompetencyViewDto> childCompetencyViewDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(refs)) {
            for (CompetencyRefDto ref : refs) {
                CompetencyDto childCompetencyDto = null;
                if(ref.getVersion() == 0) {
                    childCompetencyDto = competencyService.readLatestPublishedVersion(discipline, ref.getCompetencyIdentifier());
                } else {
                    childCompetencyDto = competencyService.readByDisciplineIdentifierAndVersion(discipline, ref.getCompetencyIdentifier(), ref.getVersion());
                }
                //Competency Ref should not be null
                //This means there is no matching identifier.
                if(childCompetencyDto == null) {
                    throw new InvalidCompetencyRefException(String.format("Could not find child competency for ref: %s", ref));
                } else {
                    childCompetencyViewDtos.add(assembleCompetencyViewDto(childCompetencyDto));
                }
            }
        }
        return childCompetencyViewDtos;
    }
    
    public CompetencyViewDto assembleCompetencyViewDto(CompetencyDto competencyDto){
        if(competencyDto == null){
            return null;
        }

        CompetencyViewDto competencyViewDto = new CompetencyViewDto();
        competencyViewDto.setIdentifier(competencyDto.getIdentifier());
        competencyViewDto.setVersion(competencyDto.getVersion());
        competencyViewDto.setPublished(competencyDto.isPublished());
        competencyViewDto.setDiscipline(competencyDto.getDiscipline());
        competencyViewDto.setDescription(competencyDto.getDescription());
        List<CompetencyViewDto> childCompetencyViewDtos = getChildCompetencyViewDtos(competencyDto.getDiscipline(), competencyDto.getChildCompetencyDtoRefs());
        competencyViewDto.setChildCompetencyViewDtos(childCompetencyViewDtos);
        return competencyViewDto;
    }
}
