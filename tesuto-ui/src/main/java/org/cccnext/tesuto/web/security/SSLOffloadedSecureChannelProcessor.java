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
package org.cccnext.tesuto.web.security;

import org.apache.commons.lang3.StringUtils;


import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.AbstractRetryEntryPoint;
import org.springframework.security.web.access.channel.ChannelEntryPoint;
import org.springframework.security.web.access.channel.InsecureChannelProcessor;
import org.springframework.security.web.access.channel.RetryWithHttpsEntryPoint;
import org.springframework.security.web.access.channel.SecureChannelProcessor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("secureChannelProcessor")
public class SSLOffloadedSecureChannelProcessor extends SecureChannelProcessor {

	@Override
	public void decide(FilterInvocation filterInvocation, Collection<ConfigAttribute> configAttributes) throws IOException, ServletException {
		if (channelShouldBeSecure(configAttributes)) {
			if (!isSecureChannel(filterInvocation)) {
				redirectRequestToSecureChannel(filterInvocation);
			}
		}
	}

	private boolean channelShouldBeSecure(Collection<ConfigAttribute> configAttributes) {
		boolean channelShouldBeSecure = false;
		for (ConfigAttribute configAttribute : configAttributes) {
			if (supports(configAttribute)) {
				channelShouldBeSecure = true;
			}
		}
		return channelShouldBeSecure;
	}

	private boolean isSecureChannel (FilterInvocation filterInvocation) {
		String protocol = filterInvocation.getRequest().getHeader("x-forwarded-proto");
		checkForRedirectLoops(protocol);
		boolean secureChannel = false;
		if ("https".equalsIgnoreCase(protocol)) {
			secureChannel = true;
		}
		return secureChannel;
	}

	private void checkForRedirectLoops(String protocol) {
		if (StringUtils.isBlank(protocol)) {
			log.warn("Channel security is configured, but the proxy is not sending security headers.  Possible redirect loop.");
		}
	}

	private void redirectRequestToSecureChannel(FilterInvocation filterInvocation) throws IOException, ServletException {
		log.debug("Redirecting request to secure channel: {}", filterInvocation.getRequest().getRequestURI());
		getEntryPoint().commence(filterInvocation.getRequest(), filterInvocation.getResponse());
	}

}
