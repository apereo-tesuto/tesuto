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

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema="public", name="placement_component_notification")
public class PlacementComponentNotification {

    @Id
    @GeneratedValue
    @Column(name="placement_component_notification_id")
    private Integer placementComponentNotificationId;

    @Column(name="placement_component_id")
    private String placementComponentId;

    @Column(name="notification_sent")
    private Date notificationSent;

    @Column(name="notification_success")
    private boolean success;

    public Integer getPlacementComponentNotificationId() {
        return placementComponentNotificationId;
    }

    public void setPlacementComponentNotificationId(Integer placementComponentNotificationId) {
        this.placementComponentNotificationId = placementComponentNotificationId;
    }

    public String getPlacementComponentId() {
        return placementComponentId;
    }

    public void setPlacementComponentId(String placementComponentId) {
        this.placementComponentId = placementComponentId;
    }

    public Date getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Date notificationSent) {
        this.notificationSent = notificationSent;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
