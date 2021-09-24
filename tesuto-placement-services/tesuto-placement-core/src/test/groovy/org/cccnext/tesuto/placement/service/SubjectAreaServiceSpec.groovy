package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.Discipline
import org.cccnext.tesuto.placement.model.DisciplineSequence
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class SubjectAreaServiceSpec extends Specification {

  @Shared ClassPathXmlApplicationContext context
	@Shared SubjectAreaService service
	@Shared SubjectAreaServiceAdapter adapter
	@Shared PlacementAuditService placementAuditService
	@Shared DisciplineAssembler disciplineAssembler
	@Shared CourseAssembler courseAssembler

	@Shared generator
	@Shared courseIdsToDelete = []
	@Shared courseHistoryIdsToDelete = []
	@Shared disciplineIdsToDelete = []
	@Shared Discipline aDiscipline
	@Shared DisciplineSequence aSequence
	

	def setupSpec() {
		context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		service = context.getBean("subjectAreaService")
		adapter = context.getBean("subjectAreaServiceAdapter")
		placementAuditService = context.getBean("placementAuditService")
		disciplineAssembler = context.getBean("disciplineAssembler")
		courseAssembler = context.getBean("courseAssembler")
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
	Course makeCourse(DisciplineSequence sequence, Course course) {
		Course created = service.createCourse(sequence, course)
		courseIdsToDelete += created.courseId
		course.courseId = created.courseId
		created
	}

	Discipline makeDiscipline(Discipline discipline) {
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

	def "After upserting an existing discipline Sequence, the changes are persisted"() {
		setup:
			def discipline = service.createDiscipline(generator.randomDiscipline())
			disciplineIdsToDelete += discipline.disciplineId
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

  def "After publishing a subject, the version is published date are set correctly"() {
    when:
    def discipline = makeDiscipline(generator.randomDiscipline())
    def dto = adapter.createVersionedSubjectArea(discipline)
    def dto2 = adapter.createVersionedSubjectArea(discipline)

    then:
    dto != null
    dto.disciplineId == discipline.disciplineId
    dto.version == 1
    Math.abs(dto.publishedDate.getTime() - System.currentTimeMillis()) <= 1000l
    dto2 != null
    dto2.disciplineId == discipline.disciplineId
    dto2.version == 2
    Math.abs(dto2.publishedDate.getTime() - System.currentTimeMillis()) <= 1000l
  }

  def "No published version is found before one is published"() {
    when:
    def discipline = makeDiscipline(generator.randomDiscipline())
    then:
    adapter.getLatestVersionForSubjectArea(discipline.disciplineId) == null
  }

  def "A published version can be selected by discipline id and version"() {
    when:
    def discipline = makeDiscipline(generator.randomDiscipline())
    def dto = adapter.createVersionedSubjectArea(discipline)
    then:
    adapter.getLatestVersionForSubjectArea(discipline.disciplineId) == dto
  }

	def cleanupSpec() {
		courseIdsToDelete.each { service.deleteCourse( it) }
		courseIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
		courseHistoryIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
		if(aDiscipline != null) {
			disciplineIdsToDelete << aDiscipline.disciplineId
		}
		disciplineIdsToDelete.each { disciplineId ->
      adapter.deleteVersionedSubjectAreaByDisciplineId(disciplineId)
	  def discipline = service.getDiscipline(disciplineId)
	  def competencyAttributeId
	  if(discipline.competencyAttributes != null) {
		  competencyAttributeId = discipline.competencyAttributes.competencyAttributeId
		  
	  }
      service.deleteDiscipline(disciplineId)
	  placementAuditService.deleteAuditRows("history_competency_attributes", "competency_attribute_id", competencyAttributeId)  
	  placementAuditService.deleteAuditRows("history_college_discipline_sequence", "college_discipline_id", disciplineId)
      placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", disciplineId)
    }
	}

}
