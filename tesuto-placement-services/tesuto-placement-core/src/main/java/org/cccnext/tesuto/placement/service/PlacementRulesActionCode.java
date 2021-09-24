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
package org.cccnext.tesuto.placement.service;

/**
 * This interface of string constants should probably be moved to some other place and replaced with an ENUM.
 * I just don't like the magic action code string floating thru the code.
 */
public interface PlacementRulesActionCode {
    final static String FAILED_ON_VALIDATE = "FAILED_ON_VALIDATE";
    final static String PLACEMENT_SERVICE_DOWN = "PLACEMENT_SERVICE_DOWN";
    final static String NO_ACTIONS_FOUND = "NO_ACTIONS_FOUND";
    final static String VARIABLE_SET_NOT_FOUND = "VARIABLE_SET_NOT_FOUND";
    final static String RULE_ARE_LOADING = "Rules are currently being loaded";

}
