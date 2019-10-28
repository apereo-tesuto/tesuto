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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.RuleTemplateController;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.TextArea;

@SpringComponent
@UIScope
public class RuleTemplateEditor extends AttributeEditor<RuleTemplateView> {

	private static final long serialVersionUID = 1L;

	private Binder<RuleTemplateView> binder = new Binder<>(RuleTemplateView.class);

	private RuleTemplateView ruleTemplateView;

	private TextArea imports = new TextArea("Imports");

	private TextArea definitions = new TextArea("Simple Assigments");

	private TextArea whenStatement = new TextArea("Conditional Statements");

	private TextArea thenClause = new TextArea("Then Statement");
	
	private TextArea testTokenValues = new TextArea("Test Token Values");
	
	private TextArea customFunctions = new TextArea("Custom Functions");

	private RuleTemplateController controller;

	@Autowired
	public RuleTemplateEditor(RuleTemplateController controller) {
		this.controller = controller;
		binder.bindInstanceFields(this);
	}

	public void init(RuleAttributesView currentAttributes, EngineController applicationController) {
		definitions.setWidth(1000, Unit.PIXELS);
		definitions.setHeight(120, Unit.PIXELS);
		whenStatement.setWidth(1000, Unit.PIXELS);
		whenStatement.setHeight(120, Unit.PIXELS);
		thenClause.setWidth(1000, Unit.PIXELS);
		thenClause.setHeight(120, Unit.PIXELS);
		imports.setWidth(1000, Unit.PIXELS);
		imports.setHeight(120, Unit.PIXELS);
		testTokenValues.setWidth(1000, Unit.PIXELS);
		testTokenValues.setHeight(60, Unit.PIXELS);
		customFunctions.setWidth(1000, Unit.PIXELS);
        customFunctions.setHeight(120, Unit.PIXELS);
        customFunctions.setDescription("Custom functions are added to the end of the drl and need to be referenced by the base package: net.ccctechcenter.drools");
		super.init(currentAttributes, applicationController);

	}

	protected void initializeLayout() {
		addActionsToLayout();
		getMainLayout().addComponent(testTokenValues, 0);
		getMainLayout().addComponent(customFunctions, 0);
		getMainLayout().addComponent(thenClause, 0);
		getMainLayout().addComponent(whenStatement, 0);
		getMainLayout().addComponent(definitions, 0);
		getMainLayout().addComponent(imports, 0);
		addEventInfoToLayout();
		addDescriptionInfo();
		addApplicationInfoLayout();
		addObjectStateInfoLayout();
	}

	protected void save(RuleTemplateView view) {
		updateFields(view);
		try {
			view = controller.save(view);
			setMessage(messageSaveSucceeded(view.getTitle(), view.getId()));
			setView(view);
		} catch (Exception exception) {
			setMessage(messageSaveFailed(view.getTitle(), view.getId(), exception));
		}
	}

	protected RuleTemplateView get(String ruleId) {
		try {
			ruleTemplateView = controller.getRuleById(ruleId);
		} catch (ObjectNotFoundException exception) {
			setMessage(messageObjectNotFound("Decision Tree View (Ruleset Row with id) has been deleted!", ruleId));
			ruleTemplateView = new RuleTemplateView();
		} catch (Exception exception) {
			setMessage(messageGetFailed("Decision Tree View (Ruleset Row with id)", ruleId, exception));
			ruleTemplateView = new RuleTemplateView();
		}

		ruleTemplateView.adjustValuesForUI();
		setView(ruleTemplateView);
		return ruleTemplateView;
	}

	protected void delete(RuleTemplateView toBeDeleted) {
		controller.deleteRule(toBeDeleted.getId());
		RuleTemplateView deleted = get(toBeDeleted.getId());
		if(StringUtils.isNotBlank(deleted.getId())) {
		    setMessage(messageObjectRetired("Rule", deleted.getId(), deleted.getTitle()));
		}
	}

