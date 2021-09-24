package org.cccnext.tesuto.placement.service

import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.view.CourseViewDto
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto
import org.cccnext.tesuto.placement.view.student.CourseStudentViewDto
import org.dozer.Mapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.context.support.ClassPathXmlApplicationContext

import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations="/test-application-context.xml")
class AssemblerSpec extends Specification {

    @Shared generator
    @Shared CompetencyAttributesGenerator attributeGenerator
	@Shared ApplicationContext context
    @Shared    PlacementGenerator placementGenerator
    @Autowired PlacementService placementService
    @Autowired DisciplineAssembler disciplineAssembler
    @Autowired CourseAssembler courseAssembler
    @Autowired DisciplineSequenceAssembler sequenceAssembler
    @Autowired PlacementAssembler placementAssembler
    @Autowired PlacementComponentAssembler placementComponentAssembler;
    @Autowired VersionedSubjectAreaStudentViewAssembler studentSubjectAssembler;
    @Autowired Mapper mapper
    @Autowired PlacementStudentViewAssembler studentPlacementAssembler	
	@Shared disciplineIdsToDelete = []
	
	@Shared SubjectAreaServiceAdapter adapter
	@Shared SubjectAreaService subjectAreaService
	@Shared PlacementAuditService placementAuditService
	@Shared CompetencyAttributesService competencyAttributesService

    def setupSpec() {
		context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		
		 this.placementGenerator = new PlacementGenerator()
		 adapter = placementGenerator.adapter = context.getBean("subjectAreaServiceAdapter")
		 subjectAreaService = placementGenerator.subjectAreaService = context.getBean("subjectAreaService")
		 competencyAttributesService = placementGenerator.attributesService = context.getBean("competencyAttributesService")
		 placementGenerator.competencyAttributesAssembler = context.getBean("competencyAttributesAssembler")
		 placementAuditService = placementGenerator.placementAuditService = context.getBean("placementAuditService")
		 placementGenerator.setDisciplineIdsToDelete(disciplineIdsToDelete)
		 this.generator = new SubjectAreaGenerator(null)
        this.attributeGenerator = new CompetencyAttributesGenerator()
    }

    def "disassembly reverses assembly on disciplines"() {
        when:
        def discipline = generator.randomDiscipline()
        then:
        disciplineAssembler.disassembleDto(disciplineAssembler.assembleDto(discipline)) == discipline
    }

    def "assembly reverses disassembly on disciplines"() {
        when:
        def view = generator.randomDisciplineViewDto()
		def reconstitutedView = disciplineAssembler.assembleDto(disciplineAssembler.disassembleDto(view))
        then:
        reconstitutedView == view
    }

    def "disassembly reverses assembly on discipline with attributes"() {
        when:
        def discipline = generator.randomDiscipline()
        def attributes = attributeGenerator.randomCompetencyAttribute("ENGLISH")
        discipline.setCompetencyAttributes(attributes)

        then:
        disciplineAssembler.disassembleDto(disciplineAssembler.assembleDto(discipline)) == discipline
    }

    def "disassembly reverses assembly on courses"() {
        when:
        def course = generator.randomCourse()
        course.courseId = generator.randomInt(1,1000)
        course.disciplineSequenceCourses = [generator.randomDisciplineSequenceCourse(course)]
        then:
        courseAssembler.disassembleDto(courseAssembler.assembleDto(course)) == course
    }

    def "assembly reverses disassembly on courses"() {
        when:
        def discipline = generator.randomDisciplineViewDto()
        def sequence = generator.randomDisciplineSequenceViewDto(discipline)
        def view = generator.randomCourseViewDto(sequence)
        def course = courseAssembler.disassembleDto(view)
        def reassembled = courseAssembler.assembleDto(course)
        then:
        reassembled == view
    }

    def "disassembly reverses assembly on sequences"() {
        when:
        def discipline = generator.randomDiscipline()
        def sequence = generator.addCB21(generator.randomDisciplineSequence(discipline))
        then:
        sequenceAssembler.disassembleDto(sequenceAssembler.assembleDto(sequence)) == sequence
    }
	
    def "assembly reverses disassembly on sequences"() {
        when:
        def discipline = generator.randomDisciplineViewDto()
        def view = generator.randomDisciplineSequenceViewDto(discipline)
        then:
        sequenceAssembler.assembleDto(sequenceAssembler.disassembleDto(view)) == view
    }

