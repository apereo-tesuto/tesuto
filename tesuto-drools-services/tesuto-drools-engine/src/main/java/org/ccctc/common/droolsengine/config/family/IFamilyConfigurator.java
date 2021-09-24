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
package org.ccctc.common.droolsengine.config.family;

import java.util.List;

import org.ccctc.common.droolscommon.model.FamilyDTO;

/**
 * A "family" is a grouping of rule engines under one group identifier (familyId). 
 * For example, one family might be the set of rule engines for a particular college (and the familyId == cccMisCode).
 */
public interface IFamilyConfigurator {
    /**
     * @param skipRetries
     * @return The list of all DTOs that the reader has configured
     */
    public List<FamilyDTO> getFamilies(boolean skipRetries);
    
    /**
     * @param familyId
     * @return The DTO with the configuration for the family identified by the supplied id
     */
    public FamilyDTO getFamily(String familyId);

    /**
     * @return The name of the reader. For these readers, the name is not unique, but rather identifies the "type" of the reader.
     */
    public String getName();

}
