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
package org.cccnext.tesuto.content.repository.mongo;

import java.util.List;

import org.cccnext.tesuto.content.model.item.AssessmentItem;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AssessmentItemRepository extends CrudRepository<AssessmentItem, String> {
    AssessmentItem findByNamespaceAndIdentifierAndVersion(String namespace, String identifier, int version);

    List<AssessmentItem> findByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier);

    AssessmentItem findTopByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier);

    List<AssessmentItem> findByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String itemId, Boolean isPublished);

    AssessmentItem findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String itemId, Boolean isPublished);

    @Query("{$and:[{\"itemMetadata.itemBankStatusType\":\"AVAILABLE\"},{\"published\":true},{\"itemMetadata.competencies.competencyRef.competencyMapDiscipline\":?0},{\"itemMetadata.competencies.competencyRef.competencyRefId\":?1}]}")
    List<AssessmentItem> findByCompetency(String mapDiscipline, String competencyIdentifier);
}
