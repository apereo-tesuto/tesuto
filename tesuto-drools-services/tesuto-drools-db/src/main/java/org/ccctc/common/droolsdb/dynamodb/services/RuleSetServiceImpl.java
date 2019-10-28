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
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.dynamodb.dao.RuleSetDAO;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSet;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleSetDTOMapper;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.common.droolsdb.services.RuleSetService;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class RuleSetServiceImpl implements RuleSetService, InitializingBean {

    @Autowired
    private RuleSetDAO repository;

    @Autowired
    private RuleSetRowService ruleSetRowService;

    @Autowired
    private RuleSetDTOMapper mapper;

    @Autowired
    private DynamoDBTableService tableService;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableService.createTable(RuleSet.TABLE_NAME, RuleSet.class);
    }

    @Override
    public RuleSetDTO getRuleSet(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            throw new ObjectNotFoundException("RuleSet ID cannot be blank");
        }
        try {
            RuleSet ruleSet = repository.findById(ruleSetId).get();
            if (ruleSet == null) {
                throw new ObjectNotFoundException("RuleSet[" + ruleSetId + "] not found");
            }
            RuleSetDTO ruleSetDTO = mapper.mapTo(ruleSet);
            return ruleSetDTO;
        }
        catch (Exception e) {
            throw new ObjectNotFoundException("RuleSet[" + ruleSetId + "] not found");
        }
    }

    @Override
    public List<RuleSetDTO> getAllRuleSets() {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findAll();
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByEngine(String application) {
        List<RuleSet> rules = (List<RuleSet>) repository.findByEngine(application);
        List<RuleSetDTO> ruleDTOs = mapper.mapTo(rules);
        return ruleDTOs;
    }

    @Override
    public List<RuleSetDTO> getRulesSetByEngineAndStatus(String application, String status) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByEngineAndStatus(application, status);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByStatus(String status) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByStatus(status);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByEngineAndStatusAndFamily(String application, String status, String cccMisCode) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByEngineAndStatusAndFamily(application, status,
                        cccMisCode);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByParameters(String application, String status, String cccMisCode) {
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        form.setEngine(application);
        form.setStatus(status);
        form.setFamily(cccMisCode);
        return find(form);
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByEngineAndFamily(String application, String cccMisCode) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByEngineAndFamily(application, cccMisCode);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByStatusAndFamily(String status, String cccMisCode) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByStatusAndFamily(status, cccMisCode);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> getRuleSetsByFamily(String cccMisCode) {
        List<RuleSet> ruleSets = (List<RuleSet>) repository.findByFamily(cccMisCode);
        List<RuleSetDTO> ruleSetDTOs = mapper.mapTo(ruleSets);
        return ruleSetDTOs;
    }

    @Override
    public List<RuleSetDTO> find(RuleAttributeFacetSearchForm form) {
        List<RuleSet> foundRows = new ArrayList<>();

        log.info("Entered RuleAttributeFacetSearchForm\n");
        form.clean();
        if (form.getId() != null) {
            log.info(String.format("Find by id : %s \n", form.getId()));
            RuleSetDTO ruleSet = getRuleSet(form.getId());
            if (ruleSet != null) {
                log.info(String.format("Found by id : %s \n", ruleSet.getId()));
            }
            return Arrays.asList(ruleSet);
        }

        if (form.getFamily() != null) {
            log.info(String.format("Find by Family : %s \n", form.getFamily()));
            foundRows.addAll(repository.findByFamily(form.getFamily()));
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by Family.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by Family : %d \n", foundRows.size()));
        }

        if (form.getCompetencyMapDiscipline() != null) {
            log.info(String.format("Find by CompetencyMapDiscipline : %s \n", form.getCompetencyMapDiscipline()));
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDisciplineAndIdIn(form.getCompetencyMapDiscipline(),
                                getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByCompetencyMapDiscipline(form.getCompetencyMapDiscipline()));
            }
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by CompetencyMapDiscipline.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by CompetencyMapDiscipline : %d \n", foundRows.size()));
        }

        if (form.getEvent() != null) {
            log.info(String.format("Find by Event : %s \n", form.getEvent()));

            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByEventAndIdIn(form.getEvent(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByEvent(form.getEvent()));
            }
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by Event.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by Event : %d \n", foundRows.size()));
        }

        if (form.getCategory() != null) {
            log.info(String.format("Find by Category : %s \n", form.getEvent()));
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByCategoryAndIdIn(form.getCategory(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByCategory(form.getCategory()));
            }
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by Category.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by Category : %d \n", foundRows.size()));
        }

        if (form.getStatus() != null) {
            log.info(String.format("Find by Status : %s \n", form.getEvent()));
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByStatusAndIdIn(form.getStatus(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByStatus(form.getStatus()));
            }
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by Status.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by Status : %d \n", foundRows.size()));
        }

        if (form.getEngine() != null) {
            log.info(String.format("Find by Engine : %s \n", form.getEvent()));
            if (foundRows.size() > 0) {
                foundRows = returnAnd(foundRows, repository.findByEngineAndIdIn(form.getEngine(), getIds(foundRows)));
            } else {
                foundRows = returnAnd(foundRows, repository.findByEngine(form.getEngine()));
            }
            if (foundRows.size() == 0) {
                log.info(String.format("None Found by Engine.\n"));
                return new ArrayList<>();
            }
            log.info(String.format("Found by Engine : %d \n", foundRows.size()));
        }

        if (foundRows.isEmpty()) {
            return getAllRuleSets();
        } else {
            foundRows = repository.findByIdIn(getIds(foundRows));
        }
        return mapper.mapTo(foundRows);
    }

    private List<String> getIds(List<RuleSet> foundRows) {
        return foundRows.stream().map(r -> r.getId()).collect(Collectors.toList());
    }

    private List<RuleSet> returnAnd(List<RuleSet> foundRows, List<RuleSet> requiredRows) {
        // this is expecting that if foundRows is already 0 would have been
        // previously returned
        if (foundRows.size() == 0) {
            return requiredRows;
        }
        List<String> andItems = requiredRows.stream().map(RuleSet::getId).collect(Collectors.toList());

        return foundRows.stream().filter(e -> andItems.contains(e.getId())).collect(Collectors.toList());
    }

    @Override
    public RuleSetDTO save(RuleSetDTO ruleSetDTO) {
        checkDatabaseBeforeSaving(ruleSetDTO.getId());
        checkDTOBeforeSaving(ruleSetDTO);

        RuleSet ruleSet = mapper.mapFrom(ruleSetDTO);
        repository.save(ruleSet);
        return ruleSetDTO;
    }

    @Override
    public RuleSetDTO duplicate(String ruleSetId) {
        RuleSetDTO ruleSetDTO = this.getRuleSet(ruleSetId);
        ruleSetDTO.setId("");
        ruleSetDTO.setStatus(DRAFT);
        ruleSetDTO.setVersion("");
        RuleSetDTO updatedRuleSetDTO = this.save(ruleSetDTO);
        return updatedRuleSetDTO;
    }

    @Override
    public void delete(String ruleSetId) {
        if (!StringUtils.isBlank(ruleSetId)) {
            RuleSet ruleSet = null;
            try {
                ruleSet = repository.findById(ruleSetId).get();
            }
            catch (Exception didnotexist) {
                return;
            }
            if (ruleSet != null) {
                if (!ruleSet.getStatus().equals(DRAFT)) {
                    RuleSetDTO ruleSetDTO = mapper.mapTo(ruleSet);
                    ruleSetDTO.setStatus(RETIRED);
                    repository.save(mapper.mapFrom(ruleSetDTO));
                } else {
                    repository.deleteById(ruleSetId);
                }
            }
        }
    }

    /**
     * Checks the database for a RuleSetDTO that matches the ruleSet ID and
     * throws an exception if the RuleSetDTO in the database cannot be saved
     * (such as if it is deleted or published)
     * 
     * @param
     */
    public void checkDatabaseBeforeSaving(String ruleSetId) {
        if (StringUtils.isBlank(ruleSetId)) {
            return;
        }
        RuleSet existingRuleSet = null;
        try {
            existingRuleSet = repository.findById(ruleSetId).get();
        }
        catch (Exception noValuePresent) {
        }
        if (existingRuleSet != null) {
            if (RETIRED.equals(existingRuleSet.getStatus())) {
                throw new SaveException("Cannot save ruleset[" + ruleSetId + "] because its status is deleted");
            }
            if (PUBLISHED.equals(existingRuleSet.getStatus())) {
                throw new SaveException("Cannot save ruleset[" + ruleSetId + "] because its status is published");
            }
            // should never be able to get here, since if there is a version
            // status should be published.
            if (!StringUtils.isBlank(existingRuleSet.getVersion())) {
                throw new SaveException("Cannot save ruleset[" + ruleSetId + "] because it has a version["
                                + existingRuleSet.getVersion() + "].  Uses duplicate to create a new rule");
            }
        }

    }

    /**
     * checks the fields within RuleSetDTO to see if it has any missing or
     * invalid data before saving to the database
     * 
     * @param ruleSetDTO
     */
    public void checkDTOBeforeSaving(RuleSetDTO ruleSetDTO) {
        if (StringUtils.isBlank(ruleSetDTO.getId())) {
            String uuid = UUID.randomUUID().toString();
            ruleSetDTO.setId(uuid);
        }
        if (!StringUtils.isBlank(ruleSetDTO.getVersion())) {
            ruleSetDTO.setStatus(PUBLISHED);
        }
        if (StringUtils.isBlank(ruleSetDTO.getStatus())) {
            ruleSetDTO.setStatus(DRAFT);
        }
        if (StringUtils.isBlank(ruleSetDTO.getEngine())) {
            throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId() + "] because application is blank");
        }
        if (StringUtils.isBlank(ruleSetDTO.getFamily())) {
            throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId() + "] because cccMisCode is blank");
        }
        String status = ruleSetDTO.getStatus();
        if (!VALID_STATUSES.contains(status)) {
            throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId() + "] because status[" + status + "] is invalid");
        }
        if (PUBLISHED.equalsIgnoreCase(ruleSetDTO.getStatus()) && ruleSetDTO.getRuleSetRowIds().size() < 1
                        && StringUtils.isBlank(ruleSetDTO.getRuleSetDrl())) {
            throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId()
                            + "] because a published ruleset must have associated rules or a valid ruleset DRL");
        }
        if (CollectionUtils.isEmpty(ruleSetDTO.getRuleSetRowIds())) {
            return;
        }
        for (String ruleSetRowId : ruleSetDTO.getRuleSetRowIds()) {
            RuleSetRowDTO ruleSetRowDTO = ruleSetRowService.getRuleSetRow(ruleSetRowId);

            if (!PUBLISHED.equals(ruleSetRowDTO.getStatus())) {
                throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId() + "] because rulesetrow[" + ruleSetRowId
                                + "] is not published");
            }
            if (!ruleSetRowDTO.getEngine().equals(ruleSetDTO.getEngine())) {
                throw new SaveException("Cannot save ruleset[" + ruleSetDTO.getId()
                                + "] because ruleset application and rulesetrow application do not match");
            }
        }
    }

    @Override
    public void publish(String ruleSetId, Boolean publish) {
        if (StringUtils.isBlank(ruleSetId)) {
            return;
        }

        RuleSet ruleSet = repository.findById(ruleSetId).get();
        if (publish) {
            if (!ruleSet.getVersion().equals(PUBLISHED))
                ruleSet.setVersion("1");
            ruleSet.setStatus(PUBLISHED);
            save(mapper.mapTo(ruleSet));
        } else if (ruleSet.getVersion().equals(PUBLISHED) && !ruleSet.getVersion().equals(DRAFT)) {
            delete(ruleSetId);
        }
    }
}
