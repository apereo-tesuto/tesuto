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
package org.cccnext.tesuto.content.model.item.interaction;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentSimpleAssociableChoice extends AssessmentInteraction {
    private static final long serialVersionUID = 1L;
    private Integer matchMax;
    private Integer matchMin = 0;
    private String identifier;
    private String content;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMatchMax() {
        return matchMax;
    }

    public void setMatchMax(Integer matchMax) {
        this.matchMax = matchMax;
    }

    public Integer getMatchMin() {
        return matchMin;
    }

    public void setMatchMin(Integer matchMin) {
        this.matchMin = matchMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AssessmentSimpleAssociableChoice that = (AssessmentSimpleAssociableChoice) o;

        if (matchMax != null ? !matchMax.equals(that.matchMax) : that.matchMax != null)
            return false;
        if (matchMin != null ? !matchMin.equals(that.matchMin) : that.matchMin != null)
            return false;
        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null)
            return false;
        return !(content != null ? !content.equals(that.content) : that.content != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (matchMax != null ? matchMax.hashCode() : 0);
        result = 31 * result + (matchMin != null ? matchMin.hashCode() : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
