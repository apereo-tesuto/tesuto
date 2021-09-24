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
package org.cccnext.tesuto.placement;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationServiceImpl;
import org.cccnext.tesuto.placement.service.PlacementEvaluationService;
import org.cccnext.tesuto.placement.service.PlacementHelperService;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringRunner.class)
public class PlacementHelperServiceTest {

    PlacementHelperService service;

    @Before
    public void setup() {
        PsychometricsCalculationService calcService = new PsychometricsCalculationServiceImpl();
        service = new PlacementHelperService();
        PlacementEvaluationService placementEvaluationService = new PlacementEvaluationService();
        placementEvaluationService.setCalcService(calcService);
        service.setPlacementEvaluationService(placementEvaluationService);
    }

    @Test
    public void testAssessmentFindClosedToTransferLevelPrereqTrue() throws JsonProcessingException, IOException,
            ScriptException {

        Map<String, CompetencyDifficultyDto> competencydifficulties = getCompetencyDifficultyMap();
        VersionedSubjectAreaViewDto subjectArea = buildVersionedSubjectAreaViewDto(true);

        for (int i = 0; i <= 6; i++) {
            DisciplineSequenceViewDto disciplineSequence = service.findClosestToTransferLevel(competencydifficulties,
                    subjectArea, ((double) i));
            int k = i;
            if (i == 0)
                k = 1;
            assertTrue("Failed on test: " + i, assertExpectedSequence(disciplineSequence, k));
        }
    }

    @Test
    public void testAssessmentFindClosedToTransferLevelPrereqFalse() throws JsonProcessingException, IOException,
            ScriptException {

        Map<String, CompetencyDifficultyDto> competencydifficulties = getCompetencyDifficultyMap();
        VersionedSubjectAreaViewDto subjectArea = buildVersionedSubjectAreaViewDto(false);

        for (int i = 0; i <= 6; i++) {
            DisciplineSequenceViewDto disciplineSequence = service.findClosestToTransferLevel(competencydifficulties,
                    subjectArea, ((double) i));
            int k = i + 1;
            if (i == 0)
                k = 1;
            else if (i == 6)
                k = 6;
            assertTrue("Failed on test: " + i, assertExpectedSequence(disciplineSequence, k));
        }

    }

    @Test
    public void testEmptyDisciplineSequencesReturnsNull() throws JsonProcessingException, IOException, ScriptException {

        Map<String, CompetencyDifficultyDto> competencydifficulties = getCompetencyDifficultyMap();
        VersionedSubjectAreaViewDto subjectArea = buildVersionedSubjectAreaViewDto(false);
        subjectArea.setDisciplineSequences(new HashSet<>());
        DisciplineSequenceViewDto disciplineSequence = service.findClosestToTransferLevel(competencydifficulties,
                subjectArea, 6.0);
        assertNull(disciplineSequence);

    }
    
    @Test
    public void testNullDisciplineSequencesReturnsNull() throws JsonProcessingException, IOException, ScriptException {

        Map<String, CompetencyDifficultyDto> competencydifficulties = getCompetencyDifficultyMap();
        VersionedSubjectAreaViewDto subjectArea = buildVersionedSubjectAreaViewDto(false);
        subjectArea.setDisciplineSequences(null);
        DisciplineSequenceViewDto disciplineSequence = service.findClosestToTransferLevel(competencydifficulties,
                subjectArea, 6.0);
        assertNull(disciplineSequence);
    }

    private Map<String, CompetencyDifficultyDto> getCompetencyDifficultyMap() {
        Map<String, CompetencyDifficultyDto> map = new HashMap<>();
        addCompetencyDifficultyDto(map, "competency6", (Double) 6.0);
        addCompetencyDifficultyDto(map, "competency5", 5.0);
        addCompetencyDifficultyDto(map, "competency4", 4.0);
        addCompetencyDifficultyDto(map, "competency3", 3.0);
        addCompetencyDifficultyDto(map, "competency2", 2.0);
        addCompetencyDifficultyDto(map, "competency1", 1.0);
        return map;
    }

    private VersionedSubjectAreaViewDto buildVersionedSubjectAreaViewDto(boolean usePrereqPlacementMethod) {
        VersionedSubjectAreaViewDto sa = new VersionedSubjectAreaViewDto();
        Set<DisciplineSequenceViewDto> seqs = new HashSet<>();
        seqs.add(buildDisciplineSequenceViewDto('Y', 0, (Integer) 4, (Integer) 6, "competency6"));
        seqs.add(buildDisciplineSequenceViewDto('Y', 0, (Integer) 3, (Integer) 5, "competency5"));
        seqs.add(buildDisciplineSequenceViewDto('Y', 0, (Integer) 2, (Integer) 4, "competency4"));
        seqs.add(buildDisciplineSequenceViewDto('Y', 0, (Integer) 1, (Integer) 3, "competency3"));
        seqs.add(buildDisciplineSequenceViewDto('A', 1, (Integer) 1, (Integer) 2, "competency2"));
        seqs.add(buildDisciplineSequenceViewDto('B', 2, (Integer) 1, (Integer) 1, "competency1"));
        sa.setDisciplineSequences(seqs);
        sa.setUsePrereqPlacementMethod(usePrereqPlacementMethod);
        return sa;

    }

    private DisciplineSequenceViewDto buildDisciplineSequenceViewDto(char cb21Code, Integer level, Integer courseGroup,
            Integer ability, String competencyId) {
        DisciplineSequenceViewDto ds = new DisciplineSequenceViewDto();
        ds.setCb21Code(cb21Code);
        ds.setLevel(level);
        ds.setCourseGroup(courseGroup);

        ds.setDisciplineId(ability);
        CompetencyGroupViewDto cg = new CompetencyGroupViewDto();

        cg.setCompetencyIds(new HashSet<String>());
        cg.getCompetencyIds().add(competencyId);
        cg.setCompetencyGroupId(ability);
        cg.setPercent(50);
        CourseViewDto c = new CourseViewDto();
        c.setCompetencyGroups(new HashSet<>());
        c.getCompetencyGroups().add(cg);
        c.setCompetencyGroupLogic("[ " + ability + "]");

        ds.setCourses(new HashSet<CourseViewDto>());
        ds.getCourses().add(c);
        return ds;
    }

    private void addCompetencyDifficultyDto(Map<String, CompetencyDifficultyDto> map, String id, Double difficulty) {
        CompetencyDifficultyDto cd = new CompetencyDifficultyDto();
        cd.setDifficulty(difficulty);
        CompetencyDto c = new CompetencyDto();
        c.setId(id);
        cd.setCompetency(c);
        map.put(id, cd);
    }

    private boolean assertExpectedSequence(DisciplineSequenceViewDto seq, int courseGroup) {
        return seq.getDisciplineId() == courseGroup;
    }

}
