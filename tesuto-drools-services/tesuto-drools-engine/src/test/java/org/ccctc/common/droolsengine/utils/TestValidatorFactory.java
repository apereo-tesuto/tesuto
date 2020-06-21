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
package org.ccctc.common.droolsengine.utils;

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;
import org.ccctc.common.droolsengine.facts.IPreProcessorFactory;
import org.ccctc.common.droolsengine.facts.NoOpPreProcessor;

public class TestValidatorFactory implements IPreProcessorFactory {
    private List<IFactsPreProcessor> validators = new ArrayList<IFactsPreProcessor>();

    public TestValidatorFactory() {
        validators.add(new NoOpPreProcessor());
    }
    
    @Override
    public List<IFactsPreProcessor> getFactPreProcessors() {
        return validators;
    }
}
