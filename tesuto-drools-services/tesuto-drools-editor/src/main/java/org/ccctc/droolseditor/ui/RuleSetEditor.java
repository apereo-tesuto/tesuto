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

import static org.ccctc.common.droolscommon.model.RuleStatus.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.RuleSetController;
import org.ccctc.droolseditor.services.RuleEditorClient;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

@SpringComponent
@UIScope
public class RuleSetEditor extends AttributeEditor<RuleSetDTO> {

    private static final long serialVersionUID = 1L;

    private RuleSetDTO ruleSetDTO;

    RuleSetController controller;

    private Binder<RuleSetDTO> binder = new Binder<>(RuleSetDTO.class);

    private TextArea ruleSetRowIds = new TextArea("Rule Set Row Ids");
    
    private TextArea testFacts = new TextArea("Test Facts");
    
    private TextArea ruleSetDrl = new TextArea("Rule Set DRL");
    
    private TextArea ruleSetDrlValidationCsv = new TextArea("Rule Set DRL Validation CSV");

    private Button loadRules = new Button("Load Rules");
    private Button executeRules = new Button("Execute Rules Engine");
    private Button rulesEngineStatus = new Button("Rules Engine Status");
    private Button viewRules = new Button("View Rules");

    private RuleEditorClient client;

    @Autowired
    public RuleSetEditor(RuleSetController controller, RuleEditorClient client) {
        this.controller = controller;
        this.client = client;
    }

    public void init(RuleAttributesView currentAttributes, EngineController applicationController) {
        testFacts.setVisible(false);
        if (client.hasValidEndpoint()) {
            loadRules.setDescription(
                    "Currently Load Rules only loads to one instance, for use on local and ci/test only for the Assess Rules Service");
            loadRules.addClickListener(e -> loadRules());
            rulesEngineStatus.addClickListener(e -> rulesEngineStatus());
            viewRules.addClickListener(e -> this.viewRulesEngineDrls());
            rulesEngineStatus.setDescription(
                    "Currently Load Rules Status only checks status for one instance, for use on local and ci/test only for the Assess Rules Service");
            executeRules.addClickListener(e -> executeRulesEngine());
            executeRules.setDescription("Use this to attempt to execute a set for facts against access rules microservice.");
            testFacts.setDescription("Add a single set of test facts to generate a response. Format: {key1:value1,key2:value2}");            
            testFacts.setWidth(600, Unit.PIXELS);
            testFacts.setHeight(120, Unit.PIXELS);
            testFacts.setVisible(true);
        }
        ruleSetRowIds.setWidth(600, Unit.PIXELS);
        ruleSetRowIds.setHeight(120, Unit.PIXELS);
        
        ruleSetDrl.setWidth(1000, Unit.PIXELS);
        ruleSetDrl.setHeight(120, Unit.PIXELS);

        ruleSetDrlValidationCsv.setWidth(1000, Unit.PIXELS);
        ruleSetDrlValidationCsv.setHeight(120, Unit.PIXELS);
          
        super.init(currentAttributes, applicationController);
        binder.forField(ruleSetRowIds).withConverter(new StringToListConverter()).bind(RuleSetDTO::getRuleSetRowIds,
                RuleSetDTO::setRuleSetRowIds);
        binder.bindInstanceFields(this);
    }

    @Override
    protected void initializeLayout() {

        CssLayout actions = addActionsToLayout();
        actions.addComponent(viewRules);
        actions.addComponent(loadRules);
        actions.addComponent(rulesEngineStatus);
        actions.addComponent(executeRules);
        if (client.hasValidEndpoint()) {
            getMainLayout().addComponent(testFacts, 0);
            getMainLayout().addComponent(ruleSetDrlValidationCsv, 0);
            getMainLayout().addComponent(ruleSetDrl, 0);
        }
        addEventInfoToLayout();
        addDescriptionInfo();
        addApplicationInfoLayout();
        addObjectStateInfoLayout();
    }

    @Override
    protected HorizontalLayout addObjectStateInfoLayout() {
        HorizontalLayout objectStateInfo = super.addObjectStateInfoLayout();
        objectStateInfo.addComponent(ruleSetRowIds, 3);
        return objectStateInfo;
    }

    @Override
    protected void save(RuleSetDTO view) {
        updateFields(view);
        boolean doPublish = false;
        try {
            view = controller.updateRuleSet(view, view.getId(), doPublish);
            setMessage(messageSaveSucceeded(view.getTitle(), view.getId()));
            setView(view);
        } catch (Exception exception) {
            setMessage(messageSaveFailed(view.getTitle(), view.getId(), exception));
        }
    }
    
    @Override
    protected void updateFields(RuleSetDTO view) {
        if(view == null) {
            return;
        }
        super.updateFields(view);
        view.setRuleSetDrl(ruleSetDrl.getValue());
        view.setRuleSetDrlValidationCsv(ruleSetDrlValidationCsv.getValue());
    }

    protected void publish(RuleSetDTO view) {
        boolean doPublish = true;
        try {
            view.setStatus(PUBLISHED);
            view.setVersion("1");
            view = controller.updateRuleSet(view, view.getId(), doPublish);
            setMessage(messagePublishSucceeded(view.getTitle(), view.getId()));
            setView(view);
        } catch (Exception exception) {
            setMessage(messagePublishFailed(view.getTitle(), view.getId(), exception));
        }
    }

    @Override
    protected RuleSetDTO get(String ruleSetId) {
        try {
            ruleSetDTO = controller.get(ruleSetId);
        } catch (ObjectNotFoundException exception) {
            setMessage(messageObjectNotFound("Ruleset Row with id: ", ruleSetId));
            ruleSetDTO = new RuleSetDTO();
        } catch (Exception exception) {
            setMessage(messageGetFailed("Ruleset Row with id: ", ruleSetId, exception));
            ruleSetDTO = new RuleSetDTO();
        }

        ruleSetDTO.adjustValuesForUI();
        setView(ruleSetDTO);
        return ruleSetDTO;
    }

