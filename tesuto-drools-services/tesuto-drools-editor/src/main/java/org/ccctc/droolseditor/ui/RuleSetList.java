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

import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.RuleSetController;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;

@SpringComponent
@UIScope
public class RuleSetList extends AttributeFilters<RuleSetDTO> {

	private static final long serialVersionUID = 1L;
	 
	private RuleSetController controller;
	private RuleSetEditor ruleSetEditor;
	
	final Grid<RuleSetDTO> grid;

	final Map<String, Object> filters;
	
	@Autowired
	Mapper mapper;

	@Autowired
	public RuleSetList(RuleSetController controller) {
		this.controller = controller;
		grid = new Grid<>(RuleSetDTO.class);
		filters = new HashMap<>();		
	}
	
	   public void init(
	           RuleSetEditor ruleSetEditor, RuleAttributesView currentAttributes,
	           EngineController applicationController, FamilyViewController collegeViewController) {
	           this.ruleSetEditor = ruleSetEditor;
	           super.init(filters,  grid, currentAttributes, applicationController, collegeViewController);
	           grid.setSelectionMode(SelectionMode.SINGLE);
	           grid.asSingleSelect().addValueChangeListener(e -> {
	                       ruleSetEditor.setView(e.getValue());
	           });
	   }
	
	@Override
	public void buildSearchFeature(Map<String,Object> filters, HorizontalLayout searchObjectState,HorizontalLayout searchApplication,HorizontalLayout searchEventType) {
		super.buildSearchFeature(filters, searchObjectState, searchApplication, searchEventType);
	}
	
	protected void updateFiltersFromForm(RuleAttributeFacetSearchForm form, final Map<String, Object> filters) {
		super.updateFiltersFromForm(form, filters);
	}
	
	   protected RuleAttributeFacetSearchForm getSearchForm() {
	        return  getSearchForm(filters);
	    }
	    
	
	@Override
	protected RuleAttributeFacetSearchForm getSearchForm(final Map<String, Object> filters) {
		RuleAttributeFacetSearchForm form = super.getSearchForm(filters);
		return form;
	}
	
	@Override
	public List<RuleSetDTO> findByForm(RuleAttributeFacetSearchForm form) {
		updateFiltersFromForm(form, filters) ;
		return controller.find(form);
	}
	
	@Override
	public Grid<RuleSetDTO> getGrid(){
		return grid;
	}
	
    @Override
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public String getGridObjectType() {
        return RuleSetDTO.class.getName();
    }
}
