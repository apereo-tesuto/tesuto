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
package org.cccnext.tesuto.delivery.model.internal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Document(collection = "ActivityLog")
public class ActivityLog {

    @Id
    private String id;
    private String eName;
    @Field(value = "data")
    private ActivityLogData activityLogData;
    private String uId;
    private String asId;
    private String ctId;
    private String t;
    private String time;
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public ActivityLogData getActivityLogData() {
        return activityLogData;
    }

    public void setActivityLogData(ActivityLogData activityLogData) {
        this.activityLogData = activityLogData;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getAsId() {
        return asId;
    }

    public void setAsId(String asId) {
        this.asId = asId;
    }

    public String getCtId() {
        return ctId;
    }

    public void setCtId(String ctId) {
        this.ctId = ctId;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("eName: ").append(eName).append("\n");
        sb.append("activityLogData: \n").append(activityLogData).append("\n");
        sb.append("uId: ").append(uId).append("\n");
        sb.append("asId: ").append(asId).append("\n");
        sb.append("ctId: ").append(ctId).append("\n");
        sb.append("t: ").append(t).append("\n");
        sb.append("time: ").append(time).append("\n");
        sb.append("userId: ").append(userId).append("\n");
        // sb.append("data: ").append(activityLogData == null ? null :
        // activityLogData.toString()).append("\n");
        return sb.toString();
    }
}
