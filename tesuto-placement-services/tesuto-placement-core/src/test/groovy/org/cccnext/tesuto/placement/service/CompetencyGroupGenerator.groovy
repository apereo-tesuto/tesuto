package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.CompetencyGroup
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.DisciplineSequence
import org.cccnext.tesuto.placement.view.*
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto
import org.cccnext.tesuto.content.dto.competency.CompetencyDto
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto;
import org.cccnext.tesuto.placement.model.CompetencyGroupMapping


class CompetencyGroupGenerator extends SubjectAreaGenerator {

	CompetencyGroupService competencyGroupService

	private Course aCourse
	private CompetencyGroup aCompetencyGroup
	private Integer competencyGroupIdIndex = 1;

	Integer competencyGroupIdIndex=1;

	String AND = "\"AND\","
	String OR = "\"OR\","

	public CompetencyGroupGenerator(CompetencyGroupService competencyGroupService, SubjectAreaService service) {
		this.competencyGroupService = competencyGroupService
		this.service = service
	}

	Course getACourse() {
		if(aCourse == null) {
			def DisciplineSequence sequence = getASequence()
			aCourse = service.createCourse(sequence ,randomCourse())
		}
		return aCourse
	}

	CompetencyGroup getACompetencyGroup() {
		if(this.aCompetencyGroup == null) {
			Course course = getACourse();
			Integer groupId = competencyGroupService.create(randomString(100), randomCompetencyGroupViewDto(course.getCourseId()))
			aCompetencyGroup = competencyGroupService.get(groupId);
		}
		return aCompetencyGroup
	}

	CompetencyGroupViewDto randomCompetencyGroupViewDto(int courseId) {
		int competencyGroupId = randomInt(1,100);
		Set<String> competencyGroupMappings = randomCompetencyGroupMappingViewDtos(competencyGroupId)
		new CompetencyGroupViewDto(
				competencyGroupId: competencyGroupId,
				name: randomString(3,99),
				courseId: courseId,
				percent: randomInt(5,100),
				competencyIds: competencyGroupMappings
		)
	}

	Set<String> randomCompetencyGroupMappingViewDtos(int competencyGroupId) {
		return (0..randomInt(0, 5)).collect {
			randomString(5,79)
		}.toSet()
	}

	CompetencyGroupViewDto copyCompetencyGroupViewDto(CompetencyGroupViewDto original) {
		CompetencyGroupViewDto copy = new CompetencyGroupViewDto();
		copy.competencyGroupId = original.competencyGroupId;
		copy.courseId = original.courseId;
		copy.percent = original.percent;
		copy.name = original.name;
		copy.competencyIds = original.competencyIds.collect()
		return copy
	}


List<Course> makeRandomCourses(number){
		List<Course> courses = []
		Integer count = 0
		while (count++ < number) {
				courses << randomCourse()
		}
		return courses
	}

List<CourseViewDto> makeRandomCourseDtos(number){
		List<CourseViewDto> courses = []
		Integer count = 0
		while (count++ < number) {
				courses << randomCourseDto()
		}
		return courses
	}

	Map<String, CompetencyDifficultyDto> makeCompetencyMap(List<CompetencyDto> competencies, difficultyMin, difficultyMax) {
		Map<String, CompetencyDifficultyDto> competencyMap = new HashMap<>();
		for(CompetencyDto competency: competencies) {
			CompetencyDifficultyDto difficulty = new CompetencyDifficultyDto(competency, randomDouble(difficultyMin,difficultyMax));
			competencyMap.put(competency.identifier,difficulty)
		}
		return competencyMap;
	}

	Integer makeLogicString(competencyGroups, index, StringBuilder logic){
		while (index < competencyGroups.size()) {
			if(randomBoolean()) {
				CompetencyGroupViewDto comp = competencyGroups[index++]
				logic.append(comp.competencyGroupId + ",")
				if(randomBoolean()) {
					logic.append(AND)
				} else {
					logic.append(OR)
				}
			} else {
				logic.append("[ ")
				index  = makeLogicString(competencyGroups, index, logic)
			}

		}
		logic.append(" ]")
		return index
	}

	List<CompetencyDto> makeCompetencies(Set<CompetencyGroup> competencyGroups) {
		List<CompetencyDto> competencies = []
		for(CompetencyGroup cg:competencyGroups) {
			for(CompetencyGroupMapping cpId:cg.competencyGroupMappings) {
				def CompetencyDto dif = new CompetencyDto()
				dif.identifier = cpId.competencyId
				competencies.add(dif);
			}
		}
		return competencies
	}

	List<CompetencyDto> makeCompetenciesDtos(Set<CompetencyGroupViewDto> competencyGroups) {
		List<CompetencyDto> competencies = []
		for(CompetencyGroupViewDto cg:competencyGroups) {
			for(String cpId:cg.competencyIds) {
				def CompetencyDto dif = new CompetencyDto()
				dif.identifier = cpId
				competencies.add(dif);
			}
		}
		return competencies
	}

	CompetencyGroup randomCompetencyGroup(){
	CompetencyGroup cg = new CompetencyGroup(
			probabilitySuccess:50,
			competencyGroupId:competencyGroupIdIndex++,
			competencyGroupMappings:(0..randomInt(1,12)).collect({randomCompetencyGroupMaping()}).toSet()
		)
		return cg
	}

	CompetencyGroupMapping randomCompetencyGroupMaping() {
		CompetencyGroupMapping cgm = new CompetencyGroupMapping(
		competencyId:randomString(),
		)
		return cgm
	}
}