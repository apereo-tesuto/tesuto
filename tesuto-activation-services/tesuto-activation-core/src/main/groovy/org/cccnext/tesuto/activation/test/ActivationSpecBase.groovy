package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.cccnext.tesuto.activation.model.*
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.admin.dto.UserAccountDto
import org.cccnext.tesuto.exception.ValidationException
import org.cccnext.tesuto.content.model.DeliveryType
import org.joda.time.DateTime
import org.springframework.security.access.AccessDeniedException
import spock.lang.Shared

import static org.cccnext.tesuto.activation.model.Activation.Status.*

abstract class ActivationSpecBase extends SpecBase {

    @Shared ActivationService service
    @Shared activationIdsToCleanup = []
    @Shared String CURRENT_USER_ID
    @Shared UserAccountDto user


    //To be called from the implementation
    def setupSpec() {
        service = activationService
        CURRENT_USER_ID = generator.randomString()
        user = generator.createTestUserAccountDto(CURRENT_USER_ID, "Fred " + CURRENT_USER_ID, false)
        testEventService.remotePasscodeService.remoteConfig = generator.randomConfiguration()
    }

    def makeActivation(String user, ProtoActivation proto) {
        String id = service.create(user, proto)
        activationIdsToCleanup << id
        return id
    }

    def makeActivation() {
        Activation activation = generator.createPersistedActivation()
        activationIdsToCleanup << activation.activationId
        return activation
    }

    def "When finding a single activation by activationId, The activation returned must have the correct id"() {
        when: def activation= service.find(activationId)
        then: activation.activationId == activationId
        where: activationId << generator.persistedActivationIds
    }

    def "When doing a find for an activation that doesn't exist, an exception is thrown"() {
        when: service.find(activationId)
        then: thrown(ActivationNotFoundException)
        where: activationId = "bogus"
    }

    def "When finding a set of activations by activationId, all the activations returned have the correct id"() {
        when:
        def activations = service.find(activationIds)
        then:
        activations.every { act ->
            activationIds.contains(act.activationId)
        }
        where:
        activationIds << generator.activationIdSets
    }


    def "After creating an activation, the activation can be found by activationId"() {
        when:
        def id = makeActivation("test", activation)
        Activation newActivation = service.find(id);
        AssessmentDto assessment = service.assessmentService.readLatestPublishedVersion(newActivation.assessmentScopedIdentifier);
        activationIdsToCleanup << id

        then:
        activation.userId == newActivation.userId
        activation.assessmentScopedIdentifier == newActivation.assessmentScopedIdentifier
        activation.locationId == newActivation.locationId
        newActivation.creatorName == service.userAccountService.getUserAccount(newActivation.creatorId).displayName
        newActivation.assessmentTitle == assessment.title
        Math.abs(activation.startDate.time - newActivation.startDate.time) < 1000
        newActivation.attributes == null //we should not be returning the attributes

        where: activation << generator.activationsToCreate
    }

    def "After creating an activation, the end date is calculated properly"() {
        when:
        def activationId = makeActivation("test", activation)
        activationIdsToCleanup << activationId
        def newActivation = service.find(activationId)

        then:
        def minEndDate = new DateTime(newActivation.getStartDate()).plusDays(service.minDurationDays)
        newActivation.getEndDate().after(minEndDate.toDate())
        newActivation.getEndDate().before(minEndDate.plusDays(1).toDate())

        where: activation << generator.randomActivation()
    }

    def "After creating an activation with the future start date, the status should be ACTIVATED"() {
        when:
          def startDate = new DateTime(DateTime.now().plusDays(3));
          activation.setStartDate(startDate.toDate());
          activation.setEndDate(startDate.plusDays(5).toDate());
          def activationId = makeActivation("test", activation)
          activationIdsToCleanup << activationId
          activation = service.find(activationId)

        then:
          activation.status == ACTIVATED
        where: activation << generator.randomActivation()
    }

    def "After creating an activation with the start date, the status should be READY"() {
        when:
          def startDate = new DateTime(DateTime.now().minusDays(1));
          activation.setStartDate(startDate.toDate());
          activation.setEndDate(startDate.plusDays(5).toDate());
          def activationId = makeActivation("test", activation)
          activationIdsToCleanup << activationId
          activation = service.find(activationId)

        then:
          activation.status == READY
        where: activation << generator.randomActivation()
    }

