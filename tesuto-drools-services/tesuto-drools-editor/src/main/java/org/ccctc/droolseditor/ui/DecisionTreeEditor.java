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
import org.ccctc.droolseditor.controllers.DecisionTreeController;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SpringComponent
@UIScope
public class DecisionTreeEditor extends AttributeEditor<DecisionTreeView> {

	private static final long serialVersionUID = 1L;

	private DecisionTreeView decisionTreeView;

	DecisionTreeController controller;

	private Binder<DecisionTreeView> binder = new Binder<>(DecisionTreeView.class);

	private TextArea tokenValuesAsCsv = new TextArea("Token Values (csv)");

	private TextArea validationCsv = new TextArea("Validation (csv)");

	private TextField ruleId = new TextField("Rule Id");

	@Autowired
	public DecisionTreeEditor(DecisionTreeController controller) {
		this.controller = controller;

		binder.bindInstanceFields(this);
	}

	public void init(RuleAttributesView currentAttributes,
            EngineController applicationController ) {
		tokenValuesAsCsv.setWidth(1000, Unit.PIXELS);
		tokenValuesAsCsv.setHeight(120, Unit.PIXELS);

		validationCsv.setWidth(1000, Unit.PIXELS);
		validationCsv.setHeight(120, Unit.PIXELS);
		ruleId.setWidth(300, Unit.PIXELS);
		super.init(currentAttributes, applicationController);
	}

	@Override
	protected void initializeLayout() {
		addActionsToLayout();
		getMainLayout().addComponent(validationCsv, 0);
		getMainLayout().addComponent(tokenValuesAsCsv, 0);
		addEventInfoToLayout();
		addDescriptionInfo();
		addApplicationInfoLayout();
		addObjectStateInfoLayout();
	}

	@Override
	protected HorizontalLayout addObjectStateInfoLayout() {
		HorizontalLayout objectStateInfo = super.addObjectStateInfoLayout();
		objectStateInfo.addComponent(ruleId, 3);
		return objectStateInfo;
	}

	@Override
	protected void updateFields(DecisionTreeView view) {
	       if(view == null) {
	            return;
	        }
		super.updateFields(view);
		view.setValidationCsv(validationCsv.getValue());
		view.setTokenValuesAsCsv(tokenValuesAsCsv.getValue());
	}

	@Override
	protected void save(DecisionTreeView view) {
		updateFields(view);
		try {
			view = controller.upsertDecisionTree(decisionTreeView);
			setMessage(messageSaveSucceeded(view.getTitle(), view.getId()));
			setView(view);
		} catch (Exception exception) {
			setMessage(messageSaveFailed(view.getTitle(), view.getId(), exception));
		}
	}

	protected void publish(DecisionTreeView view) {
		view.setStatus(PUBLISHED);
		view.setVersion("1");
		DrlValidationResults results = controller.publishDecisionTree(view);
		if (results.getIsValid()) {
			setMessage(messagePublishSucceeded(view.getTitle(), view.getId()));
			setView(view);
		} else {
			setMessage(messagePublishFailed(view.getTitle(), view.getId(), results));
		}

	}

	@Override
	protected DecisionTreeView get(String decisionTreeId) {
		try {
			decisionTreeView = controller.getDecisionTree(decisionTreeId);
		} catch (ObjectNotFoundException exception) {
			setMessage(messageObjectNotFound("Ruleset Row with id: ", decisionTreeId));
			decisionTreeView = new DecisionTreeView();
		} catch (Exception exception) {
			setMessage(messageGetFailed("Ruleset Row with id: ", decisionTreeId, exception));
			decisionTreeView = new DecisionTreeView();
		}

		decisionTreeView.adjustValuesForUI();
		setView(decisionTreeView);
		return decisionTreeView;
	}

	@Override
	public List<DecisionTreeView> findByForm(RuleAttributeFacetSearchForm form) {
		return controller.find(form);
	}

