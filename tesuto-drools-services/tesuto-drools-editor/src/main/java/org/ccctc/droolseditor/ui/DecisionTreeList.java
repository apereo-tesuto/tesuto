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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.DecisionTreeController;
import org.ccctc.droolseditor.views.DecisionTreeView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

@SpringComponent
@UIScope
public class DecisionTreeList extends AttributeFilters<DecisionTreeView> {

    private static final long serialVersionUID = 1L;

    private static final String FILTER_RULE_ID = "ruleId";

    private DecisionTreeController controller;

    final Grid<DecisionTreeView> grid;

    final Map<String, Object> filters;

    @Autowired
    Mapper mapper;

    @Autowired
    public DecisionTreeList(DecisionTreeController controller) {
        this.controller = controller;
        grid = new Grid<>(DecisionTreeView.class);
        filters = new HashMap<>();
    }

    public void init(DecisionTreeEditor decisionTreeEditor, RuleAttributesView currentAttributes,
            EngineController applicationController, FamilyViewController collegeViewController) {
        super.init(filters, grid, currentAttributes, applicationController, collegeViewController);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().addValueChangeListener(e -> {
            decisionTreeEditor.setView(e.getValue());
        });
    }

    @Override
    public void buildSearchFeature(Map<String, Object> filters, HorizontalLayout searchObjectState,
            HorizontalLayout searchApplication, HorizontalLayout searchEventType) {
        addFilter(filters, searchObjectState, FILTER_RULE_ID, "Rule Id").setWidth(300, Unit.PIXELS);
        super.buildSearchFeature(filters, searchObjectState, searchApplication, searchEventType);
    }

    protected void updateFiltersFromForm(RuleAttributeFacetSearchForm form, final Map<String, Object> filters) {
        super.updateFiltersFromForm(form, filters);
        ((TextField) filters.get(FILTER_RULE_ID)).setValue(form.getRuleId() == null ? "" : form.getRuleId());
    }

    protected RuleAttributeFacetSearchForm getSearchForm() {
        return getSearchForm(filters);
    }

    @Override
    protected RuleAttributeFacetSearchForm getSearchForm(final Map<String, Object> filters) {
        RuleAttributeFacetSearchForm form = super.getSearchForm(filters);
        form.setRuleId(((TextField) filters.get(FILTER_RULE_ID)).getValue());
        return form;
    }
   

    @Override
    public List<DecisionTreeView> findByForm(RuleAttributeFacetSearchForm form) {
        updateFiltersFromForm(form, filters);
        return controller.getDeveloperDecisionTrees(form);
    }

    @Override
    public Grid<DecisionTreeView> getGrid() {
        return grid;
    }

    @Override
    public Map<String, Object> getFilters() {
        return filters;
    }

    @Override
    public String getGridObjectType() {
        return DecisionTreeView.class.getName();
    }

    @Override
    protected void updateFiltersFromCurrentAttributes(final Map<String, Object> filters) {
        super.updateFiltersFromCurrentAttributes(filters);
        if (getGridObjectType().equals(currentAttributes.getType())) {
            ((TextField) filters.get(FILTER_RULE_ID))
                    .setValue(currentAttributes.getRuleId() == null ? "" : currentAttributes.getRuleId());
        } else if (RuleTemplateView.class.getName().equals(currentAttributes.getType())) {
            ((TextField) filters.get(FILTER_RULE_ID))
            .setValue(currentAttributes.getId() == null ? "" : currentAttributes.getId());
        } else {
            ((TextField) filters.get(FILTER_RULE_ID)).setValue("");
        }
    }

    @Override
    protected void updateCurrentAttributesFromFilters(final Map<String, Object> filters) {
        super.updateCurrentAttributesFromFilters(filters);
        currentAttributes.setRuleId(((TextField) filters.get(FILTER_RULE_ID)).getValue());
    }
}
