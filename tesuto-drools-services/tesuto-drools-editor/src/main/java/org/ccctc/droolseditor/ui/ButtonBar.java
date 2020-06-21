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

import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path = "/ui")
@Push(transport = Transport.WEBSOCKET_XHR) // Websocket would bypass the filter chain, Websocket+XHR works
public class ButtonBar extends UI {

    private static final long serialVersionUID = 1L;

    DecisionTreeList decisionTreeList;
    DecisionTreeEditor decisionTreeEditor;
    RuleTemplateEditor ruleEditor;
    RuleTemplateList ruleTemplateList;
    RuleSetEditor ruleSetEditor;
    RuleSetList ruleSetList;

    EngineEditor applicationEditor;
    EngineList applicationList;

    FamilyViewEditor familyViewEditor;
    FamilyViewList familyViewList;

    Window message = new Window();
    String primaryButtonStyle;
    String buttonStyle;

    VerticalLayout mainLayout = new VerticalLayout();

    private Button ruleListBtn = new Button("Rule List", FontAwesome.SEARCH);
    private Button ruleBtn = new Button("Edit Rule");
    private Button decisionTreeListBtn = new Button("Ruleset Row List", FontAwesome.SEARCH);
    private Button decisionTreeBtn = new Button("Ruleset Row");
    private Button ruleSetListBtn = new Button("Ruleset List", FontAwesome.SEARCH);
    private Button ruleSetBtn = new Button("Ruleset");

    private Button applicationListBtn = new Button("Application List", FontAwesome.SEARCH);
    private Button applicationBtn = new Button("Application");
    private Button collegeListBtn = new Button("College List", FontAwesome.SEARCH);
    private Button collegeBtn = new Button("College");

    private HorizontalLayout actions = new HorizontalLayout(ruleListBtn, ruleBtn, decisionTreeListBtn, decisionTreeBtn,
            ruleSetListBtn, ruleSetBtn);

    private HorizontalLayout more = new HorizontalLayout(applicationListBtn, applicationBtn, collegeListBtn,
            collegeBtn);

    private RuleAttributesView currentAttributes = new RuleAttributesView();

    private EngineController applicationController;
    private FamilyViewController collegeViewController;

    @Autowired
    public ButtonBar(DecisionTreeEditor decisionTreeEditor, RuleTemplateEditor ruleEditor, RuleSetEditor ruleSetEditor,
            RuleTemplateList ruleTemplateList, DecisionTreeList decisionTreeList, RuleSetList ruleSetList,
            EngineList applicationList, EngineEditor applicationEditor, FamilyViewList collegeList,
            FamilyViewEditor collegeEditor, EngineController applicationController,
            FamilyViewController collegeViewController) {
        this.decisionTreeList = decisionTreeList;
        this.decisionTreeEditor = decisionTreeEditor;
        this.ruleEditor = ruleEditor;
        this.ruleTemplateList = ruleTemplateList;
        this.ruleSetEditor = ruleSetEditor;
        this.ruleSetList = ruleSetList;

        this.applicationList = applicationList;
        this.applicationEditor = applicationEditor;
        this.familyViewList = collegeList;
        this.familyViewEditor = collegeEditor;

        this.applicationController = applicationController;
        this.collegeViewController = collegeViewController;
        mainLayout.addComponent(more);
        mainLayout.addComponent(actions);
        mainLayout.addComponent(ruleTemplateList);
        primaryButtonStyle = ruleListBtn.getPrimaryStyleName();
        buttonStyle = ruleListBtn.getStyleName();
        setContent(mainLayout);
    }

