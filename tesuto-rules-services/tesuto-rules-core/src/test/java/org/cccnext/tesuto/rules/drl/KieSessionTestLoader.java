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
package org.cccnext.tesuto.rules.drl;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.drools.core.SessionConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

public class KieSessionTestLoader {


    public static StatelessKieSession loadKieSession(String rulesetHeader, String ruleSetId,  String ruleSetRowId, String ruleId,  String rule, String rulesetFooter, String csv) throws Exception {
    	Reader in = new StringReader(csv);
    	CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
    	Iterator<CSVRecord> rpsiterator = parser.getRecords().iterator();
    	CSVRecord header = rpsiterator.next();
    	StringBuilder ruleset = new StringBuilder(rulesetHeader);
    	Integer ruleIndex = 1;
    	while(rpsiterator.hasNext()) {
    		addRule(ruleSetId, ruleSetRowId,  ruleId, ruleIndex.toString(), ruleset, new String(rule), header.iterator(), rpsiterator.next().iterator());
    		ruleIndex++;
    	}
    	parser.close();
    	ruleset.append(rulesetFooter);
    	
    	KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        StringReader reader = new StringReader(ruleset.toString());
        kfs.write( "src/main/resources/droolsTest.drl",
                   kieServices.getResources().newReaderResource(reader) );
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieSessionConfiguration conf=  SessionConfiguration.getDefaultInstance();
        StatelessKieSession ksession = kieContainer.newStatelessKieSession(conf);
        
        if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
            List<Message> errors = kieBuilder.getResults().getMessages(Level.ERROR);
            StringBuilder sb = new StringBuilder("Errors:");
            for (Message msg : errors) {
                sb.append("\n  " + msg);
            }
            throw new Exception(sb.toString());
        }
        
        return ksession;
    }

    public static StatelessKieSession loadKieSession(String rulesetHeader, String rule, String rulesetFooter, String csv) throws Exception {
    	return loadKieSession(rulesetHeader, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), rule, rulesetFooter, csv);
    }
    
    private static void addRule(String ruleSetId, String ruleSetRowId, String ruleId, String rowNumber, StringBuilder ruleset, String rule, Iterator<String> headers, Iterator<String> ruleParamSet) {
    	rule = rule.replace("${rule_set_id}", ruleSetId);
    	rule = rule.replace("${rule_set_row_id}", ruleSetRowId);
    	rule = rule.replace("${rule_id}", ruleId);
    	rule = rule.replace("${row_number}", rowNumber);
    	while(headers.hasNext()) {
    		rule = rule.replace("${" + headers.next() + "}", ruleParamSet.next());
    	}
    	ruleset.append("\n" + rule);
    	return;
    }
}
