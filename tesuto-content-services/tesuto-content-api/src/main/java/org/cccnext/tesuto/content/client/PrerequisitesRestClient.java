package org.cccnext.tesuto.content.client;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.PrerequisitesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PrerequisitesRestClient extends BaseRestClient<ScopedIdentifier> implements PrerequisitesService {


	@Value("${content.http.port}")
	private Integer httpPort;

	@Value("${content.server.port}")
	private Integer serverPort;

	@Value("${content.server.context}")
	private String context;

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
		return "assessment-items";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public void set(ScopedIdentifier assessmentIdentifier, Collection<ScopedIdentifier> prerequisites) {
		throw new NotImplementedException("set has not been implemented in PrerequisitesRestClient");
	}

	@Override
	public Set<ScopedIdentifier> get(ScopedIdentifier assessmentIdentifier) {
		throw new NotImplementedException("get has not been implemented in PrerequisitesRestClient");
	}

}
