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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.domain.multiplemeasures.Fact;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

/**
 * Created by bruce on 4/12/17.
 */
@DynamoDBTable(tableName="variable_set")
public class VariableSetMapper {

    private String cccId;
    private String id;
    private String misCode;
    private Date createDate;
    private String source;
    private Date sourceDate;
    private String sourceType;
    private Set<Fact> facts;

    @DynamoDBHashKey
    public String getCccId() {
        return cccId;
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
    }

    @DynamoDBRangeKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
    }

    @DynamoDBAttribute
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @DynamoDBAttribute
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @DynamoDBAttribute
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @DynamoDBAttribute
    public Date getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(Date sourceDate) {
        this.sourceDate = sourceDate;
    }

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = FactSetConverter.class)
    public Set<Fact> getFacts() {
        return facts;
    }

    public void setFacts(Set<Fact> facts) {
        this.facts = new HashSet<>(facts);
    }
}
