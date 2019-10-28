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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.model.CollegeDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleSetRestClientStub implements RuleSetReader, RuleServiceWebServiceQueries {

    public static final String SEED_DATA_RULES_DIRECTORY = "src/test/resources/seed_data/";
    
    @Value("${seed.data.env:local}")
    String prefix;

    private ObjectMapper mapper = new ObjectMapper();
   
    public List<ActionResult> executeRulesEngine(String engineId, Map<String, ?> facts) {
        return null;
    }

    public List<RuleSetDTO> getLogics(String miscode, String competencyMapDiscipline) {
        try {
                File ruleSetAsList = FileUtils.getFile(SEED_DATA_RULES_DIRECTORY + "/rule_sets/" + prefix + "_rule_set.json");
                if(ruleSetAsList.exists()) {
                    return generateRuleSetsFromList( ruleSetAsList, miscode,  competencyMapDiscipline);
                } else {
                    File seedDataDirectory = FileUtils.getFile(SEED_DATA_RULES_DIRECTORY + "/rule_sets");
                    File[] files = seedDataDirectory.listFiles();
                    return generateRuleSets(files, miscode, competencyMapDiscipline);
                }
            }  catch(Exception exception) {
            }
            return null;
    }

    private List<RuleSetDTO> generateRuleSets(File[] files, String miscode, String competencyMapDiscipline) {
        List<RuleSetDTO> ruleSets = new ArrayList<>();
         
       try {
            for (File file : files) {
                String rule = FileUtils.readFileToString(file);
                RuleSetDTO ruleSet = mapper.readValue(rule, RuleSetDTO.class);
                if (ruleSet.getFamily().equalsIgnoreCase(miscode)
                        && ruleSet.getCompetencyMapDiscipline().equalsIgnoreCase(competencyMapDiscipline)) {
                    ruleSets.add(ruleSet);
                }
            }
        } catch (Exception exception) {

        }
        return ruleSets;
    }
    
    private List<RuleSetDTO> generateRuleSetsFromList(File file, String miscode, String competencyMapDiscipline) throws JsonParseException, JsonMappingException, IOException {
        String rule = FileUtils.readFileToString(file);
        List<RuleSetDTO>ruleSet = Arrays.asList(mapper.readValue(rule, RuleSetDTO[].class));
        if(CollectionUtils.isEmpty(ruleSet)) {
            return null;
        }
       return ruleSet.stream().filter(r -> r.getFamily().equalsIgnoreCase(miscode)
                        && r.getCompetencyMapDiscipline().equalsIgnoreCase(competencyMapDiscipline))
                        .collect(Collectors.toList());
    }

    @Override
    public void requestOnBoardCollege(String cccMisCode, String description) {
        
    }

	@Override
	public RuleSetDTO findByRuleSetId(String id) {
		RuleSetDTO ruleSetDto = new RuleSetDTO();
		ruleSetDto.setId(id);
		return ruleSetDto;
	}

	@Override
	public List<CollegeDTO> findColleges(String status) {
		//Auto-generated method stub
		return null;
	}
}
