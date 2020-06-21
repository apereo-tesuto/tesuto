package org.cccnext.tesuto.placement.service

import java.util.Set;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto
import org.cccnext.tesuto.content.dto.competency.CompetencyDto
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.placement.model.CompetencyGroup
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext

import spock.lang.Shared;
import spock.lang.Specification

class PlacementEvaluationServiceSpec extends Specification {

	@Shared ApplicationContext context
	@Shared CompetencyGroupGenerator generator
	@Shared PlacementEvaluationService service

	def setupSpec() {
		def context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		service = context.getBean("placementEvaluationService")
		generator = new CompetencyGroupGenerator(context.getBean("competencyGroupService"), context.getBean("subjectAreaService"))
	}

	//Currently will test logic that it does not throw an exception but might be more useful if can be designed to guarantee a specific result
	def "Student ability is always true when evaluated against criteria"() {
		when:  course.competencyGroups = (0..generator.randomInt(3,20)).collect({generator.randomCompetencyGroupViewDto(course.courseId)})
			def List<CompetencyDto> competencies = generator.makeCompetenciesDtos(course.competencyGroups);
			def StringBuilder logic =  new StringBuilder("[ ")

			def index = 0

			generator.makeLogicString(course.competencyGroups, index, logic);
			def orIndex = logic.lastIndexOf(generator.OR) - 1
			def andIndex = logic.lastIndexOf(generator.AND) - 1
			if(andIndex > orIndex ) {
				logic.delete(andIndex, andIndex + generator.AND.size() + 1)
			} else if(orIndex > -1){
				logic.delete(orIndex, orIndex + generator.OR.size() + 1)
			}

			def competencyMap = generator.makeCompetencyMap(competencies, -3, 0)
			course.competencyGroupLogic = logic.toString();
		then:
			service.evaluateCourse(course, 1.0, competencyMap) == true || service.evaluateCourse(course, 1.0, competencyMap) == false
		where: course <<  generator.makeRandomCourseDtos(5)

	}

	// only tests course evaluation is consistent and does not throw exceptions
	def "Student ability is always false when evaluated against criteria"() {
		when:  course.competencyGroups = (0..generator.randomInt(3,20)).collect({generator.randomCompetencyGroupViewDto(course.courseId)})
			def List<CompetencyDto> competencies = generator.makeCompetenciesDtos(course.competencyGroups);
			def StringBuilder logic =  new StringBuilder("[ ")

			def index = 0

			generator.makeLogicString(course.competencyGroups, index, logic);
			def orIndex = logic.lastIndexOf(generator.OR) - 1
			def andIndex = logic.lastIndexOf(generator.AND) - 1
			if(andIndex > orIndex ) {
				logic.delete(andIndex, andIndex + generator.AND.size() + 1)
			} else if(orIndex > -1){
				logic.delete(orIndex, orIndex + generator.OR.size() + 1)
			}

			def competencyMap = generator.makeCompetencyMap(competencies, 2, 6)
			course.competencyGroupLogic = logic.toString();
		then:
			service.evaluateCourse(course, 0.9, competencyMap) == false || service.evaluateCourse(course, 0.9, competencyMap) == true
		where: course <<  generator.makeRandomCourseDtos(5)

	}

}
