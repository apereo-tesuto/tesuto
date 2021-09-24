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
package org.cccnext.tesuto.admin.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by jasonbrown on 8/1/16.
 */
@Entity
@Table(schema="public",name = "college_attribute")
public class CollegeAttribute implements Serializable{
    static final long serialVersionUID = 1l;

    @Id
    @Column(name="college_id")
    String collegeId;

    @Column(name="english_placement_option")
    String englishPlacementOption;

    @Column(name="esl_placement_option")
    String eslPlacementOption;

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getEnglishPlacementOption() {
        return englishPlacementOption;
    }

    public void setEnglishPlacementOption(String englishPlacementOption) {
        this.englishPlacementOption = englishPlacementOption;
    }

    public String getEslPlacementOption() {
        return eslPlacementOption;
    }

    public void setEslPlacementOption(String eslPlacementOption) {
        this.eslPlacementOption = eslPlacementOption;
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
