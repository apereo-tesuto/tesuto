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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.domain.util.upload.CsvImportLineResult;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseRestClient<T> implements TestRestClient<T> {

	@Autowired
	protected RestOperations restTemplate;

	@Value("${tesuto.service.host}")
	String host;

	@Value("${tesuto.service.scheme}")
	String scheme;
	
	
	@Value("#{${microservice.rest.clients.dependencies:null}}")
	Map<String, List<String>> restClientsDependencies;
	
	@Override
	public Map<String, List<String>> microserviceRestClientDependencies() {
		return restClientsDependencies;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	private final Class<T> genericType;
		
	@SuppressWarnings("unchecked")
	public BaseRestClient() {
		this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseRestClient.class);
	}

	public UriComponentsBuilder endpointBuilder(String... endpoints) {
		return UriComponentsBuilder.newInstance().scheme(scheme()).host(host()).port(port()).pathSegment(context())
				.pathSegment(service()).pathSegment(version()).pathSegment(controller()).pathSegment(endpoints);
	}

	public String endpointUriString(String... endpoints) {
		return endpointBuilder(endpoints).toUriString();
	}
	
	public T retrieve(UriComponentsBuilder builder) {
		return retrieve(builder, HttpMethod.GET, null);
	}
	
	public Set<T> retrieveSet(UriComponentsBuilder builder) {
		return retrieveSet(builder, HttpMethod.GET, null);
	}
	
	public List<T> retrieveList(UriComponentsBuilder builder) {
		return retrieveList(builder, HttpMethod.GET, null);
	}
	

	@SuppressWarnings("rawtypes")
	public T retrieve(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			requestBody = addGenericHeaders(requestBody);
			T result = restTemplate.exchange(builder.toUriString(), method, requestBody, genericType).getBody();
			return result;
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	public Object retrieveObject(UriComponentsBuilder builder, Class type) {
		return retrieveObject( builder, HttpMethod.GET, null, type);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object retrieveObject(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody, Class type) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<?> result = restTemplate.exchange(builder.toUriString(), method, requestBody, type);
			return result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object retrieveObject(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody, ParameterizedTypeReference type) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<?> result = restTemplate.exchange(builder.toUriString(), method, requestBody, type);
			return result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String retrieveString(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			return (String)retrieveObject(builder, method, requestBody, String.class);
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	public Boolean retrieveBoolean(UriComponentsBuilder builder, String trueValue) {
		return retrieveBoolean( builder, HttpMethod.GET, null, trueValue);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean retrieveBoolean(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody, String trueValue) {
		try {
			String status = (String)retrieveObject(builder, method, requestBody, String.class);
			return status.equals(trueValue) ? new Boolean(true): new Boolean(false);
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List<T> retrieveList(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<List<T>> result = restTemplate.exchange(builder.toUriString(), method, requestBody, new ParameterizedTypeReference<List<T>>() {
			});
			return result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Set<T> retrieveSet(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<Set<T>> result = restTemplate.exchange(builder.toUriString(), method, requestBody, new ParameterizedTypeReference<Set<T>>() {
			});
			return (Set<T>)result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List<String> retrieveStringList(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<List<String>> result = restTemplate.exchange(builder.toUriString(), method, requestBody, new ParameterizedTypeReference<List<String>>() {
			});
			return result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Set<String> retrieveStringSet(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			requestBody = addGenericHeaders(requestBody);
			ResponseEntity<Set<String>> result = restTemplate.exchange(builder.toUriString(), method, requestBody, new ParameterizedTypeReference<Set<String>>() {
			});
			return result.getBody();
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public void update(UriComponentsBuilder builder, HttpMethod method, HttpEntity requestBody) {
		try {
			restTemplate.exchange(builder.toUriString(), method, requestBody, void.class);
		} catch (HttpClientErrorException ex) {
			log.error(
					errorMessage(method, builder, ex),
					ex);
		} catch (Exception ex) {
			log.error(errorServiceMessage(method, builder, ex), ex);
		}
	}


	protected String service() {
		return "service";
	}

	protected String version() {
		return "v1";
	}

	protected Integer port() {
		if (scheme.equals("http")) {
			return httpPort();
		}
		return serverPort();
	}

	protected String scheme() {
		return scheme;
	}

	protected String host() {
		if (uniqueServiceHost() == null)
			return host;
		return uniqueServiceHost();
	}

	protected abstract Integer httpPort();

	protected abstract Integer serverPort();

	protected abstract String controller();

	protected abstract String context();

	// in case microservice is hosted with unique host name
	protected String uniqueServiceHost() {
		return null;
	}
	
	public List<RestClientTestResult> validateEndpoints() {
		return new ArrayList<RestClientTestResult>();
	}
	
	public RestOperations getRestTemplate() {
		return restTemplate;
	}
	
	public Class<T> getGenericType() {
		return genericType;
	}
	
	private String errorMessage(HttpMethod method, UriComponentsBuilder builder, Exception ex) {
		return errorMessage(method, builder, ex," apparently failure caused by http error. Service may be down.");
	}
	
	private String errorServiceMessage(HttpMethod method, UriComponentsBuilder builder, Exception ex) {
		return errorMessage(method, builder, ex,"Error caused by service. Service may be down.");
	}
	
	private String errorMessage(HttpMethod method, UriComponentsBuilder builder, Exception ex, String suffix) {
		return "A " + method.toString() + " to " + builder.toUriString() + " failed on request. Error message: " + ex.getMessage() + suffix;
	}
	
	public ParameterizedTypeReference<List<T>> parameterizedListReference() {
		return new ParameterizedTypeReference<List<T>>(){};
	}
	
	
	

}
