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
package org.ccctc.common.droolscommon.validation;

public class DrlValidationData {
	
	private String drl;
	
	private String  validationCsv;
	
	private Boolean csvValidationRequired;
	
	public String getDrl() {
		return drl;
	}

	public void setDrl(String drl) {
		this.drl = drl;
	}

	public String getValidationCsv() {
		return validationCsv;
	}

	public void setValidationCsv(String validationCsv) {
		this.validationCsv = validationCsv;
	}

	public Boolean getCsvValidationRequired() {
		return csvValidationRequired;
	}

	public void setCsvValidationRequired(Boolean csvValidationRequired) {
		this.csvValidationRequired = csvValidationRequired;
	}
}
