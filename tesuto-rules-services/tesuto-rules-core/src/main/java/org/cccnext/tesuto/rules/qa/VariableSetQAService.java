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
package org.cccnext.tesuto.rules.qa;

import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.MATH_RANKING_KEY;
import static org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils.addMathRanking;
import static org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService.SELF_REPORTED;

import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.rules.service.ApplyFactMapper;
import org.cccnext.tesuto.rules.service.MultipleMeasureFactProcessingUtils;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class VariableSetQAService  {
    
    @Autowired
    ApplyFactMapper factMapper;

    @Qualifier("operationalDataStore")
    @Autowired
    private OperationalDataStoreService service;
    
    @Autowired
    MultipleMeasureFactProcessingUtils mmFactUtils;

 
    @Value("classpath:cal-pas-variable-sets.csv")
    private Resource calPasResources;
    
    @Value("classpath:apply-self-reported-variable-sets.csv")
    private Resource selfReportedResources;
    
    @Value("classpath:erp-cal-pas-variable-sets.csv")
    private Resource erpCalpasResources;
   
    
    public VariableSet createVariableSet(VariableSet variableSet) {
        String cccId = null;
        
        if( variableSet.getFacts().containsKey("cccId")) {
            cccId = variableSet.getFacts().get("cccId").getValue();
        } else if( variableSet.getFacts().containsKey("cccid")) {
            cccId = variableSet.getFacts().get("cccid").getValue();
        } else {
            cccId = variableSet.getFacts().get("ccc_id").getValue();
        }
        
        String cccMisCode = null;
        if( variableSet.getFacts().containsKey("college_id")) {
            cccMisCode = variableSet.getFacts().get("college_id").getValue();
        } else  if( variableSet.getFacts().containsKey("cccMisCode")){
            cccMisCode = variableSet.getFacts().get("cccMisCode").getValue();
        }
        upsertStudent(cccId) ;
        service.addVariableSet(cccId, variableSet);
        return getExpectedVariableSet( cccId, cccMisCode, false);
    }
    
    public VariableSet createVariableSetFromMap(Map<String,String> facts) {
        VariableSet variableSet = new VariableSet();
        upsertStudent(getValue("cccId", facts)) ;
        String cccMisCode = getValue("cccMisCode", facts);
        String sourceType = getValue("sourceType", facts) ;
        String source = getValue("source", facts) ;
        variableSet.setId(UUID.randomUUID().toString());
        variableSet.setMisCode(cccMisCode);
        variableSet.setSource(source);
        variableSet.setSourceDate(new Date());
        variableSet.setSourceType(sourceType);
        Map<String,Fact> variableSetFacts = new HashMap<>();
        for(String key:facts.keySet()) {
            variableSetFacts.put(key, getFact( key,  facts.get(key),  source,  sourceType));
        }
        variableSet.setFacts(variableSetFacts);
        
        service.addVariableSet(getValue("cccId", facts), variableSet);
        
        return getExpectedVariableSet( getValue("cccId", facts), cccMisCode, false);
    }
    
    private String getValue(String key, Map<String,String> facts) {
        if(facts.containsKey(key)) {
            return facts.get(key);
        } else {
            throw new PoorlyFormedRequestException("Missing value for key:" + key);
        }
    }

    public VariableSet createSelfReportedVariableSet(String cccId, String cccMisCode, Integer row) throws Exception {
        Iterator<CSVRecord> rows =  injestCSV(selfReportedResources) ;
        
        upsertStudent(cccId);
        VariableSet variableSet = buildVariableSet( cccId,  cccMisCode,   OperationalDataStoreService.APPLY_SOURCE, OperationalDataStoreService.SELF_REPORTED,  row,   rows );
        service.addVariableSet(cccId, variableSet);

        return getExpectedVariableSet( cccId, cccMisCode, true);
    }
    
    public VariableSet createErpCalpassVariableSet(String cccId, String cccMisCode, Integer row) throws Exception {
        Iterator<CSVRecord> rows =  injestCSV(erpCalpasResources) ;
        
        upsertStudent(cccId);
        VariableSet variableSet = buildVariableSet( cccId,  cccMisCode,    OperationalDataStoreService.CALPAS_SOURCE, OperationalDataStoreService.VERIFIED,  row,   rows );
        service.addVariableSet(cccId, variableSet);

        return getExpectedVariableSet( cccId, cccMisCode, true);
    }
    
    public VariableSet createCalPasVariableSet(String cccId, String cccMisCode,  Integer row) throws Exception {
        Iterator<CSVRecord> rows =  injestCSV(calPasResources) ;
        upsertStudent(cccId);
        VariableSet variableSet = buildVariableSet( cccId,  cccMisCode,    OperationalDataStoreService.CALPAS_SOURCE, OperationalDataStoreService.VERIFIED,  row,   rows );
        service.addVariableSet(cccId, variableSet);
        return getExpectedVariableSet( cccId, cccMisCode, false);
    }

    public VariableSet getExpectedVariableSet(String cccId, String micCode, Boolean selfReportedOptIn) {
        return mmFactUtils.chooseVariableSet(cccId, micCode, selfReportedOptIn);
    }
    
    public VariableSet getRulesVariableSet(String cccId, String variableSetId) {
        return normalizeVariableSet(service.fetchVariableSetById(cccId, variableSetId ));
    }
    
    public VariableSet getRulesVariableSet(String cccId, String micCode, Boolean selfReportedOptIn) {
        VariableSet variableSet =  getExpectedVariableSet( cccId, micCode, selfReportedOptIn) ;
        return normalizeVariableSet(variableSet) ;
    }
    
    private VariableSet normalizeVariableSet(VariableSet variableSet) {
        if(variableSet == null) {
            return variableSet;
        }
        if(SELF_REPORTED.equals(variableSet.getSourceType())){
            variableSet.getFacts().putAll(factMapper.mapFacts(variableSet.getFacts()));
        }
        if(!variableSet.getFacts().containsKey(MATH_RANKING_KEY)) {
            addMathRanking(variableSet.getFacts());
        }
        return variableSet;
    }
    
    private void upsertStudent(String cccId) {
        if (service.fetchStudent(cccId) == null) {
            Student student = new Student();
            student.setCccId(cccId);
            student.setSsId(UUID.randomUUID().toString());
            service.createStudent(student);
        }
    }
    
    private VariableSet buildVariableSet(String cccId, String cccMisCode,  String factsSource,  String factsType, Integer row,  Iterator<CSVRecord> rows ) throws Exception {
         CSVRecord headers = rows.next();
        int index = 0;
        CSVRecord desiredRow = null;
        while (rows.hasNext() && row > index++) {
            desiredRow = rows.next();
        }
        if(desiredRow != null)
           return  buildVariableSet(cccId,   cccMisCode, factsSource, factsType, headers.iterator(), desiredRow.iterator());
        else
            throw new NotFoundException("SELF_REPORTED S variable set not found for row " + row);
    }
    
    private VariableSet buildVariableSet(String cccid,  String miscode,String factsSource, String sourceType, Iterator<String> headers, Iterator<String> values) {
        VariableSet variableSet = new VariableSet();
        variableSet.setMisCode(miscode);
        variableSet.setId(UUID.randomUUID().toString());
        variableSet.setSource(factsSource);
        variableSet.setCreateDate(new Date());
        variableSet.setSourceDate(new Date());
        variableSet.setSourceType(sourceType);
        Map<String, Fact> facts = new HashMap<>();
        variableSet.setFacts(facts);

        while (headers.hasNext() && values.hasNext()) {
            Fact fact = getFact(headers.next(), values.next(), factsSource, sourceType);
            facts.put(fact.getName(), fact);
        }

        return variableSet;
    }

    private Iterator<CSVRecord> injestCSV(Resource variableSets) throws Exception {
        Reader in = new StringReader(IOUtils.toString(variableSets.getInputStream(), "UTF-8"));
        CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
        Iterator<CSVRecord> rows = parser.getRecords().iterator();
        parser.close();
        return rows;
    }
   
    
    private Fact getFact(String name, String value, String source, String sourceType) {
        Fact fact = new Fact();
        fact.setName(name);
        fact.setSource(source);
        fact.setSourceDate(new Date());
        fact.setSourceType(sourceType);
        fact.setValue(value);
        return fact;
    }
}
