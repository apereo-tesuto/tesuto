package org.cccnext.tesuto.placement.service;

import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification
import org.cccnext.tesuto.util.test.Generator
import org.cccnext.tesuto.placement.model.PlacementEventLog

import org.cccnext.tesuto.placement.model.PlacementEventLog.EventType


class PlacementEventLogServiceSpec extends Specification {

	@Shared PlacementEventLogService logService
	@Shared Generator generator

	def setupSpec() {

		def context = new ClassPathXmlApplicationContext("/test-application-context.xml")
		logService = context.getBean("placementEventLogService")
		generator = new Generator()
	}


	def generatePlacementEventLog() {
		new PlacementEventLog(
                trackingId: generator.randomId(),
                cccId: generator.randomId(),
				subjectAreaId: generator.randomInt(1,100),
				subjectAreaVersionId: generator.randomInt(1,100),
                misCode: generator.randomId(),
                event: generator.randomMember(EventType.values()),
                message: generator.randomString()
        )
	}

	boolean almostEqual(PlacementEventLog log1, PlacementEventLog log2) {
		log1.trackingId == log2.trackingId && log1.cccId == log2.cccId && log1.subjectAreaId == log2.subjectAreaId &&
				log1.subjectAreaVersionId == log2.subjectAreaVersionId && log1.misCode == log2.misCode &&
				log1.event == log2.event && log1.message == log2.message
	}

    def "A log can be created and retrieved"() {
        when:
        def event = generatePlacementEventLog()
        logService.log(event.trackingId, event.cccId, event.subjectAreaId, event.subjectAreaVersionId, event.misCode, event.event, event.message)
		def saved = logService.findLogsByTrackingId(event.trackingId).find { almostEqual(it, event) }
        then:
		saved != null
		saved.createDate.after(new Date(System.currentTimeMillis() - 5000l))
		cleanup: if (saved != null) logService.delete(saved.placementEventLogId)
    }
}


