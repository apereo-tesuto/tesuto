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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.model.DisciplineSequence;
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse;
import org.cccnext.tesuto.placement.repository.jpa.DisciplineRepository;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleStatus;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class OnboardCollegeService implements InitializingBean {
	
    ObjectMapper mapper = new ObjectMapper();
    static final String MULTIPLE_MEASURE_PLACEMENT = "MULTIPLE_MEASURE_PLACEMENT";
    static final String PLACEMENT_ACTION = "PLACEMENT_ACTION ";
    static final String ASSIGNED_PLACEMENT_ACTION = "ASSIGNED_PLACEMENT_ACTION ";

    @Value("classpath:defaults")
    private Resource defaultSubjectAreas;

    @Autowired
    private RuleSetReader reader;

    @Autowired
    private DisciplineAssembler assembler;

    @Autowired
    private DisciplineRepository repository;

    @Autowired
    private CourseAssembler courseAssembler;

    @Autowired
    private SubjectAreaServiceAdapter adapter;

    @Autowired
    private RuleServiceWebServiceQueries ruleSetService;

    @Autowired
    private DisciplineSequenceAssembler dsassembler;

    public void onboardCollege(String cccMisCode, String description) throws IOException {
       
        Set<Discipline> disciplines = getResources();
        for (Discipline discipline : disciplines) {
        	if(hasDisciplineCompetencyMap(cccMisCode, discipline.getCompetencyMapDiscipline()))
        		continue;
            String competencyMapDiscipline = discipline.getCompetencyMapDiscipline();
           List<RuleSetDTO> ruleSets = getCollegeRulesets( cccMisCode,  
                   description,  
                   competencyMapDiscipline,   
                   discipline.getCompetencyAttributes());

            discipline.setCollegeId(cccMisCode);
            discipline.setDisciplineId(null);
            discipline.setCreatedOnDate(null);
            discipline.setDirty(true);

            String mmDecisionLogic = getComparableLogicId(ruleSets,
                    discipline.getCompetencyAttributes().getMmDecisionLogic());
            discipline.getCompetencyAttributes().setMmDecisionLogic(mmDecisionLogic);

            String placementLogic = getComparableLogicId(ruleSets,
                    discipline.getCompetencyAttributes().getMmPlacementLogic());
            discipline.getCompetencyAttributes().setMmPlacementLogic(placementLogic);

            String assignedPlacementLogic = getComparableLogicId(ruleSets,
                    discipline.getCompetencyAttributes().getMmAssignedPlacementLogic());
            discipline.getCompetencyAttributes().setMmAssignedPlacementLogic(assignedPlacementLogic);
            CompetencyAttributes attributes = discipline.getCompetencyAttributes();
            attributes.setCompetencyAttributeId(0);
            Integer onboardDisciplineId = adapter.createDiscipline(assembler.assembleDto(discipline));

            for (DisciplineSequence sequence : discipline.getDisciplineSequences()) {
                sequence.setDisciplineId(onboardDisciplineId);
                sequence.setDiscipline(repository.getOne(onboardDisciplineId));
               Set<DisciplineSequenceCourse> dscOriginal = sequence.getDisciplineSequenceCourses();
               sequence.setDisciplineSequenceCourses(null);
                DisciplineSequenceViewDto squenceView = dsassembler.assembleDto(sequence);
                squenceView.setDisciplineId(onboardDisciplineId);
                DisciplineSequenceViewDto onboardSequence = adapter.upsert(squenceView);
                for (DisciplineSequenceCourse dsc : dscOriginal) {
                    dsc.getCourse().setDisciplineSequenceCourses(new HashSet<>(Arrays.asList(dsc)));
                    dsc.getCourse().setCompetencyGroupLogic("");
                    dsc.getCourse().setCompetencyGroups(null);
                    dsc.setDisciplineId(onboardDisciplineId);
                }
                adapter.upsert(squenceView);
                for (DisciplineSequenceCourse courseseq : dscOriginal) {
                    CourseViewDto course = courseAssembler.assembleDto(courseseq.getCourse());
                    course.setAuditId(null);
                    course.setCourseId(null);
                    course.setCourseGroup(onboardSequence.getCourseGroup());
                    course.setCompetencyGroupLogic("");
                    course.setCompetencyGroups(null);
                    adapter.createCourse(onboardDisciplineId, course);
                }
                adapter.createVersionedSubjectArea(onboardDisciplineId);
            }
        }
    }
    
    private List<RuleSetDTO> getCollegeRulesets(String cccMisCode, String description, String competencyMapDiscipline,  CompetencyAttributes attributes) {
        List<RuleSetDTO> ruleSets = reader.getLogics(cccMisCode, competencyMapDiscipline);
       String logicId =  getComparableLogicId( ruleSets, attributes.getMmDecisionLogic());
       if(StringUtils.isBlank(logicId)) {
           reader.requestOnBoardCollege(cccMisCode, description);
           ruleSets = reader.getLogics(cccMisCode, competencyMapDiscipline);
       }
       return ruleSets.stream().map(r -> ruleSetService.findByRuleSetId(r.getId())).collect(Collectors.toList());
    }

    public String saveCurrentSubjectAreasAsDefaults(String cccMiscode) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
        StringBuilder disciplineBuilder = new StringBuilder("[");
        Set<Discipline> disciplines = repository.findDisciplinesByCollegeId(Arrays.asList(cccMiscode));
        for (Discipline discipline : disciplines) {
            if(adapter.getPublishedVersionForSubjectArea(discipline.getDisciplineId()) == null)
                continue;
            Discipline complete = repository.findDisciplineWithSequences(discipline.getDisciplineId());
            discipline.setCollegeId("SYS");
            String fileName = complete.getTitle().replaceAll(" ", "_") + "_" + dateFormat.format(new Date()) + ".json";
            try {
                File defaultFile = new File(defaultSubjectAreas.getFile(), fileName);
                defaultFile.createNewFile();
                mapper.writeValue(defaultFile, complete);
            } catch(Exception exception) {
                
            }
            disciplineBuilder.append(mapper.writeValueAsString(complete)).append(",");
        }
        disciplineBuilder.deleteCharAt(disciplineBuilder.lastIndexOf(","));
        disciplineBuilder.append("]");
        return disciplineBuilder.toString();
    }

    private String getComparableLogicId(List<RuleSetDTO> ruleSets, String logicId) {
        if (StringUtils.isBlank(logicId)) {
            return null;
        }
        try {
            RuleSetDTO matchingLogic = ruleSetService.findByRuleSetId(logicId);

            if (matchingLogic == null) {
                return null;
            }
            
            List<RuleSetDTO> sameCategory = ruleSets.stream()
                    .filter(r -> matchingLogic.getCategory().equals(r.getCategory()))
                    .collect(Collectors.toList());
            
            List<RuleSetDTO> sameEvent = sameCategory.stream()
                    .filter(r -> matchingLogic.getEvent().equals(r.getEvent()))
                    .collect(Collectors.toList());
            List<RuleSetDTO> sameTitle = sameEvent.stream()
                    .filter(r -> matchingLogic.getEvent().equals(r.getEvent()))
                    .collect(Collectors.toList());
            
            List<RuleSetDTO> publishedRuleSets = sameTitle.stream()
                    .filter(r -> RuleStatus.PUBLISHED.equals(r.getStatus()))
                    .collect(Collectors.toList());
            

            
            List<RuleSetDTO> sameApplicationRuleSets = publishedRuleSets.stream()
                    .filter(r -> matchingLogic.getEngine().equals(r.getEngine()))
                    .collect(Collectors.toList());
            List<RuleSetDTO> sameCompRuleSets = sameApplicationRuleSets.stream()
                    .filter(r -> matchingLogic.getCompetencyMapDiscipline().equals(r.getCompetencyMapDiscipline()))
                    .collect(Collectors.toList());

            List<RuleSetDTO> sameEventRuleSets = sameCompRuleSets.stream()
                    .filter(r -> matchingLogic.getEvent().equals(r.getEvent())).collect(Collectors.toList());

            if (sameEventRuleSets.isEmpty()) {
                return null;
            }
            if (sameEventRuleSets.size() == 1) {
                RuleSetDTO sameEventRuleSet = sameEventRuleSets.get(0);
                if (CollectionUtils.isEmpty(matchingLogic.getRuleSetRowIds())) {
                    if (CollectionUtils.isEmpty(sameEventRuleSet.getRuleSetRowIds()))
                        return sameEventRuleSet.getId();
                    else
                        return null;
                } else {
                    if (CollectionUtils.isEmpty(sameEventRuleSet.getRuleSetRowIds()))
                        return null;
                    List<String> ruleSetRowIds = matchingLogic.getRuleSetRowIds().stream()
                            .filter(r -> sameEventRuleSet.getRuleSetRowIds().contains(r)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ruleSetRowIds))
                        return sameEventRuleSet.getId();
                }
            }
            if (CollectionUtils.isEmpty(matchingLogic.getRuleSetRowIds())) {
                List<RuleSetDTO> emptyRowIds = ruleSets.stream()
                        .filter(r -> CollectionUtils.isEmpty(r.getRuleSetRowIds())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(emptyRowIds)) {
                    return null;
                }
                return emptyRowIds.get(0).getId();
            }         

            List<RuleSetDTO> noEmptyRowIds = ruleSets.stream()
                    .filter(r -> CollectionUtils.isNotEmpty(r.getRuleSetRowIds())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(noEmptyRowIds)) {
                return null;
            }
            List<RuleSetDTO> rowIdsMatch = noEmptyRowIds.stream()
                    .filter(r -> r.getRuleSetRowIds().contains(matchingLogic.getRuleSetRowIds().get(0)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(rowIdsMatch)) {
                return null;
            }
            return rowIdsMatch.get(0).getId();
        } catch (Exception exception) {
        	log.error("unable to determine comparable logic of logic id" +  logicId, exception);
            return null;
        }

    }
    
    private Boolean hasDisciplineCompetencyMap(String collegeId, String competencyMapDiscipline) {
    	Set<Discipline> disciplines = repository.findByCollegeIdAndCompetencyMapDiscipline(collegeId, competencyMapDiscipline);
    	if(disciplines != null && !disciplines.isEmpty()) 
    		return true;
    	return false;
    }
    
    private Set<Discipline> getResources() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader cl = this.getClass().getClassLoader(); 
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resourceResolver.getResources(getDirectoryPath() + "/*.json");
        Set<Discipline> objects = new HashSet<>();
        for (Resource resource : resources) {
            Discipline object = (Discipline) mapper.readValue(resource.getInputStream(), Discipline.class);
            objects.add(object);
        }
        return objects;
    }
    
    
    
    private String getDirectoryPath() {
        return "classpath:defaults";
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
