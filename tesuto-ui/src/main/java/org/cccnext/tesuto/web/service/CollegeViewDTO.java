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

import org.ccctc.common.droolscommon.model.FamilyDTO;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CollegeViewDTO {
    private String id;
    private String cccMisCode;
    private String description;
    private String timestamp;

    public CollegeViewDTO(FamilyDTO collegeDTO) {
        this.id = collegeDTO.getId();
        this.cccMisCode = collegeDTO.getFamilyCode();
        this.description = collegeDTO.getDescription();
        if(collegeDTO.getEdited() == null) {
        	this.timestamp = String.valueOf(collegeDTO.getCreated() == null ? 
            		0L : collegeDTO.getEdited().getTime());
        } else {
        	this.timestamp =  String.valueOf(collegeDTO.getEdited().getTime());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFamily() {
        return cccMisCode;
    }

    public void setFamily(String cccMisCode) {
        this.cccMisCode = cccMisCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
