package org.cccnext.tesuto.content.service.assessment

import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.model.ScopedIdentifier
import org.cccnext.tesuto.content.service.AssessmentAccessService
import org.cccnext.tesuto.content.service.AssessmentService
import org.cccnext.tesuto.activation.model.AssessmentAccess
import org.cccnext.tesuto.content.model.AssessmentAccessImpl
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared

public class AssessmentServiceSpec extends AssessmentServiceBaseSpec {

    @Shared assessmentAccessService
    
    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        service = context.getBean(AssessmentService.class)
        assessmentAccessService = context.getBean(AssessmentAccessService.class)
    }
    
    def "When reading the cached latest version of an assessment, the latest version is returned"() {
        setup:
        def identifier =  generator.randomId()
        
        def Map results = generator.generateAssessmentsWithVersion(service, 
                                                                  identifier, 
                                                                  generator.setVersionRandomly)
        when: def assessment = service.readLatestPublishedVersion(generator.namespace, identifier)
        then: assessment.version == results.max
        cleanup: results.ids.each { id -> service.delete(id)}
    }

    def "When reading all versions of an assessment, all versions are returned"() {
        setup:
        def identifier = generator.randomId()
        def repetitions = generator.randomInt(1, 10)
        def assessmentIds = []
        def nextVersion = 0
        (1..repetitions).each {
            AssessmentDto assessment = generator.randomAssessment()
            assessment.identifier = identifier
            nextVersion += generator.randomInt(1, 10)
            assessment.version = nextVersion
            def newAssessment = service.create(assessment)
            assessmentIds += newAssessment.id
        }

        when: def versions = service.readVersions(new ScopedIdentifier(generator.namespace, identifier))

        then: versions.size() == repetitions

        cleanup: assessmentIds.each { id -> service.delete(id) }
    }

    def "When reading the latest version of an assessment, the latest version is returned"() {
        setup:
        def maxVersion = 0
        def identifier = generator.randomId()
        def repetitions = generator.randomInt(1, 5)
        def assessmentIds = []
        def nextVersion = 0
        (1..repetitions).each {
            AssessmentDto assessment = generator.randomAssessment()
            assessment.identifier = identifier
            nextVersion += generator.randomInt(1, 10)
            assessment.version = nextVersion
            maxVersion = nextVersion
            def newAssessment = service.create(assessment)
            assessmentIds += newAssessment.id
        }

        when: def assessment = service.readLatestPublishedVersion(generator.namespace, identifier)

        then: assessment.version == maxVersion

        cleanup: assessmentIds.each { id -> service.delete(id) }
    }

    def "When reading a specific version of an assessment, that specific version is returned"() {
        setup:
        def versions = []
        def identifier = generator.randomId()
        def repetitions = generator.randomInt(1, 5)
        def assessmentIds = []
        def nextVersion = 0
        (1..repetitions).each {
            AssessmentDto assessment = generator.randomAssessment()
            assessment.identifier = identifier
            nextVersion += generator.randomInt(1, 10)
            assessment.version = nextVersion
            versions << assessment.version
            def newAssessment = service.create(assessment)
            assessmentIds += newAssessment.id
        }
        def expectedVersion = versions[generator.randomInt(0, versions.size()-1)]

        when: def assessment = service.readVersion(new ScopedIdentifier(generator.namespace, identifier), expectedVersion)

        then: assessment.version == expectedVersion

        cleanup: assessmentIds.each { id -> service.delete(id) }
    }

    def "When reading all assessments, only the latest, published versions are returned"() {
        setup:
        def maxVersions = [:]
        def uniqueAssessments = generator.randomInt(1, 5)
        def originalAssessmentCount = service.readPublishedUnique().size()
        def assessmentIds = []
        (1..uniqueAssessments).each {
            def identifier = generator.randomId()
            def repetitions = generator.randomInt(1, 5)
            def nextVersion = 0
            (1..repetitions).each {
                AssessmentDto assessment = generator.randomAssessment()
                assessment.identifier = identifier
                assessment.published = generator.randomBoolean()
                nextVersion += generator.randomInt(1, 10)
                assessment.version = nextVersion
                maxVersions[identifier] = nextVersion
                def newAssessment = service.create(assessment)
                assessmentIds += newAssessment.id
            }
        }

        when: def assessments = service.readPublishedUnique()

        then: assessments.each {assessment ->
            if(maxVersions.containsKey(assessment.identifier)) {
                assessment.version == maxVersions[assessment.identifier]
            }}
            assessments.size() == originalAssessmentCount + uniqueAssessments
        cleanup: assessmentIds.each { id -> service.delete(id) }
    }
    
    def "When reading all assessments, only the latest, published, allowed versions are returned"() {
        setup:
        def maxVersions = [:]
        def uniqueAssessments = 20
        def originalAssessmentCount = service.readPublishedUnique().size()
        def assessmentIds = []
        def allowedAssessments = []
        def userId = "userId"
        def locationId = 1
        (1..uniqueAssessments).each {
            def identifier = generator.randomId()
            def repetitions = generator.randomInt(1, 5)
            def nextVersion = 0
            if(generator.randomBoolean()) {
                def randomDate = generator.randomDate();
                def startDate = dateToCalendar(randomDate)
                randomDate.setTime(randomDate.getTime() + generator.randomInt(5000, 10000000));
                def endDate = dateToCalendar(randomDate);
                AssessmentAccess assessmentAccess = new AssessmentAccessImpl(
                   userId:userId,
                   locationId:locationId,
                   assessmentIdentifier:identifier,
                   assessmentNamespace:generator.namespace,
                   active:generator.randomBoolean(),
                   startDate: generator.randomBoolean() ? startDate : null,
                   endDate: generator.randomBoolean() ? endDate : null
                )
                assessmentAccessService.create(assessmentAccess)
                allowedAssessments += assessmentAccess;
            }
            (1..repetitions).each {
                AssessmentDto assessment = generator.randomAssessment()
                assessment.identifier = identifier
                assessment.published = generator.randomBoolean()
                nextVersion += generator.randomInt(1, 10)
                assessment.version = nextVersion
                if(assessment.published) {
                    maxVersions[identifier] = nextVersion
                }
                def newAssessment = service.create(assessment)
                assessmentIds += newAssessment.id
            }
        }

        when: def activationTime = Calendar.getInstance()
            def assessments = service.readPublishedUniqueForUserAndLocation(userId, Integer.toString(locationId))
        
        then: maxVersions.size() >= assessments.size()
        
        if(assessments.size() > 0) {
            assessments.each { assessment ->
                maxVersions.containsKey(assessment.identifier) == true
            }
        }
        allowedAssessments.each {allowed ->
          if(allowed.active == true) {
             assessmentsContainsIdentifier(allowed.assessmentIdentifier, assessments) ==
                (doesStartDateAllowActivation(allowed.startDate, activationTime) 
                && doesEndDateAllowActivation(allowed.endDate, activationTime))
           } else {
             assessmentsContainsIdentifier(allowed.assessmentIdentifier, assessments) == false
           }
        }
        
        cleanup: assessmentIds.each { id -> service.delete(id) }
        allowedAssessments.each{access -> assessmentAccessService.delete((AssessmentAccess)access)}
    }
    
    def doesStartDateAllowActivation(Calendar startDate, Calendar activationTime) {
        return  startDate ==  null || startDate.before(activationTime) ? true : false
    }
    
     def doesEndDateAllowActivation(Calendar endDate, Calendar activationTime) {
        return  endDate ==  null || endDate.after(activationTime) ? true : false
    }
    
    def assessmentsContainsIdentifier(String assessmentIdentifier, List<AssessmentDto> assessments) {
         def count = 0
         assessments.each {a -> if(a.identifier.equals(assessmentIdentifier)){
             count++}
          }
        return count > 0 ? true : false;
    }
    
    def dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
