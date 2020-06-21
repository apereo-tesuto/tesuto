package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.placement.model.*
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto
import org.cccnext.tesuto.util.test.Generator

class CompetencyAttributesGenerator extends SubjectAreaGenerator {

    public CompetenceAttributesGenerator() {
    }

    CompetencyAttributes randomCompetencyAttribute(String competency) {
        def  attr  = CompetencyAttributes.createInstance(competency)
        attr.optInMultiMeasure = randomBoolean()
        attr.useSelfReportedDataForMM = randomBoolean()
        attr.highestLevelReadingCourse = randomString(1,50)
        attr.showPlacementToEsl = randomBoolean()
        attr.showPlacementToNativeSpeaker = randomBoolean()
        return attr
    }

    CompetencyAttributesViewDto randomCompetencyAttributesViewDto(String competencyCode) {
        return new CompetencyAttributesViewDto(
            competencyCode: competencyCode,
            competencyAttributeId: randomInt(1,10000),
            optInMultiMeasure: randomBoolean(),
            useSelfReportedDataForMM: randomBoolean(),
            highestLevelReadingCourse: randomString(1,120),
            showPlacementToEsl: randomBoolean(),
            showPlacementToNativeSpeaker: randomBoolean(),
            prerequisiteGeneralEducation: randomString(1,120),
            prerequisiteStatistics: randomString(1,120)
        )
    }

    CompetencyAttributesViewDto copyCompetencyAttributesViewDto(CompetencyAttributesViewDto original) {
        return new CompetencyAttributesViewDto(
            competencyCode: original.competencyCode,
            competencyAttributeId: original.competencyAttributeId,
            optInMultiMeasure: original.optInMultiMeasure,
            useSelfReportedDataForMM:  original.useSelfReportedDataForMM,
            highestLevelReadingCourse:  original.highestLevelReadingCourse,
            showPlacementToEsl:  original.showPlacementToEsl,
            showPlacementToNativeSpeaker:  original.showPlacementToNativeSpeaker,
            prerequisiteGeneralEducation:  original.prerequisiteGeneralEducation,
            prerequisiteStatistics:  original.prerequisiteStatistics
        )
    }

}