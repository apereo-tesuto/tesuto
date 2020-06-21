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
package org.cccnext.tesuto.rules.service;

import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.MATH_RANKING_KEY;
import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.addMathRanking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.rules.view.BasicStudentView;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class MultipleMeasureFactService implements IFactsPreProcessor, InitializingBean {

    final static String NAME = "MULTIPLE_MEASURE_VARIABLE_SET";

    @Autowired
    List<FactMapper> factMappers;
    
    @Autowired
    MultipleMeasureFactProcessingUtils mmFactUtils;


    @Value("${MULTIPLE_MEASURE_VARIABLE_SET_ENABLED}")
    private Boolean enabled;
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean isEnabled(Map<String, Object> facts) {
        if (!enabled) {
            return false;
        }
        return true;
    }

    public String getCccid(Map<String, Object> facts) {
        String cccid = (String) facts.get(PlacementMapKeys.CCCID_KEY);
        if (StringUtils.isBlank(cccid)) {
            return "";
        }
        return cccid;
    }

    
    @Override
    public List<String> processFacts(Map<String, Object> facts) {
        log.info("Validating facts with MultipleMeasureFactService");
        List<String> errors = new ArrayList<String>();

        if (!StringUtils.equals(String.valueOf( facts.get(PlacementMapKeys.EVENT_TRIGGER_KEY)), 
                PlacementMapKeys.EVENT_TRIGGER_COMPONENT)) {
            return errors;
        }

        String cccid = getCccid(facts);
        if (StringUtils.isBlank(cccid)) {
            log.error("Cannot retrieve MultipleMeasure ODS with a blank cccid");
            errors.add("Cannot retrieve MultipleMeasure ODS with a blank cccid");
            return errors;
        }
        
        if (!facts.containsKey(PlacementMapKeys.RULE_SET_ID_KEY)) {
            log.error("Cannot create Placement with a blank rule set id");
            errors.add("Cannot create Placement with a blank rule set id");
        }
        
        if(!facts.containsKey(PlacementMapKeys.MISCODE_KEY)) {
            log.error("Cannot retrieve MultipleMeasure ODS with a blank miscode");
            errors.add("Cannot retrieve MultipleMeasure ODS with a blank miscode");
            return errors;
        }
        addStudentView(facts);
       
        String misCode = (String) facts.get(PlacementMapKeys.MISCODE_KEY);
        Boolean selfReportedOptIn = Boolean.parseBoolean((String)facts.getOrDefault(PlacementMapKeys.OPT_IN_KEY, "false"));

        VariableSet vs = mmFactUtils.chooseVariableSet(cccid, misCode, selfReportedOptIn);

        if (vs != null) {       
            log.info(String.format("Variable set %s found for student with id %s", vs, cccid));
            List<FactMapper> filteredFactMappers = factMappers.stream().filter( fm -> fm.getSource().equals(vs.getSource())).collect(Collectors.toList());
           
            if(filteredFactMappers.size() == 1){
                FactMapper factMapper = filteredFactMappers.get(0);
                log.debug(String.format("Variable set %s found for student with id %s requires conversion from " + vs.getSource() + " source", vs, cccid));
                vs.getFacts().putAll(factMapper.mapFacts(vs.getFacts()));
            }
            if(!vs.getFacts().containsKey(MATH_RANKING_KEY)) {
                log.debug(String.format("Variable set %s found for student with id %s requires conversion from apply source does not contain math ranking.", vs, cccid));
                addMathRanking(vs.getFacts());
            }
            facts.put(PlacementMapKeys.MULTIPLE_MEASURE_VARIABLE_SET_KEY, vs);
        } else {
            log.info(String.format("Cannot execute RulesEngine without a Multiple Measure Variable Set for student with id %s", vs, cccid));
            errors.add(String.format("Cannot execute RulesEngine without a Multiple Measure Variable Set for student with id %s", vs, cccid));
            return errors;
        }

        return errors;
    }

    private void addStudentView(Map<String,Object> facts) {
        BasicStudentView student = new BasicStudentView();
        String misCode = (String) facts.get(PlacementMapKeys.MISCODE_KEY);
        String cccid = (String) facts.get(PlacementMapKeys.CCCID_KEY);
        student.setCccid(cccid);
        Map<String,Integer> collegeStatues = new HashMap<>();
        collegeStatues.put(misCode, 1);
        student.setCollegeStatuses(collegeStatues);
        
        facts.put(PlacementMapKeys.STUDENT_VIEW_KEY, student);
    }

}
