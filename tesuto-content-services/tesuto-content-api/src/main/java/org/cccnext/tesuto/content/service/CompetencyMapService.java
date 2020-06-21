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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;

/**
 * Created by jasonbrown on 6/17/16.
 */
public interface CompetencyMapService extends CompetencyMapReader {

    public CompetencyMapDto create(CompetencyMapDto competencyMapDto);

    public List<CompetencyMapDto> create(List<CompetencyMapDto> competencyMapDtos);

    public void delete(String identifier);

    public CompetencyMapDto readById(String identifier);

    public List<CompetencyMapDto> read();

    public List<CompetencyMapDto> readPublishedUnique();

    public int getNextVersion(String discipline);

    public Boolean setPublishFlag(String discipline, String identifier, int version, boolean isPublished);

    List<CompetencyMapDto> read(String discipline);

    public CompetencyMapDto readByVersion(String discipline, Integer version);

    CompetencyMapDto readByTitleAndVersion(String title, Integer version);
}
