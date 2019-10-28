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
package org.cccnext.tesuto.activation.jpa;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.TestEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

/**
 * Created by bruce on 3/2/17.
 */
@Entity
@Table(schema="public", name="test_event_acknowledgement")
public class JpaTestEventAcknowledgement {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="acknowledgement_id")
    private int acknowledgementId;

    @Column(name="test_event_id")
    private int testEventId;

    @Column(name="acknowledgement_date")
    private Date acknowledgementDate;

    public int getAcknowledgementId() {
        return acknowledgementId;
    }

    public void setAcknowledgementId(int acknowledgementId) {
        this.acknowledgementId = acknowledgementId;
    }

    public int getTestEventId() {
        return testEventId;
    }

    public void setTestEventId(int testEventId) {
        this.testEventId = testEventId;
    }

    public Date getAcknowledgementDate() {
        return acknowledgementDate;
    }

    public void setAcknowledgementDate(Date acknowledgementDate) {
        this.acknowledgementDate = acknowledgementDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
