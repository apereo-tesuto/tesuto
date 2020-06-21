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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.droolseditor.controllers.EngineController;
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
public class EngineEditor extends CssLayout {
    private static final long serialVersionUID = 1L;

    public static final List<String> VALID_DATA_SOURCES = Arrays.asList("maven", "editor");

    private Button activate = new Button("Activate");

    private EngineDTO engineDTO;

    private TextField artifactId = new TextField("Artifact Id");

    private Binder<EngineDTO> binder = new Binder<>(EngineDTO.class);

    private TextArea categories = new TextArea("Categories (,)");

    private EngineController engineController;

    private ComboBox<String> dataSource = new ComboBox<String>("Data Source");

    private Button delete = new Button("Delete", FontAwesome.TRASH_O);

    private TextArea events = new TextArea("Events (,)");

    private TextField groupId = new TextField("Group Id");

    private TextArea message = new TextArea("Message");

    private TextField name = new TextField("Name");

    private Button save = new Button("Save", FontAwesome.SAVE);

    private ComboBox<String> status = new ComboBox<>("Status");

    private TextField version = new TextField("Version ID");

    private CssLayout actions = new CssLayout(save, activate, delete);

    private HorizontalLayout engineInfo = new HorizontalLayout(name, status);

    private HorizontalLayout dataInfo = new HorizontalLayout(dataSource, events, categories);

    private HorizontalLayout repositoryInfo = new HorizontalLayout(groupId, artifactId, version);

    private VerticalLayout mainLayout = new VerticalLayout(engineInfo, dataInfo, repositoryInfo, actions);

    @Autowired
    public EngineEditor(EngineController controller) {
        this.engineController = controller;
        binder.forField(events).withConverter(new StringToSetConverter()).bind(EngineDTO::getEvents, EngineDTO::setEvents);
        binder.forField(categories).withConverter(new StringToSetConverter()).bind(EngineDTO::getCategories, EngineDTO::setCategories);
        binder.bindInstanceFields(this);
    }

    public void activate() {
        updateObject(engineDTO);
        try {
            if (StringUtils.isNotBlank(engineDTO.getName())) {
                engineDTO.setStatus(StandardStatus.ACTIVE);
                engineDTO = engineController.addEngine(engineDTO);
                engineDTO.adjustValuesForUI();
                updateUI(engineDTO);
                binder.setBean(engineDTO);
                setMessage("Application is now active and can be used to define rules, rulesetrows and rulesets.");
            } else {
                this.setMessage("Application requires a name to be activated.");
            }
        }
        catch (Exception exception) {
            setMessage("actvated", exception);
        }
    }

    public void delete() {
        updateObject(engineDTO);
        try {
            if (StringUtils.isNotBlank(engineDTO.getName())) {
                engineDTO = engineController.deleteEngine(engineDTO.getName());
                engineDTO.adjustValuesForUI();
                updateUI(engineDTO);
                binder.setBean(engineDTO);
                setMessage("Application is now inactive or deleted.");
            } else {
                this.setMessage("Application requires a name to be deleted.");
            }
        }
        catch (Exception exception) {
            setMessage("deleted", exception);
        }
    }

    public void edit(EngineDTO engineDTO) {
        if (engineDTO == null) {
            engineDTO = new EngineDTO();
            engineDTO.adjustValuesForUI();
        }
        engineDTO.adjustValuesForUI();
        this.engineDTO = engineDTO;
        binder.setBean(engineDTO);
        updateUI(engineDTO);
        this.markAsDirtyRecursive();
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
        delete.addClickListener(e -> delete());
        activate.addClickListener(e -> activate());
        version.setWidth(80, Unit.PIXELS);
        status.setWidth(120, Unit.PIXELS);
        events.setWidth(300, Unit.PIXELS);
        events.setDescription("Use comma delimited values.");
        categories.setWidth(300, Unit.PIXELS);
        categories.setDescription("Use comma delimited values");
        status.setEnabled(false);
        status.setItems(VALID_STATUSES);
        dataSource.setItems(VALID_DATA_SOURCES);
        engineDTO = new EngineDTO();
        binder.setBean(engineDTO);
        updateObject(engineDTO);
        addComponents(mainLayout);
    }

    public void save() {
        updateObject(engineDTO);
        try {
            if (StringUtils.isNotBlank(engineDTO.getName())) {
                engineDTO = engineController.addEngine(engineDTO);
                engineDTO.adjustValuesForUI();
                updateUI(engineDTO);
                binder.setBean(engineDTO);
                setMessage("Save completed successfully");
            } else {
                this.setMessage("Application requires a name to be saved.");
            }
        }
        catch (Exception exception) {
            setMessage("save", exception);
        }
    }

    protected void setMessage(String value) {
        message.setValue(value);
    }

    private void setMessage(String action, Exception exception) {
        setMessage(String.format("College could not be %s for the following reasons: %s \n Stack Trace: \n%s", action,
                        exception.getMessage(), ExceptionUtils.getFullException(exception)));
    }

    private void updateObject(EngineDTO engineDTO) {
        engineDTO.setName(name.getValue());
        engineDTO.setDataSource(dataSource.getValue());
        engineDTO.setArtifactId(artifactId.getValue());
        engineDTO.setGroupId(groupId.getValue());
        engineDTO.setStatus(status.getValue());
        engineDTO.setVersion(version.getValue());
    }

    private void updateUI(EngineDTO engineDTO) {
        name.setValue(engineDTO.getName());
        dataSource.setValue(engineDTO.getDataSource());
        artifactId.setValue(engineDTO.getArtifactId());
        groupId.setValue(engineDTO.getGroupId());
        status.setValue(engineDTO.getStatus());
        version.setValue(engineDTO.getVersion());
    }

}
