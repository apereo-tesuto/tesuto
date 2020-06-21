package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.CompetencyGroup
import org.cccnext.tesuto.placement.model.PlacementRevision
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto

class CompetencyGroupServiceSpec extends PlacementSpecBase {

	def makeCompetencyGroup(CompetencyGroupViewDto competencyGroup) {
		def competencyGroupId = serviceCompetencyGroup.create(competencyGroup)
		competencyGroupsToDelete += competencyGroupId
		competencyGroup.setCompetencyGroupId(competencyGroupId)
		return competencyGroup
	}

	def "After creating a CompetencyGroup, we can retrieve it by id"() {
		when:  def created = makeCompetencyGroup(competencyGroup)
		then:  def retrieved = serviceCompetencyGroup.get(created.competencyGroupId)
			retrieved == created
		where: competencyGroup << [ competencyGenerator.randomCompetencyGroupViewDto(courseId) ]
	}

	def "After creating a CompetencyGroup, we can retrieve it by course id"() {
		when:  def created 
		       try {created = makeCompetencyGroup(competencyGroup)} catch(Exception e) {e.printStackTrace();throw e}
		then:  def retrievedSet = serviceCompetencyGroup.getCompetencyGroups(courseId)
			def filteredSet = retrievedSet.findAll{ it.competencyGroupId == created.competencyGroupId }
			filteredSet[0]  == created
		where: competencyGroup << [ competencyGenerator.randomCompetencyGroupViewDto(courseId) ]
	}

	def "After updating a CompetencyGroup, we can retrieve it by id"() {
		when:  def created = makeCompetencyGroup(competencyGroup)
		created.setName(competencyGenerator.randomString(1,99))
		created.setPercent(competencyGenerator.randomInt(0,100))
		created.setCompetencyIds(competencyGenerator.randomCompetencyGroupMappingViewDtos(created.getCompetencyGroupId()))
		int updatedCompetencyGroupId = serviceCompetencyGroup.upsert(created)
		then:  def retrieved = serviceCompetencyGroup.get(updatedCompetencyGroupId)
			retrieved == created
		where: competencyGroup << [ competencyGenerator.randomCompetencyGroupViewDto(courseId) ]
	}

	def "After deleting a CompetencyGroup, we can not retrieve it by id"() {
		when:  def created = makeCompetencyGroup(competencyGroup)
		serviceCompetencyGroup.delete(created.competencyGroupId)
		then:  def retrieved = serviceCompetencyGroup.get(created.competencyGroupId)
			retrieved == null
		cleanup:
			competencyGroupsToDelete -= created.competencyGroupId
			placementAuditService.deleteAuditRows("history_competency_group", "competency_group_id", created.competencyGroupId)
		where: competencyGroup << [ competencyGenerator.randomCompetencyGroupViewDto(courseId) ]
	}

	def "After updating a CompetencyGroup, we can retrieve original by revision"() {
		when:  def CompetencyGroupViewDto created = makeCompetencyGroup(competencyGroup)
		def original = competencyGenerator.copyCompetencyGroupViewDto(created)
		PlacementRevision revision = placementAuditService.getCurrentRevision();
		created.setName(competencyGenerator.randomString(1,99))
		created.setPercent(competencyGenerator.randomInt(0,100))
		created.setCompetencyIds(competencyGenerator.randomCompetencyGroupMappingViewDtos(created.getCompetencyGroupId()))
		int updatedCompetencyGroupId = serviceCompetencyGroup.upsert(created)
		then:  def CompetencyGroup retrieved = (CompetencyGroup)placementAuditService.getByIdAndRevision(CompetencyGroup.class, updatedCompetencyGroupId, revision.getId())
			CompetencyGroupViewDto retreivedView = competencyGroupAssembler.assembleDto(retrieved);
			retreivedView == original
		where: competencyGroup << [ competencyGenerator.randomCompetencyGroupViewDto(courseId) ]
	}
}
