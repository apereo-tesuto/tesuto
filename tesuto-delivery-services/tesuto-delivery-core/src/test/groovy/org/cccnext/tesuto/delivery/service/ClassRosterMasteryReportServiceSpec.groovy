package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.view.*
import org.cccnext.tesuto.content.dto.AssessmentBaseType
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet
import org.cccnext.tesuto.delivery.report.dto.PerformanceCountDto
import org.cccnext.tesuto.admin.viewdto.StudentViewDto
import org.cccnext.tesuto.delivery.exception.InvalidRosterMasteryReportException
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared

import java.time.LocalDate
import java.time.ZoneId

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
class ClassRosterMasteryReportServiceSpec extends DeliverySpecBase {

    @Shared
    ClassRosterMasteryReportService classRosterMasteryReportService;

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        classRosterMasteryReportService = context.getBean("classRosterMasteryReportService")
        classRosterMasteryReportService.assessmentSessionRepository = context.getBean("assessmentSessionRepositoryStub")
        classRosterMasteryReportService.assessmentReader = context.getBean("assessmentReader")
    }

    def "validateReport throws no InvalidRosterMasteryReportException when validating a valid report"() {
        when:
        def performanceRanges = generatePerformanceRanges(false)
        def competencyCategories = generateCompetencyCategoryMetadataList(false, false)
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def orderedCompetencies = generateOrderedCompetencySet()
        def studentMedian = 5.0
        def report = classRosterMasteryReportService.generateClassRosterMasteryReport(performanceRanges,
                competencyCategories,
                assessmentSessions,
                orderedCompetencies,
                studentMedian)
        classRosterMasteryReportService.validateReport(report)

        then:
        notThrown InvalidRosterMasteryReportException

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "validateReport throws exception when not enough students scored within a competency category"() {
        when:
        def performanceRanges = generatePerformanceRanges(false)
        def competencyCategories = generateCompetencyCategoryMetadataList(false, false)
        def competencyCategory = competencyCategories.get(0)
        def competencyPerformanceRanges = competencyCategory.getPerformanceRanges()
        def performanceRange = competencyPerformanceRanges.get(0)
        performanceRange.setMin(2.0) // causing the student with 1.0 ability to be "uncounted"
        competencyPerformanceRanges.set(0, performanceRange)
        competencyCategory.setPerformanceRanges(competencyPerformanceRanges)
        competencyCategories.set(0, competencyCategory)
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def orderedCompetencies = generateOrderedCompetencySet()
        def studentMedian = 5.0
        def report = classRosterMasteryReportService.generateClassRosterMasteryReport(performanceRanges,
                competencyCategories,
                assessmentSessions,
                orderedCompetencies,
                studentMedian)
        classRosterMasteryReportService.validateReport(report)

        then:
        thrown InvalidRosterMasteryReportException

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "validateReport throws exception when not enough students scored within overall performance ranges"() {
        when:
        def performanceRanges = generatePerformanceRanges(false)
        def performanceRange = performanceRanges.get(0)
        performanceRange.setMin(2.0) // causing the student with 1.0 ability to be "uncounted"
        performanceRanges.set(0, performanceRange)
        def competencyCategories = generateCompetencyCategoryMetadataList(false, false)
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def orderedCompetencies = generateOrderedCompetencySet()
        def studentMedian = 5.0
        def report = classRosterMasteryReportService.generateClassRosterMasteryReport(performanceRanges,
                                                                                      competencyCategories,
                                                                                      assessmentSessions,
                                                                                      orderedCompetencies,
                                                                                      studentMedian)
        classRosterMasteryReportService.validateReport(report)

        then:
        thrown InvalidRosterMasteryReportException

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "calculateMedianDaysSinceAssessed gets the correct rounded median days from an even list of assessmentSessions"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def medianDaysSinceAssessed = classRosterMasteryReportService.calculateMedianDaysSinceAssessed(assessmentSessions)

        then:
        medianDaysSinceAssessed == 5

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "calculateMedianDaysSinceAssessed gets the correct median days from an odd list of assessmentSessions"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(9, assessmentDto, false)
        def medianDaysSinceAssessed = classRosterMasteryReportService.calculateMedianDaysSinceAssessed(assessmentSessions)

        then:
        medianDaysSinceAssessed == 4

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "getAssessmentSessionsByMaxStudentAbility returns a list of sessions with maximum student ability for each student"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(20, assessmentDto, true)
        def maxAbility = 0
        for (AssessmentSession assessmentSession : assessmentSessions) {
            if (assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue() > maxAbility) {
                maxAbility = assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue()
            }
        }
        def studentAssessmentSessionMap = [:]
        studentAssessmentSessionMap.put(generator.randomId(), assessmentSessions)
        def maxStudentAbilityAssessmentSessionList = classRosterMasteryReportService.getAssessmentSessionsByMaxStudentAbility(studentAssessmentSessionMap)

        then:
        maxStudentAbilityAssessmentSessionList.size() == 1
        maxStudentAbilityAssessmentSessionList.get(0).getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue() == maxAbility

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "getFilteredAssessmentSessionMapByStudent returns a map of student->assessmentSessions"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(20, assessmentDto, true)
        def assessmentSessionsWithUndesiredSessions = generateUndesiredAssessmentSessions(10,
                assessmentDto,
                assessmentSessions.get(0).getCompetencyMapOrderIds())
        assessmentSessions.addAll(assessmentSessionsWithUndesiredSessions)
        classRosterMasteryReportService.assessmentSessionRepository.save(assessmentSessions)
        def student = new StudentViewDto()
        student.setCccid(generator.randomId())
        def studentList = Arrays.asList(student)
        def studentAssessmentSessionMap = classRosterMasteryReportService.getFilteredAssessmentSessionMapByStudent(studentList,
                assessmentDto.assessmentMetadata?.competencyMapDisciplines?.get(0))

        then:
        if (assessmentDto.assessmentMetadata != null) {
            studentAssessmentSessionMap.keySet().size() == 1
            studentAssessmentSessionMap.keySet().contains(student.getCccid())
            def studentAssessmentSessions = studentAssessmentSessionMap.get(student.getCccid())
            studentAssessmentSessions.size() == 20
        } else { // random assessment contains no metadata
            studentAssessmentSessionMap.keySet().size() == 0
        }

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "generateCompetencyCategoryCountMap generates accurate map for given category performance data"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def competencyCategoryList = generateCompetencyCategoryMetadataList()
        def studentMedian = 5.0
        def competencyCategoryCountMap = classRosterMasteryReportService.generateCompetencyCategoryCountMap(assessmentSessions, competencyCategoryList, studentMedian)

        then:
        for (String category : competencyCategoryCountMap.keySet()) {
            def categoryMap = competencyCategoryCountMap.get(category)
            categoryMap.get("Fourth Range").getCount() == 2
            !categoryMap.get("Fourth Range").isHighlighted()
            categoryMap.get("Third Range").getCount() == 3
            !categoryMap.get("Third Range").isHighlighted()
            categoryMap.get("Second Range").getCount() == 3
            categoryMap.get("Second Range").isHighlighted()
            categoryMap.get("First Range").getCount() == 2
            !categoryMap.get("First Range").isHighlighted()
        }

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "initializeCompetencyCategoryCountMap properly initializes a new map for a given set of performance ranges"() {
        when:
        def competencyCategoryList = generateCompetencyCategoryMetadataList()
        def initializedCompetencyCategoryCountMap = classRosterMasteryReportService.initializeCompetencyCategoryCountMap(competencyCategoryList)

        then:
        initializedCompetencyCategoryCountMap.keySet().size() == 6
        for (String category : initializedCompetencyCategoryCountMap.keySet()) {
            def categoryMap = initializedCompetencyCategoryCountMap.get(category)
            categoryMap.keySet().size() == 5
            for (String performanceRange : categoryMap.keySet()) {
                categoryMap.get(performanceRange).getCount() == 0
                !categoryMap.get(performanceRange).isHighlighted()
            }
        }
    }

    def "generateOverallPerformanceCountMap generates accurate map for given overall performance data"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(10, assessmentDto, false)
        def performanceRanges = generatePerformanceRanges(false)
        def studentMedian = 5.0
        def overallPerformanceCountMap = classRosterMasteryReportService.generateOverallPerformanceCountMap(
                assessmentSessions, performanceRanges, studentMedian)

        then:
        overallPerformanceCountMap.get("Fourth Range").getCount() == 2
        !overallPerformanceCountMap.get("Fourth Range").isHighlighted()
        overallPerformanceCountMap.get("Third Range").getCount() == 3
        !overallPerformanceCountMap.get("Third Range").isHighlighted()
        overallPerformanceCountMap.get("Second Range").getCount() == 3
        overallPerformanceCountMap.get("Second Range").isHighlighted()
        overallPerformanceCountMap.get("First Range").getCount() == 2
        !overallPerformanceCountMap.get("First Range").isHighlighted()

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "highlightMedianPerformance properly highlights the correct median performance range"() {
        when:
        def performanceRanges = generatePerformanceRanges()
        def performanceCountMap = classRosterMasteryReportService.initializePerformanceCountMap(performanceRanges)
        def studentMedian = 5.0

        then:
        def highlightedPerformanceMap = classRosterMasteryReportService.highlightMedianPerformance(studentMedian, performanceCountMap, performanceRanges)
        Collections.sort(performanceRanges, classRosterMasteryReportService.getPerformanceRangeComparator())
        def found = false
        for (PerformanceRangeMetadataDto performanceRange : performanceRanges) {
            if (performanceRange.getMin() <= studentMedian
                &&  studentMedian <= performanceRange.getMax()) {
                if (!found) {
                    found = true
                    highlightedPerformanceMap.get(performanceRange.getIdentifier()).isHighlighted()
                } else {
                    !highlightedPerformanceMap.get(performanceRange.getIdentifier()).isHighlighted()
                }
            } else {
                !highlightedPerformanceMap.get(performanceRange.getIdentifier()).isHighlighted()
            }
        }

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "initializePerformanceCountMap properly initializes a new map for a given set of performance ranges"() {
        when:
        List<PerformanceRangeMetadataDto> performanceRanges = generatePerformanceRanges()
        Map<String, PerformanceCountDto> initializedMap = classRosterMasteryReportService.initializePerformanceCountMap(performanceRanges)

        then:
        initializedMap.keySet().size() == performanceRanges.size() + 1
        for (PerformanceRangeMetadataDto performanceRange : performanceRanges) {
            initializedMap.get(performanceRange.getIdentifier()).getCount() == 0
            !initializedMap.get(performanceRange.getIdentifier()).isHighlighted()
        }
        initializedMap.get("uncounted").getCount() == 0

    }
    def "determinePerformanceCategory returned uncounted for student ability outside range"() {
        when:
        def performanceRanges = generatePerformanceRanges()
        def assessmentSession = generateAssessmentSession(assessmentDto)
        assessmentSession.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, 1000.0))

        then:
        def category = classRosterMasteryReportService.determinePerformanceCategory(assessmentSession, performanceRanges)
        category.equals("uncounted")

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "determinePerformanceCategory properly categorizes a valid assessment session"() {
        when:
        def performanceRanges = generatePerformanceRanges()
        def assessmentSession = generateAssessmentSession(assessmentDto)
        assessmentSession.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, -1.0))

        then:
        def category = classRosterMasteryReportService.determinePerformanceCategory(assessmentSession, performanceRanges)
        category.equals("First Range")

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "getLatestAssessmentSessionFromStudentAssessmentSessionMap returns the most recent assessment session"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(50, assessmentDto, true)
        Date now = new Date()
        AssessmentSession assessmentSession = generateAssessmentSession(assessmentDto, assessmentSessions.get(0).getCompetencyMapOrderIds())
        assessmentSession.setCompletionDate(now)
        assessmentSessions.add(assessmentSession)
        def studentSessionMap = [:]
        studentSessionMap.put(generator.randomId(), assessmentSessions)

        then:
        AssessmentSession nowSession = classRosterMasteryReportService.getLatestAssessmentSessionFromStudentAssessmentSessionMap(studentSessionMap)
        nowSession.getCompletionDate().equals(now)

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "filterAssessmentSessions properly filters out assessment sessions that do not match criteria"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(20, assessmentDto, true)
        def assessmentSessionsWithUndesiredSessions = generateUndesiredAssessmentSessions(10,
                assessmentDto,
                assessmentSessions.get(0).getCompetencyMapOrderIds())
        assessmentSessions.addAll(assessmentSessionsWithUndesiredSessions)
        def discipline = assessmentDto.assessmentMetadata?.competencyMapDisciplines?.get(0)

        then:
        List<AssessmentSession> filteredAssessmentSessions =
                classRosterMasteryReportService.filterAssessmentSessionsByCompetencyMapDisciplineAndStudentAbility(assessmentSessions, discipline)
        if (assessmentDto.getAssessmentMetadata() != null) {
            filteredAssessmentSessions.size() == 20
        } else {
            filteredAssessmentSessions.size() == 0
        }

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "filterAssessmentSessions properly filters out assessment sessions when assessment does not contain the desired discipline"() {
        when:
        String originalCompetencyMapDiscipline = null
        if (assessmentDto.getAssessmentMetadata() != null) {
            originalCompetencyMapDiscipline = assessmentDto.assessmentMetadata.competencyMapDisciplines.get(0)
            if (generator.randomBoolean()) {
                assessmentDto.assessmentMetadata.setCompetencyMapDisciplines(Arrays.asList(generator.randomString()))
            } else {
                assessmentDto.assessmentMetadata.setCompetencyMapDisciplines(null)
            }
        }
        def assessmentSessions = generateAssessmentSessionList(20, assessmentDto, true)

        then:
        List<AssessmentSession> filteredAssessmentSessions =
                classRosterMasteryReportService.filterAssessmentSessionsByCompetencyMapDisciplineAndStudentAbility(assessmentSessions, originalCompetencyMapDiscipline)
        filteredAssessmentSessions.size() == 0

        where:
        assessmentDto << [ classRosterMasteryReportService.assessmentReader.create(generator.randomAssessment(3)) ]
    }

    def "calculatedMedianStudentAbility properly calculates median for list with an odd number of elements"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(9, null, false)

        then:
        classRosterMasteryReportService.calculateMedianStudentAbility(assessmentSessions) == 5
    }

    def "calculatedMedianStudentAbility properly calculates median for list with an even number of elements"() {
        when:
        def assessmentSessions = generateAssessmentSessionList(10, null, false)

        then:
        classRosterMasteryReportService.calculateMedianStudentAbility(assessmentSessions) == (double)((5.0+6.0)/2.0)
    }

    List<AssessmentSession> generateUndesiredAssessmentSessions(int numToGenerate,
                                                                AssessmentDto assessmentDto,
                                                                Map<String, String> competencyMapOrderIds) {
        List<AssessmentSession> assessmentSessions = new ArrayList<>()
        for (int i = 0; i < numToGenerate; i++) {
            AssessmentSession badSession = generateAssessmentSession(assessmentDto, competencyMapOrderIds)
            if (generator.randomBoolean()) {
                badSession.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, Double.NaN));
            } else if (generator.randomBoolean()) {
                def randomMap = [:]
                randomMap.put(generator.randomString(), generator.randomString())
                badSession.setCompetencyMapOrderIds(randomMap)
            } else {
                badSession.setContentId(generator.randomString())
            }
            assessmentSessions.add(badSession)
        }
        return assessmentSessions
    }

    List<AssessmentSession> generateAssessmentSessionList(int numToGenerate,
                                                          AssessmentDto assessmentDto,
                                                          boolean generateRandomData) {
        List<AssessmentSession> assessmentSessionList = new ArrayList<>()
        def competencyMapOrderIds = [:]
        competencyMapOrderIds.put(assessmentDto?.assessmentMetadata?.competencyMapDisciplines?.get(0),generator.randomString())
        for (int index = 0; index < numToGenerate; index++) {
            assessmentSessionList.add(generateAssessmentSession(assessmentDto, competencyMapOrderIds, generateRandomData, index))
        }
        return assessmentSessionList
    }

    AssessmentSession generateAssessmentSession(AssessmentDto assessmentDto, Map<String, String> competencyMapOrderIds) {
        return generateAssessmentSession(assessmentDto, competencyMapOrderIds, true, 0)
    }

    AssessmentSession generateAssessmentSession(AssessmentDto assessmentDto) {
        def competencyMapOrderIds = [:]
        if (assessmentDto?.assessmentMetadata?.competencyMapDisciplines?.get(0) != null) {
            competencyMapOrderIds.put(assessmentDto.assessmentMetadata.competencyMapDisciplines.get(0), generator.randomString())
        }
        return generateAssessmentSession(assessmentDto, competencyMapOrderIds)
    }

    AssessmentSession generateAssessmentSession(AssessmentDto assessmentDto,
                                                Map<String, String> competencyMapOrderIds,
                                                boolean generateRandomData,
                                                int assessmentSessionIndex) {
        AssessmentSession assessmentSession = new AssessmentSession()
        assessmentSession.setContentId(assessmentDto?.getId())
        if (generateRandomData) {
            assessmentSession.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, generator.randomDouble(-2, 6)))
        } else {
            assessmentSession.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, assessmentSessionIndex + 1))
        }
        if (assessmentDto?.assessmentMetadata?.competencyMapDisciplines?.get(0) != null) {
            assessmentSession.setCompetencyMapOrderIds()
        }
        assessmentSession.setCompetencyMapOrderIds(competencyMapOrderIds)
        if (generateRandomData) {
            assessmentSession.setCompletionDate(generator.randomDateBeforeNow())
        } else {
            LocalDate localDate = LocalDate.now().minusDays(assessmentSessionIndex)
            assessmentSession.setCompletionDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        }
        return assessmentSession
    }

    def createOutcome(def identifier, def value){
        new Outcome(
                outcomeIdentifier: identifier,
                value: value,
                baseType: AssessmentBaseType.FLOAT
        )
    }

    def generateOrderedCompetencySet() {
        def selectedOrderedCompetencies = generateSelectedOrderedCompetencies()
        def orderedCompetencySet = new OrderedCompetencySet(selectedOrderedCompetencies, null)
        return orderedCompetencySet
    }
}
