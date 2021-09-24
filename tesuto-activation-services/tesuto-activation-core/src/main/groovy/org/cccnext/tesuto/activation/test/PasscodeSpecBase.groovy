package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.cccnext.tesuto.activation.model.*
import org.cccnext.tesuto.activation.PasscodeValidationAttempt.ValidationResult
import spock.lang.Shared

abstract class PasscodeSpecBase extends SpecBase {

    @Shared PasscodeDao dao
    @Shared PasscodeService service


    //To be called from the implementation
    def setupSpec() {
        dao = context.getBean("passcodeDao")
        service = createServiceWithConfigs()
    }


    PasscodeServiceConfiguration randomConfiguration() {
        def config = new PasscodeServiceConfiguration(
                minExpiration: generator.randomInt(1,10),
                passcodeLength: generator.randomInt(2,10)
        )
        if (generator.randomBoolean()) {
            config.minuteHand = 15*generator.randomInt(1,3)
        }
        if (generator.randomBoolean()) {
            config.hourHand = generator.randomInt(0,23)
        }
        return config
    }

    PasscodeService createServiceWithConfigs() {
        def service = new PasscodeService()
        service.privateConfig = randomConfiguration()
        service.publicConfig = randomConfiguration()
        service.dao = dao
        service.activationService = activationService
        service.passcodeServiceUtil = context.getBean("passcodeServiceUtil")
        return service
    }

    Passcode generatePasscode(String userId) {
        (generator.randomBoolean() ? service.generatePrivatePasscode(userId) : service.generatePublicPasscode(userId))
    }


    //This is for modifying a previously generated passcode for testing purposes
    void savePasscode(Passcode passcode, PasscodeService service) {
        dao.savePasscode(passcode)
        service.setPasscodeExpirationDate(passcode)
    }

    Passcode generateInvalidPasscode(String userId) {
        Passcode passcode = generatePasscode(userId)
        passcode.createDate = new GregorianCalendar()
        //A long, long time ago ...
        passcode.createDate.set(Calendar.YEAR, passcode.createDate.getMinimum(Calendar.YEAR))
        savePasscode(passcode, service)
        return passcode
    }


    void safeDeletePasscode(Passcode passcode) {
        if (passcode != null) {
            service.delete(passcode.value)
        }
    }


    Calendar minutesBeforeNow(int minutes) {
        def calendar = new GregorianCalendar()
        calendar.add(Calendar.MINUTE, -1*minutes)
        return calendar
    }


    def "after deleting a passcode, the passcode is no longer valid"() {
        when:
        Passcode passcode = generatePasscode(generator.randomId())
        then:
        service.delete(passcode.value) //snuck in a verification that delete returns true
        !service.isValidPasscode(passcode.value)
        where:
        i << [1..3]
    }


    def "deleting a non-existing passcode returns false"() {
        expect: !service.delete(generator.randomId())
    }


    def "if a passcode is valid for a user, findAllPasscodesByUser will return it"() {
        setup: def userId = generator.randomId()
        when: def passcode = generatePasscode(userId)
        then: service.findAllPasscodesByUser(userId).contains(passcode)
        cleanup: safeDeletePasscode(passcode)
    }


    def "Passcode validation is case insensitive"() {
        when: def passcode = generatePasscode(generator.randomId())
        then:
        service.isValidPasscode(passcode.value.toUpperCase())
        service.isValidPasscode(passcode.value.toLowerCase())
        cleanup: safeDeletePasscode(passcode)
    }



    def "findAllPasscodesByUser returns only valid passcodes"() {
        setup:
        def userId = generator.randomId()
        def passcode1 = generatePasscode(userId)
        def passcode2 = generateInvalidPasscode(userId)

        when: def passcodes = service.findAllPasscodesByUser(userId)
        then: passcodes.size() == 0 || passcodes.each { passcode -> service.isValidPasscode(passcode.value) }

        cleanup:
        safeDeletePasscode(passcode1)
        safeDeletePasscode(passcode2)

        where: i << [1..4]
    }


    def "generating a public passcode for a userId invalidates the previous one "() {
        setup:
        def userId = generator.randomId()
        def passcode1 = service.generatePublicPasscode(userId)
        def passcode2 = service.generatePublicPasscode(userId)

        expect:
        !service.isValidPasscode(passcode1.value)
        service.isValidPasscode(passcode2.value)

        cleanup:
        safeDeletePasscode(passcode1)
        safeDeletePasscode(passcode2)
    }


    def "generating a private passcodefor a userId invalidates the previous one "() {
        setup:
        def userId = generator.randomId()
        def passcode1 = service.generatePrivatePasscode(userId)
        def passcode2 = service.generatePrivatePasscode(userId)

        expect:
        !service.isValidPasscode(passcode1.value)
        service.isValidPasscode(passcode2.value)

        cleanup:
        safeDeletePasscode(passcode1)
        safeDeletePasscode(passcode2)
    }


