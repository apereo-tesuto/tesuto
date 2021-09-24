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
package org.cccnext.tesuto.content.model;

import java.io.Serializable;

import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

/**
 * Created by bruce on 1/8/16.
 */

/**
 * Contains the information necessary to lookup the latest version of a content
 * object (assessment or item).
 */
public class ScopedIdentifier implements Serializable {

    private static final long serialVersionUID = -7227197022218670650L;
    private String namespace;
    private String identifier;

    public ScopedIdentifier() {
    }
    
    public ScopedIdentifier(ScopedIdentifierDto dto) {
        namespace = dto.getNamespace();
        identifier = dto.getIdentifier();
    }

    public ScopedIdentifier(String namespace, String identifier) {
        this.namespace = namespace;
        this.identifier = identifier;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ScopedIdentifier))
            return false;

        ScopedIdentifier that = (ScopedIdentifier) o;

        if (getNamespace() != null ? !getNamespace().equals(that.getNamespace()) : that.getNamespace() != null)
            return false;
        return getIdentifier() != null ? getIdentifier().equals(that.getIdentifier()) : that.getIdentifier() == null;

    }

    @Override
    public int hashCode() {
        int result = getNamespace() != null ? getNamespace().hashCode() : 0;
        result = 31 * result + (getIdentifier() != null ? getIdentifier().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScopedIdentifier{" + "namespace='" + namespace + '\'' + ", identifier='" + identifier + '\'' + '}';
    }

    public String scopedTag() {
        return namespace + ":" + identifier;
    }

    public String scopedTag(String seperator) {
        return namespace + seperator + identifier;
    }

}
