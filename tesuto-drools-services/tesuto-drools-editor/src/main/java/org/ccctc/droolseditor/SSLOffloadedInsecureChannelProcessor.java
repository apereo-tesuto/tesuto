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
package org.ccctc.droolseditor;

import org.apache.commons.lang3.StringUtils;


import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.AbstractRetryEntryPoint;
import org.springframework.security.web.access.channel.ChannelEntryPoint;
import org.springframework.security.web.access.channel.InsecureChannelProcessor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Alex Bragg <abragg@unicon.net>
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Slf4j
@Service("insecureChannelProcessor")
public class SSLOffloadedInsecureChannelProcessor extends InsecureChannelProcessor {

	private ChannelEntryPoint channelEntryPoint = new FullyQualfiedRetryWithHttpEntryPoint();

	@Override
	public void decide(FilterInvocation filterInvocation, Collection<ConfigAttribute> configAttributes) throws IOException, ServletException {
		if (channelShouldBeInsecure(configAttributes)) {
			if (!isInsecureChannel(filterInvocation)) {
				redirectRequestToInsecureChannel(filterInvocation);
			}
		}
	}

	private boolean channelShouldBeInsecure(Collection<ConfigAttribute> configAttributes) {
		boolean channelShouldBeInsecure = false;
		for (ConfigAttribute configAttribute : configAttributes) {
			if (supports(configAttribute)) {
				channelShouldBeInsecure = true;
			}
		}
		return channelShouldBeInsecure;
	}

	private boolean isInsecureChannel (FilterInvocation filterInvocation) {
		String protocol = filterInvocation.getRequest().getHeader("x-forwarded-proto");
		boolean insecureChannel = false;
		if (StringUtils.isBlank(protocol)) {
			insecureChannel = true;
		}
		if ("http".equalsIgnoreCase(protocol)) {
			insecureChannel = true;
		}
		return insecureChannel;
	}

	private void redirectRequestToInsecureChannel(FilterInvocation filterInvocation) throws IOException, ServletException {
		log.debug("Redirecting request to insecure channel: {}", filterInvocation.getRequest().getRequestURI());
		channelEntryPoint.commence(filterInvocation.getRequest(), filterInvocation.getResponse());
	}

	private class FullyQualfiedRetryWithHttpEntryPoint extends AbstractRetryEntryPoint {
		public FullyQualfiedRetryWithHttpEntryPoint() {
			super("http://", 80);
		}

		@Override
		protected Integer getMappedPort(Integer mapFromPort) {
			Integer mappedPort = getPortMapper().lookupHttpPort(mapFromPort);
			if (mappedPort == null) {
				mappedPort = mapFromPort;
			}
			return mappedPort;
		}
	}
}
