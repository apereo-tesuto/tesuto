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

import org.cccnext.tesuto.content.model.Assessment;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface AssessmentRepository extends CrudRepository<Assessment, String> {
    Assessment findByNamespaceAndIdentifierAndVersion(String namespace, String identifier, int version);

    List<Assessment> findByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier);

    Assessment findTopByNamespaceAndIdentifierOrderByVersionDesc(String namespace, String identifier);

    List<Assessment> findByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String identifier, Boolean isPublished);

    Assessment findTopByNamespaceAndIdentifierAndPublishedOrderByVersionDesc(String namespace, String identifier, Boolean isPublished);

    List<Assessment> findByPublished(Boolean isPublished);

    /**
     * Returns nulls for everything but version (and id?)
     * @param namespace
     * @param identifier
     * @return
     */
    @Query(value="{ 'namespace' : ?0, 'identifier' : ?1}", fields="{ 'version' : 1 }")
    List<Assessment> findVersionsByNamespaceAndIdentifier(String namespace, String identifier);

}
