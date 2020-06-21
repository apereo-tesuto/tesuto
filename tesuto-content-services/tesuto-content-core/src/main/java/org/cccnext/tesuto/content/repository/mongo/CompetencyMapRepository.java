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

import org.cccnext.tesuto.content.model.competency.CompetencyMap;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jasonbrown on 6/17/16.
 */
public interface CompetencyMapRepository extends CrudRepository<CompetencyMap, String> {

    public List<CompetencyMap> findByDiscipline(String discipline);

    public CompetencyMap findByDisciplineAndVersion(String discipline, int version);

    public CompetencyMap findByTitleAndVersion(String title, int version);

    public List<CompetencyMap> findByDisciplineOrderByVersionDesc(String discipline);

    public List<CompetencyMap> findByDisciplineAndPublishedOrderByVersionDesc(String discipline, Boolean isPublished);

    public List<CompetencyMap> findByPublished(Boolean isPublished);
}
