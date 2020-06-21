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
package org.cccnext.tesuto.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@Service
public class UrlService {

	@Value("${ui.service.base.url}")
	String ui;
	
	@Value("${placement.service.base.url}")
	String placement;
	
	@Value("${rules.service.base.url}")
	String rules;

	@Value("${content.service.base.url}")
	String content;
	
	@Value("${delivery.service.base.url}")
	String delivery;
	
	@Value("${activation.service.base.url}")
	String activation;
	
	@Value("${admin.service.base.url}")
	String admin;
	
	@Value("${qti.import.service.base.url}")
	String qtiImport;
	
	@Value("${service.base.suffix:ServiceBaseUrl}")
	private String suffix;
	
	@Value("${preview.service.base.url}")
	private String preview;
	
	@Value("${reports.service.base.url}")
	private String reports;

	public String getUI() {
		return ui;
	}

	public String getPlacement() {
		return placement;
	}

	public String getRules() {
		return rules;
	}

	public String getContent() {
		return content;
	}

	public String getDelivery() {
		return delivery;
	}

	public String getActivation() {
		return activation;
	}

	public String getAdmin() {
		return admin;
	}

	public String getQtiImport() {
		return qtiImport;
	}
	
	public String getSuffix() {
		return suffix;
	}

	public String getPreview() {
		return preview;
	}

	public String getReports() {
		return reports;
	}

	

	public void addBaseUrls(Model model) {
		model.addAttribute(getAttributeName("activation"), activation);
		model.addAttribute(getAttributeName("admin"), admin);
		model.addAttribute(getAttributeName("content"), content);
		model.addAttribute(getAttributeName("delivery"), delivery);
		model.addAttribute(getAttributeName("placement"), placement);
		model.addAttribute(getAttributeName("preview"), preview);
		model.addAttribute(getAttributeName("qtiImport"), qtiImport);
		model.addAttribute(getAttributeName("reports"), reports);
		model.addAttribute(getAttributeName("rules"), rules);
		model.addAttribute(getAttributeName("status"), ui);
		model.addAttribute(getAttributeName("ui"), ui);
	}
	
	private String getAttributeName(String name) {
		return name + suffix;
	}
}