    def "assembly reverses disassembly on placementViewDto"() {
        when:
            def placementDto = placementGenerator.randomPlacementViewDto()
            def placement = placementAssembler.disassembleDto(placementDto)
            def placementDtoNew = placementAssembler.assembleDto(placement)
            def updated = placementService.updatePlacement(placementDtoNew)
			compareObject(updated, placementDto)
         then:
            updated == placementDto
    } 

	
    def "disassembly reverses assembly on placement"() {
        when:
            def placement = placementGenerator.randomPlacement(0)
            def placementDto = placementAssembler.assembleDto(placement)
            def placementNew = placementAssembler.disassembleDto(placementDto)
        then:
            placementNew == placement
    }

    def "assembly reverses assembly on tesutoPlacementComponents"() {
        when:
            def placementComponent = placementGenerator.randomPlacementComponent("tesuto", 0)
            def placementComponentDto = placementComponentAssembler.assembleDto(placementComponent)
            def placementComponentNew = placementComponentAssembler.disassembleDto(placementComponentDto)
        then:
            placementComponentNew == placementComponent
    }

	
    def "assembly reverses assembly on mmapPlacementComponents"() {
        when:
            def placementComponent = placementGenerator.randomPlacementComponent("mmap", 0)
            def placementComponentDto = placementComponentAssembler.assembleDto(placementComponent)
            def placementComponentNew = placementComponentAssembler.disassembleDto(placementComponentDto)
        then:
            placementComponentNew == placementComponent
    }

	
    def "assembly reverses disassembly on tesutoPlacementComponentDto"() {
        when:
            def placementComponentDto = placementGenerator.randomPlacementComponentViewDto("tesuto", 0)
            def placementComponent = placementComponentAssembler.disassembleDto(placementComponentDto)
            def placementComponentDtoNew = placementComponentAssembler.assembleDto(placementComponent)
        then:
            placementComponentDtoNew == placementComponentDto
    }

    def "assembly reverses disassembly on mmapPlacementComponentDto"() {
        when:
            def placementComponentDto = placementGenerator.randomPlacementComponentViewDto("mmap", 0)
            def placementComponent = placementComponentAssembler.disassembleDto(placementComponentDto)
            def placementComponentDtoNew = placementComponentAssembler.assembleDto(placementComponent)
        then:
            placementComponentDtoNew == placementComponentDto
    }

    def "assembling a student view versioned subject area works correctly"() {
        when:
            def discipline = generator.randomDiscipline()
            def actualVersionedSubjectArea = studentSubjectAssembler.doAssemble(discipline)

        then:
            actualVersionedSubjectArea.disciplineId == discipline.disciplineId
            actualVersionedSubjectArea.noPlacementMessage == discipline.noPlacementMessage
            actualVersionedSubjectArea.title == discipline.title
    }


	
    def "assembling a student view placement dto works correctly"() {
        given:
            SubjectAreaServiceAdapter adapter = Mock()
            studentPlacementAssembler = new PlacementStudentViewAssembler(
                    mapper: mapper,
                    adapter: adapter
            )
        when:
            def placement
			try { placement = placementGenerator.randomAssignedPlacement(0) } catch (Exception e) { e.printStackTrace(); throw e; }
            placement.setCourseGroup(7)
            def discipline = generator.randomDiscipline()
            discipline.setDisciplineId(placement.getDisciplineId())
            def versionedSubjectArea = placementGenerator.randomVersionedSubjectAreaView(discipline)
            versionedSubjectArea.setDisciplineSequences(generateRandomSequences())
            versionedSubjectArea.getDisciplineSequences().add(generateTargetSequence(placement.cb21Code, placement.courseGroup, placement.disciplineId))
            adapter.getVersionedSubjectAreaDto(placement.getDisciplineId(), placement.getSubjectAreaVersion()) >> versionedSubjectArea
            adapter.getCoursesFromVersionedSubjectArea(versionedSubjectArea, placement.getCb21Code(), placement.getCourseGroup()) >> generator.randomCourses()
            def actualStudentViewDto = studentPlacementAssembler.assembleDto(placement)

        then:
            actualStudentViewDto.cccid == placement.cccid
            actualStudentViewDto.assignedDate == placement.assignedDate
            actualStudentViewDto.courses.size() != 0
            actualStudentViewDto.disciplineId == placement.disciplineId
            actualStudentViewDto.createdOn == placement.createdOn
            actualStudentViewDto.sequenceInfo.cb21Code == placement.cb21Code
            actualStudentViewDto.sequenceInfo.courseGroup == placement.courseGroup
    }

    DisciplineSequenceViewDto generateTargetSequence(char cb21Code, int courseGroup, int disciplineId) {
        new DisciplineSequenceViewDto(
                cb21Code: cb21Code,
                courseGroup: courseGroup,
                showStudent: true,
                explanation: "This is an explanation",
                disciplineId: disciplineId
        )
    }

