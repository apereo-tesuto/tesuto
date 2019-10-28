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
package org.ccctc.droolseditor.ui;

import static org.ccctc.common.droolscommon.model.RuleStatus.DRAFT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.TestConfig;
import org.ccctc.droolseditor.UITestConfig;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.RuleTemplateController;
import org.ccctc.droolseditor.services.DynamoDBTest;
import org.ccctc.droolseditor.views.FamilyView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.vaadin.server.VaadinRequest;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = { TestConfig.class, UITestConfig.class } )
@ActiveProfiles(profiles = "aws")
public class RuleTemplateUITest extends DynamoDBTest {
    private final static String DEFAULT_RULETEMPLATE = "/ruleTemplateView.json";

    private final static String DEFAULT_SIMPLE_RULETEMPLATE = "/ruleTemplateViewSimple.json";

    @Autowired
    FamilyViewController collegeViewController;

    RuleAttributesView currentAttributes = new RuleAttributesView();

    @Autowired
    EngineController engineController;

    @Autowired
    Mapper mapper;

    @Autowired
    RuleTemplateController ruleTemplateController;

    RuleTemplateEditor ruleTemplateEditor;

    RuleTemplateList ui;
    
    VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EngineDTO addEngine(String name) {
        EngineDTO engineDTO = new EngineDTO();
        engineDTO.setName(name);
        engineDTO.setEvents(new HashSet(Arrays.asList(new String[] { "event" })));
        engineDTO.setCategories(new HashSet(Arrays.asList(new String[] { "category" })));
        engineDTO.setStatus(StandardStatus.ACTIVE);
        return engineDTO;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public FamilyView addFamilyView(String familyCode) {
        FamilyView collegeView = new FamilyView();
        collegeView.setFamilyCode(familyCode);
        collegeView.setSelectedApplications(new HashSet(Arrays.asList(new String[] { "assessment", "sns-listener" })));
        collegeView.setStatus(StandardStatus.ACTIVE);
        return collegeView;
    }

    private RuleTemplateView getSimpleRuleTemplate() {
        RuleTemplateView ruleTemplateView = readObjectFromFile(DEFAULT_SIMPLE_RULETEMPLATE, RuleTemplateView.class);
        ruleTemplateController.save(ruleTemplateView);
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setEvent("simple-event");
        List<RuleTemplateView> list = ui.findByForm(form);
        // apparently there may be a bug in deleteTable for embedded dynamodb
        return list.get(0);
    }

    private RuleTemplateView loadRuleTemplateView(String fileName) {
        createRuleTableForTest();
        RuleTemplateView ruleTemplateView = readObjectFromFile(fileName, RuleTemplateView.class);
        ruleTemplateController.save(ruleTemplateView);
        ruleTemplateView.setId("");
        ruleTemplateView.setStatus(DRAFT);
        return ruleTemplateController.save(ruleTemplateView);
    }

    @Before
    public void setup() {
        loadRuleTemplateView(DEFAULT_RULETEMPLATE);

        engineController.addEngine(addEngine("sns-listener"));
        engineController.addEngine(addEngine("assessment"));
        collegeViewController.addFamily(addFamilyView("cccMisCode"));
        collegeViewController.addFamily(addFamilyView("ZZ1"));
        ruleTemplateEditor = new RuleTemplateEditor(ruleTemplateController);
        ruleTemplateEditor.init(currentAttributes, engineController);
        ui = new RuleTemplateList(ruleTemplateController);
        ui.setMapper(mapper);
        ui.init(ruleTemplateEditor, currentAttributes, engineController, collegeViewController);
        ui.findByCurrentAttributes();
    }

    @Test
    public void testPublish() {
        RuleTemplateView ruleTemplateView = getSimpleRuleTemplate();
        ruleTemplateView.setTitle("Title Of New Publish Test");
        ruleTemplateView.setId("");
        ruleTemplateView.setEvent("Publish Event");
        ruleTemplateEditor.updateUI(ruleTemplateView);
        ruleTemplateEditor.save(ruleTemplateView);
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setEvent("Publish Event");
        List<RuleTemplateView> list = ui.findByForm(form);

        ruleTemplateEditor.publish(list.get(0));
        String publishMessage = ruleTemplateEditor.getMessage();

        assertTrue(publishMessage, publishMessage.contains("has been successfully published and will no longer be editable"));
    }

    @Test
    public void testSave() {
        List<RuleTemplateView> list = ui.findByForm(new RuleAttributeFacetSearchForm());
        // apparently there may be a bug in deleteTable for embedded dynamodb
        int tableSize = list.size();
        RuleTemplateView ruleTemplateView = list.get(0);
        ruleTemplateView.setTitle("Title Of New Test");
        ruleTemplateView.setId("");
        ruleTemplateView.setEvent("Test Event");
        ruleTemplateEditor.updateUI(ruleTemplateView);
        ruleTemplateEditor.save(ruleTemplateView);
        list = ui.findByForm(new RuleAttributeFacetSearchForm());
        assertEquals(tableSize + 1, list.size());
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setEvent("Test Event");
        list = ui.findByForm(form);
        assertEquals(1, list.size());
        assertEquals("Test Event", list.get(0).getEvent());
        assertEquals("Title Of New Test", list.get(0).getTitle());
    }

    @Test
    public void testTemplateListing() {
        List<RuleTemplateView> list = ui.findByForm(new RuleAttributeFacetSearchForm());
        assertEquals(2, list.size());
        list = ui.findByForm(ui.getSearchFormFromAttributes(list.get(0)));
        assertEquals(1, list.size());
    }

    @Test
    public void testValidateFails() {

        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setEvent("event");
        List<RuleTemplateView> list = ui.findByForm(form);
        // apparently there may be a bug in deleteTable for embedded dynamodb
        RuleTemplateView ruleTemplateView = list.get(0);
        ruleTemplateView.setTitle("Title Of New Test For Validation");
        ruleTemplateView.setId("");
        ruleTemplateView.setEvent("Test Validate Event");
        ruleTemplateEditor.updateUI(ruleTemplateView);
        ruleTemplateEditor.validate(ruleTemplateView);
        String validationMessage = ruleTemplateEditor.getMessage();
        assertTrue(validationMessage, validationMessage.contains("VALIDATION FAILED FOR THE FOLLOWING REASONS"));
    }

    @Test
    public void testValidateSucceeds() {
        RuleTemplateView ruleTemplateView = getSimpleRuleTemplate();
        ruleTemplateEditor.updateUI(ruleTemplateView);
        ruleTemplateEditor.validate(ruleTemplateView);
        String validationMessage = ruleTemplateEditor.getMessage();
        assertTrue(validationMessage, validationMessage.contains("VALIDATION SUCCEEDED: Generated Rule Follows"));
    }

}