    def "When an activation is created the createDate and creatorId are set appropriately"() {
        when:
        def creatorId = generator.randomId()
        def now = new Date(System.currentTimeMillis() - 1000)
        def activationId = makeActivation(creatorId, activation)
        activationIdsToCleanup << activationId
        activation = service.find(activationId)
        then:
        activation.createDate.after(now)
        activation.creatorId == creatorId
        where:
        activation << generator.randomActivation()
    }


    def "Certain fields are nulled out or empty when an activation is created"() {
        when:
        def activationId = makeActivation("test", activation)
        activationIdsToCleanup << activationId
        def newActivation = service.find(activationId)
        then:
        newActivation.getCurrentAssessmentSessionId() == null
        newActivation.getAssessmentSessionIds().size() == 0
        newActivation.getStatusChangeHistory().size() == 0
        where:
        activation << generator.randomActivation()
    }


    def "After deleting an activation, the activation cannot be found"() {
        when:
        def returnedVal = service.delete(activationId)
        service.find(activationId)
        then:
        thrown(ActivationNotFoundException)
        returnedVal == true
        where:
        activationId << generator.activationIdsToDelete
    }


    def "After canceling an activation, the activation shows a status of DEACTIVATED and the userId that canceled, with the correct reason"() {
        when:
        def id = makeActivation("ICreated", proto)
        def reason = generator.randomString()
        service.cancel(id, user, reason)
        def activation = service.find(id)
        then:
        activation.status == DEACTIVATED
        activation.statusChangeHistory[0].userName == user.displayName
        activation.statusChangeHistory[0].reason == reason
        where:
        proto << [ generator.randomActivation() ]
    }


    def "After pausing an activation, the activation shows a status of PAUSED and the userId that canceled"() {
        when:
        def id = makeActivation("ICreated", proto)
        service.pause(id, user)
        def activation = service.find(id)
        activation.endDate = new Date(System.currentTimeMillis()+10000l)
        then:
        activation.status == PAUSED
        activation.statusChangeHistory[0].userName == user.displayName
        where:
        proto << [ generator.randomActivation() ]
    }


    def "A PAUSED expiration becomes INCOMPLETE after it's endDate has passed"() {
        when:
        def id = makeActivation("ICreated", proto)
        service.pause(id, user)
        def activation = service.find(id)
        def newEndDate = new DateTime(DateTime.now().minusDays(1));
        activation.endDate = newEndDate.toDate();
        then:
        activation.status == INCOMPLETE
        where:
        proto << [ generator.randomActivation() ]
    }


    def "Starting and stopping an assessment adds the correct amount of time to timeSpentOnAssessment()"() {
        when:
        long timeSpent = activation.timeSpentOnAssessment
        service.addStatusChange(user, activation, IN_PROGRESS, null, null)
        long newStart = activation.statusChangeHistory[0].changeDate.getTime()
        service.addStatusChange(user, activation, (generator.randomBoolean() ? PAUSED : COMPLETE), null, null)
        long delta = activation.statusChangeHistory[0].changeDate.getTime() - newStart

        then:
        activation.timeSpentOnAssessment == timeSpent + delta

        where:
        activation << generator.generatePausedActivations(1)
    }

    def "assessedAssessment properly updates status change history"() {
        when:
        def assessmentSessionId = service.launch(activation, user, null)
        service.assessedAssessment(assessmentSessionId, user, new Date())
        def updatedActivation = service.find(activation.getActivationId())

        then:
        updatedActivation.statusChangeHistory[0].newStatus == Activation.Status.ASSESSED

        where:
        activation << generator.generateReadyActivations(5)
    }

    def "hasBeenAssessed properly detects assessed assessments"() {
        when:
        def assessmentSessionId = service.launch(activation, user, null)
        service.assessedAssessment(assessmentSessionId, user, new Date())
        def updatedActivation = service.find(activation.getActivationId())

        then:
        service.hasBeenAssessed(updatedActivation)

        where:
        activation << generator.generateReadyActivations(5)
    }

    def "hasBeenAssessed properly detects assessments that have not been assessed"() {
        when:
        def assessmentSessionId = service.launch(activation, user, null)
        def updatedActivation = service.find(activation.getActivationId())

        then:
        !service.hasBeenAssessed(updatedActivation)

        where:
        activation << generator.generateReadyActivations(5)
    }

    def "If an activation is IN_PROGRESS, then time spent in that state is added to timeSpentOnAssessment"() {
        when:
        long delta = System.currentTimeMillis() - activation.statusChangeHistory[0].changeDate.getTime()
        then:
        activation.timeSpentOnAssessment >=  delta
        where:
        activation << generator.generateInProgressActivations(5)
    }


