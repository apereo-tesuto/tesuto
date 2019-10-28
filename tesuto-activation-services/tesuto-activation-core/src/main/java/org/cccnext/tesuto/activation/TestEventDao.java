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

import java.util.Set;

import org.cccnext.tesuto.activation.model.TestEvent;

/**
 * Created by bruce on 11/16/16.
 */
public interface TestEventDao {

    void acknowledge(TestEvent testEvent);

    TestEvent create(TestEvent testEvent);

    void delete(int testEventId);

    TestEvent find(int testEventId);

    TestEventWithUuid findWithUuid(int testEventId);

    Set<TestEvent> findByCollegeId(String collegeId);

    TestEvent findByUuid(String uuid);

    void update(TestEvent testEvent);
}
