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
package org.cccnext.tesuto.content.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.competency.Category;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Created by jasonbrown on 6/29/16.
 */
@Entity
@Table(schema="public",name = "item_use_category")
//TODO Decide if we till need this @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ItemUseCategory implements Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "namespace", nullable = false, length = 100, unique = true)
    private String namespace;

    @Column(name = "use_for_branch_rule", nullable = false)
    private boolean usedForBranchRule;

    @Column(name = "use_for_placement_model", nullable = false)
    private boolean usedForPlacementModel;

    public ItemUseCategory() {
    }

    public ItemUseCategory(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        this.namespace = category.getNamespace();
        this.usedForBranchRule = category.isUsedForBranchRule();
        this.usedForPlacementModel = category.isUsedForPlacementModel();
    }

    @Override
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean isUsedForBranchRule() {
        return usedForBranchRule;
    }

    public void setUsedForBranchRule(Boolean usedForBranchRule) {
        this.usedForBranchRule = usedForBranchRule;
    }

    @Override
    public boolean isUsedForPlacementModel() {
        return usedForPlacementModel;
    }

    public void setUseForPlacementModel(boolean useForPlacementModel) {
        this.usedForPlacementModel = useForPlacementModel;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
