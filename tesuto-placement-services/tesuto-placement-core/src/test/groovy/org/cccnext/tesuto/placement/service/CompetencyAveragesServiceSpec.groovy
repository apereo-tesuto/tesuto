import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto
import org.cccnext.tesuto.placement.dto.CollegeCompetencyAverageViewDto
import org.cccnext.tesuto.placement.model.CompetencyGroup
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.repository.jpa.CompetencyGroupRepository
import org.cccnext.tesuto.placement.service.CompetencyAveragesService
import org.cccnext.tesuto.placement.service.CompetencyGroupGenerator
import org.cccnext.tesuto.placement.service.CompetencyGroupService
import org.cccnext.tesuto.placement.service.PlacementAuditService
import org.cccnext.tesuto.placement.service.SubjectAreaGenerator
import org.cccnext.tesuto.placement.service.SubjectAreaService
import org.cccnext.tesuto.placement.stub.CompetencyMapOrderServiceStub
import org.cccnext.tesuto.admin.service.CollegeService
import org.cccnext.tesuto.admin.service.CollegeReader
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
class CompetencyAveragesServiceSpec extends Specification {

    @Shared ApplicationContext context
    @Shared SubjectAreaService subjectAreaService
    @Shared SubjectAreaGenerator generator

    @Shared CompetencyAveragesService competencyAveragesService
    @Shared CollegeReader collegeService

    @Shared CompetencyGroupGenerator competencyGenerator
    @Shared CompetencyGroupService competencyGroupService
    @Shared CompetencyGroupRepository competencyGroupRepository

    @Shared CompetencyMapOrderServiceStub competencyMapOrderReader;
    @Shared CollegeReader collegeReader;

    @Shared PlacementAuditService placementAuditService
    @Shared courseIdsToDeleted = []
    @Shared courseHistoryIdsToDeleted = []
    @Shared disciplineIdsToDeleted = []
    @Shared competencyGroupIdsToBeDeleted = []

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/test-application-context.xml")
        subjectAreaService = context.getBean("subjectAreaService")
        generator = new SubjectAreaGenerator(subjectAreaService)
        competencyAveragesService = context.getBean("competencyAveragesService")
        collegeService = context.getBean("collegeService")
        competencyGroupService = context.getBean("competencyGroupService")
        competencyGenerator = new CompetencyGroupGenerator(competencyGroupService, subjectAreaService)
        competencyGroupRepository = context.getBean("competencyGroupRepository")
        competencyMapOrderReader = context.getBean("competencyMapOrderService")
        competencyAveragesService.competencyMapOrderReader = competencyMapOrderReader
        collegeReader = collegeService
        competencyAveragesService.collegeRestClient = collegeReader
        placementAuditService = context.getBean("placementAuditService")
    }

    def "calculateAveragesForCourseAndSubjectArea properly calculates averages for simple input"() {
        when:
        def course = generator.randomCourse()
        def subjectArea = generator.getADiscipline()
        def disciplineSequence = generator.getASequence()
        Course created = subjectAreaService.createCourse(disciplineSequence, course)

        Set<CompetencyGroup> competencyGroups = new HashSet<>()
        def competencyGroupId = competencyGroupService.create(competencyGenerator.randomCompetencyGroupViewDto(created.getCourseId()))
        competencyGroupIdsToBeDeleted += competencyGroupId
        competencyGroups.add(competencyGroupService.repository.findOne(competencyGroupId))

        courseIdsToDeleted += created.courseId
        def college = collegeService.getCollegeByMisCode("12345")
        competencyMapOrderReader.generateCompetencyDifficultyDtos(competencyGroups)
        competencyMapOrderReader.putCompetenciesIntoMap(subjectArea.getCompetencyMapDiscipline(), competencyMapOrderReader.getOrderedCompetencies("test",0));
        then:
        CollegeCompetencyAverageViewDto averages = competencyAveragesService.calculateCollegeCompetencyAverages(college.getCccId(), subjectArea.getDisciplineId(), created.getCourseId())
        def total = 0
        def count = 0
        for (CompetencyDifficultyDto difficultyDto : competencyMapOrderReader.competencyDifficultyDtos) {
            println(difficultyDto.getDifficulty())
            total += difficultyDto.getDifficulty()
            count++
        }
        (total/(double)count) == averages.getCourseSubjectAreaCompetencyGroupAverageViewDtoSet().iterator().next().competencyGroupAverageViewDtoSet.iterator().next().competencyGroupAverage
    }

    def cleanupSpec() {
        competencyGroupIdsToBeDeleted.each { competencyGroupService.delete(it) }
        competencyGroupIdsToBeDeleted.each {
            placementAuditService.deleteAuditRows("history_competency_group", "competency_group_id", it)
        }
        courseIdsToDeleted.each { subjectAreaService.deleteCourse( it) }
        courseIdsToDeleted.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
        courseHistoryIdsToDeleted.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
        def aDiscipline = generator.getADiscipline()
        if(aDiscipline != null) {
            disciplineIdsToDeleted << aDiscipline.disciplineId
        }
        disciplineIdsToDeleted.each { subjectAreaService.deleteDiscipline( it) }
        disciplineIdsToDeleted.each { placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", it) }
        context.close()
    }
}
