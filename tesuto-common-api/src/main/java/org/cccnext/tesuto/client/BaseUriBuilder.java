package org.cccnext.tesuto.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseUriBuilder {

	@Value("${tesuto.service.host}")
	String host;

	@Value("${tesuto.service.scheme}")
	String scheme;
	
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

	@SuppressWarnings("unchecked")
	public BaseUriBuilder() {
	}

	public UriComponentsBuilder endpointBuilder(String... endpoints) {
		return UriComponentsBuilder.newInstance().scheme(scheme()).host(host()).port(port()).pathSegment(context())
				.pathSegment(service()).pathSegment(version()).pathSegment(controller()).pathSegment(endpoints);
	}

	protected String service() {
		return null;
	}

	protected String version() {
		return null;
	}

	protected Integer port() {
		if (scheme().equals("http")) {
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

}
