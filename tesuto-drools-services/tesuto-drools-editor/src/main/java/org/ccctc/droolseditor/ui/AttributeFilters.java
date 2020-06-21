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

import static org.ccctc.common.droolscommon.model.RuleStatus.VALID_STATUSES;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.views.FamilyView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public abstract class AttributeFilters<T extends RuleAttributesDTO> extends CssLayout {
    private static final String FILTER_APPLICATION = "application";
    private static final String FILTER_CATEGORY = "category";
    private static final String FILTER_COMPETENCY_DISCIPLINE = "discipline";
    private static final String FILTER_EVENT = "event";
    private static final String FILTER_ID = "id";
    private static final String FILTER_MISCODE = "miscode";
    private static final String FILTER_STATUS = "status";
    private static final long serialVersionUID = 1L;

    public static final List<String> VALID_COMPETENCY_DISCIPLINES = Arrays.asList("MATH", "ENGLISH", "ESL");

    ComboBox<String> categoryFilter;

    protected RuleAttributesView currentAttributes = new RuleAttributesView();

    private EngineController engineController;

    ComboBox<String> engineFilter;

    ComboBox<String> eventFilter;

    ComboBox<String> familyFilter;

    private FamilyViewController familyViewController;

    @Autowired
    private Mapper mapper;

    private TextArea message = new TextArea("Messages");

    private ObjectMapper objectMapper = new ObjectMapper();

    protected ComboBox<String> addComboBoxFilter(Map<String, Object> filters, HorizontalLayout seachFilters, String filterName,
                    String placeHolder) {
        ComboBox<String> filter = new ComboBox<>(placeHolder);
        filter.setVisible(true);
        seachFilters.addComponentAsFirst(filter);
        filters.put(filterName, filter);
        return filter;
    }

    protected TextField addFilter(Map<String, Object> filters, HorizontalLayout seachFilters, String filterName,
                    String placeHolder) {
        TextField filter = new TextField(placeHolder);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.setVisible(true);
        seachFilters.addComponentAsFirst(filter);
        filters.put(filterName, filter);
        return filter;
    }

    public void buildSearchFeature(Map<String, Object> filters, HorizontalLayout searchObjectState,
                    HorizontalLayout searchApplication, HorizontalLayout searchEventType) {
        eventFilter = addComboBoxFilter(filters, searchEventType, FILTER_EVENT, "Event");
        eventFilter.setWidth(180, Unit.PIXELS);
        categoryFilter = addComboBoxFilter(filters, searchEventType, FILTER_CATEGORY, "Category");
        categoryFilter.setWidth(180, Unit.PIXELS);
        ComboBox<String> disciplineFilter = addComboBoxFilter(filters, searchEventType, FILTER_COMPETENCY_DISCIPLINE,
                        "Competency Map Discipline");
        disciplineFilter.setWidth(120, Unit.PIXELS);
        disciplineFilter.setEmptySelectionAllowed(true);
        disciplineFilter.setItems(VALID_COMPETENCY_DISCIPLINES);
        familyFilter = addComboBoxFilter(filters, searchApplication, FILTER_MISCODE, "College");
        familyFilter.setWidth(120, Unit.PIXELS);
        familyFilter.addSelectionListener(e -> setFamilyDependentValues(e.getValue()));
        engineFilter = addComboBoxFilter(filters, searchApplication, FILTER_APPLICATION, "Application");
        engineFilter.setWidth(120, Unit.PIXELS);
        engineFilter.addSelectionListener(e -> setEngineDependentValues(e.getValue()));
        ComboBox<String> statusFilter = addComboBoxFilter(filters, searchObjectState, FILTER_STATUS, "Status");
        statusFilter.setWidth(120, Unit.PIXELS);
        statusFilter.setEmptySelectionAllowed(true);
        statusFilter.setItems(VALID_STATUSES);
        addFilter(filters, searchObjectState, FILTER_ID, "Object Id").setWidth(300, Unit.PIXELS);
    }

    public void clearForm(final Map<String, Object> filters) {
        updateFiltersFromForm(new RuleAttributeFacetSearchForm(), filters);
    }

    public void findByCurrentAttributes() {
        setCurrentlyActiveFamilyCodes(familyViewController);
        setCurrentlyActiveEngines(engineController);
        updateFiltersFromCurrentAttributes(getFilters());
    }

    public abstract List<T> findByForm(RuleAttributeFacetSearchForm form);

    protected List<T> findWithFilters(final Map<String, Object> filters) {
        return findByForm(getSearchForm(filters));
    }

    public abstract Map<String, Object> getFilters();

    public abstract Grid<T> getGrid();

    public abstract String getGridObjectType();

    public void getListAsJson() {
        List<T> objs = findWithFilters(getFilters());
        try {
            String json = objectMapper.writeValueAsString(objs.toArray());
            setMessage(json);
        }
        catch (JsonProcessingException exception) {

        }
    }

    protected String getMessage() {
        return message.getValue();
    }

    protected TextArea getMessageUI() {
        return message;
    }

    @SuppressWarnings({ "unchecked" })
    protected RuleAttributeFacetSearchForm getSearchForm(final Map<String, Object> filters) {
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();

        form.setEngine(((ComboBox<String>) filters.get(FILTER_APPLICATION)).getValue());
        form.setFamily(((ComboBox<String>) filters.get(FILTER_MISCODE)).getValue());
        form.setCompetencyMapDiscipline(((ComboBox<String>) filters.get(FILTER_COMPETENCY_DISCIPLINE)).getValue());
        form.setStatus(((ComboBox<String>) filters.get(FILTER_STATUS)).getValue());
        form.setCategory(((ComboBox<String>) filters.get(FILTER_CATEGORY)).getValue());
        form.setEvent(((ComboBox<String>) filters.get(FILTER_EVENT)).getValue());
        form.setId(((TextField) filters.get(FILTER_ID)).getValue());
        return form;
    }

    protected RuleAttributeFacetSearchForm getSearchFormFromAttributes(T attributes) {
        RuleAttributeFacetSearchForm form = mapper.map(attributes, RuleAttributeFacetSearchForm.class);
        return form;
    }

    public T getSelected() {
        Set<T> set = getGrid().getSelectedItems();
        if (CollectionUtils.isNotEmpty(set)) {
            return set.iterator().next();
        }
        return null;
    }

    protected void init(Map<String, Object> filters, Grid<T> grid, RuleAttributesView currentAttributes,
                    EngineController engineController, FamilyViewController collegeViewController) {
        this.currentAttributes = currentAttributes;
        this.engineController = engineController;
        this.familyViewController = collegeViewController;
        HorizontalLayout actions = new HorizontalLayout();
        HorizontalLayout searchObjectState = new HorizontalLayout();
        HorizontalLayout searchApplication = new HorizontalLayout();
        HorizontalLayout searchEventType = new HorizontalLayout();
        Button clearFacets = new Button("Clear Facets");
        Button searchBtn = new Button("Start Search", FontAwesome.SEARCH);
        Button viewBtn = new Button("View Json");
        actions.addComponent(clearFacets);
        actions.addComponent(viewBtn);
        actions.addComponent(searchBtn);
        VerticalLayout facetedSearch = new VerticalLayout(actions, searchObjectState, searchApplication, searchEventType);
        facetedSearch.setCaption("Faceted Search");
        VerticalLayout mainLayout = new VerticalLayout(facetedSearch, grid);
        addComponent(mainLayout);
        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(1000, Unit.PIXELS);
        grid.setColumns("title", "category", "application", "cccMisCode", "competencyMapDiscipline");

        buildSearchFeature(filters, searchObjectState, searchApplication, searchEventType);

        clearFacets.addClickListener(e -> clearForm(filters));
        searchBtn.addClickListener(e -> updateList());
        viewBtn.addClickListener(e -> getListAsJson());
        searchBtn.focus();
    }

    private void setEngineDependentValues(String engineName) {
        if (StringUtils.isBlank(engineName)) {
            return;
        }
        EngineDTO engine = engineController.getEngine(engineName);
        eventFilter.setItems(engine.getEvents());
        categoryFilter.setItems(engine.getCategories());
    }

    private void setFamilyDependentValues(String familyCode) {
        if (StringUtils.isBlank(familyCode)) {
            return;
        }
        FamilyView familyView = familyViewController.getFamilyByFamilyCode(familyCode);
        engineFilter.setItems(familyView.getSelectedApplications());
    }

    public void setCurrentAttributes(RuleAttributesView currentAttributes) {
        this.currentAttributes = currentAttributes;
    }

    @SuppressWarnings("unchecked")
    private void setCurrentlyActiveEngines(EngineController engineController) {
        List<EngineDTO> engines = engineController.getEngines(StandardStatus.ACTIVE);
        Set<String> names = engines.stream().map(engine -> engine.getName()).collect(Collectors.toSet());
        ComboBox<String> engineFilter = (ComboBox<String>) getFilters().get(FILTER_APPLICATION);
        engineFilter.setItems(names);
    }

    @SuppressWarnings("unchecked")
    private void setCurrentlyActiveFamilyCodes(FamilyViewController familyController) {
        List<FamilyView> familyViews = familyController.getFamilies(StandardStatus.ACTIVE);
        Set<String> familyCodes = familyViews.stream().map(familyView -> familyView.getFamilyCode()).collect(Collectors.toSet());
        ComboBox<String> comboBox = (ComboBox<String>) getFilters().get(FILTER_MISCODE);
        comboBox.setItems(familyCodes);
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    protected void setMessage(String value) {
        message.setValue(value + "\n\n\nForces update event" + UUID.randomUUID().toString());
    }

    @SuppressWarnings("unchecked")
    protected void updateCurrentAttributesFromFilters(final Map<String, Object> filters) {
        currentAttributes.setEngine(((ComboBox<String>) filters.get(FILTER_APPLICATION)).getValue());
        currentAttributes.setFamily(((ComboBox<String>) filters.get(FILTER_MISCODE)).getValue());
        currentAttributes.setCompetencyMapDiscipline(((ComboBox<String>) filters.get(FILTER_COMPETENCY_DISCIPLINE)).getValue());
        currentAttributes.setStatus(((ComboBox<String>) filters.get(FILTER_STATUS)).getValue());
        currentAttributes.setCategory(((ComboBox<String>) filters.get(FILTER_CATEGORY)).getValue());
        currentAttributes.setEvent(((ComboBox<String>) filters.get(FILTER_EVENT)).getValue());
        currentAttributes.setId(((TextField) filters.get(FILTER_ID)).getValue());
        currentAttributes.setType(getGridObjectType());
    }

    @SuppressWarnings("unchecked")
    protected void updateFiltersFromCurrentAttributes(final Map<String, Object> filters) {
        ((ComboBox<String>) filters.get(FILTER_APPLICATION))
                        .setValue(currentAttributes.getEngine() == null ? "" : currentAttributes.getEngine());
        ((ComboBox<String>) filters.get(FILTER_MISCODE))
                        .setValue(currentAttributes.getFamily() == null ? "" : currentAttributes.getFamily());
        ((ComboBox<String>) filters.get(FILTER_COMPETENCY_DISCIPLINE))
                        .setValue(currentAttributes.getCompetencyMapDiscipline() == null ? ""
                                        : currentAttributes.getCompetencyMapDiscipline());
        ((ComboBox<String>) filters.get(FILTER_STATUS))
                        .setValue(currentAttributes.getStatus() == null ? "" : currentAttributes.getStatus());
        ((ComboBox<String>) filters.get(FILTER_CATEGORY))
                        .setValue(currentAttributes.getCategory() == null ? "" : currentAttributes.getCategory());
        ((ComboBox<String>) filters.get(FILTER_EVENT))
                        .setValue(currentAttributes.getEvent() == null ? "" : currentAttributes.getEvent());
        if (getGridObjectType().equals(currentAttributes.getType())) {
            ((TextField) filters.get(FILTER_ID)).setValue(currentAttributes.getId() == null ? "" : currentAttributes.getId());
        } else {
            ((TextField) filters.get(FILTER_ID)).setValue("");
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateFiltersFromForm(RuleAttributeFacetSearchForm form, final Map<String, Object> filters) {
        ((ComboBox<String>) filters.get(FILTER_APPLICATION)).setValue(form.getEngine() == null ? "" : form.getEngine());
        ((ComboBox<String>) filters.get(FILTER_MISCODE)).setValue(form.getFamily() == null ? "" : form.getFamily());
        ((ComboBox<String>) filters.get(FILTER_COMPETENCY_DISCIPLINE))
                        .setValue(form.getCompetencyMapDiscipline() == null ? "" : form.getCompetencyMapDiscipline());
        ((ComboBox<String>) filters.get(FILTER_STATUS)).setValue(form.getStatus() == null ? "" : form.getStatus());
        ((ComboBox<String>) filters.get(FILTER_CATEGORY)).setValue(form.getCategory() == null ? "" : form.getCategory());
        ((ComboBox<String>) filters.get(FILTER_EVENT)).setValue(form.getEvent() == null ? "" : form.getEvent());
        ((TextField) filters.get(FILTER_ID)).setValue(form.getId() == null ? "" : form.getId());
    }

    public void updateList() {
        try {
            getGrid().deselectAll();
            getGrid().setItems(findWithFilters(getFilters()));
            updateCurrentAttributesFromFilters(getFilters());
        }
        catch (Exception exception) {
            getGrid().deselectAll();
            setMessage("Search failed most likely because previously select object is no longer in the list, try again");
        }
    }
}
