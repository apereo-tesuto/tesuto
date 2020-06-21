package org.cccnext.tesuto.content.service

import org.cccnext.tesuto.content.dto.competency.Category
import org.cccnext.tesuto.content.model.ItemUseCategory
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.cccnext.tesuto.content.service.CategoryService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class CategoryServiceSpec extends Specification {


    @Shared ApplicationContext context
    @Shared CategoryService categoryService
    AssessmentGenerator generator = new AssessmentGenerator()

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        categoryService = context.getBean(CategoryService.class)
    }

    def "When category exists in repository isCategoryUsedInBranchRuleEvaluation will return expected value for useForBranchRule "() {
        setup:
        def c = createRandomCategory()

        when:
        def category = categoryService.create(c.categoryName, c.namespace, c.usedForBranchRule, c.usedForPlacementModel);

        then:
        categoryService.isCategoryUsedInBranchRuleEvaluation(Arrays.asList(c.categoryName), c.namespace) == c.usedForBranchRule

        cleanup:
        categoryService.delete(category)

        where:
        i << (1..3)
    }

    def "When category does not exist in repository isCategoryUsedInBranchRuleEvaluation will return true "() {
        setup:
        def c = createRandomCategory()

        when:
        def category = categoryService.create(c.categoryName, c.namespace, c.usedForBranchRule, c.usedForPlacementModel);

        then:
        categoryService.isCategoryUsedInBranchRuleEvaluation(Arrays.asList(generator.randomString()), c.namespace) == true

        cleanup:
        categoryService.delete(category)
    }

    def "When category exists in repository isCategoryUsedInPlacementModelEvaluation will return expected value for useForPlacementModel "() {
        setup:
        def c = createRandomCategory()

        when:
        def category = categoryService.create(c.categoryName, c.namespace, c.usedForBranchRule, c.usedForPlacementModel);

        then:
        categoryService.isCategoryUsedInPlacementModelEvaluation(Arrays.asList(c.categoryName), c.namespace) == c.usedForPlacementModel

        cleanup:
        categoryService.delete(category)

        where:
        i << (1..3)
    }

    def "When category does not exist in repository isCategoryUsedInPlacementModelEvaluation will return true "() {
        setup:
        def c = createRandomCategory()

        when:
        def category = categoryService.create(c.categoryName, c.namespace, c.usedForBranchRule, c.usedForPlacementModel);

        then:
        categoryService.isCategoryUsedInPlacementModelEvaluation(Arrays.asList(generator.randomString()), c.namespace) == true

        cleanup:
        categoryService.delete(category)
    }

    Category createRandomCategory(){
        Category c = new ItemUseCategory();
        c.categoryName = generator.randomString()
        c.namespace = generator.randomString()
        c.usedForBranchRule = generator.randomBoolean()
        c.useForPlacementModel = generator.randomBoolean()
        return c;
    }
}