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
package org.ccctc.droolseditor.services;

import static org.junit.Assert.assertEquals;

import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.droolseditor.TestConfig;
import org.ccctc.droolseditor.UITestConfig;
import org.ccctc.droolseditor.services.DecisionTreeViewMapper;
import org.ccctc.droolseditor.services.DynamoDBTest;
import org.ccctc.droolseditor.services.RuleTemplateViewMapper;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes={TestConfig.class, UITestConfig.class })
public class ViewMapperTest extends DynamoDBTest {

	private final static String DEFAULT_DECISION_TREE = "/decisionTreeView.json";
	
    private final static String DEFAULT_RULETEMPLATE = "/ruleTemplateView.json";
	
	@Autowired
    private DecisionTreeViewMapper decisionTreeViewMapper;
	
	@Autowired
    private RuleTemplateViewMapper ruleTemplateViewMapper;
	
	
    @Test
    public void decisionTreeViewMapper() {
    	 DecisionTreeView  decisionTreeView = readObjectFromFile(DEFAULT_DECISION_TREE,  DecisionTreeView.class);
    	 //Must use rulesetRow as transformation does not maintain column order for
    	 RuleSetRowDTO rulesetRow = decisionTreeViewMapper.doMapFrom(decisionTreeView);
    	 RuleSetRowDTO mappedRulesetRow =  decisionTreeViewMapper.mapFrom(decisionTreeViewMapper.mapTo(rulesetRow));
    	 if(!decisionTreeView.equals(mappedRulesetRow)) {
    		assertEquals("Variable Rows are different", rulesetRow.getVariableRows(), mappedRulesetRow.getVariableRows());
    		assertEquals("Validation Csv", rulesetRow.getValidationCsv(), mappedRulesetRow.getValidationCsv());
      	   assertEquals("Unequal metadata",rulesetRow, mappedRulesetRow);
    	 }
    }
    
    @Test
    public void ruleTemplateMapper() {
    	RuleTemplateView  ruleTemplateView = readObjectFromFile(DEFAULT_RULETEMPLATE, RuleTemplateView.class);
    	RuleTemplateView mappedRuleTemplateView=  ruleTemplateViewMapper.mapTo(ruleTemplateViewMapper.mapFrom(ruleTemplateView));
    	ruleTemplateView.adjustValuesForUI();
       if(!ruleTemplateView.equals(mappedRuleTemplateView)){
    	   assertEquals("Unequal Definitions",ruleTemplateView.getDefinitions(),mappedRuleTemplateView.getDefinitions());
    	   assertEquals("Unequal Imports",ruleTemplateView.getImports(),mappedRuleTemplateView.getImports());
    	   assertEquals("Unequal Then Clause",ruleTemplateView.getThenClause(),mappedRuleTemplateView.getThenClause());
    	   assertEquals("Unequal When Statement",ruleTemplateView.getWhenStatement(),mappedRuleTemplateView.getWhenStatement());
    	   assertEquals("Unequal metadata",ruleTemplateView, mappedRuleTemplateView);
       }
    }
    
  
}

