package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto
import org.cccnext.tesuto.content.service.AssessmentService
import org.springframework.stereotype.Service
import org.cccnext.tesuto.content.model.ScopedIdentifier;

/**
 * Created by bruce on 12/4/15.
 */

@Service
class AssessmentServiceStub implements AssessmentService {

    def assessments = [:]

    @Override
    AssessmentDto create(AssessmentDto assessmentDto) {
        assessments[assessmentDto.id] = assessmentDto
        return assessmentDto
    }

    @Override
    List<AssessmentDto> create(List<AssessmentDto> assessmentDtos) {
        assessmentDtos.each { assessments[it.id] = it }
        return assessmentDtos
    }

    @Override
    void delete(String id) {
        assessments.remove(id)
    }

    @Override
    AssessmentDto read(String id) {
        return assessments[id]
    }

    @Override
    AssessmentMetadataDto readLatestPublishedVersionMetadata(ScopedIdentifier identifier) {
        def assessment = readLatestVersion(identifier)
        return assessment?.assessmentMetadata
    }

    @Override
    AssessmentDto readLatestPublishedVersion(ScopedIdentifier scopedId) {
        def entry = assessments.find { key, assessment ->
            scopedId == assessment.scopedIdentifier
        }
        return entry?.value
    }

    @Override
    List<AssessmentDto> readAllRevisions(String namespace, String identifier) {
        return null
    }

    @Override
    AssessmentDto readLatestPublishedVersion(String namespace, String identifier) {
        return super.readLatestPublishedVersion(namespace, identifier)
    }

    @Override
    List<AssessmentDto> readPublishedUniqueForUserAndLocation(String userId, String locationId) {
        return null
    }

    @Override
    List<AssessmentDto> read() {
        return assessments.values().toList()
    }

    @Override
    AssessmentDto generateLinearAssessmentFromAssessmentItems(List<AssessmentItemDto> assessmentItems, String namespace) {
        return null
    }

    @Override
    int getNextVersion(String namespace, String identifier) {
        return 0
    }

    @Override
    Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished) {
        return null
    }
    
    @Override
     List<AssessmentDto> readPublishedUnique() {
        return assessments.values().toList()
    }
    
    
    @Override
     List<AssessmentDto> read(ScopedIdentifier identifier) {
        return assessments.values().toList()
    }
    
    @Override
    List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline){
    	return null;
    }
    
    @Override
    List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String partialIdentifier) {
    	return null;
    }

    @Override
    AssessmentViewDto readViewDto(String identifier, String namespace) {
        return null
    }

    @Override
    List<Integer> readVersions(ScopedIdentifier identifier) {
        return null
    }

    @Override
    public AssessmentDto readVersion(ScopedIdentifier identifier, int version) {
        def entry = assessments.find { key, assessment ->
            identifier == assessment.scopedIdentifier && version == assessment.version
        }
        return entry?.value
    }
}
