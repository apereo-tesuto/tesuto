package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto
import org.cccnext.tesuto.content.dto.competency.CompetencyDto
import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies
import org.cccnext.tesuto.admin.dto.UserAccountDto
import org.cccnext.tesuto.content.service.AssessmentItemService
import org.cccnext.tesuto.content.service.CategoryService
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.cccnext.tesuto.content.service.scoring.ExpressionEvaluationService

import org.cccnext.tesuto.delivery.model.internal.TaskSet

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom

//There are two spec classes.  The @Shared variables, setup, and cleanup are defined here.
//Note that @Shared does not mean "Shared between specs", but "Shared between methods within a spec".
public class DeliverySpecBase extends Specification {

    @Shared AssessmentGenerator generator
    @Shared DeliveryService service
    @Shared DeliveryServiceAdapter adapter
    @Shared ExpressionEvaluationService expressionEvaluationService
    @Shared AssessmentItemService itemService
    @Shared sessionIds = []
    @Shared UserAccountDto user
    @Shared CategoryService categoryService;
    @Shared SelectionService selectionService;
    @Shared TaskSetService taskSetService

    def setupSpec() {
        generator = new AssessmentGenerator()
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        generator.assessmentService = context.getBean("assessmentReader")
        generator.itemService = context.getBean("assessmentItemReader")
        generator.categoryService = context.getBean("categoryService");
        service = context.getBean("deliveryService")
        adapter = context.getBean("deliveryServiceAdapter")
        expressionEvaluationService = context.getBean("expressionEvaluationService")
        categoryService = context.getBean("categoryService")
        itemService = context.getBean("assessmentItemReader")
        selectionService = context.getBean("selectionService")
        taskSetService = context.getBean("taskSetService")
        //put random assessments and items into the services
        (1..5).each { generator.randomAssessment(2) }
        def user = new UserAccountDto()
        user.setUserAccountId("TestUser123")
        user.setDisplayName("Test User")
    }

    def setSessionAllowSkipping(String sessionId, boolean allow) {
        //All of the items need to allow skipping
        def session = service.find(sessionId)
        session.taskSetIds.each { taskSetId ->
            TaskSet taskSet = taskSetService.getTaskSet(taskSetId, session)
            taskSet.getTasks().each { task ->
                task.getItemSessions().each {
                    it.allowSkipping = allow
                }

            }
            session.saveTaskSet(taskSet)
        }
        service.save(session)
        return session
    }


    def setSessionRequireValidation(String sessionId, boolean required) {
        //All of the items need to allow skipping
        def session = service.find(sessionId)
        session.taskSetIds.each { taskSetId ->
            TaskSet taskSet = taskSetService.getTaskSet(taskSetId, session)
            taskSet.getTasks().each { task ->
                task.getItemSessions().each {
                    it.validateResponses = required
                }
            }
            session.saveTaskSet(taskSet)
        }
        service.save(session)
    }


    def cleanupSpec() {
        sessionIds.each { sessionId ->
            def session = service.find(sessionId)
            session.taskSetIds.each { taskSetService.taskSetDao.delete(it) }
            service.remove(sessionId)
        }
    }

    SelectedOrderedCompetencies generateSelectedOrderedCompetencies() {
        List<CompetencyDifficultyDto> masteredList = new ArrayList<>()
        CompetencyDifficultyDto mastered = new CompetencyDifficultyDto()
        CompetencyDto competencyDto = new CompetencyDto()
        competencyDto.setDiscipline("MATH")
        competencyDto.setIdentifier("identifier1")
        competencyDto.setDescription("This competency was mastered")
        competencyDto.setStudentDescription("This is a student description for a mastered competency")
        mastered.setCompetency(competencyDto)
        masteredList.add(mastered)
        List<CompetencyDifficultyDto> tolearnList = new ArrayList<>()
        CompetencyDifficultyDto tolearn = new CompetencyDifficultyDto()
        competencyDto = new CompetencyDto()
        competencyDto.setDiscipline("MATH")
        competencyDto.setIdentifier("identifier2")
        competencyDto.setDescription("This competency needs to be learned")
        competencyDto.setStudentDescription("This is a student description for a to-be-learned competency")
        tolearn.setCompetency(competencyDto)
        tolearnList.add(tolearn)
        SelectedOrderedCompetencies selectedOrderedCompetencies = new SelectedOrderedCompetencies(masteredList, tolearnList)
        selectedOrderedCompetencies.setStudentAbility(3.0d)
        return selectedOrderedCompetencies
    }

    List<CompetencyCategoryMetadataDto> generateCompetencyCategoryMetadataList() {
        return generateCompetencyCategoryMetadataList(true, true)
    }

