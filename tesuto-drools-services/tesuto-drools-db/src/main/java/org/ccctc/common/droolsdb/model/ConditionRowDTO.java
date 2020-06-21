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
package org.ccctc.common.droolsdb.model;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConditionRowDTO {
    private String publishedObject;
    private String publishedObjectField;
    private String condition;
    private String value1;
    private String value2;
    private String publishedName;
    private String freetext;
    private Set<String> tokensFromFreeText;

    public String getPublishedObject() {
        return publishedObject;
    }

    public ConditionRowDTO setPublishedObject(String publishedObject) {
        this.publishedObject = publishedObject;
        return this;
    }

    public String getPublishedObjectField() {
        return publishedObjectField;
    }

    public ConditionRowDTO setPublishedObjectField(String publishedObjectField) {
        this.publishedObjectField = publishedObjectField;
        return this;
    }

    public String getCondition() {
        return condition;
    }

    public ConditionRowDTO setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public String getValue1() {
        return value1;
    }

    public ConditionRowDTO setValue1(String value1) {
        this.value1 = value1;
        return this;
    }

    public String getValue2() {
        return value2;
    }

    public ConditionRowDTO setValue2(String value2) {
        this.value2 = value2;
        return this;
    }

    public String getPublishedName() {
        return publishedName;
    }

    public ConditionRowDTO setPublishedName(String publishedName) {
        this.publishedName = publishedName;
        return this;
    }

    public Set<String> getTokensFromFreeText() {
        return tokensFromFreeText;
    }

    public void setTokensFromFreeText(Set<String> tokensFromFreeText) {
        this.tokensFromFreeText = tokensFromFreeText;
    }

    public String getFreetext() {
        return freetext;
    }

    public ConditionRowDTO setFreetext(String freetext) {
        this.freetext = freetext;
        return this;
    }

    public String toString() {
        return "[" + publishedObject + "." + publishedObjectField + ":" + condition + ":" + value1 + ":" + value2 + "]";
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
