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

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.droolseditor.controllers.EngineController;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class EngineList extends CssLayout {
    private static final long serialVersionUID = 1L;

    private EngineController engineController;

    final Grid<EngineDTO> grid;

    @Autowired
    Mapper mapper;

    final Button searchBtn = new Button("Search", FontAwesome.SEARCH);

    final ComboBox<String> status = new ComboBox<>("Status");

    @Autowired
    public EngineList(EngineController controller) {
        this.engineController = controller;
        grid = new Grid<>(EngineDTO.class);
    }

    public void init(EngineEditor engineEditor) {
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().addValueChangeListener(e -> {
            engineEditor.edit(e.getValue());
        });
        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(800, Unit.PIXELS);
        grid.setColumns("name", "dataSource", "status");
        searchBtn.addClickListener(e -> updateList());
        searchBtn.focus();
        status.setItems(StandardStatus.VALID_STATUSES);
        HorizontalLayout actions = new HorizontalLayout(status, searchBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid);
        addComponent(mainLayout);
    }

    public void updateList() {
        grid.deselectAll();
        grid.setItems(engineController.getEngines(status.getValue()));
    }
}
