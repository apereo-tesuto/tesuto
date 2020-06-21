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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.EAGER;

/**
 * Created by bruce on 12/15/16.
 */

@Entity
@DiscriminatorValue("TestEvent")
public class JpaTestEventActivation extends JpaActivation {

    @ManyToOne(optional=false, fetch=EAGER)
    @JoinColumn(name="test_event_id")
    private JpaTestEvent testEvent;

    public JpaTestEvent getTestEvent() {
        return testEvent;
    }

    public void setTestEvent(JpaTestEvent testEvent) {
        this.testEvent = testEvent;
    }
}
