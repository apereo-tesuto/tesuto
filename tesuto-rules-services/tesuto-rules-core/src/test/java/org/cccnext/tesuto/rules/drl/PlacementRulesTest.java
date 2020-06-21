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
package org.cccnext.tesuto.rules.drl;

import static org.cccnext.tesuto.rules.drl.KieSessionTestLoader.loadKieSession;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.RULES_DIRECTORY;
import static org.cccnext.tesuto.rules.drl.MultipleMeasureFactsGenerator.initFacts;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.MmapDataSourceType;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementRulesSourceDto;
import org.cccnext.tesuto.rules.function.DrlFunctions;
import org.ccctc.common.droolscommon.RulesAction;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class PlacementRulesTest extends DecisionTreeTestBed {

    private static final String TEST_FILE_NAME = "Placement";

    private static final String HEADER_FOOTER_FILE_NAME = "Placement";

    private Map<String, Object> facts;

    @Before
    public void setUp() {
        facts = initFacts();
        facts.put("RULE_SET_ID", "12345");
    }

    @Test
    public void testPlacementResultsReturnNoResultForStandaloneButNoStandaloneComponents() throws Exception {
        final boolean isSelfReported = false; // doesn't matter here

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(new ArrayList<>());
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents().stream().forEach(it -> it.setStandalonePlacement(false));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 0);
    }

    @Test
    public void testPlacementResultsReturnNoResultForMmapOptInButNoMmapComponents() throws Exception {
        final boolean isSelfReported = false; // doesn't matter here

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.setMmapPlacementComponents(new ArrayList<>());
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 0);
    }

    @Test
    public void testPlacementResultsReturnNoResultForNoMmapOptIn() throws Exception {
        final boolean isSelfReported = false; // doesn't matter here

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(false);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 0);
    }

    @Test
    public void testPlacementResultsReturnDesiredAssessResultForMmapAndAssessAndNotSelfReported() throws Exception {
        final boolean isStandalone = true; // doesn't matter here
        final boolean isSelfReported = false;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getAssessmentPlacementComponents().add(createDesiredPlacementComponent(false, false));
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents()
                .get(ThreadLocalRandom.current().nextInt(0,placementRulesSourceDto.getMmapPlacementComponents().size()))
                .setDataSourceType(MmapDataSourceType.STANDARD); // set at least one to not self reported
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        if (DrlFunctions.getHighestAndBestPlacementComponentFromList(placementRulesSourceDto.getMmapPlacementComponents(), isStandalone, isSelfReported) == null) {
            assertTrue(actions.size() == 0); // all mmap components were self-reported. rare case!
        } else {
            assertTrue(actions.size() == 1);
            RulesAction action = actions.get(0);
            PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
            assertEquals("desired", result.getCccid()); // assert we got the right one
        }
    }

    @Test
    public void testPlacementResultsReturnDesiredAssessResultForMmapAndAssessAndSelfReported() throws Exception {
        final boolean isStandalone = true; // doesn't matter here
        final boolean isSelfReported = true;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getAssessmentPlacementComponents().add(createDesiredPlacementComponent(false, false));
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
    }

    @Test
    public void testPlacementResultsReturnDesiredMmapResultForMmapAndAssessAndNotSelfReported() throws Exception {
        final boolean isStandalone = true; // doesn't matter here
        final boolean isSelfReported = false;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
    }

    @Test
    public void testPlacementResultsReturnDesiredMmapResultForMmapAndAssessAndSelfReported() throws Exception {
        final boolean isStandalone = true; // doesn't matter here
        final boolean isSelfReported = true;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
    }

    @Test
    public void testPlacementResultsReturnDesiredMmapResultForMmapAndStandaloneAndNotSelfReported() throws Exception {
        final boolean isStandalone = true;
        final boolean isSelfReported = false;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(new ArrayList<>());
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
    }

    @Test
    public void testPlacementRulesReturnDesiredMmapResultForMmapAndStandaloneAndSelfReported() throws Exception {
        final boolean isStandalone = true;
        final boolean isSelfReported = true;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(new ArrayList<>());
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(20));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
    }

    @Test
    public void testPlacementRulesReturnsNAForElaIndicatorWhenNoAssessComponentIsPresent() throws Exception {
        final boolean isStandalone = true;
        final boolean isSelfReported = true;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(new ArrayList<>());
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(2));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
        assertEquals("NA", result.getElaIndicator());
    }

    @Test
    public void testPlacementRulesReturnsDesiredElaIndicatorWhenAssessComponentIsPresent() throws Exception {
        final boolean isStandalone = true;
        final boolean isSelfReported = true;

        CompetencyAttributesViewDto attributes = new CompetencyAttributesViewDto();
        attributes.setOptInMultiMeasure(true);
        attributes.setUseSelfReportedDataForMM(isSelfReported);

        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        placementRulesSourceDto.setAssessmentPlacementComponents(createRandomPlacementComponents(2));
        PlacementComponentViewDto desiredAssessmentComponent = createDesiredPlacementComponent(false, false);
        desiredAssessmentComponent.setElaIndicator("desired");
        placementRulesSourceDto.getAssessmentPlacementComponents().add(desiredAssessmentComponent);
        placementRulesSourceDto.setMmapPlacementComponents(createRandomPlacementComponents(2));
        placementRulesSourceDto.getMmapPlacementComponents().add(createDesiredPlacementComponent(isStandalone, isSelfReported));
        facts.put("COMPETENCY_ATTRIBUTES", attributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementActionResult result = (PlacementActionResult) action.getActionParameters().get("placement");
        assertEquals("desired", result.getCccid()); // assert we got the right one
        assertEquals("desired", result.getElaIndicator());
    }

    private PlacementComponentViewDto createDesiredPlacementComponent(boolean isStandalone, boolean isSelfReported) {
        PlacementComponentViewDto placementComponent = new PlacementComponentViewDto();
        placementComponent.setCccid("desired");
        placementComponent.setCreatedOn(new Date());
        CB21ViewDto cb21 = new CB21ViewDto();
        cb21.setCb21Code('Y');
        cb21.setLevel(0);
        placementComponent.setCb21(cb21);
        placementComponent.setStandalonePlacement(isStandalone);
        placementComponent.setDataSourceType(isSelfReported ? MmapDataSourceType.SELF_REPORTED : MmapDataSourceType.STANDARD);
        placementComponent.setElaIndicator(RandomStringUtils.randomAlphabetic(20));
        return placementComponent;
    }

    private List<PlacementComponentViewDto> createRandomPlacementComponents(int numToCreate) {
        List<PlacementComponentViewDto> randomPlacementComponents = new ArrayList<>();
        for (int counter = 0; counter < numToCreate; counter++) {
            PlacementComponentViewDto placementComponentViewDto = new PlacementComponentViewDto();
            placementComponentViewDto.setId(RandomStringUtils.randomAlphanumeric(20));
            placementComponentViewDto.setCreatedOn(new DateTime().minusDays(ThreadLocalRandom.current().nextInt(1, 31)).toDate());
            placementComponentViewDto.setCb21(randomCb21());
            placementComponentViewDto.setStandalonePlacement(ThreadLocalRandom.current().nextBoolean());
            placementComponentViewDto.setDataSourceType(ThreadLocalRandom.current().nextBoolean() ? MmapDataSourceType.SELF_REPORTED : MmapDataSourceType.STANDARD);
            placementComponentViewDto.setElaIndicator(RandomStringUtils.randomAlphabetic(20));
            randomPlacementComponents.add(placementComponentViewDto);
        }
        return randomPlacementComponents;
    }

    private CB21ViewDto randomCb21() {
        CB21ViewDto randomCB21 = new CB21ViewDto();
        randomCB21.setLevel(ThreadLocalRandom.current().nextInt(0,9));
        return randomCB21;
    }

    private StatelessKieSession generateKieSession() throws Exception {
        String csv = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".csv"));
        String rule = FileUtils.readFileToString(new File(RULES_DIRECTORY + TEST_FILE_NAME + ".drl"));
        String rulesetHeader = FileUtils
                .readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".header"));
        String rulesetFooter = FileUtils
                .readFileToString(new File(RULES_DIRECTORY + HEADER_FOOTER_FILE_NAME + ".footer"));
        return loadKieSession(rulesetHeader, rule, rulesetFooter, csv);
    }
}
