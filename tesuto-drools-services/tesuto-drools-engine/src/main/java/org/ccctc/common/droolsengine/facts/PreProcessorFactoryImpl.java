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
package org.ccctc.common.droolsengine.facts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This default impl aggregates the beans created by type.
 */
@Service
public class PreProcessorFactoryImpl implements IPreProcessorFactory {
    private List<IFactsPreProcessor> processors = new ArrayList<IFactsPreProcessor>();

    @Override
    public List<IFactsPreProcessor> getFactPreProcessors() {
        return processors;
    }

    @Autowired
    public void setStudentProfileFactsPreProcessor(StudentProfileFactsPreProcessor processor) {
        processors.add(processor);
    }

    @Autowired
    public void setMessagingProfileFactsPreProcessor(MessagingProfileFactsPreProcessor processor) {
        processors.add(processor);
    }
}
