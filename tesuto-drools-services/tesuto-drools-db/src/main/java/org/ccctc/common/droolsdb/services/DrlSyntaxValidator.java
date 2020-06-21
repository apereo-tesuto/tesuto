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
package org.ccctc.common.droolsdb.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.exceptions.DrlInvalidSyntaxException;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;


import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DrlSyntaxValidator {
	
	
	public DrlValidationResults validate(String drl) {
		DrlValidationResults results = new DrlValidationResults();
		results.setIsValid(true);
		try {
			validateWithKieServices(drl);
		} catch(DrlInvalidSyntaxException exception) {
			results.setIsValid(false);
			results.setDrl(exception.getDrl());
			results.setExceptionMessage(exception.getMessage());
			results.setErrors(exception.getErrors());
			results.setExceptionTrace(ExceptionUtils.getFullException(exception));
		} catch(Exception exception) {
			results.setIsValid(false);
			results.setExceptionMessage(exception.getMessage());
			results.setExceptionTrace(ExceptionUtils.getFullException(exception));
		}
		return  results;
	}

	private void validateWithKieServices(String drl) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/simple.drl", ks.getResources().newReaderResource(new StringReader(drl)));
		KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();

		// check there have been no errors for rule setup
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			List<String> messages = new ArrayList<String>();
			for (Message message : results.getMessages()) {
				messages.add(message.getText());
			}
			log.error(messages.toString());
			throw new DrlInvalidSyntaxException("Error(s) Compiling DRL file", drl, messages);
		}
	}
}
