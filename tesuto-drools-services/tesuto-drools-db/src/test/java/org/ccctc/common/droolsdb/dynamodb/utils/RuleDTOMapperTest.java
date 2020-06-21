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
package org.ccctc.common.droolsdb.dynamodb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.dynamodb.model.ActionParameter;
import org.ccctc.common.droolsdb.dynamodb.model.ActionRow;
import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetDTOMapper;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetRowDTOMapper;
import org.ccctc.common.droolsdb.model.ActionParameterDTO;
import org.ccctc.common.droolsdb.model.ActionRowDTO;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={TestConfig.class})
public class RuleDTOMapperTest {

	private final static String DEFAULT_RULE_NAME = "/sampleRule.json";
	
    private final static String DEFAULT_RULESETROW_NAME = "/sampleRuleSetRowForRuleSet.json";

    private final static String DEFAULT_RULESET_NAME = "/sampleRuleSetWithVersionedRule.json";
	
	@Autowired
    private RuleDTOMapper ruleMapper;
	
	@Autowired
    private RuleSetRowDTOMapper ruleSetRowMapper;
	
	@Autowired
    private RuleSetDTOMapper ruleSetMapper;
   
    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void ruleMapper() {
        RuleDTO  ruleDTO = readObjectFromFile(DEFAULT_RULE_NAME, RuleDTO.class);
       RuleDTO mappedRuleDTO =  ruleMapper.mapTo(ruleMapper.mapFrom(ruleDTO));
       assertEquals(String.format("Rule Mapping did not work correctly:  orginal: %s , mapped: %s ", ruleDTO, mappedRuleDTO), ruleDTO, mappedRuleDTO);
    }
    
    @Test
    public void ruleSetRowMapper() {
    	RuleSetRowDTO  rulesetRowDTO = readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
    	RuleSetRowDTO mappedRuleSetRowDTO =  ruleSetRowMapper.mapTo(ruleSetRowMapper.mapFrom(rulesetRowDTO));
       assertEquals(String.format("RuleSetRow Mapping did not work correctly:  orginal: %s , mapped: %s ", rulesetRowDTO, mappedRuleSetRowDTO), rulesetRowDTO, mappedRuleSetRowDTO);
    }
    
    @Test
    public void ruleSetMapper()  {
    	RuleSetDTO  rulesetDTO = readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
    	RuleSetDTO mappedRuleSetDTO =  ruleSetMapper.mapTo(ruleSetMapper.mapFrom(rulesetDTO));
       assertEquals(String.format("RuleSet Mapping did not work correctly:  orginal: %s , mapped: %s ", rulesetDTO, mappedRuleSetDTO), rulesetDTO, mappedRuleSetDTO);
    }
    
    public <T> T readObjectFromFile(String fileName, Class<T> clazz) {
        T obj = null;
        try {
            InputStream ruleDTOIs = getClass().getResourceAsStream(fileName);
            obj = mapper.readValue(ruleDTOIs, clazz);
        } catch (JsonParseException e) {
            fail(e.getMessage());
        } catch (JsonMappingException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        validateAttributes((RuleAttributesDTO)obj);
        return obj;
    }
    
    private void validateAttributes(RuleAttributesDTO attributes) {
    	if(StringUtils.isBlank(attributes.getEngine())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getCategory())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getFamily())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getCompetencyMapDiscipline())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getEvent())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getId())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getStatus())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	if(StringUtils.isBlank(attributes.getTitle())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    	
    	if(StringUtils.isBlank(attributes.getDescription())){
    		throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
    	}
    }
}
