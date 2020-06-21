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
package org.cccnext.tesuto.client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.util.CollectionUtils;

public interface TestRestClient<T> {

	public RestOperations getRestTemplate();

	public Class<T> getGenericType();

	public Map<String, List<String>> microserviceRestClientDependencies();

	public default RestClientTestResult testRetrieve(String methodToTest, UriComponentsBuilder builder) {
		return testRetrieve(methodToTest, builder, HttpMethod.GET, null);
	}

	public default RestClientTestResult testRetrieveObject(String methodToTest, UriComponentsBuilder builder,
			Class type) {
		return testRetrieveObject(methodToTest, builder, HttpMethod.GET, null, type);
	}

	public default RestClientTestResult testRetrieveBoolean(String methodToTest, UriComponentsBuilder builder,
			String trueValue) {
		return testRetrieveBoolean(methodToTest, builder, HttpMethod.GET, null, trueValue);
	}

	public default RestClientTestResult testRetrieveSet(String methodToTest, UriComponentsBuilder builder) {
		return testRetrieveSet(methodToTest, builder, HttpMethod.GET, null);
	}

	public default RestClientTestResult testRetrieveList(String methodToTest, UriComponentsBuilder builder) {
		return testRetrieveList(methodToTest, builder, HttpMethod.GET, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default RestClientTestResult testRetrieve(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		requestBody = addGenericHeaders(requestBody);
		try {
			ResponseEntity<T> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					getGenericType());
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default RestClientTestResult testRetrieveObject(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody, Class type) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<?> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody, type);
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default RestClientTestResult testRetrieveObject(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody, ParameterizedTypeReference type) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<?> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody, type);
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default RestClientTestResult testRetrieveBoolean(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody, String trueValue) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					String.class);
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	public default RestClientTestResult testRetrieveList(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		requestBody = addGenericHeaders(requestBody);
		try {
			ResponseEntity<List<T>> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					new ParameterizedTypeReference<List<T>>() {
					});
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	public default RestClientTestResult testRetrieveSet(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<Set<T>> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					new ParameterizedTypeReference<Set<T>>() {
					});
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	public default RestClientTestResult testRetrieveStringList(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<List<String>> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					new ParameterizedTypeReference<List<String>>() {
					});
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	public default RestClientTestResult testRetrieveStringSet(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method, HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			ResponseEntity<Set<String>> result = getRestTemplate().exchange(builder.toUriString(), method, requestBody,
					new ParameterizedTypeReference<Set<String>>() {
					});
			return completeTest(testResult, result);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	public default RestClientTestResult testUpdate(String methodToTest, UriComponentsBuilder builder, HttpMethod method,
			HttpEntity requestBody) {
		RestClientTestResult testResult = buildTestResult(methodToTest, builder, method);
		try {
			requestBody = addGenericHeaders(requestBody);
			getRestTemplate().exchange(builder.toUriString(), method, requestBody, void.class);
			return completeTest(testResult);
		} catch (HttpClientErrorException ex) {
			return completeTest(testResult, ex);
		} catch (Exception ex) {
			return completeTest(testResult, ex);
		}
	}

	public default RestClientTestResult completeTest(RestClientTestResult testResult,
			@SuppressWarnings("rawtypes") ResponseEntity result) {
		String resultBody = result.getBody() != null ? result.getBody().toString() : "";
		return testResult.complete(result.getStatusCode().name(), result.getStatusCode().getReasonPhrase(), resultBody);
	}

	public default RestClientTestResult completeTest(RestClientTestResult testResult) {
		return testResult.complete("200", "void operation complete");
	}

	public default RestClientTestResult completeTest(RestClientTestResult testResult, HttpClientErrorException result) {
		return testResult.complete(result.getStatusText(), result.getStatusCode().getReasonPhrase() + "\n"
				+ result.getMessage() + "\n" + result.getResponseBodyAsString() + "\n" + stackTraceToString(result));
	}

	public default RestClientTestResult completeTest(RestClientTestResult testResult, Exception result) {
		return testResult.complete("Exception Thrown, no HttpStatus",
				result.getMessage() + "\n" + stackTraceToString(result));
	}

	public default RestClientTestResult completeTest(RestClientTestResult testResult, Object result) {
		String resultBody = result != null ? result.toString() : "";
		return testResult.complete("200", "200", resultBody);
	}

	public default RestClientTestResult buildTestResult(String methodToTest, UriComponentsBuilder builder,
			HttpMethod method) {
		RestClientTestResult result = new RestClientTestResult(getClass(), methodToTest, builder, method);
		result.setIsCalledByMicroservice(isCalledByMicroService(methodToTest));
		return result;
	}

	public default String stackTraceToString(Exception ex) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		ex.printStackTrace(ps);
		String output = new String();
		try {
			output = os.toString("UTF8");
		} catch (UnsupportedEncodingException e) {

		}
		return output;
	}

	public default HttpEntity<T> addGenericHeaders(HttpEntity requestBody) {
		if (requestBody == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
			requestBody = new HttpEntity(headers);
		}
		return requestBody;
	}

	public default HttpEntity<?> buildHttpEntity() {
		return buildHttpEntity();
	}

	public default HttpEntity<?> buildHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
		if (body == null) {
			return new HttpEntity(headers);
		}
		return new HttpEntity(body, headers);
	}

	public default Boolean isCalledByMicroService(String methodToTest) {
		// This is a bit tricky, but if a microservice has no dependencies they would not be using a RestClient so if they are requesting it means the field has not been filled in (perhaps for testing purposes)
		if (microserviceRestClientDependencies() == null || microserviceRestClientDependencies().isEmpty()) {
			return true;
		}
		if (!microserviceRestClientDependencies().containsKey(getClass().getSimpleName())) {
			return false;
		}
		return microserviceRestClientDependencies().get(getClass().getSimpleName()).contains(methodToTest) ? true : false;
	}

}