	protected void clear(RuleTemplateView ruleTemplateView) {
		super.clear(ruleTemplateView);
		ruleTemplateView.setDefinitions("");
		ruleTemplateView.setImports("");
		ruleTemplateView.setThenClause("");
		ruleTemplateView.setWhenStatement("");
		ruleTemplateView.setTestTokenValues("");
		setView(ruleTemplateView);
	}

	@Override
	protected void updateUI(RuleTemplateView ruleTemplateView) {
		binder.setBean(ruleTemplateView);
		super.updateUI(ruleTemplateView);
		if (ruleTemplateView == null) {
			definitions.setValue("");
			whenStatement.setValue("");
			thenClause.setValue("");
			imports.setValue("");
			testTokenValues.setValue("");
			customFunctions.setValue("");
			return;
		}
		definitions.setValue(ruleTemplateView.getDefinitions() == null ? "" : ruleTemplateView.getDefinitions());
		whenStatement.setValue(ruleTemplateView.getWhenStatement() == null ? "" : ruleTemplateView.getWhenStatement());
		thenClause.setValue(ruleTemplateView.getThenClause() == null ? "" : ruleTemplateView.getThenClause());
		imports.setValue(ruleTemplateView.getImports() == null ? "" : ruleTemplateView.getImports());
		testTokenValues.setValue(ruleTemplateView.getTestTokenValues() == null ? "" : ruleTemplateView.getTestTokenValues());
		customFunctions.setValue(ruleTemplateView.getCustomFunctions() == null ? "" : ruleTemplateView.getCustomFunctions());
	}

	protected void publish(RuleTemplateView view) {
		view.setStatus(PUBLISHED);
		view.setVersion("1");
		try {
			DrlValidationResults results = controller.publishRuleTemplateView(view);
			if(results.getIsValid()) {
				setMessage(messagePublishSucceeded(view.getTitle(), view.getId()));
				setView(view);
			} else {
				setMessage(messagePublishFailed(view.getTitle(), view.getId(), results));
				setView(view);
			}
		} catch (Exception exception) {
			setMessage(messagePublishFailed(view.getTitle(), view.getId(), exception));
		}
	}

	@Override
	public List<RuleTemplateView> findByForm(RuleAttributeFacetSearchForm form) {
		return controller.find(form);
	}

	@Override
	protected RuleTemplateView getView() {
	    updateFields(ruleTemplateView);
		return ruleTemplateView;
	}

	@Override
	protected void updateFields(RuleTemplateView view) {
	    if(view == null) {
	        return;
	    }
		super.updateFields(view);
		view.setDefinitions(definitions.getValue());
		view.setWhenStatement(whenStatement.getValue());
		view.setThenClause(thenClause.getValue());
		view.setImports(imports.getValue());
		view.setTestTokenValues(testTokenValues.getValue());
		view.setCustomFunctions(customFunctions.getValue());
	}

	@Override
	protected void validate(RuleTemplateView view) {
		DrlValidationResults results = controller.validateRule(view);
		if (results.getIsValid()) { 
			setMessage(messageValidationSucceeds((String) results.getDrl()));
		} else  {
			setMessage(messageValidationFailed(generateValidationSuggestion(view) , results));
		}
	}

	@Override
	protected void edit(RuleTemplateView view,
            EngineController applicationController,
            FamilyViewController collegeViewController) {
        super.edit(view, applicationController, collegeViewController);
		if ((view == null || isClean(view)) && currentAttributes != null) {
		    if(StringUtils.isNotBlank(currentAttributes.getType()) && !currentAttributes.getType().equals(DecisionTreeView.class.getName())){
                currentAttributes.setId("");
            }
		    view  = mapper.map(currentAttributes, RuleTemplateView.class);
		}
		
		final boolean persisted = StringUtils.isNotBlank(view.getId());
		if (persisted) {
			get(view.getId());
		} else {
			ruleTemplateView = view;
		}
		binder.setBean(ruleTemplateView);

		updateUI(ruleTemplateView);
	}

	@Override
	protected void setView(RuleTemplateView ruleTemplateView) {
	    super.setView(ruleTemplateView);
		this.ruleTemplateView = ruleTemplateView;
		updateUI(ruleTemplateView);
	}
}
