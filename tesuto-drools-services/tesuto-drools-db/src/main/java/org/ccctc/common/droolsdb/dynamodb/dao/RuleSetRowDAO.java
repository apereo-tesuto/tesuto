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

import org.ccctc.common.droolsdb.dynamodb.model.RuleSetRow;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RuleSetRowDAO extends CrudRepository<RuleSetRow, String> {

    List<RuleSetRow> findByEngine(String application);
    
    List<RuleSetRow> findByEngineAndStatus(String application, String status);
    
    List<RuleSetRow> findByStatus(String status);
    
    List<RuleSetRow> findByFamilyAndStatus(String cccMisCode);
    
    List<RuleSetRow> findByCompetencyMapDiscipline(String competencyMapDiscipline);
    
    List<RuleSetRow> findByCategory(String category);
    
    List<RuleSetRow> findByEvent(String event);
    
    List<RuleSetRow> findByEngineAndIdIn(String application, List<String> ids);
        
    List<RuleSetRow> findByStatusAndIdIn(String status, List<String> ids);
    
    List<RuleSetRow> findByFamilyAndStatusAndIdIn(String cccMisCode, List<String> ids);
    
    List<RuleSetRow> findByCompetencyMapDisciplineAndIdIn(String competencyMapDiscipline, List<String> ids);
    
    List<RuleSetRow> findByCategoryAndIdIn(String category, List<String> ids);
    
    List<RuleSetRow> findByEventAndIdIn(String event, List<String> ids);
    
    List<RuleSetRow> findByRuleId(String ruleId);
    
    List<RuleSetRow> findByRuleIdAndIdIn(String ruleId, List<String> ids);
    
    List<RuleSetRow> findByIdIn(List<String> ids);

}