    def "a user can simultaneously have a valid public and private passcode"() {
        setup:
        def userId = generator.randomId()
        def passcode1 = service.generatePrivatePasscode(userId)
        def passcode2 = service.generatePublicPasscode(userId)

        expect:
        service.isValidPasscode(passcode1.value)
        service.isValidPasscode(passcode2.value)

        cleanup:
        safeDeletePasscode(passcode1)
        safeDeletePasscode(passcode2)
    }


    def "a passcode remains valid for at least minExpiration minutes"() {
        setup:
        def service = createServiceWithConfigs()
        def now = new GregorianCalendar()
        def minute = now.getAt(Calendar.MINUTE)
        def hour = now.getAt(Calendar.HOUR_OF_DAY)
        def userId = generator.randomId()

        when:
        service.privateConfig.minuteHand = (generator.randomBoolean() ? null : minute - 1)
        service.publicConfig.minuteHand = (generator.randomBoolean() ? null : minute - 1)
        service.privateConfig.hourHand = (generator.randomBoolean() ? null : hour)
        service.publicConfig.hourHand = (generator.randomBoolean() ? null : hour)
        def publicCode = service.generatePublicPasscode(userId)
        def privateCode = service.generatePrivatePasscode(userId)
        publicCode.createDate = minutesBeforeNow(service.publicConfig.minExpiration - 1)
        privateCode.createDate = minutesBeforeNow(service.privateConfig.minExpiration - 1)
        savePasscode(publicCode, service)
        savePasscode(privateCode, service)

        then:
        service.isValidPasscode(publicCode.value)
        service.isValidPasscode(privateCode.value)

        cleanup:
        safeDeletePasscode(publicCode)
        safeDeletePasscode(privateCode)
    }


    def "a passcode remains valid until the minute indicated by minuteHand"() {
        when:
        def service = createServiceWithConfigs()
        def now = new GregorianCalendar()
        def minute = now.getAt(Calendar.MINUTE)
        def hour = now.getAt(Calendar.HOUR)
        if (minute != 0 ){
            //This test is worthless if we happen to be in the first minute of the hour
            service.privateConfig.minExpiration = 0 //Expire immediately
            service.publicConfig.minExpiration = 0
        } else {
            //This test doesn't work  if we happen to be in the first minute of the hour, so we'll just make sure it passes
            service.privateConfig.minExpiration = 5
            service.publicConfig.minExpiration = 5
        }
        service.privateConfig.minuteHand = minute + 2
        service.publicConfig.minuteHand = minute + 2
        service.privateConfig.hourHand = (generator.randomBoolean() ? null : hour-1)
        service.publicConfig.hourHand = (generator.randomBoolean() ? null : hour)
        def userId = generator.randomId()
        def publicCode = service.generatePublicPasscode(userId)
        def privateCode = service.generatePrivatePasscode(userId)
        publicCode.createDate = minutesBeforeNow(1)
        privateCode.createDate = minutesBeforeNow(1)
        savePasscode(publicCode, service)
        savePasscode(privateCode, service)

        then:
        minute == 0 || service.isValidPasscode(publicCode.value)
        service.isValidPasscode(privateCode.value)

        cleanup:
        safeDeletePasscode(publicCode)
        safeDeletePasscode(privateCode)
    }


    def "a passcode remains valid until the hour indicated by hourHand"() {
        when:
        def service = createServiceWithConfigs()
        def now = new GregorianCalendar()
        def minute = now.getAt(Calendar.MINUTE)
        def hour = now.getAt(Calendar.HOUR)
        service.privateConfig.minExpiration = 0 //Expire immediately
        service.publicConfig.minExpiration = 0
        service.privateConfig.hourHand = hour + 1
        service.publicConfig.hourHand = hour + 1
        service.privateConfig.minuteHand = (generator.randomBoolean() ? null : minute-1)
        service.publicConfig.minuteHand = (generator.randomBoolean() ? null : minute)
        def userId = generator.randomId()
        def publicCode = service.generatePublicPasscode(userId)
        def privateCode = service.generatePrivatePasscode(userId)
        publicCode.createDate = minutesBeforeNow(1)
        privateCode.createDate = minutesBeforeNow(1)
        savePasscode(publicCode, service)
        savePasscode(privateCode, service)

        then:
        service.isValidPasscode(publicCode.value)
        service.isValidPasscode(privateCode.value)

        cleanup:
        safeDeletePasscode(publicCode)
        safeDeletePasscode(privateCode)
    }


    def expiredCreateDate(PasscodeServiceConfiguration config) {
        def createDate = new GregorianCalendar()
        if (config.hourHand != null) {
            while (createDate.get(Calendar.HOUR_OF_DAY) != config.hourHand) {
                createDate.add(Calendar.HOUR, -1)
            }
            createDate.add(Calendar.HOUR, -1)
        }
        if (config.minuteHand != null) {
            while (createDate.get(Calendar.MINUTE) != config.minuteHand) {
                createDate.add(Calendar.MINUTE, -1)
            }
            createDate.add(Calendar.MINUTE, -1)
        }
        createDate.add(Calendar.MINUTE, -1*config.minExpiration-1)
        return createDate
    }


