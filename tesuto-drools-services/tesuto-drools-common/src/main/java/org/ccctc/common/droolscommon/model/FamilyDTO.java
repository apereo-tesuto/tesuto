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
package org.ccctc.common.droolscommon.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FamilyDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date created;
    
    private String description;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date edited;
    
    private Map<String, EngineDTO> engines;

    private String familyCode;
    
    private String id;

    private String status;

    @Deprecated
    public Map<String, EngineDTO> getEngines() {
        if (this.engines == null) {
            this.engines = new HashMap<String, EngineDTO>();
        }
        return this.engines;
    }
    
    @Deprecated
    public String getFamily() {
        return familyCode;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getDescription() {
        return description;
    }
    
    public Date getEdited() {
        return this.edited;
    }

    public EngineDTO getEngineDTO(String engineName) {
        return this.getEngineDTOs().get(engineName);
    }

    public Map<String, EngineDTO> getEngineDTOs() {
        if (this.engines == null) {
            this.engines = new HashMap<String, EngineDTO>();
        }
        return this.engines;
    }

    public String getFamilyCode() {
        return familyCode;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
    
    @Deprecated
    public FamilyDTO setFamily(String cccMisCode) {
        this.familyCode = cccMisCode;
        return this;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public FamilyDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public FamilyDTO setEngines(Map<String, EngineDTO> engines) {
        this.engines = engines;
        return this;
    }
    
    public FamilyDTO setFamilyCode(String code) {
        this.familyCode = code;
        return this;
    }

    public FamilyDTO setId(String id) {
        this.id = id;
        return this;
    }

    public FamilyDTO setStatus(String status) {
        this.status = status;
        ;
        return this;
    }

    public String toString() {
        return "[" + id + ":" + familyCode + ":" + description + ":" + status + "], engines:[" + engines + "]";
    }
}
