package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto
import org.cccnext.tesuto.content.dto.enums.MetadataType
import spock.lang.Shared
import spock.lang.Specification

public class AssessmentMetadataDtoSpec extends Specification {

	@Shared AssessmentDtoGenerator generator = new AssessmentDtoGenerator()


	def "Given an assessmentMetadata with a requirePasscode, isRequirePasscode will return the expected boolean Yes/True true, No/False/null false"() {
		when:
		def expected = generator.randomBoolean()
		def assessmentMetadata = generator.randomNotNullAssessmentMetadata()
		assessmentMetadata.requirePasscode = randomPassCodeRequired(expected)

		then:
		assessmentMetadata.isRequirePasscode() == expected

		where:
		i << (1.10)
	}

	String randomPassCodeRequired(boolean expected){
		def trueStringPool = ["Yes", "True", null, generator.randomString(), "yes", "true", "yEs", "tRue"]
		def falseStringPool = ["No", "False", "no", "nO", "fAlse", "false"]
		if(expected){
			return trueStringPool[generator.rand.nextInt(trueStringPool.size())]
		}

		return falseStringPool[generator.rand.nextInt(falseStringPool.size())]
	}

	def "Given an assessmentMetadata without a type of assessmentmetadata getSectionMetadata will return empty HashMap"() {
		when:
		assessmentMetadata.type = "Not assessmentmetadata"

		then:
		assessmentMetadata.getSectionMetadata() == [:]

		where:
		assessmentMetadata << generator.randomNotNullAssessmentMetadata()
	}

	def "Given an assessmentMetadata with a null section getSectionMetadata will return empty HashMap"() {
		when:
		assessmentMetadata.type = "assessmentmetadata"
		assessmentMetadata.section = null

		then:
		assessmentMetadata.sectionMetadata == [:]

		where:
		assessmentMetadata << generator.randomNotNullAssessmentMetadata()
	}

	def "Given a randomly generated expectedMap and randomly generated Section List based on the expectedMap getSectionMetadata will return the expectedMap"() {
		when:
		HashMap expectedMap = createExpectedRandomMap(generator.rand.nextInt(4))
		def assessmentMetadata = generator.randomNotNullAssessmentMetadata()
		assessmentMetadata.type = "assessmentmetadata"
		assessmentMetadata.section = createSectionListFromMap(expectedMap)

		then:
		assessmentMetadata.sectionMetadata == expectedMap

		where:
		i << (1..5)
	}


	def "Given a map without an itembundle for a given section will return false"() {
		when:
		def sectionIdentifier = "section identifier we are looking for"
		def map = [(sectionIdentifier) : MetadataType.TESTLET,
				   "not section identifier we are looking for" : MetadataType.ITEMBUNDLE]
		assessmentMetadata.section = createSectionListFromMap(map)

		then:
		assessmentMetadata.isSectionItemBundle(sectionIdentifier) == false

		where:
		assessmentMetadata << generator.randomNotNullAssessmentMetadata()
	}

	def "Given a map with an expected sectionIdentifer key matching itemBundle value for a given section will return true"() {
		when:
		def sectionIdentifier = "section identifier we are looking for"
		assessmentMetadata.section = createSectionListFromMap([(sectionIdentifier):MetadataType.ITEMBUNDLE])
		assessmentMetadata.computeSectionMetadata()

		then:
		assessmentMetadata.isSectionItemBundle(sectionIdentifier) == true

		where:
		assessmentMetadata << generator.randomNotNullAssessmentMetadata()
	}


	List<SectionMetadataDto> createSectionListFromMap(HashMap map){
		def keys = map.keySet()
		List<SectionMetadataDto> sectionMetadataDtoList = new ArrayList<>()
		for (key in map.keySet()){
			sectionMetadataDtoList.add(createSectionMetadata(key, map[key].toString()))
		}
		return sectionMetadataDtoList;
	}

	SectionMetadataDto createSectionMetadata(String id, String type){
		return 	new SectionMetadataDto(
						identifier: id,
						type: type
				)
	}

	HashMap createExpectedRandomMap(int sizeOfMap){
		def map = new HashMap()
		for(int i=0;i<sizeOfMap; i++){
			map[generator.randomString()] = generator.randomMetadataType()
		}
		return map
	}
}