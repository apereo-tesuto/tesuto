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
package org.cccnext.tesuto.client.dto;

import java.io.Serializable;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

public class RestClientTestResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String className;
	private String methodName;
	private String endpoint;
	private String httpCallMethod;
	private Double elapsedTime;
	private String httpStatus;
	private String message;
	private String resultBody;
	private Boolean isCalledByMicroservice;
	
	private Long duration = System.nanoTime();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getHttpCallMethod() {
		return httpCallMethod;
	}

	public void setHttpCallMethod(String httpCallMethod) {
		this.httpCallMethod = httpCallMethod;
	}

	public String getResultBody() {
		return resultBody;
	}

	public void setResultBody(String resultBody) {
		this.resultBody = resultBody;
	}
	
	public Boolean getIsCalledByMicroservice() {
		return isCalledByMicroservice;
	}

	public void setIsCalledByMicroservice(Boolean isCalledByMicroservice) {
		this.isCalledByMicroservice = isCalledByMicroservice;
	}

	public RestClientTestResult() {
		
	}

	public RestClientTestResult(Class type, String methodName, UriComponentsBuilder builder, HttpMethod method) {
		this.className = type.getSimpleName();
		this.methodName = methodName;
		this.endpoint = builder.toUriString();
		this.httpCallMethod = method.name();
	}

	public RestClientTestResult complete(String httpStatus) {
		duration = System.nanoTime() - duration;
		setElapsedTime(duration/1E09);
		this.httpStatus = httpStatus;
		return this;
	}
	
	public RestClientTestResult complete(String httpStatus, String message, String result) {
		duration = System.nanoTime() - duration;
		setElapsedTime(duration/1E09);
		this.httpStatus = httpStatus;
		this.resultBody = result;
		this.message = message;
		return this;
	}

	public RestClientTestResult complete(String httpStatus, String errorMessage) {
		duration = (System.nanoTime() - duration);
		setElapsedTime(duration/1E09);
		this.httpStatus = httpStatus;
		this.message = errorMessage;
		return this;
	}

}
