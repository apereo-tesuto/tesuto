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
package org.cccnext.tesuto.activation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

public class MongoActivationIndexManager {

    @Autowired
    MongoTemplate template;

    void init() {
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("userId", Sort.Direction.ASC).
                on("startDate", Sort.Direction.ASC).
                on("endDate", Sort.Direction.ASC));
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("userId", Sort.Direction.ASC).
                on("assessmentId", Sort.Direction.ASC).
                on("status", Sort.Direction.ASC));
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("userId", Sort.Direction.ASC).
                on("locationId", Sort.Direction.ASC).
                on("status", Sort.Direction.ASC));
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("userId", Sort.Direction.ASC).
                on("status", Sort.Direction.ASC));
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("locationId", Sort.Direction.ASC).
                on("startDate", Sort.Direction.ASC).
                on("endDate", Sort.Direction.ASC).
                on("status", Sort.Direction.ASC));
        template.indexOps(Activation.class).ensureIndex((new Index()).
                on("creatorId", Sort.Direction.ASC).
                on("createDate", Sort.Direction.ASC));

    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }
}