    @Override
    protected void init(VaadinRequest request) {
        decisionTreeEditor.init(currentAttributes, applicationController);
        ruleEditor.init(currentAttributes, applicationController);
        ruleSetEditor.init(currentAttributes, applicationController);
        decisionTreeList.init(decisionTreeEditor, currentAttributes, applicationController, collegeViewController);
        ruleTemplateList.init(ruleEditor, currentAttributes, applicationController, collegeViewController);
        ruleSetList.init(ruleSetEditor, currentAttributes, applicationController, collegeViewController);

        applicationList.init(applicationEditor);
        applicationEditor.init();
        familyViewList.init(familyViewEditor);
        familyViewEditor.init();

        ruleListBtn.addClickListener(e -> showRuleList());
        ruleBtn.addClickListener(e -> showRule());
        decisionTreeListBtn.addClickListener(e -> showDecisionTreeList());
        decisionTreeBtn.addClickListener(e -> showDecisionTree());
        ruleSetListBtn.addClickListener(e -> showRuleSetList());
        ruleSetBtn.addClickListener(e -> showRuleSet());

        applicationListBtn.addClickListener(e -> showApplicationList());
        applicationBtn.addClickListener(e -> showApplication());
        collegeListBtn.addClickListener(e -> showCollegeList());
        collegeBtn.addClickListener(e -> showCollege());

        addAlertWindow("Results For Decision Tree Action", decisionTreeEditor.getMessageUI());
        addAlertWindow("Results For Rule Action", ruleEditor.getMessageUI());
        addAlertWindow("Results For RuleSet Action", ruleSetEditor.getMessageUI());
        addAlertWindow("Results For Application Action", applicationEditor.getMessageUI());
        addAlertWindow("Results For College Action", familyViewEditor.getMessageUI());
        
        addAlertWindow("Results For Decision Tree Search", decisionTreeList.getMessageUI());
        addAlertWindow("Results For Rule Search ", ruleTemplateList.getMessageUI());
        addAlertWindow("Results For RuleSet Search", ruleSetList.getMessageUI());

        hideAll();
        actions.setVisible(true);
    }

    protected void showRuleList() {
        hideAll();
        ruleTemplateList.setVisible(true);
        mainLayout.addComponent(ruleTemplateList);
        ruleTemplateList.findByCurrentAttributes();
        setButtonStyle(ruleListBtn);
    }

    protected void showRule() {
        hideAll();
        mainLayout.addComponent(ruleEditor);
        ruleEditor.setVisible(true);
        ruleEditor.edit(ruleEditor.getView(), applicationController, collegeViewController);
        setButtonStyle(ruleBtn);
    }

    protected void showDecisionTreeList() {
        hideAll();
        decisionTreeList.setVisible(true);
        mainLayout.addComponent(decisionTreeList);
        decisionTreeList.findByCurrentAttributes();
        setButtonStyle(decisionTreeListBtn);
    }

    protected void showDecisionTree() {
        hideAll();
        mainLayout.addComponent(decisionTreeEditor);
        decisionTreeEditor.setVisible(true);
        decisionTreeEditor.edit(decisionTreeEditor.getView(), ruleIdIsValid(), applicationController,
                collegeViewController);
        setButtonStyle(decisionTreeBtn);
    }

    protected void showRuleSetList() {
        hideAll();
        ruleSetList.setVisible(true);
        mainLayout.addComponent(ruleSetList);
        ruleSetList.findByCurrentAttributes();
        setButtonStyle(ruleSetListBtn);
    }

    protected void showRuleSet() {
        hideAll();
        mainLayout.addComponent(ruleSetEditor);
        ruleSetEditor.setVisible(true);
        ruleSetEditor.edit(ruleSetEditor.getView(), applicationController, collegeViewController);
        setButtonStyle(ruleSetBtn);
    }

    protected void showApplicationList() {
        hideAll();
        applicationList.setVisible(true);
        mainLayout.addComponent(applicationList);
        setButtonStyle(applicationListBtn);
    }

    protected void showApplication() {
        hideAll();
        mainLayout.addComponent(applicationEditor);
        applicationEditor.setVisible(true);
        setButtonStyle(applicationBtn);
    }

    protected void showCollegeList() {
        hideAll();
        familyViewList.setVisible(true);
        mainLayout.addComponent(familyViewList);
        setButtonStyle(collegeListBtn);
    }

    protected void showCollege() {
        hideAll();
        mainLayout.addComponent(familyViewEditor);
        familyViewEditor.setVisible(true);
        setButtonStyle(collegeBtn);
    }

