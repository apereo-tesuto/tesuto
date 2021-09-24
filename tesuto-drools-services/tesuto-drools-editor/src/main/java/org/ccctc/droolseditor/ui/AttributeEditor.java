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
import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;
import static org.ccctc.common.droolscommon.model.RuleStatus.RETIRED;
import static org.ccctc.common.droolscommon.model.RuleStatus.VALID_STATUSES;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.common.droolscommon.validation.DrlValidationResults;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.views.FamilyView;
import org.ccctc.droolseditor.views.RuleAttributesView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AttributeEditor<T extends RuleAttributesDTO> extends CssLayout {
    private static final long serialVersionUID = 1L;

    private Button cancel = new Button("Clear Data");

    private ComboBox<String> category = new ComboBox<>("Category");

    private ComboBox<String> cccMisCode = new ComboBox<>("College miscode");

    private Button clone = new Button("Clone");

    private ComboBox<String> competencyMapDiscipline = new ComboBox<>("Competency Map Discipline");

    protected RuleAttributesView currentAttributes;

    private Button delete = new Button("Delete", FontAwesome.TRASH_O);

    private TextArea description = new TextArea("Description");

    private ComboBox<String> engine = new ComboBox<>("Application");

    private EngineController engineController;

    private ComboBox<String> event = new ComboBox<>("Event");

    private HorizontalLayout eventInfo = new HorizontalLayout(cccMisCode, event, category, competencyMapDiscipline);

    private TextField id = new TextField("id");

    private VerticalLayout mainLayout = new VerticalLayout();

    @Autowired
    Mapper mapper;

    private TextArea message = new TextArea("Messages");

    private Button publish = new Button("Publish");

    private Button save = new Button("Save", FontAwesome.SAVE);

    private ComboBox<String> status = new ComboBox<>("Status");

    private TextField title = new TextField("Title");

    private Button validate = new Button("Validate");

    private TextField version = new TextField("Version");

    private CssLayout actions = new CssLayout(save, validate, publish, cancel, delete);

    private HorizontalLayout objectStateInfo = new HorizontalLayout(id, status, version, clone);
    
    private HorizontalLayout engineInfo = new HorizontalLayout(engine, title, description);
    
    protected CssLayout addActionsToLayout() {
        mainLayout.addComponent(actions, 0);
        return actions;
    }

    protected HorizontalLayout addApplicationInfoLayout() {
        mainLayout.addComponent(engineInfo, 0);
        return engineInfo;
    }

    protected TextArea addDescriptionInfo() {
        mainLayout.addComponent(description, 0);
        return description;
    }

    protected HorizontalLayout addEventInfoToLayout() {
        mainLayout.addComponent(eventInfo, 0);
        return eventInfo;
    }

    protected HorizontalLayout addObjectStateInfoLayout() {
        mainLayout.addComponent(objectStateInfo, 0);
        return objectStateInfo;
    }

    protected ComboBox<String> cccMisCode() {
        return cccMisCode;
    }

    protected void clear(T view) {
        view.setEngine("");
        view.setFamily("");
        view.setCompetencyMapDiscipline("");
        view.setEvent("");
        view.setCategory("");
        view.setId("");
        view.setStatus(DRAFT);
        view.setTitle("");
        view.setVersion("");
        view.setDescription("");
        setActionsState(view);
        updateUI(view);
    }

    protected void clone(T view) {
        view.setId("");
        view.setStatus(DRAFT);
        view.setVersion("");
        setActionsState(view);
        updateUI(view);
    }

    protected abstract void delete(T view);

    protected void edit(T view, EngineController engineController, FamilyViewController familyViewController) {
        setCurrentlyActiveFamilyCodes(familyViewController);
        setCurrentlyActiveEngines(engineController);
    }

    public abstract List<T> findByForm(RuleAttributeFacetSearchForm form);

    protected String generateValidationSuggestion(T view) {
        if (StringUtils.isBlank(view.getEvent())) {
            StringBuilder builder = new StringBuilder("\nAn event was not selected and this may be the issue. \n");
            builder.append("\nEvent Selection is not required but")
                   .append(" you will only compile against the Rules Editor dependencies and java package.\n");
            return builder.toString();
        }
        return "";
    }

    protected abstract T get(String id);

    protected VerticalLayout getMainLayout() {
        return mainLayout;
    }

    protected String getMessage() {
        return message.getValue();
    }

    protected TextArea getMessageUI() {
        return message;
    }

    protected abstract T getView();

    protected void init(RuleAttributesView currentAttributes, EngineController engineController) {
        this.engineController = engineController;
        this.currentAttributes = currentAttributes;
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> save(getView()));
        delete.addClickListener(e -> delete(getView()));
        cancel.addClickListener(e -> clear(getView()));
        validate.addClickListener(e -> validate(getView()));
        publish.addClickListener(e -> publish(getView()));
        clone.addClickListener(e -> clone(getView()));
        engine.addSelectionListener(e -> setEngineDependentValues(e.getValue()));
        title.setWidth(300, Unit.PIXELS);
        description.setWidth(1000, Unit.PIXELS);
        description.setHeight(120, Unit.PIXELS);
        id.setWidth(300, Unit.PIXELS);
        version.setWidth(40, Unit.PIXELS);
        version.setEnabled(false);
        cccMisCode.setWidth(120, Unit.PIXELS);
        competencyMapDiscipline.setItems(AttributeFilters.VALID_COMPETENCY_DISCIPLINES);
        competencyMapDiscipline.setWidth(120, Unit.PIXELS);
        status.setWidth(120, Unit.PIXELS);
        status.setEnabled(false);
        status.setItems(VALID_STATUSES);

        clone.setDescription(
                        "Cloning simply deletes key values \nso a new object is created on Save. \nYou must still save the object");
        initializeLayout();
        addComponents(mainLayout);
    }

    protected void initializeLayout() {
    }

    protected Boolean isClean(T obj) {
        if (obj == null) {
            return true;
        }
        if (StringUtils.isNotBlank(obj.getId()))
            return false;
        if (StringUtils.isNotBlank(obj.getFamily()))
            return false;
        if (StringUtils.isNotBlank(obj.getEngine()))
            return false;
        if (StringUtils.isNotBlank(obj.getVersion()))
            return false;
        if (StringUtils.isNotBlank(obj.getStatus()))
            return false;
        if (StringUtils.isNotBlank(obj.getEvent()))
            return false;
        if (StringUtils.isNotBlank(obj.getCategory()))
            return false;
        if (StringUtils.isNotBlank(obj.getCompetencyMapDiscipline()))
            return false;
        if (StringUtils.isNotBlank(obj.getDescription()))
            return false;
        return true;

    }

    protected String messageGetFailed(String objectType, String id, Exception exception) {
        return String.format("Attempt to get object of type %s with id: %s . failed with the following errors:\n%s", title, id,
                        exception.getMessage() + "\n" + ExceptionUtils.getFullException(exception));
    }

    protected String messageObjectNotFound(String objectType, String id) {
        return String.format("%s with id: %s was not found:\n", objectType, id);
    }

    protected String messageObjectRetired(String objectName, String id, String title) {
        return String.format("%s (%s) with title: %s has been retired! It will no longer be available and can not be edited.",
                        objectName, id, title);
    }

    protected String messagePublishFailed(String title, String id, DrlValidationResults results) {
        return String.format("%s with id: %s was not published. failed with the following errors:\n%s", title, id,
                        messageValidationFailed("", results));
    }

    protected String messagePublishFailed(String title, String id, Exception exception) {
        return String.format("%s with id: %s was not published. failed with the following errors:\n%s", title, id,
                        exception.getMessage() + "\n" + ExceptionUtils.getFullException(exception));
    }

    protected String messagePublishSucceeded(String title, String id) {
        return String.format("%s with id: %s has been successfully published and will no longer be editable. You can clone.", title,
                        id);
    }

    protected String messageSaveFailed(String title, String id, Exception exception) {
        return String.format("%s with id: %s was not saved. failed with the following errors:\n%s", title, id,
                        exception.getMessage() + "\n" + ExceptionUtils.getFullException(exception));
    }

    protected String messageSaveSucceeded(String title, String id) {
        return String.format("%s with id: %s has been successfully saved. If it is marked as draft you may continue to edit it.",
                        title, id);
    }

    protected String messageValidationFailed(String suggestion, DrlValidationResults results) {
        StringBuffer errorMessage = new StringBuffer("VALIDATION FAILED FOR THE FOLLOWING REASONS:\n");
        errorMessage.append(results.getExceptionMessage());
        errorMessage.append("\nDETAILED ERROR MESSAGE:");
        errorMessage.append(suggestion);
        if (StringUtils.isNotBlank(results.getExceptionMessage()) && results.getExceptionMessage().contains("Compiling")) {
            errorMessage.append("\n\nAPPARENTLY YOU HAVE COMPILE ERRORS")
                            .append("\nIF YOU SEE messages starting with: \"Unable to resolve ObjectType\"")
                            .append("\n    1. make sure you have selected THE APPROPRIATE EVENT ")
                            .append("\n    2. MAKE SURE YOU ARE HITTING CORRECT ENDPOINT! \n");
        }
        if (CollectionUtils.isNotEmpty(results.getErrors())) {
            for (String error : results.getErrors()) {
                errorMessage.append("\n").append(error);
            }
        }
        errorMessage.append("\nGenerated DRL File:\n").append(results.getDrl());
        return errorMessage.toString();
    }

    protected String messageValidationSucceeds(String drl) {
        return String.format("VALIDATION SUCCEEDED: Generated Rule Follows \n%s", drl);
    }

    protected abstract void publish(T view);

    protected void removeValidate() {
        validate.setVisible(false);
        actions.removeComponent(validate);
    }

    protected abstract void save(T view);

    protected void setActionsState(T view) {
        if (view == null || view.getStatus() == null) {
            publish.setVisible(false);
            cancel.setVisible(false);
            delete.setVisible(false);
            delete.setCaption("Delete");
            save.setVisible(true);
            return;
        }
        if (view.getStatus().equals(DRAFT)) {
            publish.setVisible(true);
            cancel.setVisible(true);
            delete.setVisible(true);
            delete.setCaption("Delete");
            save.setVisible(true);
            return;
        }
        if (view.getStatus().equals(PUBLISHED)) {
            publish.setVisible(false);
            cancel.setVisible(true);
            delete.setVisible(true);
            delete.setCaption("Retire");
            save.setVisible(false);
            return;
        }
        if (view.getStatus().equals(RETIRED)) {
            publish.setVisible(false);
            cancel.setVisible(true);
            delete.setVisible(false);
            save.setVisible(false);
            return;
        }
    }

    private void setEngineDependentValues(String applicationName) {
        if (StringUtils.isBlank(applicationName)) {
            return;
        }
        EngineDTO application = engineController.getEngine(applicationName);
        event.setItems(application.getEvents());
        category.setItems(application.getCategories());
    }

    private void setCurrentlyActiveEngines(EngineController applicationController) {
        List<EngineDTO> applications = applicationController.getEngines(StandardStatus.ACTIVE);
        Set<String> names = applications.stream().map(a -> a.getName()).collect(Collectors.toSet());
        engine.setItems(names);
    }

    private void setCurrentlyActiveFamilyCodes(FamilyViewController familyViewController) {
        List<FamilyView> familyViews = familyViewController.getFamilies(StandardStatus.ACTIVE);
        Set<String> familyCodes = familyViews.stream().map(familyView -> familyView.getFamilyCode()).collect(Collectors.toSet());
        cccMisCode.setItems(familyCodes);
    }

    protected void setMessage(String value) {
        message.setValue(value + "\n\n\nForces update event" + UUID.randomUUID().toString());
    }

    protected void setValidateVisible(Boolean visible) {
        validate.setVisible(visible);
    }

    protected void setView(T view) {
        setActionsState(view);
    }

    public void updateCurrentAttributes(T r) {
        currentAttributes.setEngine(r.getEngine());
        currentAttributes.setTitle(r.getTitle());
        currentAttributes.setCategory(r.getCategory());
        currentAttributes.setFamily(r.getFamily());
        currentAttributes.setCompetencyMapDiscipline(r.getCompetencyMapDiscipline());
        currentAttributes.setDescription(r.getDescription());
        currentAttributes.setEvent(r.getEvent());
        currentAttributes.setStatus(r.getStatus());
        currentAttributes.setType(r.getClass().getName());
        currentAttributes.setId(r.getId());
    }

    // TODO not sure why we are having to do this technically the object is
    // bound to the ui.
    protected void updateFields(T view) {
        if (view == null) {
            return;
        }
        view.setEngine(engine.getValue());
        view.setTitle(title.getValue());
        view.setDescription(description.getValue());
        view.setEvent(event.getValue());
        view.setCategory(category.getValue());
        view.setFamily(cccMisCode.getValue());
        view.setStatus(status.getValue());
        view.setId(id.getValue());
        view.setVersion(version.getValue());
        view.setCompetencyMapDiscipline(competencyMapDiscipline.getValue());
    }

    protected void updateUI(T rule) {
        if (rule == null) {
            engine.setValue("");
            id.setValue("");
            status.setValue("");
            competencyMapDiscipline.setValue("");
            event.setValue("");
            category.setValue("");
            version.setValue("");
            title.setValue("");
            description.setValue("");
            cccMisCode.setValue("");
            return;
        }
        rule.adjustValuesForUI();
        engine.setValue(rule.getEngine());
        id.setValue(rule.getId());
        status.setValue(rule.getStatus());
        competencyMapDiscipline.setValue(rule.getCompetencyMapDiscipline());
        event.setValue(rule.getEvent());
        category.setValue(rule.getCategory());
        version.setValue(rule.getVersion());
        title.setValue(rule.getTitle());
        description.setValue(rule.getDescription());
        cccMisCode.setValue(rule.getFamily());
        updateCurrentAttributes(rule);
    }

    public void updateViewWithCurrentAttributes(T r) {
        r.setEngine(currentAttributes.getEngine());
        r.setTitle(currentAttributes.getTitle());
        r.setCategory(currentAttributes.getCategory());
        r.setFamily(currentAttributes.getFamily());
        r.setCompetencyMapDiscipline(currentAttributes.getCompetencyMapDiscipline());
        r.setDescription(currentAttributes.getDescription());
        r.setEvent(currentAttributes.getEvent());
        r.setStatus(currentAttributes.getStatus());
        if (r.getClass().getName().equals(currentAttributes.getType())) {
            r.setId(currentAttributes.getId());
        } else {
            r.setId("");
        }
    }

    protected abstract void validate(T view);

}
