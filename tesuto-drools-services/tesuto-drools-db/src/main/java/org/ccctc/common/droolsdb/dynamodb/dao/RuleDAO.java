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
package org.ccctc.common.droolsdb.dynamodb.dao;

import java.util.List;

import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RuleDAO extends CrudRepository<Rule, String>{

    List<Rule> findByEngine(String application);
    
    List<Rule> findByEngineAndStatus(String application, String status);
    
    List<Rule> findByFamilyAndStatus(String family, String status);
    
    List<Rule> findByStatus(String status);
    
    List<Rule> findByFamily(String cccMisCode);
    
    List<Rule> findByCompetencyMapDiscipline(String competencyMapDiscipline);
    
    List<Rule> findByCategory(String category);
    
    List<Rule> findByEvent(String event);
    
    List<Rule> findByEngineAndIdIn(String application, List<String> ids);
        
    List<Rule> findByStatusAndIdIn(String status, List<String> ids);
    
    List<Rule> findByFamilyAndIdIn(String cccMisCode, List<String> ids);
    
    List<Rule> findByCompetencyMapDisciplineAndIdIn(String competencyMapDiscipline, List<String> ids);
    
    List<Rule> findByCategoryAndIdIn(String category, List<String> ids);
    
    List<Rule> findByEventAndIdIn(String event, List<String> ids);
    
    List<Rule> findByIdIn(List<String> ids);
    
}
