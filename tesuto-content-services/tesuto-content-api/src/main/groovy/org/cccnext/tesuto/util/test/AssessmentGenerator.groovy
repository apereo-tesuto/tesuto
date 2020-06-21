package org.cccnext.tesuto.util.test

import org.cccnext.tesuto.content.dto.competency.Category
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator.Counter
import org.cccnext.tesuto.content.dto.AssessmentPartDto
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto
import org.cccnext.tesuto.content.service.AssessmentItemService
import org.cccnext.tesuto.content.service.AssessmentService
import org.cccnext.tesuto.content.service.CategoryService


class AssessmentGenerator extends AssessmentDtoGenerator {

    //These can  be set in the spec setup
    AssessmentService assessmentService = [create:{ AssessmentDto assessment -> assessment.id = randomId(); return assessment; }] as AssessmentService
    AssessmentItemService itemService  = [create:{ AssessmentItemDto item -> item.id = randomId(); return item; }] as AssessmentItemService
    CategoryService categoryService =[create: {p1, p2, p3, p4 -> return null; }] as CategoryService

    def namespace = "test"

    AssessmentItemDto readAssessmentItem(String namespace, String itemId) {
        return itemService.readLatestVersion(namespace, itemId);
    }
    @Override
    AssessmentDto randomAssessment(int maxSectionDepth) {
        def assessment = super.randomAssessment(maxSectionDepth)
        assessmentService.create(assessment)
        return assessment
    }

    AssessmentPartDto randomAssessmentPart(Counter counter) {
        return new AssessmentPartDto(
                id: randomId(),
                assessmentPartNavigationMode: AssessmentPartNavigationMode.LINEAR, //only one supported
                assessmentPartSubmissionMode: AssessmentPartSubmissionMode.INDIVIDUAL, //only one one supported
                duration: rand.nextDouble(),
                assessmentSections: randomSections(counter),
                itemSessionControl: randomItemSessionControl()
        )
    }

    AssessmentItemSessionControlDto randomItemSessionControl() {
        return randomBoolean() ? null :
                new AssessmentItemSessionControlDto(
                        allowSkipping: randomNullableBoolean(),
                        validateResponses: randomNullableBoolean()
                )
    }

    List<AssessmentSectionDto> randomSections(Counter counter) {
        return randomList(5)  { randomSection(counter) }
    }

    AssessmentItemDto randomItem(String title) {
        def item = new AssessmentItemDto(
                id: randomId(),
                identifier: randomId(),
                version: 1,
                namespace: namespace,
                title: title,  //The title is tied to the count so we can verify the items are delivered correctly
                label: randomId(),
                language: 'en',
                body: randomString(),
                itemMetadata: randomItemMetadata()
        )

       return item
    }

    ItemMetadataDto randomItemMetadata(){
        return new ItemMetadataDto(
                calibratedDifficulty: randomDouble(1, 5),
                itemBankStatusType: ItemBankStatusType.AVAILABLE
        )
    }

    @Override
    String randomItem(Counter count, boolean countThisItem, boolean usedInBranchRuleEvaluation) {
        //Count is now tied to Branch Rules.  Branch Rules are not random for testing.
        //If we are testing a branch rule then all the first and last sections items will be added to the count.
        //If we are not testing br then all of the items will be counted.
        if(countThisItem) {
            count.incr()
        }
        def numberInteractions = randomInt(1, 2)
        def item = new AssessmentItemDto(
                id: randomId(),
                version: 1,
                title: count.title(),  //The title is tied to the count so we can verify the items are delivered correctly
                label: randomId(),
                language: 'en',
                body: randomString(),
                responseVars: randomListSetSize(numberInteractions) { randomResponseVar() },
                outcomeDeclarationDtos: randomListSetSize(3) { randomOutcomeDeclaration() },
                interactions: randomListSetSize(numberInteractions) { randomInteraction() },
                responseProcessing: randomResponseProcessing(),
                itemMetadata: randomItemMetadata()
        )
        item.outcomeDeclarationDtos += new AssessmentOutcomeDeclarationDto(
                identifier: "SCORE",
                cardinality: randomCardinality(),
                baseType: randomBaseType(),
                defaultValue: new AssessmentDefaultValueDto(),
                normalMaximum: usedInBranchRuleEvaluation ? 3 : -5000,
                normalMinimum: 1
        )
        for(int i = 0; i < numberInteractions; i++) {
            item.interactions[i].responseIdentifier = item.responseVars[i].identifier
        }
        itemService.create(item)
        return item.id
    }


    Map generateAssessmentsWithVersion(service, identifier, setVersionFunc) {
       def repetitions = randomInt(2, 10)
       def List<Integer> versions = []
       def List<String> assessmentIds = []
       (1..repetitions).each {
            AssessmentDto assessment = randomAssessment()
            assessment.identifier = identifier
            while(true) {
                setVersionFunc(service, assessment);
                if(!versions.contains(assessment.version)) {
                   break;
                }
            } 
            versions.add(assessment.version)
            def newAssessment = service.create(assessment)
            assessmentIds += newAssessment.id
        }
        def int maxVersion = Collections.max(versions)
        return  [max:maxVersion, ids:assessmentIds]
    }
    
    Map generateAssessmentItemsWithVersion(service, identifier, setVersionFunc) {
       def repetitions = randomInt(2, 10)
       def List<Integer> versions = []
       def List<String> ids = []
       (1..repetitions).each {
            def assessmentItem = randomItem("Version Testing Item")
            assessmentItem.identifier = identifier
            while(true) {
                setVersionFunc(service, assessmentItem);
                if(!versions.contains(assessmentItem.version)) {
                  break;
                }
            } 
            versions.add(assessmentItem.version)
            def newItemAssessment = service.create(assessmentItem)
            ids += newItemAssessment.id
        }
        def int maxVersion = Collections.max(versions);
        return  [max:maxVersion, ids:ids]
    }
    
    def setVersionRandomly =  { service, a ->
        def nextVersion = randomInt(1, 99)
        a.version = nextVersion
    }
    
    def setVersionNextVersion =  { service,   a ->
        a.version = service.getNextVersion(a.namespace, a.identifier)
    }

    @Override
    List<String> createCategories(){
        def category = randomString()
        def categories = Arrays.asList(category)
        categoryService.create(category, randomString(), randomBoolean(), randomBoolean())
        return categories
    }

}