    def "passcode expires after minExpiration when minuteHand and hourHand are reached or not set"() {
        setup:
        def service = createServiceWithConfigs()
        def userId = generator.randomId()

        when:
        service.publicConfig = publicConfig
        service.privateConfig = service.privateConfig
        def publicCode = service.generatePublicPasscode(userId)
        def privateCode = service.generatePrivatePasscode(userId)
        publicCode.createDate = expiredCreateDate(service.publicConfig)
        privateCode.createDate = expiredCreateDate(service.privateConfig)
        savePasscode(publicCode, service)
        savePasscode(privateCode, service)

        then:
        !service.isValidPasscode(publicCode.value)
        !service.isValidPasscode(privateCode.value)

        cleanup:
        safeDeletePasscode(publicCode)
        safeDeletePasscode(privateCode)

        where:
        publicConfig << (1..3).collect { randomConfiguration() }
        privateConfig << (1..3).collect { randomConfiguration() }
    }


    Activation createActivation(boolean requirePasscode) {
        def proto = generator.randomActivation([requirePasscode: requirePasscode])
        return activationService.find(activationService.create("test", proto))
    }


    def "When a passcode is required, every validation attempt is logged"() {
        when:
        def activation = createActivation(true)
        def attempts = []
        def passcodes = []
        (1..generator.randomInt(1,5)).each {
            if (generator.randomBoolean()) {
                //unsuccessful attempt
                def passcodeValue = generator.randomId()
                service.attemptValidation(activation, passcodeValue, generator.randomBoolean())
                attempts << [passcode: passcodeValue, successful: false, userId: null]
            } else {
                //successful attempt
                def userId =  generator.randomId()
                def passcode = generatePasscode(userId)
                passcodes << passcode
                service.attemptValidation(activation, passcode.value, false)
                attempts << [passcode: passcode.value, successful: true, userId: userId]
            }
        }
        def attemptLog = dao.findValidationAttempts(activation.activationId)

        then:
        attemptLog.size() == attempts.size()
        attempts.each { attempt ->
            null != attemptLog.find {
                it.passcode == attempt.passcode &&
                it.successful == attempt.succsessful &&
                it.userId == attempt.userId }
        }

        cleanup:
        safeDeleteActivation(activation)
        passcodes.each { safeDeletePasscode(it) }
    }

    def "When a passcode longer than 100 characters is used, a validation attempt is still logged, but <passcode too long> is logged as the passcode"() {
        when:
        def tooLongPasscode = generator.randomString(101, 101); // why does randomString(#) return a string of length 0-#? Grr.
        def activation = createActivation(true)
        service.attemptValidation(activation, tooLongPasscode, false)
        def attemptLog = dao.findValidationAttempts(activation.activationId)

        then:
        attemptLog.size() == 1
        attemptLog.iterator().next().passcode == "<passcode too long>"

        cleanup:
        safeDeleteActivation(activation)
    }

    def "Passcode validation fails if there have been too many recent failures (lockout"() {
        setup:
        def service = createServiceWithConfigs()
        service.passcodeServiceUtil.maxFailedAttempts = generator.randomInt(1,3)
        service.passcodeServiceUtil.lockedFor = 2

        when:
        def activation = createActivation(true)
        (1..service.passcodeServiceUtil.maxFailedAttempts).each {
            service.attemptValidation(activation, generator.randomString(), false)
        }
        def userId = generator.randomId()
        def passcode = generatePasscode(userId)

        then:
        service.attemptValidation(activation, passcode.value, generator.randomBoolean()).getLeft() == ValidationResult.LOCKED;
        Thread.sleep(service.passcodeServiceUtil.lockedFor*1000l)
        service.attemptValidation(activation, passcode.value, false).getLeft() == ValidationResult.VALID;

        cleanup:
        safeDeleteActivation(activation)
        safeDeletePasscode(passcode)
    }
 

    def "If a passcode validation attempt requires a private passcode, and only a public passcode is available, PRIVATE_PASSCODE_REQUIRED status is returned"() {
        when:
        def passcode = service.generatePublicPasscode(generator.randomId())
        def activation = createActivation(true)
        then:
        service.attemptValidation(activation, passcode.value, true).getLeft() == ValidationResult.PRIVATE_PASSCODE_REQUIRED
        cleanup:
        safeDeleteActivation(activation)
        safeDeletePasscode(passcode)
    }


    def "Passcode generation does not create passcodes with ambiguous characters"() {
        when:
        def passcode = service.generatePublicPasscode(generator.randomId())
        def rejectedCharacters = "IOUV01".toCharArray()
        then:
        !doesStringContainRejectedCharacters(passcode.getValue(), rejectedCharacters)
        cleanup:
        safeDeletePasscode(passcode)
        where:
        i << (1..10)
        
        
    }

    def "Successful validation of a public passcode returns the userId that created the passcode"() {
        when:
        def passcode = service.generatePublicPasscode(generator.randomId())
        def activation = createActivation(false)
        then:
        service.attemptValidation(activation, passcode.value, false).getRight() == passcode.userId
        
        cleanup:
        safeDeleteActivation(activation)
        safeDeletePasscode(passcode)
    }

}
