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
package org.cccnext.tesuto.content.service;

import org.cccnext.tesuto.content.model.Prerequisites;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.PrerequisitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bruce on 1/7/16.
 */

@Service
public class PrerequisitesServiceImpl implements PrerequisitesService {

    @Autowired
    MongoOperations mongoOp;

    public MongoOperations getMongoOp() {
        return mongoOp;
    }

    public void setMongoOp(MongoOperations mongoOp) {
        this.mongoOp = mongoOp;
    }

    public void set(ScopedIdentifier assessmentIdentifier, Collection<ScopedIdentifier> prerequisites) {
        mongoOp.save(new Prerequisites(assessmentIdentifier, prerequisites));
    }

    public Set<ScopedIdentifier> get(ScopedIdentifier assessmentIdentifier) {
        Prerequisites prerequisites = mongoOp.findById(assessmentIdentifier, Prerequisites.class);
        if (prerequisites == null) {
            return new HashSet<>(0);
        }
        return prerequisites.getPrerequisites();
    }
}