    def "Deleting an id that does not exist returns false"() {
        when: def returnedVal = service.delete(activationId)
        then: returnedVal == false
        where: activationId = "bogus"
    }


    def contains = { collection, member ->
        collection == null || collection.size() == 0 || collection.contains(member)
    }

    def "A test event activation returned by a search contains the test event name, id, and proctor information"() {
        when:
        def original = generator.testEventActivationForQuery(query)
        def event = original.testEvent
        def activation = searchService.search(query).find { it.activationId == original.activationId }
        then:
        activation != null
        activation.testEvent.testEventId == event.testEventId
        activation.testEvent.name == event.name
        activation.testEvent.proctorFirstName == event.proctorFirstName
        activation.testEvent.proctorLastName == event.proctorLastName
        activation.testEvent.proctorEmail == event.proctorEmail
        activation.testEvent.proctorPhone == event.proctorPhone
        where: query << [ generator.searchParameters[0] ]
    }


    def "An activation returned by a search should be well formed and meet the search criteria"() {

        when:
        generator.activationForQuery(query)
        def activations = searchService.search(query)

        then:
        activations.every { act -> this.contains(query.userIds, act.userId) }
        activations.every { act -> this.contains(query.locationIds, act.locationId) }
        activations.every { act -> this.contains(query.creatorIds, act.creatorId) }
        query.minStartDate == null || activations.every { it.startDate.time >= query.minStartDate.time }
        query.maxStartDate == null || activations.every { it.startDate.time <= query.maxStartDate.time }
        query.minEndDate == null || activations.every { it.endDate.time >= query.minEndDate.time }
        query.maxEndDate == null || activations.every { it.endDate.time <= query.maxEndDate.time }

        where: query << generator.searchParameters
    }


    def "An activation that meets a search criteria is returned by the appropriate search"() {

        when:
        def activation = tuple.activation
        def query = tuple.query

        then:
        (searchService.search(query).collect { it.activationId }).contains(activation.activationId)

        where: tuple << generator.searchParameters .
                collect { parameters ->  [query: parameters, activation: generator.activationForQuery(parameters) ] } .
                findAll { map -> map.activation != null }

    }


    def invalidQueries()  {
        generator.searchParameters.collect { query ->
            def newq = query.clone()
            newq.locationIds = null
            newq.userIds = null
            newq.creatorIds = null
            newq.currentStatus = null
            newq.deliveryType = DeliveryType.ONLINE
            newq
        }
    }


    def "Searching without userId, locationId or creatorId and deliveryType.PAPER throws an exception"() {
        when: searchService.search(query)
        then: thrown(BadSearchParametersException)
        where: query << invalidQueries()
    }


    def "If a user is returned by a userCentric search, the user has an activation that meets the search criteria"() {
        when:
        def activations = searchService.search(query)
        then:
        searchService.userCentricSearch(query).every {
            activations.any { act -> act.userId == it.userId }
        }
        where: query << generator.searchParameters
    }

    def "User centric search returns no expired or canceled activations"() {
        when: def activations = searchService.userCentricSearch(query)
        then: activations.every { it.status != DEACTIVATED && it.status != EXPIRED }
        where: query << generator.searchParameters
    }


    def isDateBefore(date1, date2) {
        date1 != null && date2 != null && date1.getTime() < date2.getTime()
    }

    //Fix a query so it doesn't include expired or canceled activations
    def fixedQuery(query) {
        def newq = query.clone()
        newq.includeCanceled = false
        newq.minEndDate = new Date(System.currentTimeMillis()+86400*1000l)
        if (isDateBefore(newq.maxEndDate, newq.minEndDate)) newq.maxEndDate = null
        if (isDateBefore(newq.maxStartDate, newq.minStartDate)) newq.maxStartDate = null
        if (isDateBefore(newq.minStartDate, newq.minEndDate)) newq.minStartDate = null
        newq
    }

