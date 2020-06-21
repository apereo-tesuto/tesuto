package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.cccnext.tesuto.activation.model.*
import org.cccnext.tesuto.admin.dto.CollegeDto
import org.cccnext.tesuto.admin.dto.TestLocationDto
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.admin.dto.SecurityGroupDto
import org.cccnext.tesuto.admin.dto.UserAccountDto
import org.cccnext.tesuto.model.ScopedIdentifier
import org.cccnext.tesuto.user.service.UserAccountService
import org.cccnext.tesuto.admin.service.CollegeService
import org.cccnext.tesuto.admin.service.TestLocationService
import org.cccnext.tesuto.content.service.AssessmentService
import org.cccnext.tesuto.delivery.service.DeliveryServiceApi
import org.cccnext.tesuto.util.test.Generator
import org.joda.time.DateTime
import org.cccnext.tesuto.content.model.ScopedIdentifier
//This class has logic to generate test data for the ActivationDaoTest
class ActivationTestGenerator extends Generator {

    def maxLocationId = 10

    //Generated lists to be used in by tests
    List<Activation> activationsToCreate = []
    List<String> persistedActivationIds = []
    List<String> persistedTestEventIds = []
    List<String> activationIdsToDelete = []
    List<Set<String>> activationIdSets = []
    List<SearchParameters> searchParameters = []
    ActivationService service
    TestEventServiceImpl testEventService

    ActivationTestGenerator(service) {
        this.service = service
        initService()
        initialize()
    }


    def createUser = {
        id ->return [
                getUserAccountId: { id },
                getDisplayName: { "Fred ${id}".toString() },
                isStudent: { id == "Student" }
        ] as UserAccountDto
    }

    def initService() {
        def readLatestVersionClosure = { id ->
            new AssessmentDto(
                    title: "assessment ${id}",
                    assessmentMetadata: new AssessmentMetadataDto(requirePasscode: id.namespace == "requirePasscode"),
					namespace: id.namespace,
					identifier: id.identifier
            )
        }
        service.assessmentService = [readLatestPublishedVersion: readLatestVersionClosure] as AssessmentService
        service.deliveryService = [createUserAssessmentSession: { p1, p2, p3, p4 -> randomId() }] as DeliveryServiceApi
        service.testLocationService = [read: { id -> new TestLocationDto(name: "location ${id}") }] as TestLocationService
        service.userAccountService = [getUserAccount: { id -> createUser(id) } ] as UserAccountService

        return service;
    }


    void initialize() {
        this.activationsToCreate = makeSomeActivations()
        this.persistedActivationIds = makeSomePersistedActivations()
        this.activationIdsToDelete = makeSomePersistedActivations()
        this.activationIdSets = randomSets(this.persistedActivationIds)
        this.searchParameters = generateSearchParameters()
    }

    def userIdPool = ['user1']
    def assessmentScopedIdentifierPool  = [new ScopedIdentifier('namespace1', 'assessment1')]
    def locationIdPool = ['-1']
    def creatorIdPool = ['creatorId1']

    UserAccountDto createTestUserAccountDto(String id, String name, boolean isStudent) {
        def user = new UserAccountDto()
        user.setUserAccountId(id)
        user.setUsername(name)
        user.setDisplayName(name)
        def securityGroups = new HashSet<SecurityGroupDto>()
        if (isStudent) {
            securityGroups.add(new SecurityGroupDto(groupName: "STUDENT", securityGroupId: randomInt(1,1000000), description: randomString()))
        }
        user.setSecurityGroupDtos(securityGroups)
        return user
    }

    UserAccountDto studentForActivation(Activation activation) {
        createTestUserAccountDto(activation.userId, randomId(), true)
    }

    //a random activation that has not been persisted
    ProtoActivation randomActivation(Map parameters = [:]) {
        boolean requirePasscode = parameters['requirePasscode']?:true
        int locId = parameters['locationId']
        def activation  = new ProtoActivation()
        activation.with {
            userId = parameters.userId?:randomPooledId(userIdPool)
            assessmentScopedIdentifier = randomPooledObject(assessmentScopedIdentifierPool , { randomScopedIdentifier() })
            if (requirePasscode) {
                assessmentScopedIdentifier.namespace = "requirePasscode"
            }
			
            locationId = locId?:randomId()
            locationIdPool << locationId
            startDate = new  Date()
            if (randomBoolean()) {
                startDate = randomDate()
            }
            if (randomBoolean()) {
                endDate = randomDate()
            }
            if (endDate?.before(startDate)) {
                def temp = endDate
                startDate = endDate
                endDate = temp
            }
            while (randomBoolean()) {
                attributes.put(randomId(), rand.nextInt().toString())
            }
            deliveryType = randomDeliveryType()
        }
        return activation
    }

