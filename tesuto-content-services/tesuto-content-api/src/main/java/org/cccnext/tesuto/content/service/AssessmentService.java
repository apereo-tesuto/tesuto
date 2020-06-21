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
package org.cccnext.tesuto.content.service;

import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AssessmentService extends AssessmentReader {

    AssessmentDto create(AssessmentDto assessmentDto);

    List<AssessmentDto> create(List<AssessmentDto> assessmentDtos);

    void delete(String id);

    AssessmentMetadataDto readLatestPublishedVersionMetadata(ScopedIdentifierDto identifier);

    AssessmentDto readLatestPublishedVersion(ScopedIdentifierDto identifier);

    List<AssessmentDto> readAllRevisions(String namespace, String identifier);

    default AssessmentDto readLatestPublishedVersion(String namespace, String identifier) {
        return readLatestPublishedVersion(new ScopedIdentifierDto(namespace, identifier));
    }

    List<AssessmentDto> read();

    List<AssessmentDto> readPublishedUnique();

    AssessmentDto generateLinearAssessmentFromAssessmentItems(List<AssessmentItemDto> assessmentItems,
            String namespace);

    int getNextVersion(String namespace, String identifier);

    Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished);

    List<AssessmentDto> readPublishedUniqueForUserAndLocation(String userId, String locationId);

    List<Integer> readVersions(ScopedIdentifier identifier);


}
