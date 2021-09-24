package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult

import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto
import org.cccnext.tesuto.placement.model.*
import org.cccnext.tesuto.placement.view.*
import org.cccnext.tesuto.util.test.Generator
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PreDestroy
import org.springframework.stereotype.Service

import spock.lang.Shared

@Service
class PlacementGenerator extends Generator {

	SubjectAreaGenerator subjectAreaGenerator = new SubjectAreaGenerator(null)

	@Autowired SubjectAreaServiceAdapter adapter
	@Autowired SubjectAreaService subjectAreaService
	@Autowired CompetencyAttributesService attributesService
	@Autowired CompetencyAttributesAssembler competencyAttributesAssembler;
	@Autowired PlacementAuditService placementAuditService

    char cb21 = 'D'
    char cb21Level = 4
    int courseGroup = randomInt(1,5)
	
	def disciplineIdsToDelete

	public PlacementGenerator() {
	}
	
	public void setDisciplineIdsToDelete(disciplineIdsToDelete) {
		this.disciplineIdsToDelete = disciplineIdsToDelete
	}


	def randomDiscipline() {
		subjectAreaGenerator.randomDiscipline()
	}

	public Set<PlacementViewDto> randomPlacementViewDtoList(int listSize) {
		return (0..listSize).collect {
			randomPlacementViewDto()
		}.toSet()
	}

	PlacementViewDto randomPlacementViewDto() {
		def versionedSubjectArea = randomVersionedSubjectAreaView(getDiscipline(0))

		def placementViewDto = new PlacementViewDto(
			id: rand.nextInt(),
			cccid:  randomString(100),
			cb21Code: cb21,
            cb21: new CB21ViewDto(cb21Code: cb21, level: cb21Level),
			courseGroup: courseGroup,
            courses: [],
			collegeId: randomString(),
			disciplineId: versionedSubjectArea.disciplineId,
			subjectAreaName: versionedSubjectArea.disciplineId,
			subjectAreaVersion: versionedSubjectArea.version,
			versionedSubjectAreaViewDto: versionedSubjectArea,
			isAssigned: randomBoolean(),
			assignedDate: randomDate(),
			createRuleSetId: randomString(100),
			createRuleSetTitle: randomString(100),
			assignedRuleSetId: randomString(100),
			assignedRuleSetTitle: randomString(100),
			placementComponents:  randomPlacementComponentViewDtos(5, versionedSubjectArea.disciplineId) ,
			createdOn: randomDate()
		)
        placementViewDto.placementComponentIds = placementViewDto.placementComponents.id
        return placementViewDto
	}

	Placement randomPlacement(int disciplineId) {
		def discipline = getDiscipline(disciplineId)
		def versionedSubjectArea = randomVersionedSubjectArea(discipline)

		def placement = new Placement(
			id: rand.nextInt(),
			cccid:  randomString(100),
			cb21Code: cb21,
			courseGroup: courseGroup,
			collegeId: randomString(),
			disciplineId: versionedSubjectArea.disciplineId,
			subjectAreaName: versionedSubjectArea.disciplineId,
			subjectAreaVersion: versionedSubjectArea.version,
			isAssigned: randomBoolean(),
			assignedDate: randomDate(),
			createRuleSetId: randomString(100),
			assignedRuleSetId: randomString(100),
			placementComponents:  randomPlacementComponents(5, versionedSubjectArea.disciplineId) ,
			createdOn: randomDate()
		)
	}

	Placement randomAssignedPlacement(int disciplineId) {
		def placement = randomPlacement(disciplineId)
		placement.setAssigned(true);
		return placement
	}

	PlacementComponentViewDto randomPlacementComponentViewDto(String componentType, disciplineId) {
		def entityClass = componentType == "tesuto" ? "org.cccnext.tesuto.placement.model.TesutoPlacementComponent"
		: "org.cccnext.tesuto.placement.model.MmapPlacementComponent"
		
		def discipline = getDiscipline(disciplineId)
		def versionedSubjectArea = randomVersionedSubjectAreaView(discipline)

		def placementComponentViewDto = new PlacementComponentViewDto(
			id: rand.nextInt(),
			cccid: randomString(100),
			cb21Code: cb21,
			courseGroup: courseGroup,
			collegeId: randomString(),
            trackingId: randomString(19),
			triggerData: randomString(),
			subjectAreaId: versionedSubjectArea.disciplineId,
			subjectAreaVersion: versionedSubjectArea.version,
            versionedSubjectAreaViewDto: versionedSubjectArea,
			createdOn: randomDate(),
			entityTargetClass: entityClass
		)
		if (componentType == "tesuto") {
			placementComponentViewDto.assessmentSessionId = randomString(100)
			placementComponentViewDto.assessmentTitle = randomString(256)
			placementComponentViewDto.assessmentDate = randomDate()
			placementComponentViewDto.studentAbility = randomDouble(0.0,5.0)
		} else {
			placementComponentViewDto.dataSource = randomString(100)
			placementComponentViewDto.dataSourceDate = randomDate()
			placementComponentViewDto.dataSourceType = MmapDataSourceType.SELF_REPORTED
			placementComponentViewDto.mmapCourseCategories = randomList(5) { randomString()}
			placementComponentViewDto.mmapVariableSetId = randomString(19)
			placementComponentViewDto.mmapRuleId = randomString(19)
			placementComponentViewDto.mmapRuleSetId = randomString(19)
			placementComponentViewDto.mmapRuleSetRowId = randomString(19)
			placementComponentViewDto.mmapRowNumber = randomRangeInt(0,100)
			placementComponentViewDto.mmapCourseCategories = randomList(5) { randomString() }
			placementComponentViewDto.dataSourceDate = randomDate()
			placementComponentViewDto.dataSourceType = MmapDataSourceType.SELF_REPORTED
			placementComponentViewDto.standalonePlacement = randomBoolean()
		}
		return placementComponentViewDto
	}


