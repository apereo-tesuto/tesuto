package org.cccnext.tesuto.admin.service

import org.apache.commons.lang3.RandomStringUtils
import org.cccnext.tesuto.admin.dto.TestLocationDto
import org.cccnext.tesuto.admin.service.TestLocationService
import org.cccnext.tesuto.user.service.UserGenerator
import org.cccnext.tesuto.util.TesutoUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Ignore
import org.cccnext.tesuto.util.test.*

@TestExecutionListeners([TransactionalTestExecutionListener.class])
class TestLocationServiceImplSpec extends Specification {

    @Shared ApplicationContext context
    @Shared TestLocationService service
    @Shared UserGenerator userGenerator
    @Shared Random rand
    @Shared cccIds
    @Shared count

    boolean randomBoolean() {
        return rand.nextInt(2) == 1
    }

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/userTestApplicationContext.xml")
        service = context.getBean("testLocationService")
        userGenerator = context.getBean("userGenerator")
        rand = new Random()
        count = 9000; // Start with a number that no group of test locations are associated to.
        userGenerator.createTestColleges()
        resetCccIds()
    }

    // setup does not fire when using where: needed to use another approach
    def resetCccIds() {
        cccIds = ["ZZ1", "ZZ2", "ZZ3", "ZZ4", "ZZ5", "ZZ6", "ZZ7", "ZZ8", "ZZ9", "ZZ10"]
        //These map to test colleges in the database
    }

    def getRandomKnownCccId() {
        def cccId = cccIds[rand.nextInt(cccIds.size())]
        cccIds.remove(cccId)
        return cccId
    }

    def createListOfRandomTestLocations(){
        Set<String> ids = []
        Set<TestLocationDto> testLocationDtos = new HashSet<>()
        (2..(rand.nextInt(5)+2)).each{
            def testLocation = createRandomTestLocation()
            def createdTestLocation = service.create(testLocation)
            ids += createdTestLocation.id
            testLocationDtos += createdTestLocation
        }
        return [ids: ids, testLocations: testLocationDtos]
    }

    def createRandomTestLocation(){
        return new TestLocationDto(
                collegeId:  getRandomKnownCccId(),
                name: TesutoUtils.newId(),
                collegeName: TesutoUtils.newId(),
                streetAddress1: TesutoUtils.newId(),
                streetAddress2: TesutoUtils.newId(),
                postalCode: "POST_CODE",
                locationType: "LOCATION_TYPE",
                locationStatus: "LOCATION_STATUS",
                city: TesutoUtils.newId()
        )
    }

    //TODO Essentially the same test works as a JUnit but not as Spock? update version.
    @Transactional
    @Ignore
    def "when a test location exists we can find that location by id"(){
        when:
        TestLocationDto createdTestLocation = service.create(testLocation)

        then:
        createdTestLocation == service.read(createdTestLocation.id)

        cleanup:
        service.delete(createdTestLocation.id)
        resetCccIds()

        where:
        testLocation << createRandomTestLocation()
    }

    def "when a list of test locations exist read by a set of ids will return those ids"(){
        when:
        def map = createListOfRandomTestLocations()

        then:
        map.testLocations == service.read(map.ids)

        cleanup:
        map.ids.each{
            service.delete(it)
        }
        resetCccIds()
    }
    
    def cleanupSpec() {
        userGenerator.deleteTestColleges();
    }
}