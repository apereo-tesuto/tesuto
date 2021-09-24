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
package org.cccnext.tesuto.placementonly.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

//this is only to be used while there is not assessments. should be removed at at that time
@Service
public class CompetencyMapDisciplineStaticReader implements CompetencyMapDisciplineReader, InitializingBean {
	
	Map<String, CompetencyMapDisciplineDto> competencyMapDisciplines = new HashMap<>();

	@Override
	public CompetencyMapDisciplineDto read(String disciplineName) {
		return competencyMapDisciplines.get(disciplineName);
	}

	@Override
	public List<CompetencyMapDisciplineDto> read() {
		return new ArrayList<>(competencyMapDisciplines.values());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		CompetencyMapDisciplineDto cenglish = new CompetencyMapDisciplineDto();
		cenglish.setDisciplineName("ENGLISH");
		cenglish.setId(1);
		competencyMapDisciplines.put("ENGLISH", cenglish);
		
		CompetencyMapDisciplineDto cesl = new CompetencyMapDisciplineDto();
		cesl.setDisciplineName("ESL");
		cesl.setId(2);
		competencyMapDisciplines.put("ESL", cesl);
		
		CompetencyMapDisciplineDto cmath = new CompetencyMapDisciplineDto();
		cmath.setDisciplineName("MATH");
		cmath.setId(3);
		competencyMapDisciplines.put("MATH", cmath);
		
		
	}

}
