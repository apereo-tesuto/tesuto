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

import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.SELF_REPORTED;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.VERIFIED;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.CALPAS_SOURCE;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.APPLY_SOURCE;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class MultipleMeasureFactProcessingUtils {

    @Qualifier("operationalDataStore")
    @Autowired
    private OperationalDataStoreService ods;
    
    
    public static String MATH_RANKING_KEY = "math_ranking";
    
    public VariableSet chooseVariableSet(String cccId, String misCode, Boolean selfReportedOptIn) {
        log.info(String.format("Fetching variable set for student %s", cccId));
        Student student = ods.fetchStudent(cccId);
        log.info(String.format("Student fetched  with id %s", cccId));
               
        if(student == null || CollectionUtils.isEmpty( student.getVariableSets())){
            log.info(String.format("VariableSets were empty for student  %s", cccId));
            return null;
        }

        log.info(String.format("Fetching variable set for student with id %s", cccId));
        Optional<VariableSet> variableSet = student.getVariableSets().stream().filter(vs -> (selfReportedOptIn || !SELF_REPORTED.equals(vs.getSourceType()))
                && (vs.getMisCode() == null || vs.getMisCode().equals(misCode))).max(variableSetComparator);
        if(variableSet.isPresent()) {
            log.info(String.format("Variable set found for student with id %s", cccId));
            return ods.fetchFacts(cccId, variableSet.get());
        }
        return null;
    }
    
    public static void addMathRanking(Map<String, Fact> facts) {
        Map<String, Double> classRankings = mathClassRankings();
        Double ranking = -1.0;
        Fact rankingFact = null;
     
        if (facts.get("stat_ap") != null && facts.get("stat") != null) {
            if (parseDouble(facts.get("stat_ap").getValue()) > parseDouble(facts.get("stat").getValue())) {
                facts.put("stat", facts.get("stat_ap"));
            }
        }

        for (String key : classRankings.keySet()) {
            if (facts.containsKey(key)) {
                rankingFact = facts.get(key);
                Double grade = StringUtils.isBlank(rankingFact.getValue()) ? null
                        : Double.parseDouble(rankingFact.getValue());
                if (grade != null) {
                    if( ranking < classRankings.get(key) + (grade / 10))
                        ranking = classRankings.get(key) + (grade / 10);
                }
            }
        }
        if(ranking >= 0.0) {
            Fact rankedFact = new Fact();
            rankedFact.setName(MATH_RANKING_KEY);
            rankedFact.setValue(Double.toString(ranking));
            rankedFact.setSourceDate(new Date());
            rankedFact.setSource(rankingFact.getSource());
            rankedFact.setSourceType(rankingFact.getSourceType());
            facts.put(MATH_RANKING_KEY, rankedFact);
        }
    }

    private static Double parseDouble(String value) {
        if (StringUtils.isBlank(value)) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    protected static Map<String, Double> mathClassRankings() {

        return Collections.unmodifiableMap(new LinkedHashMap<String, Double>() {
            {
                put("calc", 6.0);
                put("calc_ap", 6.0);
                put("pre_calc", 5.0);
                put("pre_calc_ap", 5.0);
                put("trig", 4.0);
                put("trig_ap", 4.0);
                put("geo", 3.0);
                put("geo_ap", 3.0);
                put("stat", 2.0);
                put("stat_ap", 2.0);
                put("alg_ii", 2.0);
                put("alg_ii_ap", 2.0);
                put("alg_i", 1.0);
                put("alg_i_ap", 1.0);
                put("pre_alg", 0.0);
                put("pre_alg_ap", 0.0);
            }
        });
    }
    
    private Comparator<VariableSet> variableSetComparator = new Comparator<VariableSet>() {
        public int compare(VariableSet vs1, VariableSet vs2) {
            if (VERIFIED.equals(vs1.getSourceType()) && SELF_REPORTED.equals(vs2.getSourceType()))
                return 1;
            else if (SELF_REPORTED.equals(vs1.getSourceType()) && VERIFIED.equals(vs2.getSourceType()))
                return -1;
            else if (CALPAS_SOURCE.equals(vs1.getSource()) && !CALPAS_SOURCE.equals(vs2.getSource()))
                return 1;
            else if (!CALPAS_SOURCE.equals(vs1.getSource()) && CALPAS_SOURCE.equals(vs2.getSource()))
                return -1;
            else if (vs1.getSourceDate() == null && vs2.getSourceDate() == null) {
                if (vs1.getCreateDate() == null && vs2.getCreateDate() == null) {
                    return 0;
                }
                else if (vs1.getCreateDate() == null && vs2.getCreateDate() != null)
                    return 1;
                else if (vs1.getCreateDate() != null && vs2.getCreateDate() == null)
                    return -1;
                else
                    return vs1.getCreateDate().compareTo(vs2.getCreateDate());
            }
            else if (vs1.getSourceDate() == null && vs2.getSourceDate() != null)
                return 1;
            else if (vs1.getSourceDate() != null && vs2.getSourceDate() == null)
                return -1;
            else
                return vs1.getSourceDate().compareTo(vs2.getSourceDate());
        }
    };
}
