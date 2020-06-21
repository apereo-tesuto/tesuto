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
import org.cccnext.tesuto.placement.view.AssignedPlacementRulesSourceDto;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.ccctc.common.droolscommon.RulesAction;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class AssignedPlacementRulesTest extends DecisionTreeTestBed {

    private static final String TEST_FILE_NAME = "AssignedPlacement";

    private static final String HEADER_FOOTER_FILE_NAME = "AssignedPlacement";

    private Map<String, Object> facts;

    @Before
    public void setUp() {
        facts = initFacts();
        facts.put("RULE_SET_ID", "12345");
    }

    @Test
    public void testAssignedPlacementRulesReturnDesiredPlacementFromRandomPlacements() throws Exception {
        AssignedPlacementRulesSourceDto assignedPlacementRulesSourceDto = new AssignedPlacementRulesSourceDto();
        assignedPlacementRulesSourceDto.setPlacements(createRandomPlacements(20));
        assignedPlacementRulesSourceDto.getPlacements().add(createDesiredPlacement());
        facts.put("PLACEMENTS", assignedPlacementRulesSourceDto);

        StatelessKieSession kSession = generateKieSession();
        kSession.execute(facts);

        List<RulesAction> actions = (List<RulesAction>) facts.get("actions");
        assertTrue(actions.size() == 1);
        RulesAction action = actions.get(0);
        PlacementViewDto result = (PlacementViewDto) action.getActionParameters().get("assignedPlacement");
        assertEquals("desired", result.getId()); // assert we got the right one
    }

    private PlacementViewDto createDesiredPlacement() {
        PlacementViewDto placementViewDto = new PlacementViewDto();
        placementViewDto.setId("desired");
        placementViewDto.setCreatedOn(new Date());
        CB21ViewDto cb21 = new CB21ViewDto();
        cb21.setCb21Code('Y');
        cb21.setLevel(0);
        placementViewDto.setCb21(cb21);
        return placementViewDto;
    }
    private List<PlacementViewDto> createRandomPlacements(int numToCreate) {
        List<PlacementViewDto> randomPlacements = new ArrayList<>();
        for (int counter = 0; counter < numToCreate; counter++) {
            PlacementViewDto placementViewDto = new PlacementViewDto();
            placementViewDto.setId(RandomStringUtils.randomAlphanumeric(20));
            placementViewDto.setCreatedOn(new DateTime().minusDays(ThreadLocalRandom.current().nextInt(1 ,31)).toDate());
            placementViewDto.setCb21(randomCb21());
            randomPlacements.add(placementViewDto);
        }
        return randomPlacements;
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