    List<Activation> makeSomeActivations() {
        def activations = [randomActivation()]
        repeat(10) { activations << randomActivation() }
        return activations
    }


    Activation createPersistedActivation(Map parameters = [:]) {
		def activation = randomActivation(parameters)
		
		if(activation.assessmentScopedIdentifier == null)
			activation.assessmentScopedIdentifier = new ScopedIdentifier(randomId(),randomId())
        def id = service.create(randomId(), activation)
        service.find(id)
    }

    String randomPersistedActivation(Map parameters = [:]) {
        Activation activation = createPersistedActivation(parameters)
        def id = activation.activationId
        def user = studentForActivation(activation)
        if (randomBoolean()) {
            service.cancel(id, user, "")
        } else if (randomBoolean()) {
            if (activation.status == Activation.Status.READY) {
                service.launch(id, user, randomId())
                if (randomBoolean()) {
                    service.pause(id, user)
                }
            }
        }
        return id
    }

    List<String> makeSomePersistedActivations() {
        def activations = [randomPersistedActivation()]
        repeat(10) { activations << randomPersistedActivation() }
        return activations
    }


    List<Activation> generatePersistedActivations(int size) {
        return generatePersistedActivationsWithParameters(size, [:]) { true }
    }

    List<Activation> generatePersistedActivations(int size, Closure closure) {
        return generatePersistedActivationsWithParameters(size, [:], closure)
    }

    Activation makeActivationReady(Activation activation) {
        activation.startDate = new Date()
        activation.endDate = new Date(System.currentTimeMillis() + 864000000l)
        service.dao.update(activation)
        def proctor = createTestUserAccountDto(randomId(), randomId(), false)
        def student = studentForActivation(activation)
        switch(activation.status){
            case Activation.Status.DEACTIVATED:
                service.reactivate(activation, proctor)
                break
            case Activation.Status.INCOMPLETE:
            case Activation.Status.PAUSED:
                service.launch(activation, student, proctor.userAccountId)
            default:  true//nothing
        }
        service.find(activation.activationId)
    }


    List<Activation> generateReadyActivations(int size) {
        generatePersistedActivations(size).collect { makeActivationReady(it) }
    }

    List<Activation> generateInProgressActivations(int size) {
        generateReadyActivations(size).collect { activation ->
            def user = studentForActivation(activation)
            service.launch(activation, user, randomId())
            service.find(activation.activationId)
        }
    }

    List<Activation> generatePausedActivations(int size) {
        generateInProgressActivations(size).collect { activation ->
            def user = studentForActivation(activation)
            service.pause(activation, user)
            service.find(activation.activationId)
        }
    }

    List<Activation> generateExpiredActivations(int size) {
        def activations = generatePersistedActivations(size)
        activations.each { act ->
            act.endDate = new Date(System.currentTimeMillis() - 1000);
            act.startDate = new Date(act.endDate.getTime() - 1000);
            service.dao.update(act)
        }
    }

    List<Activation> generatePersistedActivationsWithParameters(int size, Map parameters, Closure condition) {
        def activations = []
        def count = 0
        while (activations.size() < size && count++ < 1000) {
            def id = randomPersistedActivation(parameters)
            persistedActivationIds << id
            def activation = service.find(id)
            if (condition(activation)) {
                activations << activation
            }
        }
        return activations
    }


     def randomSubset(List set, Integer maxSize = null) {
        maxSize = (maxSize?:set.size() >= set.size() ? set.size() : maxSize)
        def size = rand.nextInt(maxSize)
        def subset = new HashSet()
        for (int i=0; i<size; ++i) {
            subset.add(set[rand.nextInt(set.size())]) //we should probably sample without replacement, but this is good enough I guess
        }
        return subset
    }

    def randomStatus() {
        def index = rand.nextInt(7)
        return  Activation.Status.values()[index]
    }

