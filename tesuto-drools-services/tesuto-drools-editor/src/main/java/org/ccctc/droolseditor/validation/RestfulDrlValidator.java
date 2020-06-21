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
package org.ccctc.droolseditor.validation;

public class RestfulDrlValidator extends AbstractRestfulDrlValidator {

	private String event;
	
	private String tagetUrl;
	
	@Override
	public String getEvent() {
		return event;
	}

	@Override
	public String getTargetUrl() {
		return tagetUrl;
	}

	public String getTagetUrl() {
		return tagetUrl;
	}

	public void setTagetUrl(String tagetUrl) {
		this.tagetUrl = tagetUrl;
	}

	public void setEvent(String event) {
		this.event = event;
	}
}
