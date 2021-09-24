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
package org.cccnext.tesuto.placement.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.cccnext.tesuto.placement.view.MmapDataSourceType;

@Entity
@DiscriminatorValue("mmap")
public class MmapPlacementComponent extends PlacementComponent {
    private static final long serialVersionUID = 1L;

    @Column(name = "variable_set_id")
    private String mmapVariableSetId;

    @Column(name = "rule_id")
    private String mmapRuleId;

    @Column(name = "rule_set_id")
    private String mmapRuleSetId;

    @Column(name = "rule_set_title")
    private String mmapRuleSetTitle;

    @Column(name = "rule_set_row_id")
    private String mmapRuleSetRowId;

    @Column(name = "row_number")
    private Integer mmapRowNumber;

    @Column(name = "course_categories")
    @Convert(converter = StringListConverter.class)
    private List<String> mmapCourseCategories;


    @Column(name="data_source")
    private String dataSource;

    @Column(name="data_source_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSourceDate;

    @Column(name="data_source_type")
    @Enumerated(EnumType.STRING)
    private MmapDataSourceType dataSourceType;

    @Column(name = "stand_alone_placement")
    private Boolean standalonePlacement;

    public String getMmapVariableSetId() {
        return mmapVariableSetId;
    }

    public void setMmapVariableSetId(String mmapVariableSetId) {
        this.mmapVariableSetId = mmapVariableSetId;
    }

    public String getMmapRuleSetId() {
        return mmapRuleSetId;
    }

    public void setMmapRuleSetId(String mmapRuleSetId) {
        this.mmapRuleSetId = mmapRuleSetId;
    }

    public String getMmapRuleSetRowId() {
        return mmapRuleSetRowId;
    }

    public void setMmapRuleSetRowId(String mmapRuleSetRowId) {
        this.mmapRuleSetRowId = mmapRuleSetRowId;
    }

    public String getMmapRuleId() {
        return mmapRuleId;
    }

    public void setMmapRuleId(String mmapRuleId) {
        this.mmapRuleId = mmapRuleId;
    }

    public String getMmapRuleSetTitle() {
        return mmapRuleSetTitle;
    }

    public void setMmapRuleSetTitle(String mmapRuleSetTitle) {
        this.mmapRuleSetTitle = mmapRuleSetTitle;
    }

    public Integer getMmapRowNumber() {
        return mmapRowNumber;
    }

    public void setMmapRowNumber(Integer mmapRowNumber) {
        this.mmapRowNumber = mmapRowNumber;
    }

    public List<String> getMmapCourseCategories() {
        return mmapCourseCategories;
    }

    public void setMmapCourseCategories(List<String> mmapCourseCategories) {
        this.mmapCourseCategories = mmapCourseCategories;
    }

	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public Date getDataSourceDate() {
		return dataSourceDate;
	}
	public void setDataSourceDate(Date dataSourceDate) {
		this.dataSourceDate = dataSourceDate;
	}
	public MmapDataSourceType getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(MmapDataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

    public Boolean getStandalonePlacement() {
        return standalonePlacement;
    }

    public void setStandalonePlacement(Boolean standalonePlacement) {
        this.standalonePlacement = standalonePlacement;
    }
}
