package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.competency.Category
import org.cccnext.tesuto.content.service.CategoryService

/**
 * Created by jasonbrown on 6/30/16.
 */
import org.springframework.stereotype.Service

@Service
public class CategoryServiceStub implements CategoryService {

    def categories = []

    @Override
    boolean isCategoryUsedInBranchRuleEvaluation(List<String> categoryNames, String namespace) {
       for(String name: categoryNames){
            if(categories.contains(name)){
                return false
            }
       }
       return true
    }

    @Override
    boolean isCategoryUsedInPlacementModelEvaluation(List<String> categoryNames, String namespace) {
        for(String name: categoryNames){
            if(categories.contains(name)){
                return false
            }
        }
        return true
    }

    @Override
    Category create(Category category) {
        def categoryName = category.categoryName
        if(!categories.contains(categoryName)) {
            categories.add(categoryName);
        }
        return category;
    }

    @Override
    Category create(String categoryName, String namespace, boolean useForBranchRule, boolean useForPlacementModel) {
        if(!categories.contains(categoryName)) {
            categories.add(categoryName);
        }
        return null;
    }

    @Override
    void delete(Category category) {
    }
}