	@Override
	protected void delete(DecisionTreeView toBeDeleted) {
        controller.deleteDecisionTree(toBeDeleted.getId());
        DecisionTreeView deleted = get(toBeDeleted.getId());
        if(StringUtils.isNotBlank(deleted.getId())) {
            setMessage(messageObjectRetired("RuleSetRow", deleted.getId(), deleted.getTitle()));
        }
    }

	@Override
	protected void clear(DecisionTreeView decisionTreeView) {
		super.clear(decisionTreeView);
		decisionTreeView.setTokenValuesAsCsv("");
		decisionTreeView.setValidationCsv("");
		enableRuleId(true) ;
		setView(decisionTreeView);
	}

	protected void setActionsState(DecisionTreeView view) {
	    super.setActionsState(view);
	    if( StringUtils.isBlank(view.getStatus()) || view.getStatus().equals(DRAFT)) {
	        ruleId.setEnabled(true);
	    } else {
	        ruleId.setEnabled(false);
	    }
	}
	@Override
	protected void updateUI(DecisionTreeView decisionTreeView) {
		binder.setBean(decisionTreeView);
		super.updateUI(decisionTreeView);
		  
		if (decisionTreeView == null) {
			tokenValuesAsCsv.setValue("");
			validationCsv.setValue("");
			return;
		}
		tokenValuesAsCsv
				.setValue(decisionTreeView.getTokenValuesAsCsv() == null ? "" : decisionTreeView.getTokenValuesAsCsv());
		validationCsv.setValue(decisionTreeView.getValidationCsv() == null ? "" : decisionTreeView.getValidationCsv());
		currentAttributes.setRuleId(decisionTreeView.getRuleId());
	}

	protected void validate(DecisionTreeView decisionTreeView) {
		DrlValidationResults results = controller.validateDeveloperDecisionTree(decisionTreeView);
		if (results.getIsValid()) {
			setMessage(messageValidationSucceeds(results.getDrl()));
		} else {
			setMessage(messageValidationFailed(generateValidationSuggestion(decisionTreeView), results));
		}
	}

	@Override
	protected DecisionTreeView getView() {
	    updateFields(decisionTreeView);
		return decisionTreeView;
	}

	public void enableRuleId(Boolean enable) {
		ruleId.setEnabled(enable);
	}

	protected void edit(DecisionTreeView view, Boolean ruleIdIsValid,
            EngineController applicationController,
            FamilyViewController collegeViewController) {
		if ((view == null || isClean(view)) && currentAttributes != null) {
		    String ruleId = "";
		    if(StringUtils.isNotBlank(currentAttributes.getType()) && currentAttributes.getType().equals(RuleTemplateView.class.getName())) {
		        ruleId = currentAttributes.getRuleId();
		        currentAttributes.setId("");
		    } else if(StringUtils.isNotBlank(currentAttributes.getType()) && !currentAttributes.getType().equals(DecisionTreeView.class.getName())){
		        currentAttributes.setId("");
		    }
		    view = mapper.map(currentAttributes, DecisionTreeView.class);
		    view.setRuleId(ruleId);
		} 
		edit(view, applicationController, collegeViewController);
	}

	@Override
	protected void edit(DecisionTreeView view,
            EngineController applicationController,
            FamilyViewController collegeViewController) {
        super.edit(view, applicationController, collegeViewController);
		if (view == null) {
			view = new DecisionTreeView();
		}
		final boolean persisted = StringUtils.isNotBlank(view.getId());
		if (persisted) {
			get(view.getId());
		} else {
			decisionTreeView = view;
		}
		updateUI(decisionTreeView);
	}

	@Override
	protected void setView(DecisionTreeView decisionTreeView) {
	    super.setView(decisionTreeView);
		this.decisionTreeView = decisionTreeView;
		
		updateUI(decisionTreeView);
	}


}
