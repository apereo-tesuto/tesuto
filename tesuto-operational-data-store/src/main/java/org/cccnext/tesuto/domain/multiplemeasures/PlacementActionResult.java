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
package org.cccnext.tesuto.domain.multiplemeasures;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class PlacementActionResult extends ActionResult {

    private static final long serialVersionUID = 1L;

    private Character cb21Code;
    private Integer courseGroup;
    private Integer subjectAreaId;
    private Integer subjectAreaVersion;
    private Date createdOn;
    private Set<String> placementComponentIds;
    private String placementId;
    private String elaIndicator;
    
    public Character getCb21Code() {
        return cb21Code;
    }

    public void setCb21Code(Character cb21Code) {
        this.cb21Code = cb21Code;
    }

    public Integer getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(Integer courseGroup) {
        this.courseGroup = courseGroup;
    }

    public Integer getSubjectAreaId() {
        return subjectAreaId;
    }

    public void setSubjectAreaId(Integer subjectAreaId) {
        this.subjectAreaId = subjectAreaId;
    }

    public Integer getSubjectAreaVersion() {
        return subjectAreaVersion;
    }

    public void setSubjectAreaVersion(Integer subjectAreaVersion) {
        this.subjectAreaVersion = subjectAreaVersion;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    
    public Set<String> getPlacementComponentIds() {
        if (placementComponentIds == null) {
            placementComponentIds = new HashSet<>();
        }
        return placementComponentIds;
    }

    public void setPlacementComponentIds(Set<String> placementComponentIds) {
        this.placementComponentIds = placementComponentIds;
    }

    
    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getElaIndicator() {
        return elaIndicator;
    }

    public void setElaIndicator(String elaIndicator) {
        this.elaIndicator = elaIndicator;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
