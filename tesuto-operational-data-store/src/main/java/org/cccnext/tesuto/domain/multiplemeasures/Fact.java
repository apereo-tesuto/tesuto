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

public class Fact {
    private String name;
    private String value;
    private String source;  //enum?
    private String sourceType; //enum?
    private Date sourceDate;

    public Fact() {}

    public Fact(Fact old) {
        name = old.name;
        value = old.value;
        source = old.source;
        sourceType = old.sourceType;
        sourceDate = old.sourceDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    @Override
    //exclude sourceDate for convenience in building unit tests
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this,o, "sourceDate");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "sourceDate");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