    protected void setButtonStyle(Button friendlyButton) {

        ruleListBtn.setStyleName(buttonStyle);
        ruleListBtn.setPrimaryStyleName(primaryButtonStyle);

        ruleBtn.setStyleName(buttonStyle);
        ruleBtn.setPrimaryStyleName(primaryButtonStyle);

        decisionTreeListBtn.setStyleName(buttonStyle);
        decisionTreeListBtn.setPrimaryStyleName(primaryButtonStyle);

        decisionTreeBtn.setStyleName(buttonStyle);
        decisionTreeBtn.setPrimaryStyleName(primaryButtonStyle);

        ruleSetListBtn.setStyleName(buttonStyle);
        ruleSetListBtn.setPrimaryStyleName(primaryButtonStyle);

        ruleSetBtn.setStyleName(buttonStyle);
        ruleSetBtn.setPrimaryStyleName(primaryButtonStyle);

        applicationListBtn.setStyleName(buttonStyle);
        applicationListBtn.setPrimaryStyleName(primaryButtonStyle);

        applicationBtn.setStyleName(buttonStyle);
        applicationBtn.setPrimaryStyleName(primaryButtonStyle);

        collegeListBtn.setStyleName(buttonStyle);
        collegeListBtn.setPrimaryStyleName(primaryButtonStyle);

        collegeBtn.setStyleName(buttonStyle);
        collegeBtn.setPrimaryStyleName(primaryButtonStyle);

        friendlyButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
    }

    protected void hideAll() {
        ruleTemplateList.setVisible(false);
        ruleEditor.setVisible(false);
        decisionTreeList.setVisible(false);
        decisionTreeEditor.setVisible(false);
        ruleSetList.setVisible(false);
        ruleSetEditor.setVisible(false);

        applicationList.setVisible(false);
        applicationEditor.setVisible(false);
        familyViewList.setVisible(false);
        familyViewEditor.setVisible(false);
    }

    private Boolean ruleIdIsValid() {
        if (decisionTreeEditor.getView() == null && StringUtils.isBlank(decisionTreeList.getSearchForm().getRuleId())) {
            decisionTreeEditor.enableRuleId(true);
            return false;
        }
        if (decisionTreeEditor.getView() == null
                && !StringUtils.isBlank(decisionTreeList.getSearchForm().getRuleId())) {
            decisionTreeEditor.enableRuleId(true);
            return ruleIdIsValid(decisionTreeList.getSearchForm().getRuleId());
        }
        String decisionTreeRuleId = decisionTreeEditor.getView().getRuleId();
        String ruleId = ruleEditor.getView() == null ? "" : ruleEditor.getView().getId();
        if (StringUtils.isBlank(decisionTreeRuleId)) {
            decisionTreeEditor.enableRuleId(true);
            return true;
        }
        if (decisionTreeRuleId.equals(ruleId)) {
            if (ruleEditor.getView().getStatus().equals(PUBLISHED)) {
                decisionTreeEditor.enableRuleId(false);
                return true;
            } else {
                decisionTreeEditor.enableRuleId(true);
                return false;
            }
        }
        return ruleIdIsValid(decisionTreeRuleId);
    }

    private Window addAlertWindow(String title, TextArea message) {
        Window alert = new Window(title);
        VerticalLayout content = new VerticalLayout();
        content.setHeight(100, Unit.PERCENTAGE);
        content.setWidth(100, Unit.PERCENTAGE);
        message.setWidth(100, Unit.PERCENTAGE);
        message.setHeight(100, Unit.PERCENTAGE);
        message.setEnabled(false);
        content.addComponent(message);
        alert.setModal(true);
        alert.setWidth(50, Unit.PERCENTAGE);
        alert.setHeight(50, Unit.PERCENTAGE);

        alert.center();
        alert.setResizable(true);
        alert.setClosable(true);
        alert.setContent(content);
        alert.setVisible(false);

        message.addValueChangeListener(l -> {
            this.addWindow(alert);
            alert.setVisible(true);
        });
        return alert;
    }

    private Boolean ruleIdIsValid(String ruleId) {
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setId(ruleId);
        List<RuleTemplateView> results = ruleEditor.findByForm(form);
        if (results.isEmpty()) {
            decisionTreeEditor.getView().setRuleId("");
            decisionTreeEditor.enableRuleId(true);
            return false;
        } else if (results.get(0).getStatus().equals(PUBLISHED)) {
            decisionTreeEditor.enableRuleId(false);
            return true;
        }
        decisionTreeEditor.enableRuleId(false);
        return false;
    }

}
