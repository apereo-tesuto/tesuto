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
package org.cccnext.tesuto.service.multiplemeasures;

import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;

import java.util.Map;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface VariableSetParserService {

    /**
     * Given a string representation of a json object containing variable set data,
     * parse this json into a new {@link VariableSet} object.
     *
     * @param misCode
     * @param cccId
     * @param json the json to parse to a variable set
     * @return a VariableSet containing the parsed json facts
     */
    VariableSet parseJsonToVariableSet(String misCode, String cccId, String json);
}
