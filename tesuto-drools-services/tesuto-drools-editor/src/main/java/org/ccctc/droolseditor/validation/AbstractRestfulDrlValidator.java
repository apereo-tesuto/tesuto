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
package org.ccctc.droolseditor.validation;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.validation.DrlValidationData;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolscommon.validation.IDrlValidator;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AbstractRestfulDrlValidator implements IDrlValidator {

	private int maxRetryCount = 5;

	private Integer retrySleepInMillis = 10000;

	private RestTemplate restTemplate = null;

	@Override
	public DrlValidationResults validate(String drl) {
		return validate(drl, "", false);
	}
	
	@Override
	public DrlValidationResults validate(String drl, String validationCSV) {
		return validate(drl, validationCSV,  true);
	}

	public DrlValidationResults validate(String drl, String validationCSV, Boolean csvValidationRequired) {
		log.debug("MAX_RETRY_COUNT:[" + maxRetryCount + "], delay:[" + retrySleepInMillis + "]");
		DrlValidationResults results = new DrlValidationResults();
		results.setIsValid(false);

		results.setDrl(drl);
		String targetUrl = getTargetUrl();

		if (StringUtils.isBlank(targetUrl)) {
			results.addError("TargetURL cannot be blank");
			results.setExceptionMessage("Unable to process [" + this.getEvent() + "] due to null REST call Target URL");
			log.error(results.getExceptionMessage());
			return results;
		}

		HttpHeaders headers = buildHeaders();

		if (CollectionUtils.isNotEmpty(results.getErrors())) {
			results.setExceptionMessage(
					"Unable to process [" + this.getEvent() + "] due to Header configuration errors");
			log.error(results.getExceptionMessage());
			return results;
		}

		DrlValidationData validationData = new DrlValidationData();
		validationData.setDrl(results.getDrl());
		validationData.setValidationCsv(validationCSV);
		validationData.setCsvValidationRequired(csvValidationRequired);
		HttpEntity<Object> entity = new HttpEntity<Object>(validationData, headers);

		boolean success = false;
		int retryCount = 0;
		while (!success && retryCount++ < maxRetryCount) {
			try {
				ResponseEntity<DrlValidationResults> response = restTemplate.exchange(targetUrl, HttpMethod.POST,
						entity, DrlValidationResults.class);
				if (!hasValidHttpStatus(response.getStatusCode())) {
					results.addError("[" + getEvent() + "] REST Request sent, EXCEPTION THROWN ["
							+ response.getStatusCode() + "]");
					sleep(retrySleepInMillis);
				} else {
					success = true;
					DrlValidationResults validationResponse = response.getBody();
					if (validationResponse.getIsValid()) {
						results.setIsValid(true);
					} else {
						results.setExceptionMessage(validationResponse.getExceptionMessage());
						results.setExceptionTrace(validationResponse.getExceptionTrace());
						if (CollectionUtils.isNotEmpty(validationResponse.getErrors())) {
							results.getErrors().addAll(validationResponse.getErrors());
						}
					}
				}
			} catch (Exception exception) {
				results.addError("[" + getEvent() + "] REST Request sent, returned status code of ["
						+ exception.getMessage() + "], but should be [Accepted or OK]");
			}

		}
		return results;
	}

	Boolean hasValidHttpStatus(HttpStatus code) {
		return code.equals(HttpStatus.ACCEPTED) || code.equals(HttpStatus.OK);
	}

	public void sleep(int sleepDuration) {
		log.debug("Sleeping for [" + sleepDuration + "] milliseconds");
		try {
			Thread.sleep(sleepDuration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.debug("finished sleeping");
	}

	/**
	 * Must be defined. Sets the target URL to be called by the REST call.
	 * 
	 * @return
	 */
	public abstract String getTargetUrl();

	public HttpHeaders buildHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public void setRetrySleepInMillis(Integer retrySleepInMillis) {
		this.retrySleepInMillis = retrySleepInMillis;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

}