    List<Set<String>> randomSets(List<String> set) {
        def sets = [randomSubset(set)]
        while (randomBoolean()) {
            sets << randomSubset(set)
        }
        return sets
    }

    def easySearchParameter() { //a really simple search query
        def parameters = new SearchParameters()
        parameters.userIds = new HashSet<String>(1)
        parameters.userIds.add('AUser')
        return parameters
    }

    SearchParameters randomSearchParameters()  {
        def parameters = new SearchParameters()
        def choice = rand.nextFloat()
        //First set one of the mandatory parameters
        if (choice < 1/2) {
            parameters.userIds = randomSubset(userIdPool, 3)
        } else if (choice > 2/3) {
            parameters.locationIds = randomSubset(locationIdPool, 3)
        } else {
            parameters.creatorIds = randomSubset(creatorIdPool + "bogus", 3)
        }
        if(randomBoolean()) {
            parameters.userIds = randomSubset(userIdPool + "bogus", 3)
        }
        if (randomBoolean()) {
            parameters.creatorIds = randomSubset(creatorIdPool + "bogus", 3)
        }
        if (randomBoolean()) {
            parameters.locationIds = randomSubset(locationIdPool, 3)
        }
        //Setup date parameters carefully so we can construct an activation that satisfies them
        def r = new DateTime(randomDate())
        if (randomBoolean()) {
            parameters.minStartDate = r.toDate()
        }
        if (randomBoolean()) {
            parameters.maxStartDate = r.plusDays(1).toDate()
        }
        if (randomBoolean()) {
            parameters.minEndDate = r.plusDays(service.minDurationDays-1).toDate()
        }
        if (randomBoolean()) {
            parameters.maxEndDate = r.plusDays(service.minDurationDays+2).toDate()
        }
        if (randomBoolean()) {
            parameters.minStatusUpdateDate = randomDate()
        }
        if (randomBoolean()) {
            parameters.maxStatusUpdateDate = new DateTime(parameters.minStatusUpdateDate?:randomDate()).plusDays(1).toDate()
        }
        parameters.setIncludeCanceled(randomBoolean())
        return parameters
    }


    def computeStartDateForQuery(minStart, maxStart, minEnd, maxEnd) {
        if (minStart != null) {
            minStart
        } else if (maxStart != null) {
            new DateTime(maxStart).plusDays(-1).toDate()
        } else if (minEnd != null) {
            new DateTime(minEnd).plusDays(-(service.minDurationDays-1)).toDate()
        } else if (maxEnd != null) {
            new DateTime(maxEnd).plusDays(-(service.minDurationDays+21)).toDate()
        } else {
            null
        }
    }


    List<SearchParameters> generateSearchParameters() {
        def parameters = [easySearchParameter()]
        (1..5).each { parameters << randomSearchParameters() }
        return parameters
    }

    boolean isEmpty(Collection collection) {
       return collection == null || collection.size() == 0
    }

    def getAnIdFrom(ids, emptyValue) {
       return (isEmpty(ids) ? emptyValue : ids.toList().get(0))
    }

    boolean isBefore(Date date1, Date date2) {
        return date1 != null && date2 != null && date1.before(date2)
    }

    boolean isQueryUnsatisfiable(SearchParameters query) {

        [query.userIds,  query.locationIds, query.creatorIds].any { set ->
            set != null && set.size() == 0 } ||
                isBefore(query.maxEndDate, query.minStartDate) ||
                isBefore(query.maxEndDate, query.maxStartDate) ||
                isBefore(query.maxEndDate, query.minEndDate) ||
                isBefore(query.minEndDate, query.maxStartDate) ||
                isBefore(query.minEndDate, query.minStartDate) ||
                isBefore(query.maxStartDate, query.minStartDate) ||
                isBefore(query.maxStatusUpdateDate, query.minStatusUpdateDate)
    }

    Activation activationForQuery(SearchParameters query) {
        if (isQueryUnsatisfiable(query)) {
            null
        } else if (randomBoolean()) {
            individualActivationForQuery(query)
        } else {
            testEventActivationForQuery(query)
        }

    }

