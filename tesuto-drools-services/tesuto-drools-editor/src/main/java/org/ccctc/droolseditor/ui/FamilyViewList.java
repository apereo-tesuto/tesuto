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

import org.ccctc.common.droolscommon.model.StandardStatus;
import org.ccctc.droolseditor.controllers.FamilyViewController;
import org.ccctc.droolseditor.views.FamilyView;
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
public class FamilyViewList extends CssLayout {

    private static final long serialVersionUID = 1L;

    private FamilyViewController controller;

    final Grid<FamilyView> grid;
    
    final ComboBox<String> status = new ComboBox<>("Status");
    final Button searchBtn = new Button("Search", FontAwesome.SEARCH);

    @Autowired
    Mapper mapper;

    @Autowired
    public FamilyViewList(FamilyViewController controller) {
        this.controller = controller;
        grid = new Grid<>(FamilyView.class);
    }

    public void init(FamilyViewEditor collegeEditor) {
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.asSingleSelect().addValueChangeListener(e -> {
            collegeEditor.edit(e.getValue());
        });
        grid.setHeight(300, Unit.PIXELS);
        grid.setWidth(800, Unit.PIXELS);
        grid.setColumns("cccMisCode","description","status");
        searchBtn.addClickListener(e -> updateList());
        searchBtn.focus();
        status.setItems(StandardStatus.VALID_STATUSES);
        HorizontalLayout actions = new HorizontalLayout( );
        actions.addComponent(status);
        actions.addComponent(searchBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid);
        addComponent(mainLayout);
    }

    public void updateList() {
        grid.deselectAll();
        grid.setItems(controller.getFamilies(status.getValue()));
    }
}


