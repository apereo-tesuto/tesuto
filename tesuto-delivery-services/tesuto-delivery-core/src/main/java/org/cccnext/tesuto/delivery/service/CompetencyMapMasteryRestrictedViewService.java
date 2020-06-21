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
package org.cccnext.tesuto.delivery.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySetRestrictedView;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedRestrictedViewCompetencies;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.content.viewdto.CompetencyRestrictedViewDto;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;

// loop over disciplines, return in alphabetical // get by metadata ref id
public class CompetencyMapMasteryRestrictedViewService {
    
    private Integer competencySelectRange;

    private Integer competencyParentSortLevel;

    private CompetencyMapOrderReader competencyMapOrderReader;

    private PsychometricsCalculationService psychometricsCalculationService;

    private AssessmentReader assessmentReader;

    public Integer getCompetencySelectRange() {
        return competencySelectRange;
    }

    public void setCompetencySelectRange(Integer competencySelectRange) {
        this.competencySelectRange = competencySelectRange;
    }

    public Integer getCompetencyParentSortLevel() {
        return competencyParentSortLevel;
    }

    public void setCompetencyParentSortLevel(Integer competencyParentSortLevel) {
        this.competencyParentSortLevel = competencyParentSortLevel;
    }

    public CompetencyMapOrderReader getCompetencyMapOrderReader() {
        return competencyMapOrderReader;
    }

    public void setCompetencyMapOrderReader(CompetencyMapOrderReader competencyMapOrderReader) {
        this.competencyMapOrderReader = competencyMapOrderReader;
    }

    public PsychometricsCalculationService getPsychometricsCalculationService() {
        return psychometricsCalculationService;
    }

    public void setPsychometricsCalculationService(PsychometricsCalculationService psychometricsCalculationService) {
        this.psychometricsCalculationService = psychometricsCalculationService;
    }

    public AssessmentReader getAssessmentReader() {
        return assessmentReader;
    }

    public void setAssessmentReader(AssessmentReader assessmentReader) {
        this.assessmentReader = assessmentReader;
    }

    public Map<String,OrderedCompetencySetRestrictedView> getMasteryRestrictedView(AssessmentSession assessmentSession, Integer parentLevel) {
        Map<String,OrderedCompetencySet> competencySets = getCompetencySet(assessmentSession,  parentLevel);
        AssessmentDto assessmentDto = assessmentReader.read(assessmentSession.getContentId());
        if (assessmentHasPerformanceMetadata(assessmentDto)) {
            List<PerformanceRangeMetadataDto> performanceRanges = assessmentDto.getAssessmentMetadata().getOverallPerformance().getPerformanceRanges();
            List<CompetencyCategoryMetadataDto> competencyCategories = assessmentDto.getAssessmentMetadata().getCompetencyPerformance().getCompetencyCategories();
            Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMetadataDtoMap = createCompetencyCategoryMap(competencyCategories);
            return convertToRestrictedView(competencySets,
                    performanceRanges,
                    competencyCategoryMetadataDtoMap);
        } else {
            return null;
        }
    }

    private Map<String,OrderedCompetencySet> getCompetencySet(AssessmentSession assessmentSession, Integer parentLevel) {
        Map<String,OrderedCompetencySet> setOfCompetencies = new HashMap<>();
        List<String> competencyMapDisciplines = new ArrayList<>();
        if(StringUtils.isNotBlank(assessmentSession.getCompetencyMapDisciplineFromEntryTestlet())) {
            competencyMapDisciplines.add(assessmentSession.getCompetencyMapDisciplineFromEntryTestlet());
        } else if(assessmentSession.getCompetencyMapOrderIds() != null 
                && CollectionUtils.isNotEmpty(assessmentSession.getCompetencyMapOrderIds().keySet())){
            
            competencyMapDisciplines.addAll(assessmentSession.getCompetencyMapOrderIds().keySet());
        }
        for(String competencyMapDiscipline:competencyMapDisciplines) {
            OrderedCompetencySet orderedCompetencies = getCompetencies( assessmentSession,  competencyMapDiscipline,  parentLevel);
            if(orderedCompetencies != null) {
                setOfCompetencies.put(competencyMapDiscipline, orderedCompetencies);
            }
        }
        
        return setOfCompetencies;
    }
    
