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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;

import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.user.service.StudentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PlacementHelperService {

    @Autowired
    SubjectAreaServiceAdapter subjectAreaService;

    @Autowired
    PlacementEvaluationService placementEvaluationService;

    @Autowired
    StudentReader studentRestClient;

    public void setSubjectAreaService(SubjectAreaServiceAdapter subjectAreaService) {
        this.subjectAreaService = subjectAreaService;
    }

    public void setPlacementEvaluationService(PlacementEvaluationService placementEvaluationService) {
        this.placementEvaluationService = placementEvaluationService;
    }

    public void setStudentRestClient(StudentReader studentRestClient) {
        this.studentRestClient = studentRestClient;
    }

    public DisciplineSequenceViewDto findClosestToTransferLevel(
            Map<String, CompetencyDifficultyDto> competencyDifficultyMap,
            VersionedSubjectAreaViewDto versionedSubjectAreaViewDto, Double studentAbility)
            throws JsonProcessingException, IOException, ScriptException {

        List<DisciplineSequenceViewDto> sequences = orderDisciplineSequences(versionedSubjectAreaViewDto
                .getDisciplineSequences());
        
        if (CollectionUtils.isEmpty(sequences)) {
            return null;
        }

        for (DisciplineSequenceViewDto disciplineSequence : sequences) {
            if (CollectionUtils.isEmpty(disciplineSequence.getCourses())) {
                continue;
            }
            for (CourseViewDto course : disciplineSequence.getCourses()) {
                if (!courseCanBeEvaluated(course)) {
                    continue;
                }
                Boolean meets = placementEvaluationService.evaluateCourse(course, studentAbility,
                        competencyDifficultyMap);
                if (meets) {
                    if (!versionedSubjectAreaViewDto.isUsePrereqPlacementMethod()) {
                        int index = sequences.indexOf(disciplineSequence);
                        index = index ==  0  ? index : index - 1;
                        disciplineSequence = sequences.get(index);
                    }
                   return disciplineSequence;
                }
            }   
        }
        return sequences.get(sequences.size() - 1);
    }

    public Boolean courseCanBeEvaluated(CourseViewDto course) {
        if (StringUtils.isEmpty(course.getCompetencyGroupLogic())) {
            return false;
        }
        if (CollectionUtils.isEmpty(course.getCompetencyGroups())) {
            return false;
        }
        for (CompetencyGroupViewDto cg : course.getCompetencyGroups()) {
            if (cg.getCompetencyIds() != null && cg.getCompetencyIds().size() > 0) {
                return true;
            }
        }
        return false;
    }

    public Map<String, CompetencyDifficultyDto> buildCompetencyDifficultyMap(List<CompetencyDifficultyDto> competencies) {
        Map<String, CompetencyDifficultyDto> map = new HashMap<String, CompetencyDifficultyDto>();
        if (competencies != null) {
            competencies.forEach(c -> map.put(c.getCompetency().getIdentifier(), c));
        }
        return map;
    }

    public Set<String> getCollegeMisCodes(String cccid) {
        return studentRestClient.getCollegesAppliedTo(cccid);
    }

    public DisciplineSequenceViewDto findClosestToTransferLevel(Set<DisciplineSequenceViewDto> sequences, 
            Integer requestedTransferLevel,
            Set<String> programs) throws JsonProcessingException, IOException,
            ScriptException {

        List<DisciplineSequenceViewDto> orderedSequences = orderDisciplineSequences(sequences);
        if (CollectionUtils.isEmpty(sequences)) {
            return null;
        }
        DisciplineSequenceViewDto sequenceMatchedByProgram = orderedSequences.get(sequences.size() - 1);
        
        if (CollectionUtils.isNotEmpty(programs)) {
            List<DisciplineSequenceViewDto> sequencesWithPrograms = filterByPrograms(orderedSequences, programs);
            CollectionUtils.isNotEmpty(programs);
            sequenceMatchedByProgram = findSequenceClosestToTransferLevel(sequencesWithPrograms, requestedTransferLevel);
        }
        DisciplineSequenceViewDto sequenceMatched = findSequenceClosestToTransferLevel(orderedSequences, requestedTransferLevel);

        // If the sequence level found with out consideration of the filtered programs is not lower (Closer to transfer level) than the index as found by the program
        // filtered sequence list then use the index of the sequence found by the filtered sequence list
       if(sequenceMatched != null && sequenceMatchedByProgram != null) {
           if (sequenceMatched.getLevel() >= sequenceMatchedByProgram.getLevel()) {
               return sequenceMatchedByProgram;
           }
       } else if(sequenceMatched != null) {
           return sequenceMatched;
       } else {
           return sequenceMatchedByProgram;
       }
        return sequenceMatched;
    }
    
    DisciplineSequenceViewDto findSequenceClosestToTransferLevel(List<DisciplineSequenceViewDto> sequences, int transferLevel) {
        DisciplineSequenceViewDto foundSequence = null;;
        for (DisciplineSequenceViewDto sequence:sequences) {
            foundSequence = sequence;
            if (sequence.getLevel() == transferLevel) {
                return sequence;
            }
            if (sequence.getLevel() > transferLevel) { // if sequence level is higher that the requested transfer level then we get the previous sequence index.  
               int  index =  sequences.indexOf(sequence); // we may not want this behavior.
               index =index == 0 ? 0:index-1;
                return   sequences.get(index);                 
            }
        }
        return foundSequence; // always return at least a valid sequence.
    }

    List<DisciplineSequenceViewDto> orderDisciplineSequences(Set<DisciplineSequenceViewDto> sequences) {
        if (sequences == null) {
            return new ArrayList<>();
        }
        List<DisciplineSequenceViewDto> seq = new ArrayList<DisciplineSequenceViewDto>(sequences);
         seq.sort(new SequenceSorter());
         return seq;
    }

    List<DisciplineSequenceViewDto> filterByPrograms(List<DisciplineSequenceViewDto> sequences, Set<String> programs) {
        List<DisciplineSequenceViewDto> sequencesWithPrograms = new ArrayList<>();
        for (DisciplineSequenceViewDto sequence : sequences) {
            if (hasProgram(sequence, programs)) {
                sequencesWithPrograms.add(sequence);
            }
        }
        return sequencesWithPrograms;
    }

    Boolean hasProgram(DisciplineSequenceViewDto sequence, Set<String> programs) {
        for (CourseViewDto course : sequence.getCourses()) {
            if (programs.contains(course.getMmapEquivalentCode())) {
                return true;
            }
        }
        return false;
    }
    
    public class SequenceSorter implements Comparator<DisciplineSequenceViewDto> {
        public int compare(DisciplineSequenceViewDto first, DisciplineSequenceViewDto second) {
            int result = Integer.valueOf(first.getLevel()).compareTo(Integer.valueOf(second.getLevel()));
            if (result != 0) {
                return result;
            }
            else {
                return Integer.valueOf(second.getCourseGroup()).compareTo(Integer.valueOf(first.getCourseGroup()));
            }
        }
    }

}
