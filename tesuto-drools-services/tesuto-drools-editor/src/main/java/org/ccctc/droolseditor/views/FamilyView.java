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
package org.ccctc.droolseditor.views;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;



public class FamilyView {

	private String familyCode;
    private String description;
    private String id;
    private Set<String> selectedApplications;
    private String status;

    public void adjustValuesForStorage() {
        id = StringUtils.isBlank(id) ? null : id;
        familyCode = StringUtils.isBlank(familyCode) ? null : familyCode;
        description = StringUtils.isBlank(description) ? null : description;
        status = StringUtils.isBlank(status) ? null : status;
    }

    public void adjustValuesForUI() {
        id = id == null ? "" : id;
        familyCode = familyCode == null ? "" : familyCode;
        description = description == null ? "" : description;
        status = status == null ? "" : status;
        selectedApplications = selectedApplications == null ? new HashSet<String>() : selectedApplications;
    }
    
    public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getSelectedApplications() {
		return selectedApplications;
	}

	public void setSelectedApplications(Set<String> selectedApplications) {
		this.selectedApplications = selectedApplications;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
