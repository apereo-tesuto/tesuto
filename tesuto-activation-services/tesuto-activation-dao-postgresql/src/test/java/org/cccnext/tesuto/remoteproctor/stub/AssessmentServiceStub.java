/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.remoteproctor.stub;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;

import java.util.List;
import java.util.UUID;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class AssessmentServiceStub implements AssessmentService {
    @Override
    public AssessmentDto create(AssessmentDto assessmentDto) {
        return null;
    }

    @Override
    public List<AssessmentDto> create(List<AssessmentDto> assessmentDtos) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public AssessmentDto read(String id) {
        return null;
    }

    @Override
    public AssessmentMetadataDto readLatestPublishedVersionMetadata(ScopedIdentifier identifier) {
        return null;
    }

    @Override
    public AssessmentDto readLatestPublishedVersion(ScopedIdentifier identifier) {
    	AssessmentDto dto = new AssessmentDto();
    	dto.setNamespace(identifier.getNamespace());
    	dto.setIdentifier(identifier.getIdentifier());
    	dto.setVersion(1);
    	dto.setId(UUID.randomUUID().toString());
        return null;
    }

    @Override
    public List<AssessmentDto> readAllRevisions(String namespace, String identifier) {
        return null;
    }

    @Override
    public AssessmentDto readLatestPublishedVersion(String namespace, String identifier) {
        return null;
    }

    @Override
    public List<AssessmentDto> read() {
        return null;
    }

    @Override
    public List<AssessmentDto> readPublishedUnique() {
        return null;
    }

    @Override
    public AssessmentDto generateLinearAssessmentFromAssessmentItems(List<AssessmentItemDto> assessmentItems, String namespace) {
        return null;
    }

    @Override
    public int getNextVersion(String namespace, String identifier) {
        return 0;
    }

    @Override
    public Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished) {
        return null;
    }

    @Override
    public List<AssessmentDto> readPublishedUniqueForUserAndLocation(String userId, String locationId) {
        return null;
    }

    @Override
    public List<AssessmentDto> read(ScopedIdentifier identifier) {
        return null;
    }

    @Override
    public AssessmentViewDto readViewDto(String identifier, String namespace) {
        return null;
    }

    @Override
    public List<Integer> readVersions(ScopedIdentifier identifier) {
        return null;
    }

    @Override
    public AssessmentDto readVersion(ScopedIdentifier identifier, int version) {
        return null;
    }
    
    @Override
    public List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline) {
        return null;
    }
    
    
    @Override
    public List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline, String partialIdentifier) {
        return null;
    }

}
