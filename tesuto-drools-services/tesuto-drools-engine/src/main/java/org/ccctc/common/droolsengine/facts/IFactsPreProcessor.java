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

import java.util.List;
import java.util.Map;

/**
 * IFactsPreProcessor is an interface that defines the contract for all pre-processing (validations, decorating, enhancing, etc)
 * that are performed against facts before the facts enter the rules engine. Validating the data prior to presenting to the engine
 * makes it is possible to log if there are errors and to prevent the rules engine from executing when there is no possibility 
 * of rules being matched.
 */
public interface IFactsPreProcessor {
    /**
     * getName() returns the name of the processor.  The name will be used when
     * determining if the processor should be used for a specific engine.
     * @return
     */
    String getName();

    /**
     * processFacts(...) examines the facts and pre-processes the information. Possible things that might be done:
     * <li>ensure that the needed information is present
     * <li>add any missing data to the facts if desired and if the data is available.
     * <li>decorate the facts with common details
     * @param facts
     * @return List<String> of any errors that occurred.  If the list is not
     * empty, it should is assumed that the facts are not suitable for running
     * any rules.  If there are no errors, an empty list is returned.
     */
    List<String> processFacts(Map<String, Object> facts);

    /**
     * isEnabled() tells the application if this processor is enabled Whether the processing should be performed is up to the
     * class to implement.
     * @return true if the processing should be performed, otherwise false.
     */
    boolean isEnabled(Map<String, Object> facts);
}
