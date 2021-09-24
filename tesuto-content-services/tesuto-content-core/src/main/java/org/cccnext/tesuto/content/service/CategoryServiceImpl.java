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

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.competency.Category;
import org.cccnext.tesuto.content.model.ItemUseCategory;
import org.cccnext.tesuto.content.repository.jpa.CategoryRepository;
import org.cccnext.tesuto.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jasonbrown on 6/29/16.
 */
@Service(value = "categoryService")
public class CategoryServiceImpl implements CategoryService {
    
	@Autowired
    CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean isCategoryUsedInBranchRuleEvaluation(List<String> categoryNames, String namespace) {
        if (CollectionUtils.isNotEmpty(categoryNames)) {
            for (String categoryName : categoryNames) {
                ItemUseCategory category = repository.findByCategoryNameAndNamespace(categoryName, namespace);
                if (category != null && !category.isUsedForBranchRule()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCategoryUsedInPlacementModelEvaluation(List<String> categoryNames, String namespace) {
        if (CollectionUtils.isNotEmpty(categoryNames)) {
            for (String categoryName : categoryNames) {
                ItemUseCategory category = repository.findByCategoryNameAndNamespace(categoryName, namespace);
                if (category != null && !category.isUsedForPlacementModel()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    public ItemUseCategory create(Category category) {
        return repository.save(new ItemUseCategory(category));
    }

    @Override
    @Transactional
    public ItemUseCategory create(String categoryName, String namespace, boolean useForBranchRule,
            boolean useForPlacementModel) {
        ItemUseCategory itemUseCategory = new ItemUseCategory();
        itemUseCategory.setCategoryName(categoryName);
        itemUseCategory.setNamespace(namespace);
        itemUseCategory.setUsedForBranchRule(useForBranchRule);
        itemUseCategory.setUseForPlacementModel(useForPlacementModel);
        return repository.save(itemUseCategory);
    }

    @Override
    @Transactional
    public void delete(Category category) {
        repository.delete(new ItemUseCategory(category));
    }
}
