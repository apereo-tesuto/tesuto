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
import java.util.Map;

/**
 * Created by bruce on 4/5/17.
 */
public class VariableSet {
    private String id;
    private String misCode;
    private Date createDate;
    private String source;
    private String sourceType;
    private Date sourceDate;
    private Map<String,Fact> facts; //Null unless part of a Student that was fetched "with facts"

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Date getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(Date sourceDate) {
        this.sourceDate = sourceDate;
    }

    public Map<String, Fact> getFacts() {
        return facts;
    }

    public void setFacts(Map<String, Fact> facts) {
        this.facts = facts;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this,o, "facts");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "facts");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