    def "If a user has an activation that meets a search criteria, userCentricSearch should return all the user's non-canceled/expired activations"() {
        when:
        def activation = generator.activationForQuery(query)
        def newActivation = null
        if (activation != null) { //if activation is null, the query can't be satisfied and will return an empty set
            def proto = generator.randomActivation()
            proto.userId = activation.userId
            newActivation = service.find(makeActivation("test", proto))
        }
        def userCentric = searchService.userCentricSearch(query)
        then:
        //null activation if query is not satisfiable
        activation == null || userCentric.any { it.activationId == activation.activationId }
        newActivation == null || userCentric.any { it.activationId == newActivation.activationId } || newActivation.status in [Activation.Status.DEACTIVATED, Activation.Status.EXPIRED]

        where:
        query << generator.searchParameters.collect { fixedQuery(it) }
    }


    def "For every user who has an activation returned by userCentricSearch, an ordinary search would return at least one activation for that user that is not canceled or expired"() {
        when:
        generator.activationForQuery(query)
        def activations = searchService.userCentricSearch(query)
        def userIds = (activations.collect { it.userId }).toSet()
        def regularResults = searchService.search(query)

        then:
        userIds.every { userId ->
            regularResults.any { act ->
                act.userId == userId &&
                        act.status != Activation.Status.DEACTIVATED &&
                        act.status != Activation.Status.EXPIRED
            }
        }

        where: query << generator.searchParameters.collect { fixedQuery(it) }
    }


    def "If a user has an activation that meets a search criteria, the user is returned by the appropriate search"() {

        when:
        def activation = tuple.activation
        def query = tuple.query
        def now = System.currentTimeMillis()

        then:
        query.includeCanceled || (query.minEndDate?.time ?: 0) <= now ||
                searchService.userCentricSearch(query).any { it.userId == activation.userId }

        where: tuple << generator.searchParameters
                .collect { parameters ->  [query: parameters, activation: generator.activationForQuery(parameters) ] }
                .findAll { map -> map.activation != null }
    }


    def "After updating a search set, searches on that id return exactly the activations from the updated set"() {
        when:
        def searchId = searchService.createSearchSet(query)
        def n = generator.activationIdSets.size()
        def idSet = generator.activationIdSets[generator.rand.nextInt(n)]
        searchService.search(searchId) //just to hit the cache
        searchService.putSearchSet(searchId, idSet)
        then:
        idSet == searchService.search(searchId).get().collect { act -> act.activationId } . toSet()
        where:
        query << generator.searchParameters
    }

    def "Updating a  non-existing search set returns false"() {
        expect: !searchService.putSearchSet(generator.randomId(), generator.activationIdSets[0]);
    }

    def "Updating an existing search set returns true"() {
        when: def searchId = searchService.createSearchSet(generator.searchParameters[0])
        then: searchService.putSearchSet(searchId,generator.activationIdSets[0]);
    }


    def "After deleting a search set, searches on that searchId return Optional.empty()"() {
        when:
        def searchId = searchService.createSearchSet(query)
        searchService.search(searchId) //just to hit the cache
        searchService.deleteSearchSet(searchId)
        then:
        searchService.search(searchId) == Optional.empty()
        where:
        query << generator.searchParameters
    }

    def "After launching a READY activation, the status is in progress, and the assessment session id is set"() {
        when:
        def launchTime = new Date()
        def assessmentSessionId = service.launch(activation, user, generator.randomId())
        Activation reloadedActivation = service.find(activation.activationId)
        def history = reloadedActivation.statusChangeHistory

        then:
        reloadedActivation.assessmentSessionIds.contains(assessmentSessionId)
        reloadedActivation.currentAssessmentSessionId == assessmentSessionId
        reloadedActivation.status == IN_PROGRESS
        verifyStatusChange(history, IN_PROGRESS, launchTime, 1, CURRENT_USER_ID)

        where:
        activation << generator.generateReadyActivations(3)
    }

    def "Not allowed to launch from an activations that does not have appropriate status"() {
        when:
        service.launch(activation, user, null)
        then:
        thrown(AccessDeniedException)
        where:
        activation << generator.generatePersistedActivations(3) {
            ![READY, PAUSED, IN_PROGRESS].contains(it.status)
        }
    }


    def "when an valid activation is updated its status is also updated and both are persisted"(){
        when:
        def updateTime = new Date()
        def numberOfStatusChanges = (activation.statusChangeHistory == []) ? 1 : 2
        def randomProtoActivation = generator.randomActivation()
        def validProtoActivation = assembleValidActivation(activation, randomProtoActivation)

        service.update(user, validProtoActivation)

        def persistedActivation = service.find(activation.activationId)

        then:
        persistedActivation.locationId == activation.locationId
        persistedActivation.locationId != null
        //map comparison
        for(i in persistedActivation.attributes){
            validProtoActivation.attributes.containsKey(i.key)
            validProtoActivation.attributes[i.key] == i.value
        }

        verifyStatusChange(persistedActivation.statusChangeHistory, activation.getStatus(), updateTime, numberOfStatusChanges, CURRENT_USER_ID, "Updated")

        where:
        activation << generator.generatePersistedActivations(3){
            [ACTIVATED, READY, INCOMPLETE, EXPIRED].contains(it.status)
        }
    }

