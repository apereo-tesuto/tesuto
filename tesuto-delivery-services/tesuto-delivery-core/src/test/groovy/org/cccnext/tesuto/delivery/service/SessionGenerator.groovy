package org.cccnext.tesuto.util.test

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.model.internal.ItemSession
import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto
import org.cccnext.tesuto.content.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired

class SessionGenerator extends AssessmentGenerator {

    @Autowired
    CategoryService categoryService

    def randomDouble(){
        return randomDouble(2, 5)
    }

    def randomDoubleList(def num){
        return randomList(num, {randomDouble()})
    }

    def createItemSessions(def values, def outcomeIdentifier){
        def itemSessions = []
        values.each{
            itemSessions < createItemSession(it, outcomeIdentifier)
        }
        return  itemSessions
    }

    def createItemSession(def value, def outcomeIdentifier, def itemId, def itemRefId){
        def min = -value*2
        def max = value*2
        def outcome = createOutcome(outcomeIdentifier, min, max, value)
        def itemsSession = new ItemSession(
                itemSessionId: randomString(),
                itemId: itemId,
                itemRefIdentifier: itemRefId
        )
        itemsSession.addOutcome(outcome)
        return itemsSession
    }
    def createOutcome(def outcomeIdentifier, def min, def max, def value) {
        new Outcome(
                outcomeIdentifier: outcomeIdentifier,
                value: value,
                normalMinimum: min,
                normalMaximum: max,
                declarationType: OutcomeDeclarationType.IMPLICIT //not tested
        )
    }

    def createAssessmentSession(def expectedNamespace, def listOfItemRefIds){
        def session =  new AssessmentSession(
                assessment: randomAssessment(1)
        )
        session.assessment.namespace = expectedNamespace
        session.assessment.assessmentParts[0].assessmentSections[0].assessmentItemRefs = createItemRefs(listOfItemRefIds, expectedNamespace)
        session.assessment.assessmentMetadata = randomNotNullAssessmentMetadata()
        return session
    }

    def createItemRefs(def listOfItemRefIds, def expectedNamespace) {
        def itemRefs = []
        listOfItemRefIds.each{
            itemRefs.add(createItemRef(it, expectedNamespace))
        }
        return itemRefs
    }


    def createItemRef(def itemRefId, def namespace) {
        def itemRefDto = new AssessmentItemRefDto(
                identifier: itemRefId,
                categories: createCategories(namespace),
                itemIdentifier: randomString()
        )
        return itemRefDto
    }

    def createCategories(def namespace){
        def categoryStrings = randomList(2, {randomString()})
        def categories = []
        categoryStrings.each{
            categories.add(categoryService.create(it, namespace, false, true))
        }
        return categories
    }
}