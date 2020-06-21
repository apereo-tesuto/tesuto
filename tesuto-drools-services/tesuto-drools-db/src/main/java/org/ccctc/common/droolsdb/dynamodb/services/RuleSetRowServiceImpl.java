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
package org.ccctc.common.droolsdb.dynamodb.services;

import static org.ccctc.common.droolscommon.model.RuleStatus.DRAFT;
import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;
import static org.ccctc.common.droolscommon.model.RuleStatus.RETIRED;
import static org.ccctc.common.droolscommon.model.RuleStatus.VALID_STATUSES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolsdb.dynamodb.dao.RuleSetRowDAO;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSetRow;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetRowDTOMapper;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class RuleSetRowServiceImpl implements RuleSetRowService, InitializingBean {
    @Autowired
    private RuleSetRowDAO repository;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleSetRowDTOMapper mapper;

    @Autowired
    private DynamoDBTableService tableService;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableService.createTable(RuleSetRow.TABLE_NAME, RuleSetRow.class);
    }

    @Override
    public RuleSetRowDTO getRuleSetRow(String ruleSetRowId) {
        if (StringUtils.isBlank(ruleSetRowId)) {
            throw new ObjectNotFoundException("Rule ID cannot be blank");
        }
        try {
            RuleSetRow entity = repository.findById(ruleSetRowId).get();
            if (entity == null) {
                throw new ObjectNotFoundException("RuleSetRow[" + ruleSetRowId + "] not found");
            }
            return mapper.mapTo(entity);
        }
        catch (Exception e) {
            throw new ObjectNotFoundException("RuleSetRow[" + ruleSetRowId + "] not found");
        }
    }

    @Override
    public RuleSetRowDTO duplicate(String ruleSetRowId) {
        RuleSetRowDTO dto = getRuleSetRow(ruleSetRowId);
        dto.setId("");
        dto.setStatus(DRAFT);
        dto.setVersion("");
        return dto;
    }

    @Override
    public List<RuleSetRowDTO> getAllRuleSetRows() {
        return mapper.mapTo(repository.findAll());
    }

    @Override
    public List<RuleSetRowDTO> getRuleSetRowsByRuleId(String ruleId) {
        return mapper.mapTo(repository.findByRuleId(ruleId));
    }

    @Override
    public List<RuleSetRowDTO> find(RuleAttributeFacetSearchForm form) {
        List<RuleSetRow> foundRows = new ArrayList<>();
        form.clean();
        if (form.getId() != null) {
            return Arrays.asList(getRuleSetRow(form.getId()));
        }

        if (form.getFamily() != null) {
            foundRows.addAll(repository.findByFamilyAndStatus(form.getFamily()));
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getRuleId() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByRuleIdAndIdIn(form.getRuleId(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByRuleId(form.getRuleId()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getCompetencyMapDiscipline() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDisciplineAndIdIn(form.getCompetencyMapDiscipline(),
                                getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDiscipline(form.getCompetencyMapDiscipline()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getEvent() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByEventAndIdIn(form.getEvent(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByEvent(form.getEvent()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getCategory() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByCategoryAndIdIn(form.getCategory(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByCategory(form.getCategory()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getStatus() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByStatusAndIdIn(form.getStatus(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByStatus(form.getStatus()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (form.getEngine() != null) {
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByEngineAndIdIn(form.getEngine(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByEngine(form.getEngine()));
            }
            if (foundRows.size() == 0) {
                return new ArrayList<>();
            }
        }

        if (foundRows.isEmpty()) {
            return getAllRuleSetRows();
        } else {
            foundRows = repository.findByIdIn(getIds(foundRows));
        }
        return mapper.mapTo(foundRows);
    }

    private List<String> getIds(List<RuleSetRow> foundRows) {
        return foundRows.stream().map(r -> r.getId()).collect(Collectors.toList());
    }

    private List<RuleSetRow> returnAnd(List<RuleSetRow> foundRows, List<RuleSetRow> requiredRows) {
        // this is expecting that if foundRows is already 0 would have been
        // previously returned
        if (foundRows.size() == 0) {
            return requiredRows;
        }
        List<String> andItems = requiredRows.stream().map(RuleSetRow::getId).collect(Collectors.toList());

        return foundRows.stream().filter(e -> andItems.contains(e.getId())).collect(Collectors.toList());
    }

    public String updateVariables(String ruleSetRowId, Map<String, String> variableValues) {
        RuleSetRowDTO ruleSetRowDTO = getRuleSetRow(ruleSetRowId);
        ruleSetRowDTO.setId("");
        for (RuleVariableRowDTO variableRow : ruleSetRowDTO.getVariableRows()) {
            List<RuleVariableDTO> list = new ArrayList<>(variableRow.getVariables());
            for (RuleVariableDTO variable : list) {
                if (variableValues.containsKey(variable.getName())) {
                    variableValues.put(variable.getName(), variable.getValue());
                }
            }
        }
        return save(ruleSetRowDTO).getId();
    }

    @Override
    public RuleSetRowDTO save(RuleSetRowDTO ruleSetRowDTO) {
        checkDatabaseBeforeSaving(ruleSetRowDTO.getId());
        checkDTOBeforeSaving(ruleSetRowDTO);

        RuleSetRow ruleSetRow = mapper.mapFrom(ruleSetRowDTO);
        return mapper.mapTo(repository.save(ruleSetRow));
    }

    @Override
    public void delete(String ruleSetRowId) {
        if (!StringUtils.isBlank(ruleSetRowId)) {
            RuleSetRow ruleSetRow = null;
            try {
                ruleSetRow = repository.findById(ruleSetRowId).get();
            }
            catch (Exception didnotexist) {
                return;
            }
            if (ruleSetRow != null) {
                if (ruleSetRow.getStatus().equals(PUBLISHED)) {
                    ruleSetRow.setStatus(RETIRED);
                    repository.save(ruleSetRow);
                } else if (ruleSetRow.getStatus().equals(DRAFT)) {
                    repository.delete(ruleSetRow);
                }
            }
        }
    }

    /**
     * Checks the database for a RuleDTO that matches the rule ID and throws an
     * exception if the RuleDTO in the database cannot be saved (such as if it
     * is deleted or published)
     * 
     * @param ruleId
     */
    public void checkDatabaseBeforeSaving(String ruleSetRowId) {
        if (StringUtils.isBlank(ruleSetRowId)) {
            return;
        }
        RuleSetRow existingRuleSetRow = null;
        try {
            existingRuleSetRow = repository.findById(ruleSetRowId).get();
        }
        catch (Exception noValuePresent) {
        }
        if (existingRuleSetRow != null) {
            if (RETIRED.equals(existingRuleSetRow.getStatus())) {
                throw new SaveException("Cannot save rulesetrow[" + ruleSetRowId + "] because its status is deleted");
            }
            if (PUBLISHED.equals(existingRuleSetRow.getStatus())) {
                throw new SaveException("Cannot save rulesetrow[" + ruleSetRowId + "] because its status is published");
            }
            // should never be able to get here, since if there is a version
            // status should be published.
            if (!StringUtils.isBlank(existingRuleSetRow.getVersion())) {
                throw new SaveException("Cannot save rulesetrow[" + ruleSetRowId + "] because it has a version["
                                + existingRuleSetRow.getVersion() + "].  Uses duplicate to create a new rulesetrow");
            }
        }
    }

    /**
     * checks the fields within RuleSetRowDTO to see if it has any missing or
     * invalid data before saving to the database
     * 
     * @param ruleSetRowDTO
     */
    public void checkDTOBeforeSaving(RuleSetRowDTO ruleSetRowDTO) {
        if (StringUtils.isBlank(ruleSetRowDTO.getId())) {
            String uuid = UUID.randomUUID().toString();
            ruleSetRowDTO.setId(uuid);
        }
        if (!StringUtils.isBlank(ruleSetRowDTO.getVersion())) {
            ruleSetRowDTO.setStatus(PUBLISHED);
        }
        if (StringUtils.isBlank(ruleSetRowDTO.getStatus())) {
            ruleSetRowDTO.setStatus(DRAFT);
        }
        if (StringUtils.isBlank(ruleSetRowDTO.getEngine())) {
            throw new SaveException("Cannot save rulesetrow[" + ruleSetRowDTO.getId() + "] because application is blank");
        }
        String status = ruleSetRowDTO.getStatus();
        if (!VALID_STATUSES.contains(status)) {
            throw new SaveException("Cannot save rule[" + ruleSetRowDTO.getId() + "] because status[" + status + "] is invalid");
        }

        if (StringUtils.isBlank(ruleSetRowDTO.getRuleId())) {
            return;
        }

        String ruleId = ruleSetRowDTO.getRuleId();
        try {
            RuleDTO ruleDTO = ruleService.getRule(ruleId);
            if (!PUBLISHED.equals(ruleDTO.getStatus())) {
                throw new SaveException("Cannot save rulesetrow[" + ruleSetRowDTO.getId() + "] because rule[" + ruleId
                                + "] is not published");
            }
            if (!ruleDTO.getEngine().equals(ruleSetRowDTO.getEngine())) {
                throw new SaveException("Cannot save rulesetrow[" + ruleSetRowDTO.getId()
                                + "] because rulesetrow application and rule application do not match");
            }
        }
        catch (ObjectNotFoundException ex) {
            throw new SaveException("Cannot save rulesetrow[" + ruleSetRowDTO.getId() + "] because rule id ["
                            + ruleSetRowDTO.getRuleId() + "] can not be found.");
        }
    }

}