	public PlacementComponent randomPlacementComponent(String componentType, int disciplineId) {
		try {
			def discipline = getDiscipline(disciplineId)
			def versionedSubjectArea = randomVersionedSubjectAreaView(discipline)
			def placementComponent
			if (componentType == "tesuto") {
				placementComponent = new TesutoPlacementComponent(
						assessmentSessionId: randomString(100),
						assessmentTitle: randomString(256),
						assessmentDate: randomDate(),
						studentAbility: randomDouble(0.0, 5.0)
				)
			} else {
				placementComponent = new MmapPlacementComponent(
						dataSource: randomString(100),
						dataSourceDate: randomDate(),
						dataSourceType: MmapDataSourceType.SELF_REPORTED,
						standalonePlacement: randomBoolean(),
						mmapVariableSetId: randomString(19),
						mmapRuleId: randomString(19),
						mmapRuleSetId: randomString(19),
						mmapRuleSetRowId: randomString(19),
						mmapRowNumber: randomRangeInt(0, 100),
						mmapCourseCategories: randomList(5) { randomString() }
				)
			}

			placementComponent.id = rand.nextInt()
			placementComponent.cccid = randomString(100)
			placementComponent.trackingId = randomString(19)
			placementComponent.cb21Code = cb21
			placementComponent.courseGroup = courseGroup
			placementComponent.collegeId = randomString()
			placementComponent.triggerData = randomString()
			placementComponent.subjectAreaId = versionedSubjectArea.disciplineId
			placementComponent.subjectAreaVersion = versionedSubjectArea.version
			placementComponent.createdOn = randomDate()
			placementComponent.elaIndicator = randomString(3)

			return placementComponent
		} catch (Exception e) {
			e.printStackTrace()
			throw e
		}

	}

	public PlacementActionResult randomActionResult() {
		def discipline = getDiscipline(0)
		def versionedSubjectArea = randomVersionedSubjectArea(discipline)

		new PlacementActionResult(
				cb21Code: cb21,
				courseGroup: courseGroup,
				subjectAreaId: versionedSubjectArea.disciplineId,
				subjectAreaVersion: versionedSubjectArea.version,
				createdOn: randomDate(),
				placementComponents: randomPlacementComponents(5, versionedSubjectArea.disciplineId) ,
				placementId: randomString(100)
		)
	}

    private Set<PlacementComponentViewDto> randomPlacementComponentViewDtos(int number, disciplineId) {
       def  components = []
       (1..number).each{components << randomPlacementComponentViewDto("mmap", disciplineId)}
       return  components
    }

    private Set<PlacementComponent> randomPlacementComponents(int number, int disciplineId) {
        def  components = []
        (1..number).each{components << randomPlacementComponent("mmap", disciplineId)}
        return  components
     }
	 
	 private getDiscipline(int disciplineId) {
		 def discipline
		 if(disciplineId == 0) {
			 
			 discipline = subjectAreaService.createDiscipline(randomDiscipline())
			 disciplineIdsToDelete << discipline.disciplineId
		 } else {
			 discipline = subjectAreaService.getDiscipline(disciplineId)
		 }
		 return discipline;
	 }

	Set<CourseViewDto> makeRandomCourseDtos(number){
		Set<CourseViewDto> courses = []
		Integer count = 0
		while (count++ < number) {
				courses << subjectAreaGenerator.randomCourseDto()
		}
		return courses
	}

	VersionedSubjectAreaViewDto randomVersionedSubjectArea(Discipline discipline) {
		def subject = adapter.createVersionedSubjectArea(discipline)
		subjectAreaService.getOrCreateSequence(discipline.disciplineId, cb21, courseGroup)
        adapter.createVersionedSubjectArea(discipline)
	}

	VersionedSubjectAreaViewDto randomVersionedSubjectAreaView(Discipline discipline) {
		randomVersionedSubjectArea(discipline)
	}
}
