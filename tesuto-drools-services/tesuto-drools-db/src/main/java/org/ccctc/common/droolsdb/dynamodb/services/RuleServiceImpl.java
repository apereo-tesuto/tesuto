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
package org.ccctc.common.droolsdb.dynamodb.services;

import static org.ccctc.common.droolscommon.model.RuleStatus.DRAFT;
import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;
import static org.ccctc.common.droolscommon.model.RuleStatus.RETIRED;
import static org.ccctc.common.droolscommon.model.RuleStatus.VALID_STATUSES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolsdb.dynamodb.dao.RuleDAO;
import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleDTOMapper;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.RuleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class RuleServiceImpl implements RuleService, InitializingBean  {
    @Autowired
    private RuleDAO repository;
    
    @Autowired
    private RuleDTOMapper mapper = new RuleDTOMapper();        
    
    @Autowired
    private DynamoDBTableService tableService;
    
    @Override
	public void afterPropertiesSet() throws Exception {
    	tableService.createTable(Rule.TABLE_NAME, Rule.class);
    }
    
    @Override
    public RuleDTO getRule(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            throw new ObjectNotFoundException("Rule ID cannot be blank");
        }
        try {
            Rule rule = repository.findById(ruleId).get();
            if (rule == null) {
                throw new ObjectNotFoundException("Rule[" + ruleId + "] not found");
            }
            RuleDTO ruleDTO = mapper.mapTo(rule);
            return ruleDTO;
        }
        catch (Exception e) {
            throw new ObjectNotFoundException("Rule[" + ruleId + "] not found");
        }
    }
    
    @Override
    public List<RuleDTO> getAllRules() {
        List<Rule> rules = (List<Rule>) repository.findAll();
        List<RuleDTO> ruleDTOs = mapper.mapTo(rules);
        return ruleDTOs;
    }
    
    @Override
    public List<RuleDTO> getRulesByEngine(String application) {
        List<Rule> rules = (List<Rule>) repository.findByEngine(application);
        List<RuleDTO> ruleDTOs = mapper.mapTo(rules);
        return ruleDTOs;
    }
    
    @Override
    public List<RuleDTO> getRulesByEngineAndStatus(String application, String status) {
        List<Rule> rules = (List<Rule>) repository.findByEngineAndStatus(application, status);
        List<RuleDTO> ruleDTOs = mapper.mapTo(rules);
        return ruleDTOs;        
    }
    
    @Override
    public List<RuleDTO> getRulesByStatus(String status) {
        List<Rule> rules = (List<Rule>) repository.findByStatus(status);
        List<RuleDTO> ruleDTOs = mapper.mapTo(rules);
        return ruleDTOs;
    }
    
    @Override
    public RuleDTO save(RuleDTO ruleDTO) {
        checkDatabaseBeforeSaving(ruleDTO.getId());
        checkDTOBeforeSaving(ruleDTO);
        
        Rule rule = mapper.mapFrom(ruleDTO);
        RuleDTO  savedRule = mapper.mapTo(repository.save(rule));
        return savedRule;
    }

    @Override
    public RuleDTO duplicate(String ruleId) {
        RuleDTO ruleDTO = this.getRule(ruleId);
        ruleDTO.setId("");
        ruleDTO.setStatus(DRAFT);
        ruleDTO.setVersion("");
        RuleDTO updatedRuleDTO = this.save(ruleDTO);
        return updatedRuleDTO;
    }
    
    @Override
    public void delete(String ruleId) {
        try {
            if (!StringUtils.isBlank(ruleId)) {
                Rule rule = repository.findById(ruleId).get();
                if (rule != null) {
                    if(rule.getStatus().equals(PUBLISHED)) {
                    	rule.setStatus(RETIRED);
                        repository.save(rule);
                    } else if (rule.getStatus().equals(DRAFT)) {
                    	repository.delete(rule);
                    }
                }
            }
        }
        catch (Exception ignore) {}
    }
    
    /**
     * Checks the database for a RuleDTO that matches the rule ID and throws an exception
     * if the RuleDTO in the database cannot be saved (such as if it is deleted or published)
     * @param ruleId
     */
    public void checkDatabaseBeforeSaving(String ruleId) {
        if (StringUtils.isBlank(ruleId)) {
            return;
        }
        
        Rule existingRule = null;
        try {
            existingRule = repository.findById(ruleId).get();
        }
        catch (Exception noValuePresent) {
        }
            if (existingRule != null) {
                if (RETIRED.equals(existingRule.getStatus())) {
                    throw new SaveException("Cannot save rule[" + ruleId +"] because its status is deleted");
                }
                if (PUBLISHED.equals(existingRule.getStatus())) {
                    throw new SaveException("Cannot save rule[" + ruleId +"] because its status is published");
                }                
                // should never be able to get here, since if there is a version
                // status should be published.
                if (!StringUtils.isBlank(existingRule.getVersion())) {
                    throw new SaveException("Cannot save rule["+ ruleId + "] because it has a version[" + existingRule.getVersion() +"].  Uses duplicate to create a new rule");
                }
            }

    }
    
    /**
     * checks the fields within RuleDTO to see if it has any missing or invalid data
     * before saving to the database
     * @param ruleDTO
     */
    public void checkDTOBeforeSaving(RuleDTO ruleDTO) {
        if (StringUtils.isBlank(ruleDTO.getId())) {
            String uuid = UUID.randomUUID().toString();
            ruleDTO.setId(uuid);
        }
        if (!StringUtils.isBlank(ruleDTO.getVersion())) {
            ruleDTO.setStatus(PUBLISHED);
        }
        if (StringUtils.isBlank(ruleDTO.getStatus())) {
            ruleDTO.setStatus(DRAFT);
        }
        if (StringUtils.isBlank(ruleDTO.getEngine())) {
            throw new SaveException("Cannot save rule[" + ruleDTO.getId() + "] because application is blank");
        }
        String status = ruleDTO.getStatus();
        if (!VALID_STATUSES.contains(status)) {
            throw new SaveException("Cannot save rule[" + ruleDTO.getId() + "] because status[" + status + "] is invalid");
        }
    }
    @Override
	public List<RuleDTO> find(RuleAttributeFacetSearchForm form) {
		List<Rule> foundRows = new ArrayList<>();
		form.clean();
		if(form.getId() != null) {
			return Arrays.asList(getRule(form.getId()));
		}
		  if (form.getFamily() != null) {
	            foundRows.addAll(repository.findByFamily(form.getFamily()));
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }

	        if (form.getCompetencyMapDiscipline() != null) {
	            if(foundRows.size() > 0) {
	                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDisciplineAndIdIn(form.getCompetencyMapDiscipline(), getIds(foundRows)));
	            } else {
	                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDiscipline(form.getCompetencyMapDiscipline()));
	            }
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }

	        if (form.getEvent() != null) {
	            if(foundRows.size() > 0) {
	                foundRows = returnAnd(foundRows, repository.findByEventAndIdIn(form.getEvent(), getIds(foundRows)));
	            } else {
	                foundRows = returnAnd(foundRows, repository.findByEvent(form.getEvent()));
	            }
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }

	        if (form.getCategory() != null) {
	            if(foundRows.size() > 0) {
	                foundRows = returnAnd(foundRows, repository.findByCategoryAndIdIn(form.getCategory(), getIds(foundRows)));
	            } else {
	                foundRows = returnAnd(foundRows, repository.findByCategory(form.getCategory()));
	            }
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }

	        if (form.getStatus() != null) {
	            if(foundRows.size() > 0) {
	                foundRows = returnAnd(foundRows, repository.findByStatusAndIdIn(form.getStatus(), getIds(foundRows)));
	            } else {
	                foundRows = returnAnd(foundRows, repository.findByStatus(form.getStatus()));
	            }
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }

	        if (form.getEngine() != null) {
	            if(foundRows.size() > 0) {
	                foundRows = returnAnd(foundRows, repository.findByEngineAndIdIn(form.getEngine(), getIds(foundRows)));
	            } else {
	                foundRows = returnAnd(foundRows, repository.findByEngine(form.getEngine()));
	            }
	            if (foundRows.size() == 0) {
	                return new ArrayList<>();
	            }
	        }


		if(foundRows.isEmpty()) {
			return getAllRules();
		} else {
		    foundRows = repository.findByIdIn(getIds(foundRows));
		}
		return mapper.mapTo( foundRows);
	}
    
    private List<String> getIds(List<Rule> foundRows) {
        return foundRows.stream().map(r -> r.getId()).collect(Collectors.toList());
    }
	
	private List<Rule> returnAnd(List<Rule> foundRows, List<Rule> requiredRows) {
		//this is expecting that if foundRows is already 0 would have been previously returned
		if(foundRows.size() == 0) {
			return requiredRows;
		}
		List<String> andItems = requiredRows.stream()
			    .map(Rule::getId)
			    .collect(Collectors.toList());

			return foundRows.stream()
			            .filter(e -> andItems.contains(e.getId()))
			            .collect(Collectors.toList());
	}}
