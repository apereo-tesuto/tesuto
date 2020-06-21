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
package org.cccnext.tesuto.preview.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;

public class CompetencyMapOrderServiceCache implements CompetencyMapOrderService {

    @Override
    public String create(CompetencyMapDto mapDto) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> createForDisciplines(Set<String> disciplines) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public String findLatestPublishedIdByCompetencyMapDiscipline(String discipline) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public Integer findPositionByAbility(String id, Double studentDificulty) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String competencyMapOrderId) {
        //Auto-generated method stub

    }

    @Override
    public OrderedCompetencySet selectOrganizeByAbility(
            String id, Double studentDificulty, Integer parentLevel, Integer competencyRange) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id, int version) {
        //Auto-generated method stub
        return null;
    }

	@Override
	public String getCompetencyMapOrderId(String id) {
		//Auto-generated method stub
		return null;
	}

}
