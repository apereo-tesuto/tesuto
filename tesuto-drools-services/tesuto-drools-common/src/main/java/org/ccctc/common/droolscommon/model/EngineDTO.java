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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EngineDTO {
    private String artifactId;
    
    private Set<String> categories;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date created;
    
    private String dataSource;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date edited;
    
    private Set<String> events;
    
    private String groupId;
    
    private String name;

    private String status;
    
    private String version;
    
    public void adjustValuesForStorage() {
        name = StringUtils.isBlank(name) ? null : name;
        dataSource = StringUtils.isBlank(dataSource) ? null : dataSource;
        groupId = StringUtils.isBlank(groupId) ? null : groupId;
        artifactId = StringUtils.isBlank(artifactId) ? null : artifactId;
        version = StringUtils.isBlank(version) ? null : version;
        status = StringUtils.isBlank(status) ? null : status;
    }

    public void adjustValuesForUI() {
        name = name == null ? "" : name;
        dataSource = dataSource == null ? "" : dataSource;
        groupId = groupId == null ? "" : groupId;
        artifactId = artifactId == null ? "" : artifactId;
        version = version == null ? "" : version;
        status = status == null ? "" : status;
        categories = categories == null ?new HashSet<String>() : categories;
        events = events == null ?new HashSet<String>() : events;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getDataSource() {
        return dataSource;
    }

    public Date getEdited() {
        return this.edited;
    }

    public Set<String> getEvents() {
        return events;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public EngineDTO setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public EngineDTO setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public void setEvents(Set<String> events) {
        this.events = events;
    }

    public EngineDTO setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public EngineDTO setName(String name) {
        this.name = name;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EngineDTO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("name:[" + name + "]")
            .append(", dataSource:[" + dataSource + "]")
            .append(", groupId:[" + groupId + "]")
            .append(", artifactId:[" + artifactId + "]")
            .append(", version:[" + version + "]")
            .append(", status:[" + status + "]");
        return buf.toString();
    }
}