    Set<DisciplineSequenceViewDto> generateRandomSequences() {
        Set<DisciplineSequenceViewDto> sequences = new HashSet<>()
        (1..generator.randomInt(2,5)).each { courseGroup ->
            sequences.add(generateRandomSequence('Y' as char, courseGroup))
        }
        ('A'..'H').each { cb21Code ->
            (1..generator.randomInt(2,5)).each { courseGroup ->
                sequences.add(generateRandomSequence(cb21Code as char, courseGroup))
            }
        }
        return sequences
    }

    DisciplineSequenceViewDto generateRandomSequence(char cb21Code, int courseGroup) {
        new DisciplineSequenceViewDto(
                cb21Code: cb21Code,
                courseGroup: courseGroup,
                showStudent: generator.randomBoolean(),
                explanation: generator.randomString(),
                disciplineId: generator.randomInt(0, 100),
                level: generator.randomInt(0, 8),
                mappingLevel: generator.randomInt(0, 8),
                courses: generator.randomCourses()
        )
    }
	
	def cleanupSpec() {
		println "CLEANING UP" + disciplineIdsToDelete.size()
		disciplineIdsToDelete.each { disciplineId ->
			try {
				println "DISCIPLINE_ID " + disciplineId
				def competencyAttributeId
				def discipline = subjectAreaService.getDiscipline(disciplineId)
				
				if(discipline.competencyAttributes != null) {
					competencyAttributeId = discipline.competencyAttributes.competencyAttributeId
					
				}
				
				println "DELETING DISCIPLINE_ID FROM VERSIONED SUBJECT AREA " + disciplineId
				println "DELETING COMPETENCY_ATTRIBUTE " + competencyAttributeId
				
				adapter.deleteVersionedSubjectAreaByDisciplineId(disciplineId)
				try{
					subjectAreaService.deleteDiscipline(disciplineId)
				}catch(Exception ex) {
					println "Discipline Deleted previously !!!" + disciplineId + ex
				}
				try {
				competencyAttributesService.delete(competencyAttributeId)
				}catch(Exception ex) {
					println "Attributes Deleted previously !!!" + competencyAttributeId + ex
				}
				
			    if(competencyAttributeId != 0) placementAuditService.deleteAuditRows("history_competency_attributes", "competency_attribute_id", competencyAttributeId)  
				placementAuditService.deleteAuditRows("history_college_discipline_sequence", "college_discipline_id", disciplineId)
				placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", disciplineId)
 
			} catch (Exception e) {
				println "WE BLEW IT!!!" + e
			}
		}
		
		disciplineIdsToDelete.each { disciplineId ->
			try {	
				println "DELETEING DISCIPLINE "  + disciplineId
				subjectAreaService.deleteDiscipline(disciplineId)
 
			} catch (Exception e) {
				println "WE BLEW IT!!!" + e
			}
		}
	}
	
	def String compareObject(act, exp) {
		StringBuilder comparison = new StringBuilder();
		Field[] declaredFields = act.class.getDeclaredFields();
		for(Field field:declaredFields) {
			compareField( act, exp, field, comparison)
		}
		println "THIS IS THE COMPARISON!!!" + comparison.toString()
		return comparison
	}
	
	def void compareField( act, exp, field, comparison) {
	field.setAccessible(true);
	boolean notEqual = false;
	Object actObj;
	Object expObj;
	String delimiter = ":"
	try {
		actObj = field.get(act);
		expObj = field.get(exp);
		if (expObj == null && actObj != null) {
			string.append(Field.getName()).append("is not equal expected is null actual is not");
			return;
		}
	} catch (IllegalArgumentException | IllegalAccessException exception) {
		throw new RuntimeException("Unable to use reflection to process equivalancy.", exception);
	}
	if (actObj instanceof String) {
		if (StringUtils.isBlank(actObj) && StringUtils.isBlank(expObj)) {
            notEqual = false;
        }
        if (actObj == expObj) {
            notEqual = false;
        }
        if (actObj != null) {
            notEqual = !actObj.equals(expObj);
        }
        notEqual = !expObj.equals(actObj);
	} else {
		 if (actObj == expObj) {
            notEqual = false;
        }
        if (actObj != null) {
            notEqual = !actObj.equals(expObj);
        }
        notEqual = !expObj.equals(actObj);
	}
	if (notEqual) {
		comparison.append(act.class.simpleName)
		.append(delimiter)
		.append(field.getName())
		.append(delimiter)
		.append(useSpecialDelimiter(expObj))
		.append("!=")
		.append(useSpecialDelimiter(actObj))
		.append("\n");
	} 
}

	
	def String useSpecialDelimiter(obj) {
		if (obj instanceof Iterable<?>) {
			return StringUtils.join((Iterable<?>) obj, ":");
		}
		return obj.toString();
	}
}

