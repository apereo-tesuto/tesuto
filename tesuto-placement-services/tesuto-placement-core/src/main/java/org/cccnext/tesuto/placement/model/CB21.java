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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Calendar;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@AuditTable(schema="public",value = "history_cb21")
@Table(schema="public",name = "cb21")
public class CB21 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="cb21_code")
    private char cb21Code;

    @Column(name="levels_below_transfer")
    private int levelsBelowTransfer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Calendar createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_on")
    private Calendar updatedOn;

    public char getCb21Code() {
        return cb21Code;
    }

    public void setCb21Code(char cb21Code) {
        this.cb21Code = cb21Code;
    }

    public int getLevelsBelowTransfer() {
        return levelsBelowTransfer;
    }

    public void setLevelsBelowTransfer(int level) {
        this.levelsBelowTransfer = level;
    }

    public Calendar getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Calendar createdOn) {
        this.createdOn = createdOn;
    }

    public Calendar getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Calendar updatedOn) {
        this.updatedOn = updatedOn;
    }


}
