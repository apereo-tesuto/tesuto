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

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;

import java.util.List;

/**
 * Created by jasonbrown on 6/22/16.
 */
public interface CompetencyService {

    public List<CompetencyDto> create(List<CompetencyDto> competencyMapDtos);

    public void delete(String id);

    public CompetencyDto read(String id);

    public CompetencyDto readLatestPublishedVersion(ScopedIdentifier identifier);

    public CompetencyDto readByDisciplineIdentifierAndVersion(String discipline, String identifier, int version);
    
    public default CompetencyDto readLatestPublishedVersion(String discipline, String identifier) {
        return readLatestPublishedVersion(new ScopedIdentifier(discipline, identifier));
    }

    public List<CompetencyDto> read();

    public List<CompetencyDto> readPublishedUnique();

    public int getNextVersion(String namespace, String identifier);

    public Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished);

    List<CompetencyDto> read(ScopedIdentifier identifier);
    
    List<CompetencyDto> readAll(List<String> ids);
}
