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

import static org.ccctc.common.droolscommon.model.StandardStatus.VALID_STATUSES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.controllers.EngineController;
import org.ccctc.droolseditor.views.FamilyView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class FamilyViewEditor extends CssLayout {
    private static final long serialVersionUID = 1L;

    private Button activate = new Button("Activate");

    private TextArea availableEngines = new TextArea("Available Applications");

    private Binder<FamilyView> binder = new Binder<>(FamilyView.class);

    private TextField cccMisCode = new TextField("CCC Miscode");

    private FamilyView familyView;

    private FamilyViewController controller;

    private Button delete = new Button("Delete", FontAwesome.TRASH_O);

    private TextField description = new TextField("Name");

    private EngineController engineController;

    private TextField id = new TextField("Id");

    private TextArea message = new TextArea("Message");

    private Button save = new Button("Save", FontAwesome.SAVE);

    private TextArea selectedEngines = new TextArea("Selected Applications");

    private ComboBox<String> status = new ComboBox<>("Status");

    private HorizontalLayout familyInfo = new HorizontalLayout(cccMisCode, status, description);

    private HorizontalLayout engineInfo = new HorizontalLayout(selectedEngines, availableEngines);

    private CssLayout actions = new CssLayout(save, activate, delete);

    private VerticalLayout mainLayout = new VerticalLayout(familyInfo, engineInfo, actions);

    @Autowired
    public FamilyViewEditor(FamilyViewController controller, EngineController engineController) {
        this.controller = controller;
        this.engineController = engineController;
        binder.forField(selectedEngines).withConverter(new StringToSetConverter()).bind(FamilyView::getSelectedApplications,
                        FamilyView::setSelectedApplications);
        availableEngines.setValue(getCurrentlyActiveEngines());
        binder.bindInstanceFields(this);
    }

    public void activate() {
        updateObject(familyView);
        try {
            if (StringUtils.isNotBlank(familyView.getFamilyCode())) {
                familyView.setStatus(StandardStatus.ACTIVE);
                familyView = controller.addFamily(familyView);
                familyView.adjustValuesForUI();
                updateUI(familyView);
                binder.setBean(familyView);
                setMessage("Family is now active and can be used to define rules, rulesetrows and rulesets.");
            } else {
                this.setMessage("Family requires a code to be activated.");
            }
        }
        catch (Exception exception) {
            setMessage("activated", exception);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Set<String> convertToModel(String value) {
        if (StringUtils.isBlank(value)) {
            return new HashSet<String>();
        }
        value = StringUtils.deleteWhitespace(value);
        return new HashSet(Arrays.asList(value.split(",")));
    }

    public String convertToPresentation(Set<String> value) {
        if (value == null || value.isEmpty()) {
            return " ";
        }
        return StringUtils.deleteWhitespace(StringUtils.join(value, ","));
    }

    public void delete() {
        updateObject(familyView);
        try {
            if (StringUtils.isNotBlank(familyView.getId())) {
                familyView = controller.deleteFamily(familyView.getId());
                familyView.adjustValuesForUI();
                updateUI(familyView);
                binder.setBean(familyView);
                setMessage("Application is now inactive or deleted.");
            } else {
                this.setMessage("Family requires a code to be deleted.");
            }
        }
        catch (Exception exception) {
            setMessage("deleted", exception);
        }
    }

    public void edit(FamilyView familyView) {
        if (familyView == null) {
            familyView = new FamilyView();
            familyView.setSelectedApplications(new HashSet<String>());
            familyView.adjustValuesForUI();
        }
        updateUI(familyView);
        this.familyView = familyView;
        familyView.adjustValuesForUI();
        binder.setBean(familyView);
    }

    private String getCurrentlyActiveEngines() {
        List<EngineDTO> engines = engineController.getEngines(StandardStatus.ACTIVE);
        Set<String> names = engines.stream().map(a -> a.getName()).collect(Collectors.toSet());
        return StringUtils.deleteWhitespace(StringUtils.join(names, ","));
    }

    protected String getMessage() {
        return message.getValue();
    }

    protected TextArea getMessageUI() {
        return message;
    }

    public void init() {
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> save());
        activate.addClickListener(e -> activate());
        delete.addClickListener(e -> delete());
        description.setWidth(300, Unit.PIXELS);
        id.setWidth(120, Unit.PIXELS);
        cccMisCode.setWidth(80, Unit.PIXELS);
        status.setWidth(120, Unit.PIXELS);
        status.setEnabled(false);
        status.setItems(VALID_STATUSES);
        familyView = new FamilyView();
        binder.setBean(familyView);
        addComponents(mainLayout);
    }

    public void save() {
        updateObject(familyView);
        try {
            if (StringUtils.isNotBlank(familyView.getFamilyCode())) {
                familyView = controller.addFamily(familyView);
                updateUI(familyView);
                binder.setBean(familyView);
                setMessage("The Family is now saved.");
            } else {
                this.setMessage("Family requires a code to be saved.");
            }
        }
        catch (Exception exception) {
            setMessage("saved", exception);
        }
    }

    protected void setMessage(String value) {
        message.setValue(value);
    }

    private void setMessage(String action, Exception exception) {
        setMessage(String.format("Family could not be %s for the following reasons: %s \n Stack Trace: \n%s", action,
                        exception.getMessage(), ExceptionUtils.getFullException(exception)));
    }

    private void updateObject(FamilyView familyView) {
        familyView.setFamilyCode(cccMisCode.getValue());
        familyView.setDescription(description.getValue());

        familyView.setSelectedApplications(convertToModel(selectedEngines.getValue()));
        familyView.setStatus(status.getValue());
    }

    private void updateUI(FamilyView familyView) {
        cccMisCode.setValue(familyView.getFamilyCode());
        description.setValue(familyView.getDescription());
        selectedEngines.setValue(convertToPresentation(familyView.getSelectedApplications()));
        status.setValue(familyView.getStatus());
    }
}
