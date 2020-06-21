package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.view.*
import org.cccnext.tesuto.content.dto.AssessmentBaseType
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto
import org.cccnext.tesuto.admin.dto.UserAccountDto
import org.cccnext.tesuto.content.service.AssessmentService
import org.cccnext.tesuto.content.service.CompetencyMapOrderService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Ignore
import spock.lang.Specification
import org.cccnext.tesuto.util.test.SessionGenerator
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariable
import spock.util.concurrent.PollingConditions
import uk.ac.ed.ph.jqtiplus.value.BaseType

class PostDeliveryAssessmentCompletionServiceSpec extends Specification {

    @Shared
    PostDeliveryAssessmentCompletionService postDeliveryAssessmentCompletionService
    @Shared
    SessionGenerator generator
    @Shared
    AssessmentService assessmentService
    @Shared
    AssessmentSessionDao dao
    
    @Shared
    CompetencyMapOrderService competencyMapOrderService

    def setupSpec() {
        generator = new SessionGenerator()
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        postDeliveryAssessmentCompletionService = context.getBean("postDeliveryAssessmentCompletionService")
        assessmentService = context.getBean("assessmentReader")
        dao = context.getBean("assessmentSessionDao")
        competencyMapOrderService = context.getBean("competencyMapOrderReader")
    }


    def "with an expected assessmentSession will return the first valid entry-teslet shown to the student"(){
        when:
        def identifiers = generator.randomListSetSize(5, { generator.randomString() })
        def metadata = createMetadataWithExtraValidTestlet(identifiers)
        def sequence = randomizeOrderOfMap(identifiers)
        def firstInSequence = sequence.keySet().getAt(0)
        def noisySequence = createNoiseOnMap(sequence)
        def session = createAssessmentSession(createAssessmentCompletePlacementInputDto(firstInSequence), metadata, generator.randomString(), noisySequence)

        then:
        firstInSequence == postDeliveryAssessmentCompletionService.determineCompetencyMapDisciplineFromEntryTestlet(session)

        where:
        i << (1..5)
    }

    def "when assessmentSession is as expected post delivery will generate the expected AssessmentCompletePlacementInputDto"(){
        when:
        def assessmentCompletePlacementInputDto = createAssessmentCompletePlacementInputDto(generator.randomString())
        assessmentCompletePlacementInputDto.competencyMapDisciplines = ["ESL","English"]
        def session = createAssessmentSession(assessmentCompletePlacementInputDto)
        def user = createUserAccount(assessmentCompletePlacementInputDto)
		def generatedInputDto = postDeliveryAssessmentCompletionService.generateAssessmentCompletePlacementDto(session, false)
		assessmentCompletePlacementInputDto.setTrackingId(generatedInputDto.getTrackingId())
        then:
        assessmentCompletePlacementInputDto == generatedInputDto

        where:
        i << (1..3)
    }

    def "when the generateAssessmentPlacement flag is set to false will return null"(){
        when:
        def assessmentCompletePlacementInputDto = createAssessmentCompletePlacementInputDto(generator.randomString())
        def session = createAssessmentSession(assessmentCompletePlacementInputDto)
        def user = createUserAccount(assessmentCompletePlacementInputDto)

        session.assessment.assessmentMetadata.generateAssessmentPlacement = generator.randomString()


        then:
        null == postDeliveryAssessmentCompletionService.generateAssessmentCompletePlacementDto(session, generator.randomBoolean())

        where:
        i << (1..3)
    }
	
	@Ignore(value="TODO Ignored for now, may require updates to postDeliveryAssessmentCompletionService, not required for placement")
    def "the entry testlet will be set and session saved"(){
        when:
        def conditions = new PollingConditions(timeout: 2, initialDelay: 0.1, factor: 1.25)

        def user = createUserAccount(createAssessmentCompletePlacementInputDto())

        def identifiers = generator.randomListSetSize(5, { generator.randomString() })
        def metadata = createMetadataWithExtraValidTestlet(identifiers)
        def sequence = randomizeOrderOfMap(identifiers)
        def expectedEntryTestletCompetencyMapDiscipline = sequence.keySet().getAt(0)
        def noisySequence = createNoiseOnMap(sequence)
        def session = createAssessmentSession(createAssessmentCompletePlacementInputDto(expectedEntryTestletCompetencyMapDiscipline), metadata, generator.randomString(), noisySequence)

        dao.save(session)  // save before entry testlet competency map discipline is there.

        postDeliveryAssessmentCompletionService.completeAssessment(session.assessmentSessionId, user)

        then:
        conditions.eventually {
            AssessmentSession savedSession = dao.find(session.assessmentSessionId)
            expectedEntryTestletCompetencyMapDiscipline == savedSession.competencyMapDisciplineFromEntryTestlet
            savedSession.competencyMapOrderIds.containsKey(expectedEntryTestletCompetencyMapDiscipline)
            savedSession.competencyMapOrderIds.get(expectedEntryTestletCompetencyMapDiscipline) == savedSession.competencyMapDisciplineFromEntryTestlet + "ID"

        }

        where:
        i << (1..3)

    }

