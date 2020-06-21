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
package org.cccnext.tesuto.content.model.section;

import org.cccnext.tesuto.content.model.AbstractAssessment;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentSelection implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private int select;
    private Boolean withReplacement; // Not for pilot
    private String extensions; // Not for pilot

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public Boolean getWithReplacement() {
        return withReplacement;
    }

    public void setWithReplacement(Boolean withReplacement) {
        this.withReplacement = withReplacement;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extensions == null) ? 0 : extensions.hashCode());
        result = prime * result + select;
        result = prime * result + ((withReplacement == null) ? 0 : withReplacement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentSelection other = (AssessmentSelection) obj;
        if (extensions == null) {
            if (other.extensions != null)
                return false;
        } else if (!extensions.equals(other.extensions))
            return false;
        if (select != other.select)
            return false;
        if (withReplacement == null) {
            if (other.withReplacement != null)
                return false;
        } else if (!withReplacement.equals(other.withReplacement))
            return false;
        return true;
    }
}
