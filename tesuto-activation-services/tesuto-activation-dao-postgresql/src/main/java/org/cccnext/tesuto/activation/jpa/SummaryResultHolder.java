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

import org.cccnext.tesuto.activation.model.Activation;

/**
 * Created by bruce on 5/19/16.
 */
public class SummaryResultHolder {
    private String locationId;
    private Activation.Status status;
    private int count;

    public SummaryResultHolder(String locationId, Activation.Status  status, long count) {
        this.locationId = locationId;
        this.status = status;
        this.count = (int)count;
    }

    public String getLocationId() {
        return locationId;
    }

    public Activation.Status getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "SummaryResultHolder{" +
                "locationId=" + locationId +
                ", status=" + status +
                ", count=" + count +
                '}';
    }
}
