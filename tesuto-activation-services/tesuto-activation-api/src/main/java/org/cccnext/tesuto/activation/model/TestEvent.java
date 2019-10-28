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
package org.cccnext.tesuto.activation.model;

import java.util.Date;
import java.util.Set;

import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TestEvent {

    private int testEventId;
    private String name;
    private Date startDate;
    private Date endDate;
    private String collegeId;
    private String testLocationId;
    private DeliveryType deliveryType;
    private String proctorFirstName;
    private String proctorLastName;
    private String proctorEmail;
    private String proctorPhone;
    private Set<ScopedIdentifier> assessmentScopedIdentifiers;
    private Date createDate;
    private String createdBy;
    private Date updateDate;
    private String updatedBy;
    private String remotePasscode;
    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

}
