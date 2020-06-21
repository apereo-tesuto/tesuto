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
import org.ccctc.droolseditor.controllers.RuleTemplateController;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.ccctc.droolseditor.views.RuleTemplateView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

@SpringComponent
@UIScope
public class RuleTemplateList extends AttributeFilters<RuleTemplateView> {

    private static final long serialVersionUID = 1L;

    private final Grid<RuleTemplateView> grid;

    private final Map<String, Object> filters;

    private final RuleTemplateController controller;

    private RuleTemplateEditor ruleEditor;

    @Autowired
    public RuleTemplateList(RuleTemplateController controller) {
        this.controller = controller;
        grid = new Grid<>(RuleTemplateView.class);
        filters = new HashMap<>();
    }

    public void init(RuleTemplateEditor ruleEditor, RuleAttributesView currentAttributes, 
            EngineController applicationController, FamilyViewController collegeViewController) {
        this.ruleEditor = ruleEditor;
        this.init(filters, grid, currentAttributes, applicationController, collegeViewController);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().addValueChangeListener(e -> {
            ruleEditor.setView(e.getValue());
        });
    }

    @Override
    protected RuleAttributeFacetSearchForm getSearchFormFromAttributes(RuleTemplateView attributes) {
        RuleAttributeFacetSearchForm form = super.getSearchFormFromAttributes(attributes);
        return form;
    }

    protected RuleAttributeFacetSearchForm getSearchForm() {
        return getSearchForm(filters);
    }

    @Override
    public Grid<RuleTemplateView> getGrid() {
        return grid;
    }

    @Override
    public List<RuleTemplateView> findByForm(RuleAttributeFacetSearchForm form) {
        updateFiltersFromForm(form, filters);
        List<RuleTemplateView> views = controller.find(form);
        return views;
    }

    @Override
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public String getGridObjectType() {
        return RuleTemplateView.class.getName();
    }
}
