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
package org.ccctc.common.droolsdb.services;

import java.util.List;

import org.ccctc.common.droolscommon.model.EngineDTO;

/**
 * As of v2.0.0 of the drools-engine and drools-common libraries, "applications" have been renamed/refactored as "engines"
 */
public interface IEngineService {
    public void delete(EngineDTO engineDTO);

    public EngineDTO getEngineByName(String name);
    
    public List<EngineDTO> getEngines();

    public List<EngineDTO> getEnginesByNames(Iterable<String> names);

    public List<EngineDTO> getEnginesByStatus(String status);
    
    public EngineDTO save(EngineDTO engineDTO);
}