    def "when an invalid activation an validation exception is thrown"(){
        when:
        def randomProtoActivation = generator.randomActivation()
        def validProtoActivation = assembleValidActivation(activation, randomProtoActivation)
        def invalidActivation = assembleInvalidActivation(activation, validProtoActivation)

        service.update(user, invalidActivation)

        then:
        thrown(ValidationException)

        where:
        activation << generator.generatePersistedActivations(100){
            [ACTIVATED, READY, INCOMPLETE, EXPIRED].contains(it.status)
        }
    }

    ProtoActivation assembleInvalidActivation(Activation activation, ProtoActivation validActivation){
        def invalidAttribute =  generator.rand.nextInt(7)
        def invalidActivation = new ProtoActivation()
        invalidActivation.activationId  = (invalidAttribute == 0) ? null : validActivation.activationId
        invalidActivation.locationId    = (invalidAttribute == 1) ? generator.randomInt(0, Integer.MAX_VALUE) : validActivation.locationId
        invalidActivation.assessmentScopedIdentifier  = (invalidAttribute == 2) ? generator.randomScopedIdentifier() : validActivation.assessmentScopedIdentifier
        invalidActivation.userId        = (invalidAttribute == 3) ? generator.randomString() : validActivation.userId
        invalidActivation.startDate     = (invalidAttribute == 4) ? new Date() : validActivation.startDate
        invalidActivation.endDate       = (invalidAttribute == 5) ? new Date() : validActivation.endDate
        invalidActivation.deliveryType  = (invalidAttribute == 6) ? (activation.deliveryType == DeliveryType.PAPER ? DeliveryType.ONLINE : DeliveryType.PAPER) : activation.deliveryType
        return invalidActivation
    }

    ProtoActivation assembleValidActivation(Activation persistedActivation, ProtoActivation proto){
        new ProtoActivation(
                locationId: validNullOrObject(persistedActivation.locationId),
                activationId: persistedActivation.activationId,
                assessmentScopedIdentifier: validNullOrObject(persistedActivation.assessmentScopedIdentifier),
                userId: validNullOrObject(persistedActivation.userId),
                attributes: proto.attributes,
                startDate: validNullOrObject(persistedActivation.startDate),
                endDate: validNullOrObject(persistedActivation.endDate),
                deliveryType: validNullOrObject(persistedActivation.deliveryType),
        )
    }

    def validNullOrObject(def self){
        return generator.randomBoolean() ? null : self
    }

    def "After completing the current assessment, an activation has status COMPLETED"()           {
        when:
        def sessionId = service.launch(activation, user, null)
        def completionTime = new Date()
        service.completeAssessment(sessionId, user)
        Activation reloaded = service.find(activation.activationId)
        def history = reloaded.statusChangeHistory
        then:
        reloaded.getStatus() == COMPLETE
        verifyStatusChange(history, COMPLETE, completionTime, 1, CURRENT_USER_ID) // reloaded.userId?

        where:
        activation << generator.generateReadyActivations(3)
    }

    def "When activation is not paused and not in progress relaunchActivation will throw exception"() {
        when:
        service.relaunchActivation(activation, user, null)

        then:
        thrown(AccessDeniedException)

        where:
        activation << generator.generatePersistedActivations(3) { it.status != PAUSED && it.status != IN_PROGRESS }
    }

    def "When activation is paused or in progress relaunchActivation will set status to IN_PROGRESS"() {
        when:
        def completionTime = new Date()
        Activation reloaded = service.find(activation.activationId)
        service.pause(activation.activationId, user)
        service.relaunchActivation(reloaded, user, null)
        def history = reloaded.statusChangeHistory

        then:
        reloaded.getStatus() == IN_PROGRESS  //Relaunched assessments will return in progress
        verifyStatusChange(history, IN_PROGRESS, completionTime, 2, CURRENT_USER_ID)

        where:
        activation << generator.generateInProgressActivations(1) + generator.generatePausedActivations(1)
    }

