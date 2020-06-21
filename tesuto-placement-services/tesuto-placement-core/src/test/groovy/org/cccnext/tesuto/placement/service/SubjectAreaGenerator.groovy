package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.*
import org.cccnext.tesuto.placement.view.CourseViewDto
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto
import org.cccnext.tesuto.placement.view.DisciplineViewDto
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto
import org.cccnext.tesuto.util.test.Generator

class SubjectAreaGenerator extends Generator {

	SubjectAreaService service
	private Discipline aDiscipline
	private DisciplineSequence aSequence
    private  List<CB21> cb21s
    private  List<String> mmapEquivalentCode

	public SubjectAreaGenerator() {
        setUp()
	}

	public SubjectAreaGenerator(SubjectAreaService service) {
		this.service = service
        setUp()
        
	}

    void setUp() {
        mmapEquivalentCode = (1..10).collect({randomString(10)})
        if(service != null)
             cb21s = service.getAllCb21().asList()
    }
	String randomBoundedString() {
		randomString(1,120)
	}

	Discipline getADiscipline() {
		if (aDiscipline == null) {
			
			this.aDiscipline = service.createDiscipline(randomDiscipline())
		}
		return aDiscipline
	}

	SubjectAreaService subjectAreaService() {
		return service;
	}


	Discipline randomDiscipline() {
		def competencyMD = randomMember(["ENGLISH", "ESL", "MATH"]);
		new Discipline(
				disciplineId: rand.nextInt(),
				collegeId: randomString(1,100),
				title: randomBoundedString(),
				description: randomString(),
				competencyMapVersion: randomInt(1, 1000),
				competencyMapDiscipline: competencyMD,
				sisCode: randomString(1,100),
				usePrereqPlacementMethod:  randomBoolean(),
				noPlacementMessage: randomString(),
				competencyAttributes: randomCompetencyAttributes(competencyMD)
		)
	}

	DisciplineSequence getASequence() {
		int disciplineId = getADiscipline().disciplineId
		if (aSequence == null) {
			aSequence = aSequence(disciplineId)
			service.upsert(aSequence)
		} 
		service.getSequencesByDisciplineId(disciplineId).toList()[0] //this should populate a Sequence object
	}

	DisciplineSequence addCB21(DisciplineSequence sequence) {
		sequence.cb21 = new CB21(
				cb21Code: sequence.cb21Code,
				levelsBelowTransfer: randomInt(1,10)
		)
		sequence
	}

	DisciplineSequence aSequence(disciplineId) {
		randomDisciplineSequence(aDiscipline)
	}

	DisciplineSequence randomDisciplineSequence(Discipline discipline) {
		new DisciplineSequence(
				disciplineId: discipline.disciplineId,
				cb21Code: 'A',
				explanation: randomString(),
				mappingLevel: randomString(1,50)
		)
	}

	DisciplineSequence randomDisciplineSequence(Discipline discipline, CB21 cb21) {
		new DisciplineSequence(
				disciplineId: discipline.disciplineId,
				cb21Code: cb21.getCb21Code(),
				explanation: randomString(),
				mappingLevel: randomString(1,50)
		)
	}


	DisciplineSequenceCourse randomDisciplineSequenceCourse(Course course) {
		new DisciplineSequenceCourse(
				courseId: course.courseId,
				cb21Code: 'X',
				disciplineId: rand.nextInt()
		)
	}

	Course randomCourse() {
		new Course(
				name: randomString(),
				cid: randomString(10),
				subject: randomString(1, 64),
				number: randomString(1, 32),
				competencyGroupLogic: randomString(1000),
				sisTestCode: randomString(1,100),
				description: randomString()
      		)
	}

	Set<CourseViewDto> randomCourses() {
		Set<CourseViewDto> randomCourses = new HashSet<CourseViewDto>()
		def n = randomInt(3,10)
		n.times {
			randomCourses.add(randomCourseDto())
		}
		return randomCourses
	}

