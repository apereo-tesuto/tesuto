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
package org.cccnext.tesuto.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.web.service.CollegeViewDTO;

public class DashboardSession {
	
	private List<StudentCollegeAffiliationDto> recentStudentLogins;
	
	private List<CollegeViewDTO> collegeRules;
	
	private Map<String, List<String>> microServiceProperties = new HashMap<>();
	
	private Map<String, List<RestClientTestResult>> microServiceRestClientResults = new HashMap<>();
	
	private String uiIdleTimeoutDuration;
	
	private String metrics;
	
	private String cloudwatchLogURL;
	
	private Map<String,String> microServicePropertyKeyToNodeMap = new TreeMap<>();
	
	private Map<String,String> microServiceRestClientStatus = new TreeMap<>();
	
	private List<String> errors = new ArrayList<>();

	public List<StudentCollegeAffiliationDto> getRecentStudentLogins() {
		return recentStudentLogins;
	}

	public void setRecentStudentLogins(List<StudentCollegeAffiliationDto> recentStudentLogins) {
		this.recentStudentLogins = recentStudentLogins;
	}

	public List<CollegeViewDTO> getCollegeRules() {
		return collegeRules;
	}

	public void setCollegeRules(List<CollegeViewDTO> collegeRules) {
		this.collegeRules = collegeRules;
	}

	public Map<String, List<String>> getMicroServiceProperties() {
		return microServiceProperties;
	}

	public void setMicroServiceProperties(Map<String, List<String>> microServiceProperties) {
		this.microServiceProperties = microServiceProperties;
	}

	public Map<String, String> getMicroServicePropertyKeyToNodeMap() {
		return microServicePropertyKeyToNodeMap;
	}

	public void setMicroServicePropertyKeyToNodeMap(Map<String, String> microServicePropertyKeyToNodeMap) {
		this.microServicePropertyKeyToNodeMap = microServicePropertyKeyToNodeMap;
	}

	public String getUiIdleTimeoutDuration() {
		return uiIdleTimeoutDuration;
	}

	public void setUiIdleTimeoutDuration(String uiIdleTimeoutDuration) {
		this.uiIdleTimeoutDuration = uiIdleTimeoutDuration;
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}

	public String getCloudwatchLogURL() {
		return cloudwatchLogURL;
	}

	public void setCloudwatchLogURL(String cloudwatchLogURL) {
		this.cloudwatchLogURL = cloudwatchLogURL;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public Map<String, List<RestClientTestResult>> getMicroServiceRestClientResults() {
		return microServiceRestClientResults;
	}

	public void setMicroServiceRestClientResults(Map<String, List<RestClientTestResult>> microServiceRestClientResults) {
		this.microServiceRestClientResults = microServiceRestClientResults;
	}

	public Map<String,String> getMicroServiceRestClientStatus() {
		return microServiceRestClientStatus;
	}

	public void setMicroServiceClientSummaryMap(Map<String,String> microServiceRestClientStatus) {
		this.microServiceRestClientStatus = microServiceRestClientStatus;
	}
	

}
