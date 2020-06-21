package org.cccnext.tesuto.placement.service

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.Discipline
import org.cccnext.tesuto.placement.model.DisciplineSequence
import org.cccnext.tesuto.placement.view.CourseViewDto
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification
import org.cccnext.tesuto.placement.service.PlacementHelperService
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto

class PlacementHeplerServiceSpec extends Specification {
    @Shared SubjectAreaGenerator generator
    @Shared PlacementHelperService service

    def setupSpec() {
        def context = new ClassPathXmlApplicationContext("/test-application-context.xml")
        service = context.getBean("placementHelperService")
        if(service == null)
            throw new Exception("no service")
        generator = new SubjectAreaGenerator(context.getBean("subjectAreaService"))
    }

    def "Sorting Discipline Sequences Results In Proper Ordering of Transfer Level and Course Group"() {
        when: def orderedSequences = service.orderDisciplineSequences(sequences)
        then:
        assert assertInOrder(orderedSequences)
        where: sequences <<  [randomSetSequences(50)]
    }
    
    def "Find Index Closest To Transfer Level"() {
        when:
        def  sequences =  removeDuplicateLevels(randomSetSequences(6))
        def cb21 = generator.getRandomCb21()
        def orderedSequences = service.orderDisciplineSequences(sequences)
        def foundSequence = service.findSequenceClosestToTransferLevel(orderedSequences.toList(), cb21.levelsBelowTransfer)
        def  foundIndex = orderedSequences.indexOf(foundSequence)
        then:
            assert assertClosestSequence(orderedSequences, foundSequence, foundIndex)
        where: i << (1..10)
    }
    
    def "Find Closest To Transfer Level With Programs"() {
        when:
        def  sequences =  randomSetSequences(50)
        def cb21 = generator.getRandomCb21()
        def orderedSequences = service.orderDisciplineSequences(sequences)
        def programs = (0..generator.randomRangeInt(0,3)).collect({generator.randomMmapEquivalentCode()}).toSet()
        def foundSequence = service.findClosestToTransferLevel(sequences, cb21.levelsBelowTransfer, programs)
        def  foundIndex = orderedSequences.indexOf(foundSequence)
        then:
            assert assertClosestSequence(orderedSequences, foundSequence, foundIndex, programs)
        where: i << (1..10)
    }
    
    def "Filter Discipline Sequences By Programs"() {
        when: 
        def  sequences =  randomSetSequences(50)
        def programs = (1..3).collect({generator.randomMmapEquivalentCode()}).toSet()
        def filteredSequences = service.filterByPrograms(sequences.toList(), programs)
        then:
            assert assertSequencePrograms(filteredSequences, programs)
        where: where: i << (1..3)
    }

    def randomSetSequences(total) {
        def Set<DisciplineSequenceViewDto> sequences = [];
        for(int i; i < total ;i++) {
            sequences << generator.randomDisciplineSequenceViewDto((Integer)1)
        }
        sequences
    }
    
    def Set<DisciplineSequenceViewDto> removeDuplicateLevels(Set<DisciplineSequenceViewDto> sequences) {
        Map sequenceMap = [:]
        sequences.forEach({sequenceMap.put(it.cb21Code, it)})
        sequenceMap.values()
    }

    def boolean assertInOrder(sequences) {
        for(int i = 1; i < sequences.size(); i++) {
            DisciplineSequenceViewDto sequence1 = sequences[i - 1]
            DisciplineSequenceViewDto sequence2 = sequences[i ]
            if(sequence1.getLevel().intValue() > sequence2.getLevel().intValue()) {
                return false;
            }
            if(sequence1.getLevel().intValue() == sequence2.getLevel().intValue()) {
                if(sequence1.getCourseGroup().intValue() < sequence2.getCourseGroup().intValue()) {
                    return false;
                }
            }
        }
        return true
    }

    def boolean assertSequencePrograms(sequences, programs) {
        for(int i = 0; i < sequences.size(); i++) {
            DisciplineSequenceViewDto sequence = sequences[i ]
            def hasProgram = false
            sequence.getCourses().forEach({
                if(programs.contains(it.mmapEquivalentCode)) {
                    hasProgram = true
                }
            })
            if(!hasProgram) {
                return false;
            }
        }
        return true
    }
    
    def boolean assertClosestSequence(sequences, foundSequence, foundIndex) {
        boolean indexMatches = false
         for(int i = 0; i < sequences.size(); i++) {
             DisciplineSequenceViewDto sequence = sequences[i ];
             if(i == foundIndex) {
                 indexMatches = true
             }
             if(sequence.getLevel() >= foundSequence.getLevel() && indexMatches == false) {
                 return false;
             }
             
             if(sequence.getLevel() < foundSequence.getLevel() && indexMatches == true) {
                 return false;
             }
         }
         return indexMatches;
         
    }
    
    def boolean assertClosestSequence(sequences, foundSequence, foundIndex, programs) {
        boolean indexMatches = false
         for(int i = 0; i < sequences.size(); i++) {
             DisciplineSequenceViewDto sequence = sequences[i ];
             if(i == foundIndex) {
                 indexMatches = true
             }
             if(CollectionUtils.isNotEmpty(programs)) {
                 if(!service.hasProgram(sequence, programs)){
                     continue;
                 }
             }
             if(sequence.getLevel() > foundSequence.getLevel() && indexMatches == false) {
                 return false;
             }
             
             if(sequence.getLevel() < foundSequence.getLevel() && indexMatches == true) {
                 return false;
             }
         }
         return indexMatches;
         
    }
}