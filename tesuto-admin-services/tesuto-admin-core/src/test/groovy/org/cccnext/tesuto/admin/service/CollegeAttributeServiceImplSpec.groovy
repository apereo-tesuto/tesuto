package org.cccnext.tesuto.admin.service

import org.cccnext.tesuto.admin.dto.CollegeAttributeDto
import org.cccnext.tesuto.exception.PoorlyFormedRequestException
import org.cccnext.tesuto.admin.service.CollegeAttributeService
import org.cccnext.tesuto.user.service.UserGenerator
import org.cccnext.tesuto.util.TesutoUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import spock.lang.Shared
import spock.lang.Specification

class CollegeAttributeServiceImplSpec extends Specification {

    @Shared ApplicationContext context
    @Shared CollegeAttributeService service
    @Shared UserGenerator userGenerator
    @Shared Random rand
    @Shared cccIds

    boolean randomBoolean() {
        return rand.nextInt(2) == 1
    }

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/userTestApplicationContext.xml")
        service = context.getBean("collegeAttributeService")
        userGenerator = context.getBean("userGenerator")
		if(userGenerator.collegeRepository == null) {
			throw new RuntimeException("A is null")
		}
        rand = new Random()
        userGenerator.createTestColleges()
        resetCccIds()
    }

    // setup does not fire when using where: needed to use another approach
    def resetCccIds() {
        cccIds = ["ZZ1", "ZZ2", "ZZ3", "ZZ4", "ZZ5", "ZZ6", "ZZ7", "ZZ8", "ZZ9", "ZZ10"] //These map to test colleges in the database
    }

    def getRandomKnownCccId(){
        def cccId = cccIds[rand.nextInt(cccIds.size())]
        cccIds.remove(cccId)
        return cccId
    }

    def getRandomValidEslPlacementOption(){
        def englishOptions = ["ENGLISH", "ESL", "BOTH"]
        return englishOptions[rand.nextInt(englishOptions.size())]
    }

    def getRandomValidEnglishPlacementOption(){
        def englishOptions = ["ENGLISH", "BOTH"]
        return englishOptions[rand.nextInt(englishOptions.size())]
    }

    def getRandomInvalidPlacementOption() {
        def englishOptions = [null, TesutoUtils.newId()]
        return englishOptions[rand.nextInt(englishOptions.size())]
    }

    CollegeAttributeDto createRandomCollegeAttribute(){
        new CollegeAttributeDto(
                collegeId: getRandomKnownCccId(),
                englishPlacementOption: getRandomValidEnglishPlacementOption(),
                eslPlacementOption: getRandomValidEslPlacementOption()
        )
    }

    CollegeAttributeDto createRandomCollegeAttributeInvalid(){
        def flag = randomBoolean()
        return new CollegeAttributeDto(
                collegeId: getRandomKnownCccId(),
                englishPlacementOption: flag ? getRandomValidEnglishPlacementOption() : getRandomInvalidPlacementOption(),
                eslPlacementOption: flag ? getRandomInvalidPlacementOption() : getRandomValidEslPlacementOption()
        )
    }

    List<CollegeAttributeDto> createRandomListCollegeAttributes(){
        return (2..(rand.nextInt(5)+2)).collect({createRandomCollegeAttribute()})
    }

    def "when a college attribute is updated that update is persisted"(){
        when:
        CollegeAttributeDto updatedCollegeAttribute = service.create(collegeAttribute)

        then:
        updatedCollegeAttribute == service.read(updatedCollegeAttribute.collegeId)

        cleanup:
        resetCccIds()

        where:
        collegeAttribute << createRandomCollegeAttribute()
    }

    def "when a college attribute does not contain a valid esl or english placement will throw exception"(){
        when:
        service.create(collegeAttribute)

        then:
        thrown(PoorlyFormedRequestException)

        cleanup:
        resetCccIds()

        where:
        collegeAttribute << createRandomCollegeAttributeInvalid()
    }

    def "when a list of college attributes exist read will return at least the known list of college attributes"(){
        when:
        List<CollegeAttributeDto> randomCollegeAttributes = createRandomListCollegeAttributes()
        List<CollegeAttributeDto> updatedCollegeAttributes = service.create(randomCollegeAttributes)
        List<CollegeAttributeDto> readCollegeAttributes = service.read()

        then:
        updatedCollegeAttributes.each{
            readCollegeAttributes.contains(it)
        }
    }
    
    def cleanupSpec() {
        userGenerator.deleteTestColleges();
    }

}