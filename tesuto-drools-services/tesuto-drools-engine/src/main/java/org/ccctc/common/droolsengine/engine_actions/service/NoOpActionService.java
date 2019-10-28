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
package org.ccctc.common.droolsengine.engine_actions.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccctc.common.droolscommon.RulesAction;

public class NoOpActionService implements IActionService {

    public static final String NAME = "NOOP";
    
    public Set<String> getRequiredParameters() {
        return new HashSet<String>();
    }
    
    public String getName() {
        return NAME;
    }

    public List<String> execute(RulesAction action, Map<String, Object> parameters) {
        // NOOP
        return new ArrayList<String>();
    }

    public final List<String> verifyRequiredParameters(RulesAction action) {
        return new ArrayList<String>();
    }

}
