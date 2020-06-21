package org.cccnext.tesuto.web.client;

import org.cccnext.tesuto.client.BaseUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebUIUriBuilder extends BaseUriBuilder {
	
	@Value("${ui.jsp.http.port}")
	private Integer httpPort;

	@Value("${ui.jsp.server.port}")
	private Integer serverPort;

	@Value("${ui.jsp.server.context}")
	private String context;
	
	@Value("${ui.jsp.service.scheme}")
	String webScheme;

	@Override
	protected Integer httpPort() {
		return httpPort;
	}

	@Override
	protected Integer serverPort() {
		return serverPort;
	}

	@Override
	protected String controller() {
		return null;
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public String getScheme() {
		return webScheme;
	}

	@Override
	public void setScheme(String scheme) {
		this.webScheme = scheme;
	}
	
	@Override
	protected String scheme() {
		return webScheme;
	}
	
	public String buildAssessmentUrl(String assessmentSessionId) {
		return endpointBuilder("assessment").queryParam("assessmentSessionId", assessmentSessionId).toUriString();
	}
	
	public String buildAssessmentPrintUrl(String assessmentSessionId) {
		return endpointBuilder("assessment").queryParam("assessmentSessionId", assessmentSessionId).toUriString();
	}
	


}
