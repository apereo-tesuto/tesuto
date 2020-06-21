package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.BeforeTransaction
import org.springframework.transaction.annotation.Transactional

import spock.lang.*

@ContextConfiguration(locations = "/test-application-context.xml")
class CourseSequenceSpec extends Specification {

    @Autowired
    SubjectAreaService subjectAreaService

    @Autowired
    PlacementAuditService placementAuditService

    @Autowired
    SubjectAreaServiceAdapter subjectAreaServiceAdapter

    @Autowired
    CourseAssembler courseAssembler

    SubjectAreaGenerator generator

    def setup() {
        generator = new SubjectAreaGenerator(subjectAreaService)
    }

    @Transactional
    def "After creating a course we can retrieve it by id and with sequences"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
        when:
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        then:
          course == subjectAreaService.getCourse(course.courseId)
          def foundWithSeq = subjectAreaService.getCourseWithSequences(course.courseId)
          course == foundWithSeq
          foundWithSeq.disciplineSequenceCourses.size() == 1
          foundWithSeq.disciplineSequenceCourses.iterator()[0].disciplineSequence == sequence
    }

    @Transactional
    def "After creating a course, it is returned by getCourseWithSequences() and the sequence is matching" () {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
        when:
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        then:
            def foundCourse = subjectAreaService.getCourseWithSequences(course.courseId)
            foundCourse != null
            foundCourse.disciplineSequenceCourses.size() == 1
            def dsc = foundCourse.disciplineSequenceCourses.iterator()[0]
            dsc.courseGroup == courseGroup
            dsc.cb21Code == 'Y' as char
    }

    @Transactional
    def "After updating a course, the changes are persisted"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            def updatedCourse = generator.randomCourseViewDtoUpdate(course, sequence)
            subjectAreaService.updateCourse(course, updatedCourse)
        then:
            Course foundCourse = subjectAreaService.getCourse(course.courseId)
            foundCourse == updatedCourse
    }

    @Transactional
    def "All courses returned by getCourses(disciplineId) are from the correct discipline"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
            def course1 = subjectAreaService.createCourse(sequence, generator.randomCourse());
        expect:
            subjectAreaService.getCourses(discipline.disciplineId).every {
                co ->
                    def fullCourse = subjectAreaService.getCourseWithSequences(co.courseId)
                    fullCourse.disciplineSequences.any { it.disciplineId == discipline.disciplineId }
                }
    }

    @Transactional
    def "The sequence of a course can be changed"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = 5
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
            def course1 = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            def newCb21 = 'A' as char
            def newCourseGroup = courseGroup + 1;
            def courseDto = courseAssembler.assembleDto(course)
            courseDto.cb21Code = newCb21
            courseDto.courseGroup = newCourseGroup
            subjectAreaService.updateCourse(course, courseDto)
        then:
            subjectAreaService.getCourseWithSequences(course.courseId).getDisciplineSequenceCourses()
                    .any { it.cb21Code == newCb21 &&  it.courseGroup == newCourseGroup}
            !subjectAreaService.getCourseWithSequences(course.courseId).getDisciplineSequenceCourses()
                    .any { it.cb21Code == 'Y' as char && it.courseGroup == courseGroup }
    }

    @Transactional
    def "After deleting a course, the course cannot be retrieved"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
            def course1 = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            subjectAreaService.deleteCourse(course.courseId)
        then:
            subjectAreaService.getCourse(course.courseId) == null
    }

    @Transactional
    def "After deleting a course, it cannot be returned and the sequence has been removed" () {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            subjectAreaService.deleteCourse(course.courseId)
        then:
            def foundCourse = subjectAreaService.getCourse(course.courseId)
            foundCourse == null
    }

    @Transactional
    def "After changing course group for course, the sequence changed and the previous has been removed" () {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
             def courseDto = courseAssembler.assembleDto(course)
             courseDto.courseGroup = courseGroup+1
             subjectAreaService.updateCourse(course, courseDto)
        then:
            def foundCourse = subjectAreaService.getCourse(course.courseId)
            foundCourse != null
    }

    @Transactional
    def "After deleting a course, the course cannot be retrieved"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            subjectAreaServiceAdapter.deleteCourse(course.courseId)
        then:
            subjectAreaServiceAdapter.getCourse(course.courseId) == null
    }

    @Transactional
    def "After updating a course, the changes are persisted"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            def updatedCourse = generator.randomCourseViewDtoUpdate(course, sequence)
            subjectAreaServiceAdapter.updateCourse(updatedCourse)
        then:
            subjectAreaServiceAdapter.getCourse(course.courseId) == updatedCourse
    }

    def "After updating a course, the original state can be retrieved by audit"() {
        setup:
            def discipline = subjectAreaService.createDiscipline(generator.randomDiscipline())
            def courseGroup = generator.randomInt(1,15)
            def sequence = subjectAreaService.getOrCreateSequence(discipline.disciplineId, 'Y' as char, courseGroup)
            def course = subjectAreaService.createCourse(sequence, generator.randomCourse());
        when:
            def courseDto = courseAssembler.assembleDto(course)
            def original = generator.copyCourseViewDto(courseDto)
            def revision = placementAuditService.getCurrentRevision();
            def updatedCourse = generator.randomCourseViewDtoUpdate(course, sequence)
            subjectAreaServiceAdapter.updateCourse(updatedCourse)
        then:
            Course retrieved = (Course)placementAuditService.getByIdAndRevision(Course.class, original.courseId, revision.getId())
            retrieved != null
            def List<Object> dscs = placementAuditService.getEntitiesByRevisionByPropertyValue(DisciplineSequenceCourse.class, revision.getId(), "courseId", original.courseId);
            Set<DisciplineSequenceCourse> dscset = []
            dscs.forEach({dscset.add(it)})
            retrieved.setDisciplineSequenceCourses(dscset)
            def retrivedDto = courseAssembler.assembleDto(retrieved)

        cleanup:
            // Envers creates audit records on commit, so we do not use @Transactional and clean up manually
            subjectAreaService.deleteCourse(course.getCourseId());
            placementAuditService.deleteAuditRows("history_course", "course_id", course.getCourseId());

            subjectAreaService.deleteDiscipline(discipline.getDisciplineId());
            placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", discipline.getDisciplineId());
            placementAuditService.deleteAuditRows("history_college_discipline_sequence", "college_discipline_id", discipline.getDisciplineId());

    }

}