    IndividualActivation individualActivationForQuery(SearchParameters query) {
        ProtoActivation proto = new ProtoActivation()
        proto.userId = getAnIdFrom(query.userIds, randomId())
        proto.assessmentScopedIdentifier = randomScopedIdentifier()
        proto.locationId = getAnIdFrom(query.locationIds, -1)
        def start = computeStartDateForQuery(query.minStartDate, query.maxStartDate, query.minEndDate, query.maxEndDate)
        proto.startDate = (start == null ? randomDate() : start)
        proto.deliveryType = randomDeliveryType()
        def creatorId = getAnIdFrom(query.creatorIds, randomId())
        def activationId = service.create(creatorId, proto)
        this.persistedActivationIds << activationId

        if (query.isIncludeCanceled() && randomBoolean()) {
            def randomId = randomId()
            def user = createTestUserAccountDto(randomId, "Fred " + randomId, false)
            service.cancel(activationId, user, "")
        }
        def activation = service.find(activationId)
        fixStatusUpdate(activation, query)
    }

    Activation fixStatusUpdate(Activation activation, SearchParameters query) {
        def statusUpdateTooSmall = isBefore(activation.statusUpdateDate, query.minStatusUpdateDate)
        def statusUpdateTooLarge = isBefore(query.maxStatusUpdateDate, activation.statusUpdateDate)
        if (statusUpdateTooSmall) {
            activation.statusUpdateDate = query.minStatusUpdateDate
        }
        if (statusUpdateTooLarge) {
            activation.statusUpdateDate = query.maxStatusUpdateDate
        }
        if (statusUpdateTooSmall || statusUpdateTooLarge) {
            service.dao.update(activation)
            activation = service.find(activation.activationId)
        }
        activation
    }

    TestEventActivation testEventActivationForQuery(SearchParameters query) {
        TestEvent event = testEventForQuery(query)
        def userId = getAnIdFrom(query.userIds, randomId())
        def creatorId = getAnIdFrom(query.creatorIds, randomId())
        def creator = createTestUserAccountDto(creatorId, randomString(), false)
        testEventService.createActivationsFor(creator, event, [userId, randomId()].toSet())
        def activations = service.findActivationsByTestEventId(event.testEventId)
        this.persistedActivationIds.addAll(activations.collect { it.activationId })
        def activation = activations.find { it.userId == userId }
        if (query.isIncludeCanceled() && randomBoolean()) {
            service.cancel(activation.activationId, creator, "no reason")
            activation = service.find(activation.activationId)
        }
        fixStatusUpdate(activation, query)
    }


    TestEvent testEventForQuery(SearchParameters query) {
        TestEvent event = randomTestEvent()
        if (query.locationIds != null && query.locationIds.size() != 0) {
            event.testLocationId = query.locationIds[0]
        }
        def startDate = computeStartDateForQuery(query.minStartDate, query.maxStartDate, query.minEndDate, query.maxEndDate)
        if (startDate != null) {
            event.startDate = startDate
        }
        event.endDate =
                (query.maxEndDate != null ? event.endDate = query.maxEndDate
                        : (query.minEndDate != null && query.minEndDate.after(event.startDate)) ? query.minEndDate
                        : event.startDate.plus(1)
        )
        event.testEventId = testEventService.create(randomId(), event)
        persistedTestEventIds << event.testEventId
        event
    }

    PasscodeServiceConfiguration randomConfiguration() {
        def config = new PasscodeServiceConfiguration(
                passcodeLength: randomInt(2,10)
        )
        return config
    }

    TestEvent randomTestEvent(PasscodeServiceConfiguration passcodeConfiguration = randomConfiguration()) {
        def startDate = randomDate()
        new TestEvent(
                name: randomWord(),
                startDate: startDate,
                endDate: startDate.plus(1),
                collegeId: randomId(),
                testLocationId: randomId(),
                deliveryType: randomDeliveryType(),
                proctorFirstName: randomWord(),
                proctorLastName: randomWord(),
                proctorEmail: randomEmail(),
                proctorPhone: randomPhone(),
                assessmentScopedIdentifiers:  randomSet(5) { new ScopedIdentifier(namespace: randomId(), identifier: randomId()) },
                createDate: new Date(),
                createdBy: randomId(),
                updateDate: new Date(),
                updatedBy: randomId(),
                remotePasscode: randomPasscode(passcodeConfiguration.prefix, passcodeConfiguration.passcodeLength)
        )
    }


}
