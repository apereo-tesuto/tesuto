package org.cccnext.tesuto.delivery.service

import org.cccnext.tesuto.content.dto.AssessmentComponentDto
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import org.cccnext.tesuto.util.test.AssessmentGenerator

class SelectionServiceSpec extends Specification {

    @Shared
    SelectionService selectionService
    @Shared AssessmentGenerator generator
    def setupSpec() {
        generator = new AssessmentGenerator()
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        selectionService = context.getBean("selectionService")
    }

    def "when given an expected list of components will return required/notRequired indices"(){
        when:
        def numberOfIndices = generator.randomInt(1, 10)
        def requiredIndices = createExpectedSetOfRequiredIndices(numberOfIndices)
        def notRequiredIndices = createExpectedSetOfNotRequiredIndices(requiredIndices)
        def components = createRandomRequiredComponents(requiredIndices)

        then:
        requiredIndices == selectionService.determineIndices(components).getRight()
        notRequiredIndices == selectionService.determineIndices(components).getLeft()

        where:
        i << (1..10)
    }

    def "when given a random list of components will return the expected components"(){
        when:
        def numberOfIndices = generator.randomInt(1, 50)
        def requiredIndices = createExpectedSetOfRequiredIndices(numberOfIndices)
        def componentsToProcess = createRandomRequiredComponents(requiredIndices)
        def selection = createSelection(numberOfIndices)

        def calculatedComponents = selectionService.calculateComponents(componentsToProcess, selection)

        then:
        calculatedComponents.size() >= selection.select
        calculatedComponents.size() >= requiredIndices.size()
        verifyComponentsAreInExpectedOrder(calculatedComponents, componentsToProcess)
        verifyContainsAllRequiredComponents(calculatedComponents, componentsToProcess)
        where:
        i << (1..10)
    }

    void verifyContainsAllRequiredComponents(def calculatedComponents, def componentsToProcess){
        componentsToProcess.each{
            if(it.isRequired) {
                assert calculatedComponents.contains(it)
            }
        }
    }

    void verifyComponentsAreInExpectedOrder(def calculatedComponents, def componentsToProcess){

        while(componentsToProcess.size() != 0){
            if(componentsToProcess[0] == calculatedComponents[0]){
                calculatedComponents.remove(0)
            }else{
                componentsToProcess.remove(0)
            }
        }
        assert componentsToProcess.size() == 0
        assert calculatedComponents.size() == 0
        assert calculatedComponents == componentsToProcess
    }

    AssessmentSelectionDto createSelection(def num){
        new AssessmentSelectionDto(
                select: generator.randomInt(1, num)
        )
    }
    SortedSet<Integer> createExpectedSetOfRequiredIndices(def numberOfIndices){
        SortedSet<Integer> indices = new TreeSet<>();
        def previousIndex = 0;
        (0..<numberOfIndices).each{
            previousIndex += generator.randomInt(1, 5)
            indices.add(previousIndex)
        }
        return indices
    }

    SortedSet<Integer> createExpectedSetOfNotRequiredIndices(SortedSet<Integer> requiredIndices){
        SortedSet<Integer> indices = new TreeSet<>();
        (0..requiredIndices.last()).each{
            if(!requiredIndices.contains(it)) {
                indices.add(it)
            }
        }
        return indices
    }

    List<AssessmentComponentDto> createRandomRequiredComponents(def requiredIndices){
        def maxIndex = requiredIndices.max()
        List<AssessmentItemRefDto> itemRefDtos = createRandomNotRequiredComponents(maxIndex)
        requiredIndices.each{
            itemRefDtos[it].isRequired = true
        }

        List<AssessmentComponentDto> componentDtos = new ArrayList<>(itemRefDtos);

        def randomIndex = generator.rand.nextInt(maxIndex)
        componentDtos[randomIndex] = createSectionDto(componentDtos[randomIndex])

        return componentDtos
    }

    AssessmentSectionDto createSectionDto(AssessmentItemRefDto itemRefDto){
        new AssessmentSectionDto(
                id: generator.randomString(),
                isRequired: itemRefDto.isRequired
        )
    }

    List<AssessmentItemRefDto> createRandomNotRequiredComponents(def max){
        return generator.randomListSetSize(max+1, {createItemRefDto()})
    }

    AssessmentItemRefDto createItemRefDto(){
        new AssessmentItemRefDto(
                branchRules: null,
                categories: null,
                identifier: generator.randomString(),
                isRequired: false,
        )
    }
}