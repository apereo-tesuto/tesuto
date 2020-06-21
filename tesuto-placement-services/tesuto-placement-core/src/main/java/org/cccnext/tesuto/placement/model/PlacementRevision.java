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
package org.cccnext.tesuto.placement.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.cccnext.tesuto.placement.service.PlacementRevisionListener;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(schema="public",name = "history_placement_revision")
@RevisionEntity(PlacementRevisionListener.class)
public class PlacementRevision extends DefaultRevisionEntity {
    
    @Column(name = "user_account_id")
    private String userAccountId;

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }
    
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(schema="public",name = "revchanges", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entityname")
    @Fetch(FetchMode.JOIN)
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames = new HashSet<>();

    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !(o instanceof PlacementRevision) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final PlacementRevision that = (PlacementRevision) o;

        if ( modifiedEntityNames != null ? !modifiedEntityNames.equals( that.modifiedEntityNames )
                : that.modifiedEntityNames != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (modifiedEntityNames != null ? modifiedEntityNames.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlacementRevision(" + super.toString() + ", modifiedEntityNames = " + modifiedEntityNames + ")";
    }

}
