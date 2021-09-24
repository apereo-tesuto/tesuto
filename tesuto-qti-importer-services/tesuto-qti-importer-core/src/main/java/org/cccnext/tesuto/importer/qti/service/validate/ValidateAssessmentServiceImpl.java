/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.importer.qti.service.validate;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;
import org.cccnext.tesuto.content.service.scoring.ExpressionEvaluationService;
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentService;
import org.cccnext.tesuto.service.importer.validate.ValidatedNode;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;
import org.cccnext.tesuto.util.ValidationUtil;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;



import uk.ac.ed.ph.jqtiplus.node.content.xhtml.table.Col;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class ValidateAssessmentServiceImpl implements ValidateAssessmentService {
    private final String EXIT_TEST = "EXIT_TEST";
    private final String EXIT_TESTPART = "EXIT_TESTPART";
    private final String EXIT_SECTION = "EXIT_SECTION";

    private ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> branchRulesMap; // sectionid -> branchrules

    private ConcurrentHashMap<String, String> nextSectionMap; // sectionid -> next sectionid

    ExpressionEvaluationService expressionEvaluationService;

    public ExpressionEvaluationService getExpressionEvaluationService() {
        return expressionEvaluationService;
    }

    public void setExpressionEvaluationService(ExpressionEvaluationService expressionEvaluationService) {
        this.expressionEvaluationService = expressionEvaluationService;
    }

    /**
     * Iterates through a list of assessments checking for cycles. Also verifies
     * branch rule target exists.
     *
     * Cycles are given by two scenarios. 1. An assessment has two sections with
     * the same id. Section 1 Section 2 Section 1 (same id as first section) 2.
     * Branch rules are evaluated on there own and should not be treated as a
     * linear assessment Section 1 -- always evaluated first Branches to section
     * 3 Section 2 Branch rule target to section 1 cycle Section 3 Branch rule
     * target to section 2
     *
     * @param assessmentDtos
     * @param setMap : item id -> all possible scores for that item
     */
    @Override
    public List<ValidationMessage> processAssessments(List<AssessmentDto> assessmentDtos, HashMap<String, SortedSet<Double>> setMap) {
        branchRulesMap = new ConcurrentHashMap<>();
        nextSectionMap = new ConcurrentHashMap<>();
        DefaultDirectedGraph<String, DefaultEdge> branchRuleGraph = new DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        List<ValidationMessage> errors = new ArrayList<>();
        assessmentDtos.forEach(assessmentDto -> {
            log.debug("Assessment Validation Started for {}", assessmentDto.getIdentifier());
            List<AssessmentSectionDto> sectionDtos = assessmentDto.getAssessmentParts().get(0).getAssessmentSections();

            log.debug("Check for cycles for {}", assessmentDto.getIdentifier());
            errors.addAll(checkForCycles(sectionDtos, branchRuleGraph));
            log.debug("Verify paper assessment for {}", assessmentDto.getIdentifier());
            errors.addAll(verifyPaperAssessment(assessmentDto));

            log.debug("Verify branch rule outcomes are possible for assessment {}", assessmentDto.getIdentifier());
            errors.addAll(verifyBranchRuleOutcomesArePossibleTestPart(sectionDtos, setMap, branchRulesMap));
            log.debug("Assessment Validation Ended for {}", assessmentDto.getIdentifier());
        });

        return errors;
    }

    //Currently 2.0 does not support branch rules at the TestPart level verification may be too strong at this level anyway
    //TODO Check to make sure the branchrule variable is actually a number and if not log warning and continue processing
    //    <baseValue > This may not be a number
    //TODO Check to make sure the branchrule variable has been calculated if not calculate to evaluate
    //     <variable identifier="testlet_id.Score"> <-- this could be a testlet that is not the current testlet
    private List<ValidationMessage> verifyBranchRuleOutcomesArePossibleTestPart(List<AssessmentSectionDto> sectionDtos,
            HashMap<String, SortedSet<Double>> setMap,
            ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> concurrentHashMap) {
        List<ValidationMessage> errors = new ArrayList<>();
        for (AssessmentSectionDto sectionDto : sectionDtos) {
            errors.addAll(verifyBranchRuleOutcomesArePossible(sectionDto, setMap, concurrentHashMap).getErrors());
        }
        return errors;
    }

    private ValidatedNode<SortedSet<Double>> verifyBranchRuleOutcomesArePossible(AssessmentSectionDto sectionDto,
            HashMap<String, SortedSet<Double>> setMap,
            ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> concurrentHashMap) {
        SortedSet<Double> possibleScores = new TreeSet<>();
        List<ValidationMessage> errors = new ArrayList<>();
        log.debug("Verify branch rule outcomes are possible for section {}", sectionDto.getId());
        //Add the scores up all items in all sections
        if (CollectionUtils.isNotEmpty(sectionDto.getAssessmentItemRefs())) {
            for (AssessmentItemRefDto assessmentItemRefDto : sectionDto.getAssessmentItemRefs()) {
                possibleScores = ValidationUtil.addPossibleScore(possibleScores, setMap.get(assessmentItemRefDto.getItemIdentifier()));
            }
        }

        if (CollectionUtils.isNotEmpty(sectionDto.getAssessmentSections())) {
            for (AssessmentSectionDto sectionDto1 : sectionDto.getAssessmentSections()) {
                ValidatedNode<SortedSet<Double>> node = verifyBranchRuleOutcomesArePossible(sectionDto1,
                        setMap, concurrentHashMap);
                errors.addAll(node.getErrors());
                possibleScores = ValidationUtil.addPossibleScore(node.getValue(), possibleScores);
            }
        }
        List<AssessmentBranchRuleDto> branchRuleDtos = concurrentHashMap.get(sectionDto.getId());
        if (CollectionUtils.isNotEmpty(branchRuleDtos)) {
            for (AssessmentBranchRuleDto branchRuleDto : branchRuleDtos) {
                if(sectionDto.getSelection() == null) {
                    ValidationMessage validationMessage = verifyBranchRuleCanEvaluateTrue(sectionDto.getId(), possibleScores, branchRuleDto);
                    if(validationMessage != null) {
                        errors.add(validationMessage);
                    }
                }
            }
        }
        log.debug("End branch rule outcomes are possible for section {}", sectionDto.getId());
        return new ValidatedNode(errors, possibleScores);
    }

    private ValidationMessage verifyBranchRuleCanEvaluateTrue(String sectionIdentifier, SortedSet<Double> set,
            AssessmentBranchRuleDto branchRuleDto) {
        if (expressionEvaluationService.evaluate(branchRuleDto, new EvaluationScoringModel(set))) {
            return null;
        }
        String errorMessage = String.format(
                "The branch score for a section %s can not achieve the score which is needed for the target %s"
                        + "\nPossible Scores for this section: %s", sectionIdentifier, branchRuleDto.getTarget(), set);
        return createAssessmentValidationError(errorMessage, "branchRule");
    }

    private List<ValidationMessage> verifyPaperAssessment(AssessmentDto assessmentDto) {
        List<ValidationMessage> errors = new ArrayList<>();
        if (assessmentDto.getAssessmentMetadata() != null && assessmentDto.getAssessmentMetadata().isPaper()) {
            errors.addAll(verifyNoBranchRulesAndNoPreconditions(assessmentDto.getAssessmentParts().get(0).getAssessmentSections()));
        }
        return errors;
    }

    private List<ValidationMessage> verifyNoBranchRulesAndNoPreconditions(List<AssessmentSectionDto> sections) {
        List<ValidationMessage> errors = new ArrayList<>();
        for (AssessmentSectionDto section : sections) {
            log.debug("Start verify no branch rules and no preconditions for section {}", section.getId());
            if (CollectionUtils.isNotEmpty(section.getBranchRules())) {
                String validationError = String.format("A paper assessment cannot have branch rules in section id %s", section.getId());
                errors.add(createAssessmentValidationError(validationError, "branchRule"));
            }

            if (CollectionUtils.isNotEmpty(section.getPreConditions())) {
                String validationError = String.format("A paper assessment cannot have preconditions in section id %s", section.getId());
                errors.add(createAssessmentValidationError(validationError, "precondition"));
            }

            if (section.getSelection() != null) {
                String validationError = String.format("A paper assessment cannot have a selection in section id %s", section.getId());
                errors.add(createAssessmentValidationError(validationError, "selection"));
            }

            if (section.getOrdering() != null && section.getOrdering().isShuffle() == true) {
                String validationError = String.format("A paper assessment cannot have shuffle set to true in section id %s", section.getId());
                errors.add(createAssessmentValidationError(validationError, "ordering"));
            }

            if (CollectionUtils.isNotEmpty(section.getAssessmentSections())) {
                errors.addAll(verifyNoBranchRulesAndNoPreconditions(section.getAssessmentSections()));
            }
            log.debug("End verify no branch rules and no preconditions for section {}", section.getId());
        }
        return errors;
    }

    private ValidationMessage createAssessmentValidationError(String message, String node){
        ValidationMessage error = new ValidationMessage();
        log.error(message);
        error.setMessage(message);
        error.setNode(node);
        error.setFileType(ValidationMessage.FileType.ASSESSMENT);
        return error;
    }

    /**
     * Check for cycles: (1) builds map of assessment section ids -> list of
     * branch rules & map of section ids -> next section id (if any duplicate
     * section ids fails validation) (2) creates a JGraphT directed graph from
     * the maps Branch Rules are evaluated as if they are all capable of being
     * true, unless they DNE in which the the next section will be treated as
     * the target Graph will always start at the first section id. Although it
     * may be "bad design" to branch to a section that occurs before it. This is
     * NOT validated.
     *
     * @param sections expects to be the sections nested in AssessmentParts[0]
     * @param graph    expects empty graph with no vertex, and no edges
     */
    private List<ValidationMessage> checkForCycles(List<AssessmentSectionDto> sections, DefaultDirectedGraph<String, DefaultEdge> graph) {
        ValidatedNode<String> lastSection = checkForCyclesInLinearAssessment(sections, null); // will init maps
        addSectionIdToNextSectionMap(lastSection.getValue(), "EXIT_TEST");
        List<ValidationMessage> errors = new ArrayList<>();
        graph.addVertex(sections.get(0).getId());
        graph.addVertex(EXIT_TEST);

        errors.addAll(lastSection.getErrors());
        errors.addAll(createBranchRuleGraphFromMap(sections.get(0).getId(), graph));
        return errors;
    }

    private ValidatedNode<String> checkForCyclesInLinearAssessment(List<AssessmentSectionDto> sections, String prevSection) {
        List<ValidationMessage> errors = new ArrayList<>();
        for (AssessmentSectionDto section : sections) {
            log.debug("Start check for cycles for section {}", section.getId());
            if (prevSection != null) {
                addSectionIdToNextSectionMap(prevSection, section.getId());
            }

            prevSection = section.getId();

            if (!branchRulesMap.containsKey(section.getId()) && CollectionUtils.isNotEmpty(section.getBranchRules())) {
                branchRulesMap.put(section.getId(), section.getBranchRules());
            }

            if (CollectionUtils.isNotEmpty(section.getAssessmentSections())) {
                ValidatedNode<String> node = checkForCyclesInLinearAssessment(section.getAssessmentSections(), prevSection);
                prevSection = node.getValue();
                errors.addAll(node.getErrors());
            }
            log.debug("End check for cycles for section {}", section.getId());
        }
        return new ValidatedNode<>(errors, prevSection);
    }

    private void addSectionIdToNextSectionMap(String prevSectionId, String sectionId) {
        if (nextSectionMap.containsKey(prevSectionId)) {
            String errorMessage = String.format("There are two sections with same identifier: %s.", prevSectionId);
            logQTIWorksDuplicateCheck(errorMessage);
        } else {
            nextSectionMap.put(prevSectionId, sectionId);
        }
    }

    private List<ValidationMessage> createBranchRuleGraphFromMap(String sectionId, DefaultDirectedGraph<String, DefaultEdge> graph) {
        List<ValidationMessage> errors = new ArrayList<>();
        if (!nextSectionMap.containsKey(sectionId)) {
            String errorMessage = String.format("The target: %s does not exist as a section identifier", sectionId);
            logQTIWorksDuplicateCheck(errorMessage);
        }
        //this section Id has already been added to the graph at this point.
        List<AssessmentBranchRuleDto> assessmentBranchRuleDtos = branchRulesMap.get(sectionId);
        if (CollectionUtils.isEmpty(assessmentBranchRuleDtos)) {
            if (nextSectionMap.containsKey(sectionId)) {
                String target = nextSectionMap.get(sectionId);
                errors.addAll(addTargetToGraph(sectionId, target, graph));
            }
        } else {
            assessmentBranchRuleDtos.forEach(assessmentBranchRuleDto -> {
                // Add Vertex and Edges
                String target = (EXIT_SECTION.equals(assessmentBranchRuleDto.getTarget())) ? nextSectionMap.get(
                        sectionId) : assessmentBranchRuleDto.getTarget();
                errors.addAll(addTargetToGraph(sectionId, target, graph));
            });
        }
        return errors;
    }

    private List<ValidationMessage> addTargetToGraph(String sectionId, String target, DefaultDirectedGraph<String, DefaultEdge> graph) {
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        List<ValidationMessage> errors = new ArrayList<>();
        if (target != null){
            if (!graph.containsVertex(target)) {
                graph.addVertex(target);
            }

            if (!graph.containsEdge(sectionId, target)) {
                graph.addEdge(sectionId, target);
            }
            // Check for cycle immediately after adding edge
            if (cycleDetector.detectCycles()) {
                String errorMessage = String.format("The section: %s has a target: %s, this creates a cycle.\n"
                                + "The following (unordered) sections will produce the cycle  %s", sectionId, target,
                        cycleDetector.findCycles());
                errors.add(createAssessmentValidationError(errorMessage, "branchRule"));
                return errors; // do not continue to process
            }

            if (!EXIT_TEST.equalsIgnoreCase(target) && !EXIT_TESTPART.equalsIgnoreCase(target)) {
                errors.addAll(createBranchRuleGraphFromMap(target, graph));
            }
        }
        return errors;
    }

    private void logQTIWorksDuplicateCheck(String errorMessage){
        log.warn(String.format("%s QTIWorks verifies this.", errorMessage));
    }
}
