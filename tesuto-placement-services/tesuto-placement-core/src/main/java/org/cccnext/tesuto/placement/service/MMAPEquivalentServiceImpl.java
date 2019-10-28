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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.model.MMAPEquivalent;
import org.cccnext.tesuto.placement.repository.jpa.MMAPEquivalentRepository;
import org.cccnext.tesuto.placement.view.MMAPEquivalentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service("mmapEquivalentService")
public class MMAPEquivalentServiceImpl implements MMAPEquivalentService {

    @Autowired
    MMAPEquivalentRepository repository;

    @Autowired
    MMAPEquivalentAssembler assembler;

    public List<MMAPEquivalentDto> getMMAPEquivalentsForCompetencyMapDiscipline(String competencyMapDiscipline) {
        List<MMAPEquivalent> equivalents = repository.findByCompetencyMapDiscipline(competencyMapDiscipline.toUpperCase());
        Collections.sort(equivalents);
        return assembler.assembleDto(equivalents);
    }
}