    def "The current user id is set in the ActivationStatusChange as expected"() {
        when:
        def expectedUserId = generator.randomId()
        def completionTime = new Date()
        user = generator.createTestUserAccountDto(expectedUserId, "Fred " + expectedUserId, false)
        service.launch(activation, user, null) //ensure that an activation has been created
        Activation reloaded = service.find(activation.activationId)
        def history = reloaded.statusChangeHistory

        then:
        verifyStatusChange(history, IN_PROGRESS, completionTime, 1, expectedUserId)

        where:
        activation << generator.generateReadyActivations(3)
    }

    def "Reactivating a canceled activation puts it in READY status"() {
        when:
        service.cancel(activation, user, "")
        if (generator.randomBoolean()) {
            service.cancel(activation, user, "") //A race condition hit and we canceled repeatedly!
        }

        service.reactivate(activation, user);
        then:
        activation.status == READY
        where:
        activation << generator.generatePersistedActivations(3) { it.status != DEACTIVATED }
    }

    def "Reactivating a canceled activation puts it in ACTIVATED status if start date is in future"() {
        when:
            service.cancel(activation, user, "")
            if (generator.randomBoolean()) {
                service.cancel(activation, user, "") //A race condition hit and we canceled repeatedly!
            }
            def startDate = new DateTime(DateTime.now().plusDays(1));
            activation.setStartDate(startDate.toDate());
            service.reactivate(activation, user);
        then:
            activation.status == ACTIVATED
        where:
            activation << generator.generatePersistedActivations(3) { it.status != DEACTIVATED }
    }


    def "Reactivating an expired activation puts it in READY status"() {
        when:  service.reactivate(activation, user);
        then:  activation.status == Activation.Status.READY
        where: activation << generator.generateExpiredActivations(3)
    }


    def "Reactivating an expired activation puts it in ACTIVATED status if start date is in future"() {
        when:
          def startDate = new DateTime(DateTime.now().plusDays(1));
          activation.setStartDate(startDate.toDate());
          service.reactivate(activation, user);
        then:  activation.status == Activation.Status.ACTIVATED
        where: activation << generator.generateExpiredActivations(3)
    }


    def "A new activation has statusUpdateDate equal to createDate"() {
        expect: activation.statusUpdateDate == activation.createDate
        where: activation << [makeActivation()]
    }

    def "statusUpdateDate is set correctly after a status change"() {
        when:
        def id = activation.activationId
        def student = generator.createTestUserAccountDto(activation.userId, generator.randomId(), true)
        def proctor = generator.createTestUserAccountDto(activation.userId, generator.randomId(), false)
        def before = activation.statusUpdateDate
        def now = new Date()
        switch (activation.status) {
            case ACTIVATED:
                service.cancel(id, proctor, "")
                break
            case READY:
                if (generator.randomBoolean()) {
                    service.launch(id, student, proctor.userAccountId)
                } else {
                    service.cancel(id, proctor, "")
                }
                break
            case IN_PROGRESS:
                if (generator.randomBoolean()) {
                    service.pause(id, student)
                } else if (generator.randomBoolean()) {
                    service.completeAssessment(activation.currentAssessmentSessionId, student)
                } else {
                    service.launch(id, student, proctor.userAccountId)
                }
                break
            case PAUSED:
                service.launch(id, student, proctor.userAccountId)
                break
            case  DEACTIVATED:
                service.reactivate(id, proctor)
                break
        }
        def reloaded = service.find(id)
        then:
        before.time < now.time
        reloaded.statusUpdateDate.time >= now.time
        where: activation << generator.generatePersistedActivations(10)
    }

    //The following tests verify that the statusChangeHistory was stored correctly
    void verifyStatusChange(def history, def activationStatus, def completionTime, def size, def expectedUserId, def reason = null) {
        assert history.size() > 0
        assert history[0].newStatus == activationStatus
        assert history[0].userId == expectedUserId
        assert history[0].userName == "Fred ${expectedUserId}"
        assert history[0].changeDate.getTime() >= completionTime.getTime()
        assert history[0].changeDate.getTime() <= System.currentTimeMillis()
        assert history[0].reason == reason
        assert history.findAll { it.newStatus == activationStatus }.size() >= size
    }


