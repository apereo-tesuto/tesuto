package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.Discipline
import org.cccnext.tesuto.placement.model.DisciplineSequence
import org.cccnext.tesuto.placement.view.CourseViewDto
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification

class PlacementServiceSpec extends Specification {

	@Shared ApplicationContext context
	@Shared SubjectAreaService service
	@Shared generator
	@Shared courseIdsToDelete = []
	@Shared courseHistoryIdsToDelete = []
	@Shared disciplineIdsToDelete = []
	@Shared Discipline aDiscipline
	@Shared PlacementAuditService placementAuditService
	@Shared DisciplineSequence aSequence
	@Shared DisciplineAssembler disciplineAssembler

	def setupSpec() {
		context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		service = context.getBean("subjectAreaService")
		disciplineAssembler = context.getBean("disciplineAssembler")
		placementAuditService = context.getBean("placementAuditService")
		generator = new SubjectAreaGenerator(service)
		aDiscipline = generator.getADiscipline()
		aSequence = generator.getASequence()
	}

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
		[sequence, generator.randomCourse(), sequence.disciplineId]
	}

	//Note this actually mutates the course parameter as a side effect
	def makeCourse(DisciplineSequence sequence, Course course) {
		Course created = service.createCourse(sequence, course)
		courseIdsToDelete += created.courseId
		course.courseId = created.courseId
		created
	}

	def makeDiscipline(Discipline discipline) {
		Discipline created = service.createDiscipline(discipline)
		discipline.disciplineId = created.disciplineId
		disciplineIdsToDelete += created.disciplineId
		created
	}

	def "getOrCreateSequence returns sequence when it exists"() {
		when:
		def expectedSequence = aSequence
		def sequence = service.getOrCreateSequence(expectedSequence.getDisciplineId(), expectedSequence.getCb21Code(), expectedSequence.getCourseGroup())

		then:
		sequence.equals(expectedSequence)
	}

	def "getOrCreateSequence returns new sequence when no sequence exists for parameters"() {
		when:
		def discipline = generator.randomDiscipline()
		def persistedDiscipline = makeDiscipline(discipline)
		def cb21 = generator.randomCB21Code()
		def courseGroup = generator.randomInt(1, 15)

		then:
		service.getSequencesByDisciplineId(persistedDiscipline.disciplineId).size() == 0
		def expectedSequence = service.getOrCreateSequence(persistedDiscipline.disciplineId, cb21, courseGroup)
		def sequences = service.getSequencesByDisciplineId(persistedDiscipline.disciplineId)
		sequences.size() == 1
		sequences.contains(expectedSequence)
	}

	def "After creating a Discipline, we can retrieve it by id"() {
		when:  def created = makeDiscipline(discipline)
		then:  service.getDiscipline(created.disciplineId) == created
		where: discipline << [ generator.randomDiscipline() ]
	}

	def "After creating a discipline, it is returned by getDisciplines()" () {
		when:  def created = makeDiscipline(discipline)
		then:  service.getDisciplines().contains(created)
		where: discipline << [ generator.randomDiscipline() ]
	}

	def "After updating a discipline, the changes are persisted"() {
		when:
		Discipline created = makeDiscipline(discipline)
		created.title = generator.randomString()
		created.description = generator.randomString()
		created.usePrereqPlacementMethod = generator.randomBoolean()
		created.sisCode = generator.randomString(10)
		service.updateDiscipline(created)
		then:
		service.getDiscipline(created.disciplineId) == created
		where:
		discipline << [ generator.randomDiscipline() ]
	}

	def "All disciplines returned by getDisciplinesByCollegeId have the correct collegeId"() {
		expect: service.getDisciplinesByCollegeId([aDiscipline.collegeId]).each { it.collegeId == aDiscipline.collegeId }
	}

	def "Any discipline 'd' is returned by getDisciplinesByCollegeId(d.collegeId)"() {
		expect: service.getDisciplinesByCollegeId([aDiscipline.collegeId]).contains(aDiscipline)
	}

	@Transactional
	def "After upserting an existing discipline Sequence, the changes are persisted"() {
		setup:
			def discipline = makeDiscipline(generator.randomDiscipline())
			def courseGroup = generator.randomInt(1,15)
			def sequence = service.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
		when:
			def mappingLevel = generator.randomString()
			def explanation = generator.randomString()
			def showStudent  = generator.randomBoolean()
			sequence.mappingLevel = mappingLevel
			sequence.explanation = explanation
			sequence.showStudent = showStudent
			service.upsert(sequence)
		then:
		def foundDs = service.getOrCreateSequence(sequence.disciplineId, sequence.cb21Code, sequence.courseGroup)
		foundDs.explanation == sequence.explanation
		foundDs.mappingLevel == sequence.mappingLevel
	}


	def "A sequence with aDisciplineId is returned by getSequencesByDisciplineId"() {
		expect: service.getSequencesByDisciplineId(aDiscipline.disciplineId).contains(aSequence)
	}

	def "All sequences returned by getSequencesByDisciplineId have the correct disciplineId"() {
		expect: service.getSequencesByDisciplineId(aDiscipline.disciplineId).every {
			it.disciplineId = aDiscipline.disciplineId
		}
	}

	def "When a discipline is created, no sequence is created with it"() {
		when:
		def discipline = makeDiscipline(generator.randomDiscipline())
		def disciplineSeq = service.getSequencesByDisciplineId(discipline.disciplineId)
		then:
		disciplineSeq.size() == 0
	}

	def "getSequencesWithDisciplineId returns data including courses "() {
		when:
		def newCourse = makeCourse(aSequence, generator.randomCourse())
		def sequences = service.getSequencesByDisciplineId(aSequence.disciplineId)
		def sequence = sequences.find { it.cb21Code == aSequence.cb21Code }
		def courses = sequence.disciplineSequenceCourses.collect { it.course }

		then: courses.contains(newCourse)
	}

	def cleanupSpec() {
		courseIdsToDelete.each { service.deleteCourse(it) }
		courseIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
		courseHistoryIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
		if (aDiscipline != null) {
			disciplineIdsToDelete << aDiscipline.disciplineId
		}
		disciplineIdsToDelete.each { 
			service.deleteDiscipline(it) }
		disciplineIdsToDelete.each {
			placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", it)
			placementAuditService.deleteAuditRows("history_college_discipline_sequence", "college_discipline_id", it)
		}
		context.close()
	}
}