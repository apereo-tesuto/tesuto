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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedRestrictedViewCompetencies;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.delivery.exception.NotEnoughStudentsAssessedException;
import org.cccnext.tesuto.delivery.exception.NotEnoughStudentsScoredException;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.report.dto.ClassRosterMasteryReportDto;
import org.cccnext.tesuto.delivery.report.dto.PerformanceCountDto;
import org.cccnext.tesuto.delivery.repository.mongo.AssessmentSessionRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ClassRosterMasteryReportService {

    @Autowired
    AssessmentSessionRepository assessmentSessionRepository;

    @Autowired
    CompetencyMapMasteryRestrictedViewService competencyMapMasteryRestrictedViewService;

    @Autowired
    @Qualifier("competencyMapOrderReader")
    CompetencyMapOrderReader competencyMapOrderService;

    @Autowired
    AssessmentReader assessmentReader;

    @Value("${placement.competency.select.parent.sort.level}")
    private int competencyParentSortLevel;

    @Value("${placement.competency.select.range}")
    private int competencySelectRange;

    public static final int MIN_REPORT_SIZE = 10;

    public ClassRosterMasteryReportDto getClassRosterMasteryRestrictedView(String competencyMapDiscipline, List<StudentViewDto> students) throws MissingAssessmentPerformanceMetadataException, NotEnoughStudentsAssessedException, NotEnoughStudentsScoredException {
        Map<String, List<AssessmentSession>> studentAssessmentSessionMap = getFilteredAssessmentSessionMapByStudent(students, competencyMapDiscipline);
        List<AssessmentSession> assessmentSessions = getAssessmentSessionsByMaxStudentAbility(studentAssessmentSessionMap);

        if (assessmentSessions.size() < MIN_REPORT_SIZE) {
            throw new NotEnoughStudentsAssessedException(assessmentSessions.size());
        }

        AssessmentSession latestAssessmentSession = getLatestAssessmentSessionFromStudentAssessmentSessionMap(studentAssessmentSessionMap);
        AssessmentDto latestAssessmentDto = assessmentReader.read(latestAssessmentSession.getContentId());

        List<PerformanceRangeMetadataDto> performanceRanges = null;
        List<CompetencyCategoryMetadataDto> competencyCategories = null;
        if (competencyMapMasteryRestrictedViewService.assessmentHasPerformanceMetadata(latestAssessmentDto)) {
            performanceRanges = latestAssessmentDto.getAssessmentMetadata().getOverallPerformance().getPerformanceRanges();
            competencyCategories = latestAssessmentDto.getAssessmentMetadata().getCompetencyPerformance().getCompetencyCategoriesByDiscipline(competencyMapDiscipline);
        } else {
            throw new MissingAssessmentPerformanceMetadataException();
        }

        double medianStudentAbility = calculateMedianStudentAbility(assessmentSessions);
        OrderedCompetencySet competencies = getCompetencies(medianStudentAbility, latestAssessmentSession.getCompetencyMapOrderIds().get(competencyMapDiscipline));

        ClassRosterMasteryReportDto classRosterMasteryReportDto = generateClassRosterMasteryReport(performanceRanges, competencyCategories, assessmentSessions, competencies, medianStudentAbility);
        classRosterMasteryReportDto.setTotalStudentCount(students.size());

        validateReport(classRosterMasteryReportDto);

        return classRosterMasteryReportDto;
    }

    private List<AssessmentSession> getAssessmentSessionsByMaxStudentAbility(Map<String, List<AssessmentSession>> studentAssessmentSessionMap) {
        List<AssessmentSession> assessmentSessions = new ArrayList<>();
        for (String studentId : studentAssessmentSessionMap.keySet()) {
            List<AssessmentSession> studentAssessmentSessions = studentAssessmentSessionMap.get(studentId);
            Collections.sort(studentAssessmentSessions, new AssessmentSessionComparator());
            assessmentSessions.add(studentAssessmentSessions.get(studentAssessmentSessions.size() - 1));
        }
        return assessmentSessions;
    }

    private AssessmentSession getLatestAssessmentSessionFromStudentAssessmentSessionMap(Map<String, List<AssessmentSession>> studentAssessmentSessionMap) {
        List<AssessmentSession> assessmentSessions = new ArrayList<>();
        for (String studentId : studentAssessmentSessionMap.keySet()) {
            assessmentSessions.addAll(studentAssessmentSessionMap.get(studentId));
        }
        Collections.sort(assessmentSessions, new AssessmentSessionDateComparator());
        return assessmentSessions.get(assessmentSessions.size() - 1);
    }

    private ClassRosterMasteryReportDto generateClassRosterMasteryReport(List<PerformanceRangeMetadataDto> performanceRanges,
                                                                         List<CompetencyCategoryMetadataDto> competencyCategories,
                                                                         List<AssessmentSession> assessmentSessions,
                                                                         OrderedCompetencySet competencies,
                                                                         Double medianStudentAbility) {
        ClassRosterMasteryReportDto classRosterMasteryReportDto = new ClassRosterMasteryReportDto();
        classRosterMasteryReportDto.setPerformanceRanges(performanceRanges);
        classRosterMasteryReportDto.setCompetencyCategories(competencyCategories);
        classRosterMasteryReportDto.setOverallPerformanceCountMap(generateOverallPerformanceCountMap(assessmentSessions, performanceRanges, medianStudentAbility));
        classRosterMasteryReportDto.setCompetencyCategoryCountMap(generateCompetencyCategoryCountMap(assessmentSessions, competencyCategories, medianStudentAbility));
        SelectedOrderedRestrictedViewCompetencies selectedOrderedRestrictedViewCompetencies = new SelectedOrderedRestrictedViewCompetencies();
        selectedOrderedRestrictedViewCompetencies.setMastered(competencyMapMasteryRestrictedViewService.convertToRestrictedView(competencies.getCompetenciesForMap().getMastered()));
        selectedOrderedRestrictedViewCompetencies.setTolearn(competencyMapMasteryRestrictedViewService.convertToRestrictedView(competencies.getCompetenciesForMap().getTolearn()));
        classRosterMasteryReportDto.setSelectedOrderedRestrictedViewCompetencies(selectedOrderedRestrictedViewCompetencies);
        classRosterMasteryReportDto.setStudentsAssessedCount(assessmentSessions.size());
        classRosterMasteryReportDto.setMedianDaysSinceAssessed(calculateMedianDaysSinceAssessed(assessmentSessions));
        return classRosterMasteryReportDto;
    }

    private int calculateMedianDaysSinceAssessed(List<AssessmentSession> assessmentSessions) {
        Collections.sort(assessmentSessions, new AssessmentSessionDateComparator());
        LocalDate now = LocalDate.now();
        int middlePosition = assessmentSessions.size() / 2;
        if (assessmentSessions.size() % 2 == 1) {
            LocalDate completionDate = assessmentSessions.get(middlePosition).getCompletionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return completionDate.until(now).getDays();
        } else {
            LocalDate completionDate1 = assessmentSessions.get(middlePosition - 1).getCompletionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate completionDate2 = assessmentSessions.get(middlePosition).getCompletionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return (int) Math.round((completionDate1.until(now).getDays() + completionDate2.until(now).getDays()) / 2.0);
        }
    }

    private Map<String, List<AssessmentSession>> getFilteredAssessmentSessionMapByStudent(List<StudentViewDto> students, String competencyMapDiscipline) {
        Map<String, List<AssessmentSession>> studentAssessmentSessionMap = new HashMap<>();
        for (StudentViewDto student : students) {
            List<AssessmentSession> studentAssessmentSessions = null;
            try {
                studentAssessmentSessions = assessmentSessionRepository.findByUserIdIgnoreCase(student.getCccid());
            } catch (Exception e) {
                log.error("An exception occurred while attempting to get assessments for student: " + student);
                log.error(e.getMessage());
                continue;
            }
            List<AssessmentSession> filteredAssessmentSessions = filterAssessmentSessionsByCompetencyMapDisciplineAndStudentAbility(studentAssessmentSessions, competencyMapDiscipline);
            if (filteredAssessmentSessions != null && filteredAssessmentSessions.size() != 0) {
                studentAssessmentSessionMap.put(student.getCccid(), filteredAssessmentSessions);
            }
        }
        return studentAssessmentSessionMap;
    }

    private OrderedCompetencySet getCompetencies(double studentAbility, String competencyMapOrderId) {
        return competencyMapOrderService
                .selectOrganizeByAbility(competencyMapOrderId, studentAbility, competencyParentSortLevel, competencySelectRange);
    }

    private void validateReport(ClassRosterMasteryReportDto classRosterMasteryReportDto) throws NotEnoughStudentsScoredException {
        int performanceCount = 0;
        for (String key : classRosterMasteryReportDto.getOverallPerformanceCountMap().keySet()) {
            if (!key.equals("uncounted")) {
                performanceCount += classRosterMasteryReportDto.getOverallPerformanceCountMap().get(key).getCount();
            }
        }
        if (performanceCount < MIN_REPORT_SIZE) {
            throw new NotEnoughStudentsScoredException(performanceCount);
        }
        for (String categoryKey : classRosterMasteryReportDto.getCompetencyCategoryCountMap().keySet()) {
            performanceCount = 0;
            for (String performanceKey : classRosterMasteryReportDto.getCompetencyCategoryCountMap().get(categoryKey).keySet()) {
                if (!performanceKey.equals("uncounted")) {
                    performanceCount += classRosterMasteryReportDto.getCompetencyCategoryCountMap().get(categoryKey).get(performanceKey).getCount();
                }
            }
            if (performanceCount < MIN_REPORT_SIZE) {
                throw new NotEnoughStudentsScoredException(performanceCount);
            }
        }
    }

    private Map<String, Map<String, PerformanceCountDto>> generateCompetencyCategoryCountMap(List<AssessmentSession> assessmentSessions,
                                                                                             List<CompetencyCategoryMetadataDto> competencyCategories,
                                                                                             Double medianStudentAbility) {
        Map<String, Map<String, PerformanceCountDto>> competencyCategoryCountMap = initializeCompetencyCategoryCountMap(competencyCategories);
        for (AssessmentSession assessmentSession : assessmentSessions) {
            for (CompetencyCategoryMetadataDto category : competencyCategories) {
                Map<String, PerformanceCountDto> categoryMap = competencyCategoryCountMap.get(category.getTitle());
                String performanceCategoryIdentifier = determinePerformanceCategory(assessmentSession, category.getPerformanceRanges());
                PerformanceCountDto performanceCountDto = categoryMap.get(performanceCategoryIdentifier);
                performanceCountDto.setCount(performanceCountDto.getCount() + 1);
                categoryMap.put(performanceCategoryIdentifier, performanceCountDto);
                competencyCategoryCountMap.put(category.getTitle(), highlightMedianPerformance(medianStudentAbility, categoryMap, category.getPerformanceRanges()));
            }
        }
        return competencyCategoryCountMap;
    }

    private Map<String, Map<String, PerformanceCountDto>> initializeCompetencyCategoryCountMap(List<CompetencyCategoryMetadataDto> competencyCategories) {
        Map<String, Map<String, PerformanceCountDto>> competencyCategoryCountMap = new HashMap<>();
        for (CompetencyCategoryMetadataDto category : competencyCategories) {
            Map<String, PerformanceCountDto> categoryMap = initializePerformanceCountMap(category.getPerformanceRanges());
            competencyCategoryCountMap.put(category.getTitle(), categoryMap);
        }
        return competencyCategoryCountMap;
    }

    private Map<String, PerformanceCountDto> generateOverallPerformanceCountMap(List<AssessmentSession> assessmentSessions,
                                                                                List<PerformanceRangeMetadataDto> performanceRanges,
                                                                                Double medianStudentAbility) {
        Map<String, PerformanceCountDto> overallPerformanceCountMap = initializePerformanceCountMap(performanceRanges);
        for (AssessmentSession assessmentSession : assessmentSessions) {
            String performanceCategoryIdentifier = determinePerformanceCategory(assessmentSession, performanceRanges);
            PerformanceCountDto performanceCountDto = overallPerformanceCountMap.get(performanceCategoryIdentifier);
            performanceCountDto.setCount(performanceCountDto.getCount() + 1);
            overallPerformanceCountMap.put(performanceCategoryIdentifier, performanceCountDto);
        }
        return highlightMedianPerformance(medianStudentAbility, overallPerformanceCountMap, performanceRanges);
    }

    private Map<String, PerformanceCountDto> highlightMedianPerformance(Double medianStudentAbility,
                                                                        Map<String, PerformanceCountDto> performanceMap,
                                                                        List<PerformanceRangeMetadataDto> performanceRanges) {
        ImmutablePair<String, Integer> medianPerformance = competencyMapMasteryRestrictedViewService.determinePerformance(medianStudentAbility, performanceRanges);
        PerformanceCountDto toBeHighlighted = performanceMap.get(medianPerformance.getLeft());
        toBeHighlighted.setHighlighted(true);
        performanceMap.put(medianPerformance.getLeft(), toBeHighlighted);
        return performanceMap;
    }

    private Map<String, PerformanceCountDto> initializePerformanceCountMap(List<PerformanceRangeMetadataDto> performanceRanges) {
        Map<String, PerformanceCountDto> overallPerformanceCountMap = new HashMap<>();
        overallPerformanceCountMap.put("uncounted", new PerformanceCountDto());
        for (PerformanceRangeMetadataDto performanceRangeMetadataDto : performanceRanges) {
            overallPerformanceCountMap.put(performanceRangeMetadataDto.getIdentifier(), new PerformanceCountDto());
        }
        return overallPerformanceCountMap;
    }

    private String determinePerformanceCategory(AssessmentSession assessmentSession, List<PerformanceRangeMetadataDto> performanceRanges) {
        String category = "uncounted";
        double studentAbility = assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue();
        Collections.sort(performanceRanges, new PerformanceRangeComparator());
        for (PerformanceRangeMetadataDto range : performanceRanges) {
            if (studentAbility >= range.getMin() && studentAbility <= range.getMax()) {
                category = range.getIdentifier();
                break;
            }
        }
        return category;
    }

    private List<AssessmentSession> filterAssessmentSessionsByCompetencyMapDisciplineAndStudentAbility(List<AssessmentSession> assessmentSessions, String competencyMapDiscipline) {
        List<AssessmentSession> filteredAssessmentSessions = new ArrayList<>();
        for (AssessmentSession assessmentSession : assessmentSessions) {
            AssessmentDto assessmentDto = assessmentReader.read(assessmentSession.getContentId());
            if (assessmentDto != null
                    && assessmentDto.getAssessmentMetadata() != null
                    && assessmentDto.getAssessmentMetadata().getCompetencyMapDisciplines() != null
                    && assessmentDto.getAssessmentMetadata().getCompetencyMapDisciplines().contains(competencyMapDiscipline)
                    && assessmentSession.getCompetencyMapOrderIds() != null
                    && assessmentSession.getCompetencyMapOrderIds().containsKey(competencyMapDiscipline)
                    && assessmentSession.getCompletionDate() != null
                    && assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY) != null
                    && !Double.isNaN(assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue())) {
                filteredAssessmentSessions.add(assessmentSession);
            }
        }
        return filteredAssessmentSessions;
    }

    private double calculateMedianStudentAbility(List<AssessmentSession> assessmentSessions) {
        Collections.sort(assessmentSessions, new AssessmentSessionComparator());
        int middlePosition = assessmentSessions.size() / 2;
        if (assessmentSessions.size() % 2 == 1) {
            return assessmentSessions.get(middlePosition).getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue();
        } else {
            return (assessmentSessions.get(middlePosition - 1).getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue() +
                    assessmentSessions.get(middlePosition).getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue()) / 2.0;
        }
    }

    /**
     * For testing purposes
     *
     * @return
     */
    PerformanceRangeComparator getPerformanceRangeComparator() {
        return new PerformanceRangeComparator();
    }

    /**
     * Older to newer
     */
    private class AssessmentSessionDateComparator implements Comparator<AssessmentSession> {
        @Override
        public int compare(AssessmentSession o1, AssessmentSession o2) {
            return o1.getCompletionDate().compareTo(o2.getCompletionDate());
        }
    }

    /**
     * Largest to smallest
     */
    private class PerformanceRangeComparator implements Comparator<PerformanceRangeMetadataDto> {
        @Override
        public int compare(PerformanceRangeMetadataDto o1, PerformanceRangeMetadataDto o2) {
            return Double.compare(o2.getMax(), o1.getMax());
        }
    }

    /**
     * Smallest to largest
     */
    private class AssessmentSessionComparator implements Comparator<AssessmentSession> {
        @Override
        public int compare(AssessmentSession o1, AssessmentSession o2) {
            return Double.compare(o1.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue(), o2.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue());
        }
    }
}