    def "A student name should never be recorded in a status change event"() {
        when:
        def userId = activation.activationId
        user = generator.createTestUserAccountDto(userId, "Fred " + userId, true)
        def activationId = activation.activationId
        //All the service methods a student is allowed to do
        def sessionId = service.launch(activation, user, null) //start
        activation = service.find(activationId)
        service.pause(activation, user) //pause
        activation = service.find(activationId)
        service.launch(activation, user, null) //restart
        activation = service.find(activationId)
        service.launch(activation, user, null) //restart again (after a disconnection)
        service.completeAssessment(sessionId, user) //finish
        activation = service.find(activationId)

        then:activation.statusChangeHistory.every { it.userName == null }

        where: activation << generator.makeActivationReady(makeActivation())
    }

    def "Can create a test Event and a related Activation"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        then:
        service.find(activationId).testEvent.testEventId == testEventId
        cleanup:
        if (testEventId != null) {
            service.findActivationsByTestEventId(testEventId).each { service.delete(it.activationId) }
            testEventService.delete(testEventId)
        }
    }


    def "FindByTestEventId selects activations with the correct testEventId, and no others"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def results = service.findActivationsByTestEventId(testEventId)
        then:
        results.every { it.testEvent.testEventId == testEventId }
        results.any { it.activationId == activationId }
        cleanup:
        if (results != null) results.each { service.delete(it.activationId) }
        if(testEventId != null) testEventService.delete(testEventId)
    }


    def "SummarizeByLocation() is correct"() {
        when:
        def activations = searchService.search(query)
        def numCanceled = activations.findAll({ it.status == DEACTIVATED }).size()
        def summary = searchService.summarizeByLocation(query)
        def summaryCanceled = summary.collect({ key, map -> map[DEACTIVATED]?:0 }).sum()?:0
        then:  numCanceled == summaryCanceled
        where: query << generator.searchParameters
    }

    //not really a test, it just to kicks the report method to make sure the query doesn't blow up
    def "test reports"() {
        when:
        def from = new GregorianCalendar()
        from.add(Calendar.DAY_OF_YEAR, -1)
        def now = new GregorianCalendar()
        service.report(from,now);
        then: true
    }

    def "createActivationStatusChangeRow creates the expected comma delimited string"(){
        when:
        def activationId = randomString()
        def assessmentTitle = randomString()
        def activationUserId = randomString()
        def activation = createActivation(activationId, assessmentTitle, activationUserId)

        def userId = randomString()
        def userName = randomString()
        def newStatus = generator.randomMember(Activation.Status.enumConstants)
        def statusChange = createActivationStatusChange(userId, userName, newStatus)

        def expectedString = createExpectedRow(activationId ,assessmentTitle, activationUserId, userId, userName, newStatus)

        then:
        expectedString == service.createActivationStatusChangeRow(activation, statusChange)

        where:
        i << (1..10)
    }

    def "createActivationStatusChangeRows creates the expected map"(){
        when:
        def activationId = randomString()
        def assessmentTitle = randomString()
        def activationUserId = randomString()
        def activation = createActivation(activationId, assessmentTitle, activationUserId)

        def userId = randomString()
        def userName = randomString()
        def newStatus = generator.randomMember(Activation.Status.enumConstants)
        def count = generator.rand.nextInt(5) + 1
        def beforeServiceCall = new Date()
        addStatusChangeToActivationDesiredNumberOfTimes(activation, userId, userName, newStatus, count)
        def expectedKey = createExpectedRow(activationId ,assessmentTitle, activationUserId, userId, userName, newStatus)
        def map = service.createMapOfActivationStatusChangeReportRows(activation)
        then:
        verifyDates(beforeServiceCall, count, map.get(expectedKey))

        where:
        i << (1..10)
    }

    void verifyDates(def beforeServiceCall, def expectedNumberOfDates, def listOfDates){
        (0..<expectedNumberOfDates).forEach({
            assert listOfDates[it].getTime() >= beforeServiceCall.getTime()
            assert listOfDates[it].getTime() <= System.currentTimeMillis()
        })
    }

    def "createCompleteMapForActivationStatusChangeReport creates the expected map"(){
        when:
        //Create the first activation
        def activationId1 = generator.randomString() //not null so we have two different keys
        def assessmentTitle1 = randomString()
        def activationUserId1 = randomString()
        def activation1 = createActivation(activationId1, assessmentTitle1, activationUserId1)

        //Add a bunch of status changes
        def userId1 = randomString()
        def userName1 = randomString()
        def newStatus1 = generator.randomMember(Activation.Status.enumConstants)
        def count1 = generator.rand.nextInt(5) + 1
        def beforeServiceCall1 = new Date()
        addStatusChangeToActivationDesiredNumberOfTimes(activation1, userId1, userName1, newStatus1, count1)

        def expectedKey1 = createExpectedRow(activationId1, assessmentTitle1, activationUserId1, userId1, userName1, newStatus1)

        //Create the second activation
        def activationId2 = generator.randomString() //not null so we have two different keys
        def assessmentTitle2 = randomString()
        def activationUserId2 = randomString()
        def activation2 = createActivation(activationId2, assessmentTitle2, activationUserId2)

        //Add a bunch of status changes to the second activation
        def userId2 = randomString()
        def userName2 = randomString()
        def newStatus2 = generator.randomMember(Activation.Status.enumConstants)
        def count2 = generator.rand.nextInt(5) + 1
        def beforeServiceCall2 = new Date()
        addStatusChangeToActivationDesiredNumberOfTimes(activation2, userId2, userName2, newStatus2, count2)

        def expectedKey2 = createExpectedRow(activationId2, assessmentTitle2, activationUserId2, userId2, userName2, newStatus2)

        //Add the activations to the collection
        Collection<Activation> activations = new ArrayList<>(2)
        activations.add(activation1)
        activations.add(activation2)


        def map = service.createCompleteMapForActivationStatusChangeReport(activations)

        then:
        verifyDates(beforeServiceCall1, count1, map.get(expectedKey1))
        verifyDates(beforeServiceCall2, count2, map.get(expectedKey2))

        where:
        i << (1..10)
    }


    def "After adding users to a test event, we can find the correct activations for those users"() {
        setup:
        def event = generator.randomTestEvent()
        def eventId = testEventService.create(generator.randomId(), event)
        event.testEventId = eventId
        def studentIds = generator.randomSet(3) { generator.randomId() }

        when:
        testEventService.createActivationsFor(user, event, studentIds)
        def activations = service.findActivationsByTestEventId(eventId)

        then:
        activations.collect( { it.userId} ).containsAll(studentIds)
        activations.collect( { it.assessmentScopedIdentifier }).toSet() == event.assessmentScopedIdentifiers
        activations.each { act ->
            assert act.startDate == event.startDate
            assert act.endDate == event.endDate
            assert act.locationId == event.testLocationId
            assert act.deliveryType == event.deliveryType
            assert act.creatorId == user.userAccountId
        }

        cleanup:
        if (activations != null) {
            activations.each { service.delete(it.activationId) }
        }
        if (event != null)  {
            testEventService.delete(eventId)
        }
    }

    def "Regular activations are not TestEventActivations"() {
        when:
        def activationId = generator.randomString()
        def assessmentTitle = randomString()
        def activationUserId = randomString()
        def activation = createActivation(activationId, assessmentTitle, activationUserId)

        then:
        !service.isTestEventActivation(activation)
    }

    // This might seem silly, but we really do need to make sure that we detect
    // them properly so that we don't allow them to be reactivated.
    def "TestEventActivations are detected properly"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def activation = service.find(activationId)

        then:
        service.isTestEventActivation(activation)
    }

    void addStatusChangeToActivationDesiredNumberOfTimes(activation, requestorId, requestorUserName, newStatus, iterations) {
        iterations.times {
            activation.addStatusChange(requestorId, requestorUserName, null, newStatus, null)
        }

    }

    String createExpectedRow(def id, def title, def actUserId, def requestorId, def userName, def status){
          return createExpectedString(id) + ',' +
                 createExpectedString(title) + ',' +
                 createExpectedString(actUserId) + ',' +
                 createExpectedString(requestorId) + ',' +
                 createExpectedString(userName)  + ',' +
                 createExpectedString(status)
    }

    String createExpectedString(def string){
        if(string == null){
            return '""'
        }
        else{
            return '"' + string + '"'
        }
    }
    String randomString(){
        return generator.randomBoolean() ? null : generator.randomString()
    }
    Activation createActivation(def id, def title, activationUserId){
        return new IndividualActivation(
                activationId: id,
                assessmentTitle: title,
                userId: activationUserId
        )

    }

    ActivationStatusChange createActivationStatusChange(def userId, def userName, def newStatus){
        return new ActivationStatusChange(userId, userName, null, newStatus, new Date(), randomString())
    }


    def cleanupSpec() {
        def toCleanup = generator.persistedActivationIds +  this.activationIdsToCleanup
        toCleanup.each { service.delete(it) }
        generator.persistedTestEventIds.each { testEventService.delete(it) }
    }
}