	CourseViewDto randomCourseDto() {
		new CourseViewDto(
				courseId: rand.nextInt(),
				name: randomString(),
				cid: randomString(10), 
				subject: randomString(1, 64),
				number: randomString(1, 32),
				competencyGroupLogic: randomString(1000),
				sisTestCode: randomString(1,100),
				description: randomString(),
                courseGroup:randomInt(1,15)  ,
                mmapEquivalentCode: randomMmapEquivalentCode()
		)
	}

	DisciplineViewDto randomDisciplineViewUpdate(Discipline discipline) {
		new DisciplineViewDto(
			disciplineId: discipline.disciplineId,
			collegeId: discipline.collegeId,
			competencyMapDiscipline: discipline.competencyMapDiscipline,
			competencyMapVersion: discipline.competencyMapVersion,
			usePrereqPlacementMethod:  discipline.usePrereqPlacementMethod,
			title: randomBoundedString(),
			description: randomString(),
			noPlacementMessage: discipline.noPlacementMessage,
			sisCode: randomString(1,100))

	}

	DisciplineViewDto randomDisciplineViewDto(String collegeId = randomString(1,100)) {
		new DisciplineViewDto(
				disciplineId: rand.nextInt(),
				collegeId: collegeId,
				title: randomBoundedString(),
				description: randomString(),
				competencyMapDiscipline: randomString(),
				competencyMapVersion: rand.nextInt(),
				sisCode: randomString(1,100),
				noPlacementMessage: randomString(1, 500),
				usePrereqPlacementMethod:  randomBoolean(),
				competencyAttributes: randomCompetencyAttributesView()
		)
	}

    DisciplineSequenceViewDto randomDisciplineSequenceViewDto(DisciplineViewDto discipline) {
        randomDisciplineSequenceViewDto(discipline.disciplineId)
    }
	DisciplineSequenceViewDto randomDisciplineSequenceViewDto(Integer disciplineId) {
        CB21 cb21 = getRandomCb21();
		def sequence = new DisciplineSequenceViewDto(
				disciplineId: disciplineId,
				cb21Code: cb21.cb21Code, //not all *that* random :)
				explanation: randomString(),
				level: cb21.levelsBelowTransfer,
				showStudent: randomBoolean(),
                courseGroup:randomInt(1,15)
		)
		sequence.courses = (0..randomInt(0, 5)).collect {
			randomCourseViewDto(sequence)
		}.toSet()
		sequence
	}



	CourseViewDto randomCourseViewDto(DisciplineSequenceViewDto sequence) {
		new CourseViewDto(
				courseId: rand.nextInt(),
				name: randomString(),
				cid: randomString(10),
				subject: randomString(1, 64),
				number: randomString(1, 32),
				description: randomString(1, 3),
				competencyGroupLogic: randomString(1000),
				disciplineId: sequence.disciplineId,
				cb21Code: sequence.cb21Code,
				courseGroup: sequence.courseGroup,
                mmapEquivalentCode: randomMmapEquivalentCode()
		)
	}


	CourseViewDto randomCourseViewDtoUpdate(Course course, DisciplineSequence seq) {
		new CourseViewDto(
				courseId: course.courseId,
				name: randomString(),
				cid: randomString(10),
				subject: randomString(1, 64),
				number: randomString(1, 32),
				description: randomString(),
				competencyGroupLogic: randomString(1000),
				disciplineId: seq.disciplineId,
				cb21Code: seq.cb21Code,
				courseGroup: seq.courseGroup,
                mmapEquivalentCode: randomMmapEquivalentCode()
		)
	}

	DisciplineSequenceViewDto copyDisciplineSequenceViewDto(DisciplineSequenceViewDto original) {
		def sequence = new DisciplineSequenceViewDto(
				disciplineId: original.disciplineId,
				cb21Code: original.cb21Code,
				explanation: original.explanation,
				level: original.level,
				showStudent: original.showStudent,
				courses: original.courses.collect()
		)
	}

	CourseViewDto copyCourseViewDto(CourseViewDto original) {
		new CourseViewDto(
				courseId: original.courseId,
				name: original.name,
				cid: original.cid,
				subject: original.subject,
				number: original.number,
				description: original.description,
				competencyGroupLogic: original.competencyGroupLogic,
				disciplineId: original.disciplineId,
				cb21Code: original.cb21Code,
				auditId: original.auditId
		)
	}