    def createUserAccount(def assessmentCompletePlacementInputDto){
        new UserAccountDto()
    }

    def createAssessmentSession(assessmentCompletePlacementInputDto){
        return createAssessmentSession(assessmentCompletePlacementInputDto,
                createMetadataWithCompMapDisc(generator.randomString(), true),
                generator.randomString(),
                null)
    }

    def createOutcome(def identifier, def value){
        new Outcome(
                outcomeIdentifier: identifier,
                value: value,
                baseType: AssessmentBaseType.FLOAT
        )
    }

    def createAssessmentCompletePlacementInputDto(expectedEntryTestletCompetencyMapDiscipline){
        def mapDisciplines = [expectedEntryTestletCompetencyMapDiscipline] as Set<String>
        competencyMapOrderService.createForDisciplines(mapDisciplines)
        new AssessmentCompletePlacementInputDto(
                assessmentSessionId: generator.randomString(),
                assessmentTitle: generator.randomString(),
                cccid: generator.randomId(),
                completionDate: new Date(),
                studentAbility: generator.randomDouble(),
                competencyMapDisciplines: mapDisciplines,
                elaIndicator: expectedEntryTestletCompetencyMapDiscipline
        )
    }

    def createAssessmentSession(AssessmentCompletePlacementInputDto assessmentCompletePlacementInputDto, def metadata, def assessmentId, def sequence){
        def session = new AssessmentSession(
                assessmentSessionId: assessmentCompletePlacementInputDto.assessmentSessionId,
                userId: assessmentCompletePlacementInputDto.cccid,
                contentId: assessmentId,
                assessment: createAssessment(metadata, assessmentId, assessmentCompletePlacementInputDto.assessmentTitle),
                sequence: sequence,
                completionDate: assessmentCompletePlacementInputDto.completionDate,
                competencyMapDisciplineFromEntryTestlet : assessmentCompletePlacementInputDto.elaIndicator
        )
        session.addOutcome(createOutcome(Outcome.CAI_STUDENT_ABILITY, assessmentCompletePlacementInputDto.studentAbility))

        return session
    }

    def createAssessment(def metadata, def assessmentId, def assessmentTitle){
        def assessment = new AssessmentDto(
                assessmentMetadata: metadata,
                id: assessmentId,
                title: assessmentTitle
        )
        assessmentService.create(assessment)
        return assessment
    }

    def createMetadata(def key, def expected){
        return new AssessmentMetadataDto(
                generateAssessmentPlacement: 'YES',
                section: generator.randomList(1, {createSection(key, expected)})
        )
    }

    def createNoiseOnMap(def map){
        def noisyMap = [:]
        if(generator.randomBoolean()){
            def randomKeys = generator.randomListSetSize(5, { generator.randomString() })
            noisyMap.putAll(randomizeOrderOfMap(randomKeys))
        }
        noisyMap.putAll(map)
        return noisyMap
    }

    def randomizeOrderOfMap(def keys){
        HashMap map = new LinkedHashSet<>();
        //Expects key of size 5
        (0..4).each {
            def index = generator.rand.nextInt(keys.size())
            map[keys[index]] = keys[index]
            keys.remove(index)
        }
        return map
    }

    def createMetadataWithExtraValidTestlet(def keys){
        def metadata = createMetadata(keys[0], true)
        //assumes set size of 5
        (1..4).each {
            metadata.section.add(createSection(keys[it], true))
        }

        return metadata
    }

    def createMetadataWithCompMapDisc(def key, def expected){
        return new AssessmentMetadataDto(
                generateAssessmentPlacement: 'YES',
                section: generator.randomList(1, {createSection(key, expected)}),
                competencyMapDisciplines: ["ESL", "English"]
        )
    }

    def createSection(def key, def expected){
        def section = new SectionMetadataDto(
                identifier: key,
                type: "ENTRY-TESTLET",
                competencyMapDiscipline: key
        )

        if(!expected){
            if(generator.randomBoolean()) {
                section.identifier = generator.randomString()
            }else{
                section.type = generator.randomString()
            }
        }
        return section
    }
}