    @Override
    public List<RuleSetDTO> findByForm(RuleAttributeFacetSearchForm form) {
        return controller.find(form);
    }

    @Override
    protected void delete(RuleSetDTO toBeDeleted) {
        controller.deleteRuleSet(toBeDeleted.getId());
        RuleSetDTO deleted = get(toBeDeleted.getId());
        if (StringUtils.isNotBlank(deleted.getId())) {
            setMessage(messageObjectRetired("RuleSet", deleted.getId(), deleted.getTitle()));
        }
    }

    @Override
    protected void clear(RuleSetDTO ruleSetDTO) {
        super.clear(ruleSetDTO);
        setView(ruleSetDTO);
    }

    protected void validate(RuleSetDTO ruleSetDTO) {
        if(StringUtils.isBlank(ruleSetDTO.getRuleSetDrl())) {
            this.setMessage("RuleSets do not need to be validated as rules and rule rows are valid");
            return;
        }
            DrlValidationResults results = controller.validateRuleSet(ruleSetDTO);
            if (results.getIsValid()) { 
                setMessage(messageValidationSucceeds((String) results.getDrl()));
            } else  {
                setMessage(messageValidationFailed(generateValidationSuggestion(ruleSetDTO),results));
            }
    }

    @Override
    protected RuleSetDTO getView() {
        updateFields(ruleSetDTO);
        return ruleSetDTO;
    }

    @Override
    protected void edit(RuleSetDTO view, EngineController applicationController,
            FamilyViewController collegeViewController) {
        super.edit(view, applicationController, collegeViewController);
        if ((view == null || isClean(view)) && currentAttributes != null) {
            List<String> ruleSetRowIds = null;
            if(StringUtils.isNotBlank(currentAttributes.getType()) && currentAttributes.getType().equals(RuleSetRowDTO.class.getName())) {
              ruleSetRowIds = Arrays.asList(currentAttributes.getRuleId());
            }else if(StringUtils.isNotBlank(currentAttributes.getType()) && !currentAttributes.getType().equals(RuleSetDTO.class.getName())){
                currentAttributes.setId("");
            }
            view = mapper.map(currentAttributes, RuleSetDTO.class);
            view.setRuleSetRowIds(ruleSetRowIds);
        }
        final boolean persisted = StringUtils.isNotBlank(view.getId());
        if (persisted) {
            get(view.getId());
        } else {
            ruleSetDTO = view;
        }
        updateUI(ruleSetDTO);
    }

    @Override
    protected void setView(RuleSetDTO ruleSetDTO) {
        this.ruleSetDTO = ruleSetDTO;
        super.setView(ruleSetDTO);
        updateUI(ruleSetDTO);
    }

    @Override
    protected void updateUI(RuleSetDTO rulesetDTO) {
        binder.setBean(rulesetDTO);
        super.updateUI(rulesetDTO);
        ruleSetDrl
        .setValue(rulesetDTO.getRuleSetDrl() == null ? "" : rulesetDTO.getRuleSetDrl());
        ruleSetDrlValidationCsv.setValue(rulesetDTO.getRuleSetDrlValidationCsv() == null ? "" : rulesetDTO.getRuleSetDrlValidationCsv());
    }

    private void loadRules() {
        setMessage(getMessageOnRulesLoad(client.loadRules()));
    }

    private void viewRulesEngineDrls() {
        if(StringUtils.isBlank(cccMisCode().getValue())) {
            setMessage("Requires CCCMisCode:\nPlease Select a College CCCMisCode to view the current drls in that engine.");
            return;
        }
        setMessage(getMessageFromDrls(client.rulesEngineDrls(cccMisCode().getValue())));
    }
    
    private void executeRulesEngine(){
        setMessage(getMessageOnRulesLoad(client.executeFacts(testFacts.getValue())));
    }
    
    private void rulesEngineStatus() {
        setMessage(client.rulesEngineStatus());
    }
    
    protected String getMessageOnRulesLoad(DrlValidationResults results) {
        if(results.getIsValid()){
            return "Rules loaded sucessfully" ;
        }
        StringBuffer errorMessage = new StringBuffer("Rules Loading failed FOR THE FOLLOWING REASONS:\n");
        errorMessage.append(results.getExceptionMessage());
        errorMessage.append("\nDETAILED ERROR MESSAGE:");
        if (CollectionUtils.isNotEmpty(results.getErrors())) {
            for (String error : results.getErrors()) {
                errorMessage.append("\n").append(error);
            }
        }
        errorMessage.append("\nGenerated DRL Files:\n").append(results.getDrl());
        return errorMessage.toString();
    }
    
    protected String getMessageFromDrls(List<String>results) {
        return String.join("\nNew RuleSet\n", results);
    }
    
    protected String getMessageOnRulesExecute(DrlValidationResults results) {
        if(results.getIsValid()){
            return "Rules executed sucessfully returned with the following result\n" + results.getDrl();
        }
        StringBuffer errorMessage = new StringBuffer("Rules Loading failed FOR THE FOLLOWING REASONS:\n");
        errorMessage.append(results.getExceptionMessage());
        errorMessage.append("\nDETAILED ERROR MESSAGE:");
        if (CollectionUtils.isNotEmpty(results.getErrors())) {
            for (String error : results.getErrors()) {
                errorMessage.append("\n").append(error);
            }
        }
        errorMessage.append("\nGenerated DRL Files:\n").append(results.getDrl());
        return errorMessage.toString();
    }

}
