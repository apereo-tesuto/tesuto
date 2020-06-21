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
package org.cccnext.tesuto.activation;

import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.content.model.DeliveryType;

import java.util.Date;

/**
 * Created by bruce on 12/15/16.
 */
public class TestEventActivation extends Activation {

    private TestEvent testEvent;

    public TestEvent getTestEvent() {
        return testEvent;
    }

    public void setTestEvent(TestEvent testEvent) {
        this.testEvent = testEvent;
    }

    @Override
    public String getLocationId() {
        return testEvent.getTestLocationId();
    }

    @Override
    public Date getStartDate() {
        return testEvent.getStartDate();
    }

    @Override
    public Date getEndDate() {
        return testEvent.getEndDate();
    }

    @Override
    public DeliveryType getDeliveryType() {
        return testEvent.getDeliveryType();
    }
}
