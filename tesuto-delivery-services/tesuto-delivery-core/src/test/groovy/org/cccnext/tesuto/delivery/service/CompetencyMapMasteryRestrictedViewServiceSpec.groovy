package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto
import org.cccnext.tesuto.content.dto.competency.CompetencyDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto
import org.cccnext.tesuto.content.dto.metadata.CompetencyPerformanceMetadataDto
import org.cccnext.tesuto.content.dto.metadata.OverallPerformanceMetadataDto
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedRestrictedViewCompetencies
import org.cccnext.tesuto.util.test.SessionGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
class CompetencyMapMasteryRestrictedViewServiceSpec extends DeliverySpecBase {

    @Shared
    CompetencyMapMasteryRestrictedViewService competencyMapMasteryRestrictedViewService;

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        competencyMapMasteryRestrictedViewService = context.getBean("competencyMasteryService")
    }

    def "convertToRestrictedViewForMap returns overall performance data properly"() {
        when:
        def selectedOrderedCompetencies = generateSelectedOrderedCompetencies()
        def performanceRanges = generatePerformanceRanges()
        def overallPerformance = competencyMapMasteryRestrictedViewService.convertToRestrictedViewForMap(selectedOrderedCompetencies, performanceRanges)

        then:
        !overallPerformance.performance.equals("error")
        overallPerformance.position != 0

    }

    def "convertToRestrictedViewCompetencies returns sorted set of competency view data"() {
        when:
        def competencyCategoryMetadataList = generateCompetencyCategoryMetadataList()
        def competencyCategoryMetadataMap = competencyMapMasteryRestrictedViewService.createCompetencyCategoryMap(competencyCategoryMetadataList)
        def competencyViewList = competencyMapMasteryRestrictedViewService.convertToRestrictedViewCompetencies(competencyCategoryMetadataMap.get("MATH"), 7.5d)

        then:
        competencyViewList.size() == 5
        def previousDifficulty = competencyViewList.get(0).getMaxDifficulty()
        def previousTitle = competencyViewList.get(0).getTitle()
        for (int index = 1; index < competencyViewList.size(); index++) {
            if (competencyViewList.get(index).getMaxDifficulty() == previousDifficulty) {
                previousTitle.compareTo(competencyViewList.get(index).getTitle()) > 0
            } else {
                previousDifficulty > competencyViewList.get(index).getMaxDifficulty()
            }
        }
    }

    def "createCompetencyCategoryMap properly creates a map for competency category metadata"() {
        when:
        def competencyCategoryMetadataList = generateCompetencyCategoryMetadataList()
        def competencyCategoryMetadata = competencyMapMasteryRestrictedViewService.createCompetencyCategoryMap(competencyCategoryMetadataList)

        then:
        competencyCategoryMetadata.keySet().size() == 2
        competencyCategoryMetadata.keySet().contains("ENGLISH")
        competencyCategoryMetadata.keySet().contains("MATH")
        competencyCategoryMetadata.get("MATH").keySet().size() == 5
        competencyCategoryMetadata.get("ENGLISH").keySet().size() == 1
        def competencyCategoryMetadataDto = competencyCategoryMetadata.get("ENGLISH").get("ThirdId")
        competencyCategoryMetadataDto.getCompetencyMapDiscipline().equalsIgnoreCase("ENGLISH")
        competencyCategoryMetadataDto.getCompetencyRefId().equals("ThirdId")
        competencyCategoryMetadataDto.getTitle().equals("Third Title")

        where:
        i << (1..5)
    }

    def "assessmentHasPerformanceMetadata returns true for assessment with all metadata"() {
        when:
        def assessmentDto = new AssessmentDto()
        def assessmentMetadataDto = new AssessmentMetadataDto()
        def overallPerformanceMetadataDto = new OverallPerformanceMetadataDto()
        def competencyPerformanceMetadataDto = new CompetencyPerformanceMetadataDto()
        overallPerformanceMetadataDto.setPerformanceRanges(generatePerformanceRanges())
        assessmentMetadataDto.setOverallPerformance(overallPerformanceMetadataDto)
        competencyPerformanceMetadataDto.setCompetencyCategories(generateCompetencyCategoryMetadataList())
        assessmentMetadataDto.setCompetencyPerformance(competencyPerformanceMetadataDto)
        assessmentDto.setAssessmentMetadata(assessmentMetadataDto)

        then:
        competencyMapMasteryRestrictedViewService.assessmentHasPerformanceMetadata(assessmentDto)
    }

    def "assessmentHasPerformanceMetadata returns false for assessment missing overall performance"() {
        when:
        def assessmentDto = new AssessmentDto()
        def assessmentMetadataDto = new AssessmentMetadataDto()
        def competencyPerformanceMetadataDto = new CompetencyPerformanceMetadataDto()
        assessmentMetadataDto.setOverallPerformance(null)
        competencyPerformanceMetadataDto.setCompetencyCategories(generateCompetencyCategoryMetadataList())
        assessmentMetadataDto.setCompetencyPerformance(competencyPerformanceMetadataDto)
        assessmentDto.setAssessmentMetadata(assessmentMetadataDto)

        then:
        !competencyMapMasteryRestrictedViewService.assessmentHasPerformanceMetadata(assessmentDto)
    }

    def "assessmentHasPerformanceMetadata returns false for assessment missing competency performance"() {
        when:
        def assessmentDto = new AssessmentDto()
        def assessmentMetadataDto = new AssessmentMetadataDto()
        def overallPerformanceMetadataDto = new OverallPerformanceMetadataDto()
        overallPerformanceMetadataDto.setPerformanceRanges(generatePerformanceRanges())
        assessmentMetadataDto.setOverallPerformance(overallPerformanceMetadataDto)
        assessmentMetadataDto.setCompetencyPerformance(null)
        assessmentDto.setAssessmentMetadata(assessmentMetadataDto)

        then:
        !competencyMapMasteryRestrictedViewService.assessmentHasPerformanceMetadata(assessmentDto)
    }

    def "determinePerformance returns properly for performance within a range"() {
        when:
        def performanceRanges = generatePerformanceRanges()
        def result = competencyMapMasteryRestrictedViewService.determinePerformance(3.5d, performanceRanges)

        then:
        !result.getLeft().equals("error")
        result.getRight() != 0
    }

    def "determinePerformance returns error for performance outside all ranges"() {
        when:
        def performanceRanges = generatePerformanceRanges()
        def result = competencyMapMasteryRestrictedViewService.determinePerformance(100d, performanceRanges)

        then:
        result.getLeft().equals("error")
        result.getRight() == 0
    }

    def "getMaxDifficultyForPerformanceRanges returns max difficulty for a given list of performance ranges"() {
        when:
        def performanceRanges = generatePerformanceRanges()

        then:
        competencyMapMasteryRestrictedViewService.getMaxDifficultyForPerformanceRanges(performanceRanges) == performanceRanges.get(3).getMax()
    }
}
