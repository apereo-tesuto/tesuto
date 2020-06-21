package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto

/**
 * Created by bruce on 12/4/15.
 */

import org.cccnext.tesuto.content.service.AssessmentItemService
import org.springframework.stereotype.Service

@Service
public class AssessmentItemServiceStub implements AssessmentItemService {

    def items = [:]

    @Override
    AssessmentItemDto create(AssessmentItemDto assessmentItem) {
        items[assessmentItem.id] = assessmentItem
        return assessmentItem
    }

    @Override
    List<AssessmentItemDto> create(List<AssessmentItemDto> assessmentItems) {
        assessmentItems.each { items[it.id] = it }
        return assessmentItems
    }

    @Override
    AssessmentItemDto read(String id) {
        return items[id]
    }

    @Override
    AssessmentItemDto readLatestVersion(String namespace, String itemId) {
        return items[itemId]
    }

    @Override
    List<AssessmentItemDto> readAllRevisions(String namespace, String identifier) {
        return null
    }

    @Override
    AssessmentItemDto readLatestPublishedVersion(String namespace, String itemId) {
		return items[itemId]
    }

    @Override
    List<AssessmentItemDto> read() {
        return items.values().toList()
    }
    
        @Override
    List<AssessmentItemDto> getAllVersions(String namespace, String version) {
        return items.values().toList()
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
    void delete(String id) {
        items.remove(id)
    }
    
    @Override
    public AssessmentOutcomeDeclarationDto getOutcomeDeclaration(AssessmentItemDto assessmentItem, String outcomeIdentifier) {
        AssessmentOutcomeDeclarationDto result = null;
        assessmentItem.getOutcomeDeclarationDtos().each { r ->
          if(r.getIdentifier().equals(outcomeIdentifier)) {
              result = r
          }
        }
        return result;
    }
    
    @Override
    public List<AssessmentItemDto> getItemsByCompetency(String discipline, String competencyId) {
        return items.values();
    }
    
    @Override
    List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(String competencyMapDiscipline, List<String> fields) {
        return null;
    }
    
}
