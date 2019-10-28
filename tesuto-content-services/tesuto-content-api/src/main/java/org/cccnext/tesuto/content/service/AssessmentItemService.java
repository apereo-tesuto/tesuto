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

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;

public interface AssessmentItemService extends AssessmentItemReader {

    AssessmentItemDto create(AssessmentItemDto assessmentItem);

    List<AssessmentItemDto> create(List<AssessmentItemDto> assessmentItem);

    AssessmentItemDto readLatestVersion(String namespace, String itemId);

    List<AssessmentItemDto> readAllRevisions(String namespace, String identifier);


    List<AssessmentItemDto> read();

    int getNextVersion(String namespace, String identifier);


    /**
     * For a given identifier, authorNamespace, and version (which is unique)
     * set a boolean flag to indicate whether an item is "published" or not.
     * 
     * @param identifier
     * @param namespace
     * @param version
     * @param isPublished
     * @return
     */
    Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished);

    void delete(String id);

    

}