    List<CompetencyCategoryMetadataDto> generateCompetencyCategoryMetadataList(boolean makeRandomRanges, boolean useRandomCaseDisciplines) {
        List<CompetencyCategoryMetadataDto> competencyCategoryMetadataDtoList = new ArrayList<>()

        def mathDiscipline = useRandomCaseDisciplines ? (generator.randomBoolean() ? "MATH" : "math") : "MATH"
        def englishDiscipline = useRandomCaseDisciplines ? (generator.randomBoolean() ? "ENGLISH" : "english") : "ENGLISH"

        CompetencyCategoryMetadataDto competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("FirstId")
        competencyCategoryMetadataDto.setTitle("First Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(mathDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("SecondId")
        competencyCategoryMetadataDto.setTitle("Second Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(mathDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("ThirdId")
        competencyCategoryMetadataDto.setTitle("Third Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(englishDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("FourthId")
        competencyCategoryMetadataDto.setTitle("Fourth Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(mathDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("FifthId")
        competencyCategoryMetadataDto.setTitle("Fifth Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(mathDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        competencyCategoryMetadataDto = new CompetencyCategoryMetadataDto()
        competencyCategoryMetadataDto.setCompetencyRefId("SixthId")
        competencyCategoryMetadataDto.setTitle("Sixth Title")
        competencyCategoryMetadataDto.setCompetencyMapDiscipline(mathDiscipline)
        competencyCategoryMetadataDto.setPerformanceRanges(generatePerformanceRanges(makeRandomRanges))
        competencyCategoryMetadataDtoList.add(competencyCategoryMetadataDto)

        return competencyCategoryMetadataDtoList
    }

    List<PerformanceRangeMetadataDto> generatePerformanceRanges() {
        return generatePerformanceRanges(true)
    }

    List<PerformanceRangeMetadataDto> generatePerformanceRanges(boolean makeRandomRanges) {
        List<PerformanceRangeMetadataDto> performanceRangeMetadataDtoList = new ArrayList<>()
        double min = 0
        double max = 0

        PerformanceRangeMetadataDto performanceRangeMetadataDto = new PerformanceRangeMetadataDto()
        performanceRangeMetadataDto.setIdentifier("First Range")
        if (makeRandomRanges) {
            min = -10 + ThreadLocalRandom.current().nextDouble(0, 5)
            max = ThreadLocalRandom.current().nextDouble(0, 5)
        } else {
            min = 0
            max = 3
        }
        performanceRangeMetadataDto.setMin(min)
        performanceRangeMetadataDto.setMax(max)
        performanceRangeMetadataDto.setPosition(1)
        performanceRangeMetadataDtoList.add(performanceRangeMetadataDto)

        performanceRangeMetadataDto = new PerformanceRangeMetadataDto()
        performanceRangeMetadataDto.setIdentifier("Second Range")
        if (makeRandomRanges) {
            performanceRangeMetadataDto.setMin(max)
            max = max + 2 + ThreadLocalRandom.current().nextDouble(0, 5)
            performanceRangeMetadataDto.setMax(max)
        } else {
            performanceRangeMetadataDto.setMin(3)
            performanceRangeMetadataDto.setMax(6)
        }
        performanceRangeMetadataDto.setPosition(2)
        performanceRangeMetadataDtoList.add(performanceRangeMetadataDto)

        performanceRangeMetadataDto = new PerformanceRangeMetadataDto()
        performanceRangeMetadataDto.setIdentifier("Third Range")
        if (makeRandomRanges) {
            performanceRangeMetadataDto.setMin(max)
            max = max + 2 + ThreadLocalRandom.current().nextDouble(0, 5)
            performanceRangeMetadataDto.setMax(max)
        } else {
            performanceRangeMetadataDto.setMin(6)
            performanceRangeMetadataDto.setMax(9)
        }
        performanceRangeMetadataDto.setPosition(3)
        performanceRangeMetadataDtoList.add(performanceRangeMetadataDto)

        performanceRangeMetadataDto = new PerformanceRangeMetadataDto()
        performanceRangeMetadataDto.setIdentifier("Fourth Range")
        if (makeRandomRanges) {
            performanceRangeMetadataDto.setMin(max)
            max = max + 2 + ThreadLocalRandom.current().nextDouble(0, 5)
            performanceRangeMetadataDto.setMax(max)
        } else {
            performanceRangeMetadataDto.setMin(9)
            performanceRangeMetadataDto.setMax(12)
        }
        performanceRangeMetadataDto.setPosition(4)
        performanceRangeMetadataDtoList.add(performanceRangeMetadataDto)

        return performanceRangeMetadataDtoList
    }
}
