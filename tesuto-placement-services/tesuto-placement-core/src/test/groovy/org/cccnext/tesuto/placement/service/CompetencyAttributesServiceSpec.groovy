package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.*
import org.cccnext.tesuto.placement.view.*
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spock.lang.Shared
import spock.lang.Specification

class CompetencyAttributesServiceSpec extends Specification {

	@Shared ApplicationContext context
	@Shared SubjectAreaGenerator subjectAreaGenerator
	@Shared CompetencyAttributesGenerator generator
	
	@Shared SubjectAreaService subjectAreaService
	@Shared PlacementAuditService placementAuditService
	@Shared CompetencyAttributesService service
	@Shared CompetencyAttributesAssembler competencyAttributesAssembler
	
	@Shared attributeIdsToDelete = []
	@Shared Discipline aSubjectArea

	def setupSpec() {
		context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		subjectAreaService = context.getBean("subjectAreaService")
		placementAuditService = context.getBean("placementAuditService")
		service = context.getBean("competencyAttributesService")
		competencyAttributesAssembler = context.getBean("competencyAttributesAssembler")
		generator = new CompetencyAttributesGenerator()
		subjectAreaGenerator = new SubjectAreaGenerator(subjectAreaService)
	}

	def makeCompetencyAttribute(CompetencyAttributesViewDto competencyAttribute) {
		def created = service.upsert( competencyAttribute)
		attributeIdsToDelete += created.competencyAttributeId
		return created
	}

	
	def "After creating a CompetencyAttribute, we can retrieve them for the subject area"() {
		when:  def created = makeCompetencyAttribute( competencyAttribute)
		then:  def retrieved = service.get( created.competencyAttributeId)
			retrieved == created
		where: competencyAttribute << [ generator.randomCompetencyAttributesViewDto("ENGLISH") ]
	}

	def "After we updating an attribute, the value is changed"() {
		when:  competencyAttribute.setOptInMultiMeasure(true)
			service.upsert(competencyAttribute)
		then:  def retrieved = service.get( attributeIdsToDelete.get(0))
			retrieved.isOptInMultiMeasure()
		where: competencyAttribute << [ service.get(attributeIdsToDelete.get(0))]
	}


	def "After we deleting an attribute, the subject area return the proper list"() {
		when:  service.delete(competencyAttributeId)
			attributeIdsToDelete.removeAt(0)
			placementAuditService.deleteAuditRows("history_competency_attributes", "competency_attribute_id",  competencyAttributeId)
		then:  def retrieved = service.get( competencyAttributeId)
			retrieved == null
		where: competencyAttributeId << attributeIdsToDelete.get(0)
	}

	def "After we updating a attribute, we can retrieve the previous version"() {
		when:  def created = makeCompetencyAttribute( competencyAttribute)
			PlacementRevision revision = placementAuditService.getCurrentRevision();
			def original =  generator.copyCompetencyAttributesViewDto(created)
			competencyAttribute.setHighestLevelReadingCourse("teste1234")
			service.upsert(created)
		then:
			def retrieved = (CompetencyAttributes)placementAuditService.getByIdAndRevision(CompetencyAttributes.class, created.competencyAttributeId , revision.getId())
			competencyAttributesAssembler.assembleDto(retrieved) == original

		where: competencyAttribute << generator.randomCompetencyAttributesViewDto("ENGLISH")
	}

	def cleanupSpec() {
  		attributeIdsToDelete.each {
			service.delete( it)
			placementAuditService.deleteAuditRows("history_competency_attributes", "competency_attribute_id", it)
		}

		context.close()
	}

	
}
