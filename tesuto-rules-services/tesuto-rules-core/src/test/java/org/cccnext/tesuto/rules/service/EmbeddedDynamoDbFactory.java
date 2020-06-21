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
package org.cccnext.tesuto.rules.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Created by bruce on 7/13/17.
 */
public class EmbeddedDynamoDbFactory extends AbstractFactoryBean<AmazonDynamoDB> {

    @Override
    public Class<?> getObjectType() {
        return AmazonDynamoDB.class;
    }

    @Override
    protected AmazonDynamoDB createInstance() throws Exception {
        try {
            return DynamoDBEmbedded.create().amazonDynamoDB();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
