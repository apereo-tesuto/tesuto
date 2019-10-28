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

import org.ccctc.common.droolsdb.dynamodb.model.RuleSet;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RuleSetDAO extends CrudRepository<RuleSet, String> {
    
    List<RuleSet> findByEngine(String application);
    
    List<RuleSet> findByEngineAndStatus(String application, String status);
    
    List<RuleSet> findByStatus(String status);
        
    List<RuleSet> findByCompetencyMapDiscipline(String competencyMapDiscipline);
    
    List<RuleSet> findByCategory(String category);
    
    List<RuleSet> findByEvent(String event);
    
    List<RuleSet> findByEngineAndIdIn(String application, List<String> ids);
        
    List<RuleSet> findByStatusAndIdIn(String status, List<String> ids);
    
    List<RuleSet> findByFamilyAndIdIn(String cccMisCode, List<String> ids);
    
    List<RuleSet> findByCompetencyMapDisciplineAndIdIn(String competencyMapDiscipline, List<String> ids);
    
    List<RuleSet> findByCategoryAndIdIn(String category, List<String> ids);
    
    List<RuleSet> findByEventAndIdIn(String event, List<String> ids);
    
    List<RuleSet> findByEngineAndStatusAndFamily(String application, String status, String cccMisCode);
    
    List<RuleSet> findByFamily(String cccMisCode);
    
    List<RuleSet> findByEngineAndFamily(String application, String cccMisCode);
    
    List<RuleSet> findByStatusAndFamily(String status, String cccMisCode);
    
    List<RuleSet> findByIdIn(List<String> ids);
}
