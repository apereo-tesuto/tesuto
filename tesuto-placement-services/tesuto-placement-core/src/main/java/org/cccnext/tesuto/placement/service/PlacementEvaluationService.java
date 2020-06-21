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
package org.cccnext.tesuto.placement.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("placementEvaluationService")
public class PlacementEvaluationService {

    @Autowired
    PsychometricsCalculationService calcService;
    
    public void setCalcService(PsychometricsCalculationService calcService) {
        this.calcService = calcService;
    }

    public Boolean evaluateCourse(CourseViewDto course, Double studentAbility,  Map<String, CompetencyDifficultyDto> map ) throws JsonProcessingException, IOException, ScriptException {
        ObjectMapper mapper = new ObjectMapper();
        Set<CompetencyGroupViewDto> competencyGroups = course.getCompetencyGroups();
        JsonNode actualObj = mapper.readTree(course.getCompetencyGroupLogic());
        if(actualObj.isArray()) {
            StringBuilder statement = new StringBuilder("(");
            parseArray(actualObj,  competencyGroups, studentAbility, map, statement);
            statement.append(")");
            return scriptEval(statement.toString());
        }

        return evaluateCompetencyGroup(studentAbility, competencyGroups,  actualObj.asInt(), map);
    }


    private Boolean scriptEval(String expression) throws ScriptException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("JavaScript");
        expression.replace("[", "(");
        expression.replace("]", ")");
        return (Boolean)se.eval(expression);
    }

    private void parseArray(JsonNode actualObj, Set<CompetencyGroupViewDto> competencyGroups, Double studentAbility,  Map<String, CompetencyDifficultyDto> competencyDifficultyMap, StringBuilder statement) {
        for(JsonNode subObject:actualObj) {
            if(subObject.isArray()){
                statement.append("(");
                parseArray(subObject,  competencyGroups, studentAbility, competencyDifficultyMap, statement);
                statement.append(")");
            } else if(subObject.isInt()) {
                Boolean evaluation = evaluateCompetencyGroup(studentAbility, competencyGroups, subObject.intValue() , competencyDifficultyMap);
                statement.append(" ")
                .append( evaluation.toString())
                .append(" ");
            } else if(subObject.asText().equalsIgnoreCase("AND")){
                statement.append(" && ");
            }else if(subObject.asText().equalsIgnoreCase("OR")){
                statement.append(" || ");
            }
        }
    }

    private Boolean evaluateCompetencyGroup(Double studentAbility, Set<CompetencyGroupViewDto> competencyGroups, int competencyGroupId, Map<String, CompetencyDifficultyDto> competencyDifficultyMap) {
        CompetencyGroupViewDto competencyGroup = filterCompetencyGroupById(competencyGroups,  competencyGroupId);

        Double minimumStudentAbility = calcService.calculateMinimumStudentAbility(competencyGroup.getPercent() / 100.0);
        Double adjustedCompetencyDifficulty =  adjustedCompetencyDifficulty(competencyGroup.getCompetencyIds(), competencyDifficultyMap);

        return studentAbility >= calcService.adjustedCompetencyDifficulty(minimumStudentAbility, adjustedCompetencyDifficulty);
    }

    private CompetencyGroupViewDto filterCompetencyGroupById(Set<CompetencyGroupViewDto> competencyGroups, int competencyGroupId) {
        return competencyGroups.stream().filter(cg -> competencyGroupId == cg.getCompetencyGroupId()).findFirst().get();
    }

    private double adjustedCompetencyDifficulty(Set<String> competencies, Map<String, CompetencyDifficultyDto> competencyDifficultyMap) {

        int count = 0;
        double total = 0.0;
        for(String competencyId: competencies) {
            CompetencyDifficultyDto difficulty = competencyDifficultyMap.get(competencyId);
            if(difficulty != null) {
                count += 1;
                total += difficulty.getDifficulty();
            }
        }
        return (total/(double)(count == 0 ? 1: count));
    }

}
