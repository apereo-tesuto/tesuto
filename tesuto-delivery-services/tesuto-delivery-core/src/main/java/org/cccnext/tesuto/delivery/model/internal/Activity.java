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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by bruce on 11/9/15.
 */
public class Activity implements Serializable {

    private static final long serialVersionUID = 1;

    private Date timestamp;
    private Map<String, Object> data;

    public Activity(Date timestamp, Map<String, Object> data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Activity))
            return false;

        Activity activity = (Activity) o;

        if (getTimestamp() != null ? !getTimestamp().equals(activity.getTimestamp()) : activity.getTimestamp() != null)
            return false;
        return !(getData() != null ? !getData().equals(activity.getData()) : activity.getData() != null);

    }

    @Override
    public int hashCode() {
        int result = getTimestamp() != null ? getTimestamp().hashCode() : 0;
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Activity{" + "timestamp=" + timestamp + ", data=" + data + '}';
    }
}