    private OrderedCompetencySet getCompetencies(AssessmentSession assessmentSession, String competencyMapDiscipline, Integer parentLevel) {
        // TODO Get from assessmentSession once available
        if (assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY) != null 
                && assessmentSession.getCompetencyMapOrderIds() != null
                 && assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY) != null
                && !Double.isNaN(assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue())) {
            
            double studentAbility = assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue();
            
            String competencyMapOrderId = assessmentSession.getCompetencyMapOrderIds().get(competencyMapDiscipline);
            
            if (parentLevel == null) {
                parentLevel = competencyParentSortLevel;
            }

            OrderedCompetencySet selectedOrderCompetencies = competencyMapOrderReader
                    .selectOrganizeByAbility(competencyMapOrderId, studentAbility, parentLevel, competencySelectRange);

            return selectedOrderCompetencies;
        }
        return null;
    }

    private Map<String,OrderedCompetencySetRestrictedView> convertToRestrictedView(Map<String, OrderedCompetencySet> sets,
                                                                                   List<PerformanceRangeMetadataDto> performanceRanges,
                                                                                   Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMetadataDtoMap) {
        if(sets == null ||  sets.size() == 0) {
            return null;
        }
        Map<String,OrderedCompetencySetRestrictedView> restrictedSets = new HashMap<>(); 
        sets.keySet().forEach(key -> restrictedSets.put(key, convertToRestrictedView(sets.get(key), key, performanceRanges, competencyCategoryMetadataDtoMap)));
        return restrictedSets;
    }

    private OrderedCompetencySetRestrictedView convertToRestrictedView(OrderedCompetencySet orderedSet, String discipline, List<PerformanceRangeMetadataDto> performanceRanges, Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMetadataDtoMap) {
        OrderedCompetencySetRestrictedView restrictedSet = new OrderedCompetencySetRestrictedView();
        restrictedSet.setCompetenciesForMap(convertToRestrictedViewForMap(orderedSet.getCompetenciesForMap(), performanceRanges));
        restrictedSet.setCompetenciesByTopic(convertToRestrictedViewCompetencies(competencyCategoryMetadataDtoMap.get(discipline), orderedSet.getCompetenciesForMap().getStudentAbility()));
        return restrictedSet;
    }

    List<SelectedOrderedRestrictedViewCompetencies> convertToRestrictedViewCompetencies(Map<String, CompetencyCategoryMetadataDto> competencyCategoryMetadataDtoMap, Double studentAbility) {
        if (competencyCategoryMetadataDtoMap == null) {
            return null;
        }
        List<SelectedOrderedRestrictedViewCompetencies> competencies = new ArrayList<>();
        for (String refId : competencyCategoryMetadataDtoMap.keySet()) {
            SelectedOrderedRestrictedViewCompetencies competency = new SelectedOrderedRestrictedViewCompetencies();
            CompetencyCategoryMetadataDto competencyCategory = competencyCategoryMetadataDtoMap.get(refId);
            competency.setTitle(competencyCategory.getTitle());
            competency.setMaxDifficulty(getMaxDifficultyForPerformanceRanges(competencyCategory.getPerformanceRanges()));
            ImmutablePair<String, Integer> performance = determinePerformance(studentAbility, competencyCategory.getPerformanceRanges());
            competency.setPerformance(performance.getLeft());
            competency.setPosition(performance.getRight());
            competencies.add(competency);
        }

        Collections.sort(competencies);
        return competencies;
    }

    private List<SelectedOrderedRestrictedViewCompetencies> convertToRestrictedViewCompetencies(Map<String,SelectedOrderedCompetencies> sets, Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMetadataDtoMap) {
        if(sets == null ||  sets.size() == 0) {
            return null;
        }
        List<SelectedOrderedRestrictedViewCompetencies> selectedOrderedRestrictedViewCompetenciesList = new ArrayList<>();
        Map<String,SelectedOrderedRestrictedViewCompetencies> restrictedSets = new HashMap<>();
        sets.keySet().forEach(key -> {
            SelectedOrderedRestrictedViewCompetencies restrictedSet = convertToRestrictedViewByTopic(sets.get(key), competencyCategoryMetadataDtoMap);
            restrictedSets.put(key, restrictedSet);
            selectedOrderedRestrictedViewCompetenciesList.add(restrictedSet);
        });

        Collections.sort(selectedOrderedRestrictedViewCompetenciesList);

        return selectedOrderedRestrictedViewCompetenciesList;
    }

    SelectedOrderedRestrictedViewCompetencies convertToRestrictedViewForMap(SelectedOrderedCompetencies set, List<PerformanceRangeMetadataDto> performanceRanges) {
        SelectedOrderedRestrictedViewCompetencies restrictedSet = new SelectedOrderedRestrictedViewCompetencies();
        if(set.getParent() != null) {
            restrictedSet.setParent(new CompetencyRestrictedViewDto(set.getParent()));
        }
        ImmutablePair<String, Integer> performanceAndPosition = determinePerformance(set.getStudentAbility(), performanceRanges);
        restrictedSet.setPerformance(performanceAndPosition.getLeft());
        restrictedSet.setPosition(performanceAndPosition.getRight());

        restrictedSet.setMastered(convertToRestrictedView(set.getMastered()));
        restrictedSet.setTolearn(convertToRestrictedView(set.getTolearn()));
        return restrictedSet;
    }

    private SelectedOrderedRestrictedViewCompetencies convertToRestrictedViewByTopic(SelectedOrderedCompetencies set, Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMetadataDtoMap) {
        SelectedOrderedRestrictedViewCompetencies restrictedSet = new SelectedOrderedRestrictedViewCompetencies();
        if(set.getParent() != null) {
            restrictedSet.setParent(new CompetencyRestrictedViewDto(set.getParent()));
            Map<String, CompetencyCategoryMetadataDto> competencyCategoryMetadataDtoMapByDiscipline = competencyCategoryMetadataDtoMap.get(set.getParent().getIdentifier());
            if (competencyCategoryMetadataDtoMapByDiscipline != null) {
                CompetencyCategoryMetadataDto competencyCategoryMetadataDto = competencyCategoryMetadataDtoMapByDiscipline.get(set.getParent().getDiscipline());
                if (competencyCategoryMetadataDto != null) {
                    List<PerformanceRangeMetadataDto> performanceRanges = competencyCategoryMetadataDto.getPerformanceRanges();
                    ImmutablePair<String, Integer> performanceAndPosition = determinePerformance(set.getStudentAbility(), performanceRanges);
                    restrictedSet.setPerformance(performanceAndPosition.getLeft());
                    restrictedSet.setPosition(performanceAndPosition.getRight());
                    restrictedSet.setMaxDifficulty(getMaxDifficultyForPerformanceRanges(performanceRanges));
                } else {
                    restrictedSet.setPerformance("error");
                    restrictedSet.setPosition(0);
                }
            } else {
                restrictedSet.setPerformance("error");
                restrictedSet.setPosition(0);
            }
        }
        return restrictedSet;
    }

    double getMaxDifficultyForPerformanceRanges(List<PerformanceRangeMetadataDto> performanceRanges) {
        double maxDifficulty = 0;
        for (PerformanceRangeMetadataDto performanceRange : performanceRanges) {
            if (performanceRange.getMax() > maxDifficulty) {
                maxDifficulty = performanceRange.getMax();
            }
        }
        return maxDifficulty;
    }

    ImmutablePair<String, Integer> determinePerformance(Double studentAbility, List<PerformanceRangeMetadataDto> performanceRanges) {
        String performance = "error";
        Integer position = 0;
        if (performanceRanges != null) {
            for (PerformanceRangeMetadataDto performanceRange : performanceRanges) {
                if (studentAbility >= performanceRange.getMin() && studentAbility <= performanceRange.getMax()) {
                    performance = performanceRange.getIdentifier();
                    position = performanceRange.getPosition();
                    break;
                }
            }
        }
        return new ImmutablePair<>(performance, position);
    }

    private Double getAdditiveTerm(AssessmentSession  assessmentSession) {
        AssessmentDto assessment = getAssessment(assessmentSession);
        if(assessment.getAssessmentMetadata() != null) {
            return assessment.getAssessmentMetadata().getScaleAdditiveTerm();
        }
        return 10.0;
    } 
    private Double getMultiplicativeTerm(AssessmentSession assessmentSession) {
        AssessmentDto assessment = getAssessment(assessmentSession);
        if(assessment.getAssessmentMetadata() != null) {
            return assessment.getAssessmentMetadata().getScaleMultiplicativeTerm();
        }
        return 100.0;
    }
    
    private AssessmentDto getAssessment(AssessmentSession assessmentSession) {
        if(assessmentSession.getAssessment() == null) {
            assessmentSession.setAssessment(assessmentReader.read(assessmentSession.getContentId()));
        }
        return assessmentSession.getAssessment();
    }
    
    public List<CompetencyRestrictedViewDto>  convertToRestrictedView(List<CompetencyDifficultyDto> competencies) {
        return competencies.stream().map(c -> new CompetencyRestrictedViewDto(c.getCompetency())).collect(Collectors.toList());
    }

    /**
     * Returns a map of competency categories
     * discipline -> refid -> competency category
     * @param competencyCategoryMetadataDtoList
     * @return
     */
    Map<String, Map<String, CompetencyCategoryMetadataDto>> createCompetencyCategoryMap(List<CompetencyCategoryMetadataDto> competencyCategoryMetadataDtoList) {
        Map<String, Map<String, CompetencyCategoryMetadataDto>> competencyCategoryMap = new HashMap<>();

        for (CompetencyCategoryMetadataDto competencyCategory : competencyCategoryMetadataDtoList) {
            Map<String, CompetencyCategoryMetadataDto> competencyCategoryMapByRefId = competencyCategoryMap.get(competencyCategory.getCompetencyMapDiscipline());
            if (competencyCategoryMapByRefId == null) {
                competencyCategoryMapByRefId = new HashMap<>();
            }
            competencyCategoryMapByRefId.put(competencyCategory.getCompetencyRefId(), competencyCategory);
            competencyCategoryMap.put(competencyCategory.getCompetencyMapDiscipline(), competencyCategoryMapByRefId);
        }

        return competencyCategoryMap;
    }

    public boolean assessmentHasPerformanceMetadata(AssessmentDto assessmentDto) {
        return assessmentDto.getAssessmentMetadata() != null &&
                assessmentDto.getAssessmentMetadata().getOverallPerformance() != null &&
                assessmentDto.getAssessmentMetadata().getOverallPerformance().getPerformanceRanges() != null &&
                assessmentDto.getAssessmentMetadata().getCompetencyPerformance() != null &&
                assessmentDto.getAssessmentMetadata().getCompetencyPerformance().getCompetencyCategories() != null;
    }
}