	DisciplineViewDto copyDiscipline(DisciplineViewDto discipline) {
			new DisciplineViewDto(
					disciplineId: discipline.disciplineId,
					collegeId: discipline.collegeId,
					title: discipline.title,
					description: discipline.description,
					competencyMapDiscipline: discipline.competencyMapDiscipline,
					competencyMapVersion: discipline.competencyMapVersion,
					sisCode: discipline.sisCode,
					noPlacementMessage: discipline.noPlacementMessage,
					usePrereqPlacementMethod: discipline.usePrereqPlacementMethod,
					competencyAttributes: discipline.competencyAttributes
			)
	}

	Iterable<CB21> getAllCB21() {
		return service.getAllCb21()
	}
    
    CB21 getRandomCb21() {
        if(cb21s == null) {
            CB21 cb21 = new CB21()
            cb21.cb21Code = 'A'
            cb21.levelsBelowTransfer = 1
            return cb21 
        }
        return  cb21s.get(randomInt(0, cb21s.size()))
    }
    
    String randomMmapEquivalentCode() {
        return mmapEquivalentCode.get(randomInt(0, mmapEquivalentCode.size() - 1))
    }
	
	CompetencyAttributes randomCompetencyAttributes(competencyMapDiscipline) {
		if(competencyMapDiscipline == "ENGLISH") {
			new EnglishCompetencyAttributes(
			  competencyAttributeId: randomInt(0,1000),
				 optInMultiMeasure: randomBoolean(),
				 placementComponentMmap: randomBoolean(),
				 placementComponentAssess: randomBoolean(),
				 useSelfReportedDataForMM: randomBoolean(),
				 mmDecisionLogic: randomId(),
				 mmPlacementLogic: randomId(),
				 mmAssignedPlacementLogic: randomId()
			)
		} else if(competencyMapDiscipline == "ESL") {
			new EslCompetencyAttributes(
				competencyAttributeId: randomInt(0,1000),
				   optInMultiMeasure: randomBoolean(),
				   placementComponentMmap: randomBoolean(),
				   placementComponentAssess: randomBoolean(),
				   useSelfReportedDataForMM: randomBoolean(),
				   mmDecisionLogic: randomId(),
				   mmPlacementLogic: randomId(),
				   mmAssignedPlacementLogic: randomId()
			  )
		} else {
		 new MathCompetencyAttributes(
			  competencyAttributeId: randomInt(0,1000),
				 optInMultiMeasure: randomBoolean(),
				 placementComponentMmap: randomBoolean(),
				 placementComponentAssess: randomBoolean(),
				 useSelfReportedDataForMM: randomBoolean(),
				 mmDecisionLogic: randomId(),
				 mmPlacementLogic: randomId(),
				 mmAssignedPlacementLogic: randomId()
			)
		}
	}
	
	CompetencyAttributesViewDto randomCompetencyAttributesView() {
		def competencyMD = randomMember(["ENGLISH", "ESL", "MATH"])
		new CompetencyAttributesViewDto(
			competencyCode: competencyMD,
			competencyAttributeId: randomInt(0,1000),
			optInMultiMeasure: randomBoolean(),
			placementComponentMmap: randomBoolean(),
			placementComponentAssess: randomBoolean(),
			useSelfReportedDataForMM: randomBoolean(),
			highestLevelReadingCourse: competencyMD.equals("ESL") ? randomId() : null,
			showPlacementToEsl: competencyMD.equals("MATH") ? false : randomBoolean(),
			showPlacementToNativeSpeaker: competencyMD.equals("MATH") ? false : randomBoolean(),
			prerequisiteGeneralEducation: competencyMD.equals("MATH") ? randomId() : null,
			prerequisiteStatistics: competencyMD.equals("MATH") ? randomId() : null,
			mmDecisionLogic: randomId(),
			mmPlacementLogic: randomId(),
			mmAssignedPlacementLogic: randomId()
		)
	}

}