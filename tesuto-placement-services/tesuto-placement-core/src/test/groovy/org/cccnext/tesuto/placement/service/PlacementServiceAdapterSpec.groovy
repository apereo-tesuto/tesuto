package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.exception.ValidationException
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.Discipline
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse
import org.cccnext.tesuto.placement.model.PlacementRevision
import org.cccnext.tesuto.placement.view.*

class PlacementServiceAdapterSpec extends PlacementSpecBase {

	String randomString() {
		generator.randomString()
	}

	String randomString(int n) {
		generator.randomString(n)
	}

	String randomString(int min, int max) {
		generator.randomString(min, max)
	}

	def randomTriple() {
		def sequence = aSequence
		[sequence, generator.randomCourseViewDto(sequence), sequence.disciplineId]
	}

	//Note this actually mutates the course parameter as a side effect
	def makeCourse(DisciplineSequenceViewDto sequence, CourseViewDto course) {
		def courseId = service.createCourse(sequence.disciplineId, course)
		courseIdsToDelete += courseId
		CourseViewDto newCourse = service.getCourse(courseId)
		course.auditId = newCourse.auditId
		course.courseId=courseId
		return course
	}

	def makeDiscipline(DisciplineViewDto discipline) {
		def disciplineId = service.createDiscipline(discipline)
		discipline.disciplineId = disciplineId
		disciplineIdsToDelete += disciplineId
		service.getDiscipline(disciplineId)
	}

	def "After creating a Discipline, we can retrieve it by id"() {
		when:  def created = makeDiscipline(discipline)
		then:  service.getDiscipline(created.disciplineId) == created
		where: discipline << [ generator.randomDisciplineViewDto() ]
	}

	def "After creating a discipline, it is returned by getDisciplines()" () {
		when:  def created = makeDiscipline(discipline)
		then:  service.getDisciplines().contains(created)
		where: discipline << [ generator.randomDisciplineViewDto() ]
	}

	def "After updating a discipline, the changes are persisted"() {
		when:
		DisciplineViewDto created = makeDiscipline(discipline)
		created.title = randomString()
		created.description = randomString()
		created.sisCode = randomString(1,100)
		created.usePrereqPlacementMethod = !created.usePrereqPlacementMethod
		service.updateDiscipline(created)

		then:
		service.getDiscipline(created.disciplineId) == created
		where:
		discipline << [ generator.randomDisciplineViewDto() ]
	}

	def "After updating a discipline, get previous version by audit."() {
		when:
		DisciplineViewDto created = makeDiscipline(discipline)
		PlacementRevision revision = placementAuditService.getCurrentRevision();
		DisciplineViewDto original = generator.copyDiscipline(created)
		created.title = randomString()
		created.description = randomString()
		created.sisCode = randomString(1,100)
		service.updateDiscipline(created)
		Discipline retrieved = (Discipline)placementAuditService.getByIdAndRevision(Discipline.class, original.disciplineId, revision.getId())
		then:
			def DisciplineViewDto retreivedView = disciplineAssembler.assembleDto(retrieved);
			retreivedView == original

		where:
		discipline << [ generator.randomDisciplineViewDto() ]
	}

	def "Attempt to update a competencyMapDiscipline, results in exception"() {
		when:
		DisciplineViewDto created = makeDiscipline(discipline)
		created.title = randomString()
		created.description = randomString()
		created.competencyMapDiscipline = randomString()
		created.sisCode = randomString(1,100)
		service.updateDiscipline(created)
		then: ValidationException exception = thrown()
		exception.message == "Competency Map can not be changed."
		where:
		discipline << [ generator.randomDisciplineViewDto() ]
	}


	def "All disciplines returned by getDisciplinesByCollegeId have the correct collegeId"() {
		expect: service.getDisciplinesByCollegeId([aDiscipline.collegeId]).each { it.collegeId == aDiscipline.collegeId }
	}

	def "Any discipline 'd' is returned by getDisciplinesByCollegeId(d.collegeId)"() {
		expect: service.getDisciplinesByCollegeId([aDiscipline.collegeId]).contains(aDiscipline)
	}

	def "After creating a course we can retrieve it by id"() {
		when:
		def (sequence, course) = randomTriple()
		CourseViewDto created = makeCourse(sequence, course)
		def withSequences = service.getCourse(created.courseId)
		then:
		course == created
	}

	def "A course created with disciplineId is returned by getCourses(disciplineId)"() {
		when:
		def (sequence, course, disciplineId) = randomTriple()
		CourseViewDto created = makeCourse(sequence, course)
		then:
		service.getCourses(disciplineId).contains(created)
	}


	def "A course created with disciplineId and cb21Code is returned by getCourses(disciplineId, cb21Code)"() {
		when:
		def (sequence, course, disciplineId) = randomTriple()
		CourseViewDto created = makeCourse(sequence, course)
		then:
		service.getCourses(disciplineId).contains(created)
	}

	def "After upserting an existing discipline Sequence, the changes are persisted"() {
		when:
		DisciplineSequenceViewDto sequence = aSequence
		sequence.explanation = randomString()
		sequence.mappingLevel = randomString()
		service.upsert(sequence)
		then:
		service.getSequencesByDisciplineId(sequence.disciplineId).any {
			it.explanation == sequence.explanation
		}
	}

	def "A sequence with aDisciplineId is returned by getSequencesByDisciplineId"() {
		expect: service.getSequencesByDisciplineId(aDiscipline.disciplineId).any {
			it.disciplineId == aSequence.disciplineId &&
					it.cb21Code == aSequence.cb21Code
		}
	}

	def "All sequences returned by getSequencesByDisciplineId have the correct disciplineId"() {
		expect: service.getSequencesByDisciplineId(aDiscipline.disciplineId).every {
			it.disciplineId = aDiscipline.disciplineId
		}
	}

	def "getSequencesWithDisciplineId returns data including courses "() {
		when:
		def newCourse = makeCourse(aSequence, generator.randomCourseViewDto(aSequence))
		def sequences = service.getSequencesByDisciplineId(aSequence.disciplineId)
		def sequence = sequences.find { it.cb21Code == aSequence.cb21Code }

		then: sequence.courses.contains(newCourse)
	}

	def "getCollegeIdsForCourse returns the correct collegeId"() {
		when:
		def course = makeCourse(aSequence, generator.randomCourseViewDto(aSequence))
		then:
		service.getCollegeIdsForCourse(course.courseId)  == [aDiscipline.getCollegeId()].toSet()
	}


	def "disciplineSequenceExists returns true if a discipline sequence exists"() {
		expect: service.disciplineSequenceExists(aDiscipline.disciplineId, aSequence.cb21Code, aSequence.courseGroup)
	}

	def "disciplineSequenceExists returns false if a discipline sequence does not exist"() {
		expect:
		!service.disciplineSequenceExists(-1*generator.randomInt(1,1000000), aSequence.cb21Code, aSequence.courseGroup )
		!service.disciplineSequenceExists(aDiscipline.disciplineId, 'Z' as char, aSequence.courseGroup)
	}

}
