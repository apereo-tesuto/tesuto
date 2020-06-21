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
package org.cccnext.tesuto.placement.stub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.placement.model.CompetencyGroup;
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping;
import org.springframework.stereotype.Service;

@Service("competencyMapOrderService")
public class CompetencyMapOrderServiceStub implements CompetencyMapOrderService  {
    Random rand = new Random();
    Map<String,List<CompetencyDifficultyDto>> competencyMaps = new HashMap<>();
    Map<String,String> disciplineCompetenceyMapIds = new HashMap<>();
    List<CompetencyDifficultyDto> competencyDifficultyDtos = new ArrayList<>();

    public String putCompetenciesIntoMap(String disciplineMap, Collection<CompetencyDifficultyDto> difficulties) {
        String mapId = randomId();
        competencyMaps.put(mapId, new ArrayList<CompetencyDifficultyDto>(difficulties));
        disciplineCompetenceyMapIds.put(disciplineMap, mapId);
        return mapId;
    }

    @Override
    public String create(CompetencyMapDto mapDto) {
        List<CompetencyDifficultyDto> difficulties = new ArrayList<>();
        for(CompetencyRefDto ref:mapDto.getCompetencyRefs()){
            CompetencyDto competency = new CompetencyDto();
            competency.setDescription(randomString());
            competency.setIdentifier(ref.getCompetencyIdentifier());
            competency.setDiscipline(ref.getDiscipline());
            competency.setVersion(ref.getVersion());
            competency.setId(randomString());
            difficulties.add(new CompetencyDifficultyDto(competency, randomDouble(-2,6)));
        }
        return putCompetenciesIntoMap(mapDto.getDiscipline(), difficulties);
    }

    public void generateCompetencyDifficultyDtos(Set<CompetencyGroup> competencyGroups) {
        for (CompetencyGroup competencyGroup : competencyGroups) {
            for (CompetencyGroupMapping competencyGroupMapping : competencyGroup.getCompetencyGroupMappings()) {
                CompetencyDifficultyDto competencyDifficultyDto = new CompetencyDifficultyDto();
                CompetencyDto competencyDto = new CompetencyDto();
                competencyDto.setIdentifier(competencyGroupMapping.getCompetencyId());
                competencyDifficultyDto.setCompetency(competencyDto);
                competencyDifficultyDto.setDifficulty(ThreadLocalRandom.current().nextDouble(1, 10));
                competencyDifficultyDtos.add(competencyDifficultyDto);
            }
        }
    }

    @Override
    public Map<String, String> createForDisciplines(Set<String> disciplines) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
        return competencyMaps.get(id);
    }


    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String competencyMapDiscipline, int competencyMapVersion) {
        return CollectionUtils.isEmpty(competencyDifficultyDtos) ? competencyMaps.get(competencyMapDiscipline) : competencyDifficultyDtos;
    }

    public String findLatestPublishedIdByCompetencyMapDiscipline(String discipline) {
        return disciplineCompetenceyMapIds.get(discipline);
    }

    @Override
    public Integer findPositionByAbility(String id, Double studentDificulty) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String competencyMapOrderId) {
    return;
    }

    @Override
    public OrderedCompetencySet selectOrganizeByAbility(String id, Double studentDificulty, Integer parentLevel,
            Integer competencyRange) {
        //Auto-generated method stub
        return null;
    }

    String randomString() {
        return UUID.randomUUID().toString();
    }

    String randomId() {
        return UUID.randomUUID().toString();
    }

    double randomDouble(double min, double max) {
        return min + rand.nextDouble() * max;
    }

	@Override
	public String getCompetencyMapOrderId(String id) {
		//Auto-generated method stub
		return null;
	}

}
