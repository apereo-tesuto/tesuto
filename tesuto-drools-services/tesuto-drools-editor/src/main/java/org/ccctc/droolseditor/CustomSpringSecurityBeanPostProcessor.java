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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.access.channel.ChannelProcessor;
import org.springframework.stereotype.Component;

/**
 * Spring Bean post context instantiation.
 *
 * @author Alex Bragg <abragg@unicon.net>
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Component
@Profile("aws")
public class CustomSpringSecurityBeanPostProcessor implements BeanPostProcessor {
	@Autowired SSLOffloadedInsecureChannelProcessor insecureChannelProcessor;
	
	@Autowired SSLOffloadedSecureChannelProcessor secureChannelProcessor;

	private void customizeChannelDecisionManager(Object bean) {
		List<ChannelProcessor> channelProcessors = new ArrayList<>();
		channelProcessors.add(insecureChannelProcessor);
		channelProcessors.add(secureChannelProcessor);

		((ChannelDecisionManagerImpl) bean).setChannelProcessors(channelProcessors);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
		if (bean instanceof ChannelDecisionManagerImpl) {
			customizeChannelDecisionManager(bean);
		}
		return bean;
	}
}
