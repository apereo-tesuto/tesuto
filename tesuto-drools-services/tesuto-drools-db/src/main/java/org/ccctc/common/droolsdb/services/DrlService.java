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
package org.ccctc.common.droolsdb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolsdb.dynamodb.utils.RuleUtils;
import org.ccctc.common.droolsdb.model.ActionParameterDTO;
import org.ccctc.common.droolsdb.model.ActionRowDTO;
import org.ccctc.common.droolsdb.model.ConditionRowDTO;
import org.ccctc.common.droolsdb.model.RuleDTO;


import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DrlService {
    @Autowired
    private RuleSetService ruleSetService;

    @Autowired
    private RuleService ruleService;

    private static int MAX_RETRY_COUNT = 3;
    
    private static int RETRY_SLEEP_IN_MILLIS = 3000;

    @Autowired
    private RuleSetRowService rulesetRowService;

    public StringBuffer generateDrl(String ruleSetId) {
        RuleSetDTO ruleSetDTO = getRuleSet(ruleSetId);
        return generateDrl(ruleSetDTO);
    }

    public List<String> generateDrls(String ruleSetId) {
        RuleSetDTO ruleSetDTO = getRuleSet(ruleSetId);
        List<String> drls = new ArrayList<>();

        for (String ruleSetRowId : ruleSetDTO.getRuleSetRowIds()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(buildPackage());
            RuleSetRowDTO ruleSetRow = rulesetRowService.getRuleSetRow(ruleSetRowId);
            buffer.append(generateDRL(ruleSetDTO.getId(), ruleSetRow));
            drls.add(replaceStandardTokens(buffer.toString(), ruleSetId, ruleSetRow));
        }
        if (StringUtils.isNotBlank(ruleSetDTO.getRuleSetDrl())) {
            drls.add(ruleSetDTO.getRuleSetDrl().replaceAll("\\$\\{rule_set_id\\}", ruleSetId));
        }
        return drls;
    }

    public String replaceStandardTokens(String drl, String ruleSetId, RuleSetRowDTO ruleSetRow) {
        return drl.replaceAll("\\$\\{rule_set_id\\}", ruleSetId)
                .replaceAll("\\$\\{rule_set_row_id\\}", ruleSetRow.getId())
                .replaceAll("\\$\\{rule_id\\}", ruleSetRow.getRuleId());
    }

    public StringBuffer generateDrl(RuleSetDTO ruleSetDTO) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(buildPackage());
        for (String ruleSetRowId : ruleSetDTO.getRuleSetRowIds()) {
            buffer.append(generateDRL(ruleSetDTO.getId(), rulesetRowService.getRuleSetRow(ruleSetRowId)));
        }
        return buffer;
    }

    public StringBuffer generateDRL(String ruleSetId, RuleSetRowDTO ruleSetRowDTO) {
        StringBuffer buffer = new StringBuffer();
        RuleDTO ruleDTO = ruleService.getRule(ruleSetRowDTO.getRuleId());
        buffer.append(buildHeader(ruleDTO));
        List<RuleVariableRowDTO> variableRows = ruleSetRowDTO.getVariableRows();
        StringBuffer rule = generateRule(ruleSetId, ruleSetRowDTO.getId(), ruleDTO, variableRows);
        buffer.append(rule);
        if (StringUtils.isNotBlank(ruleDTO.getCustomFunctions())) {
            buffer.append("\n").append(ruleDTO.getCustomFunctions());
        }
        return buffer;
    }

    public StringBuffer generateDrl(RuleDTO ruleDTO, List<RuleVariableRowDTO> variableRows) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(buildHeader(ruleDTO));
        buffer.append(generateRule(null, null, ruleDTO, variableRows));
        return buffer;
    }

    public StringBuffer generateRule(String ruleSetId, String ruleSetRowId, RuleDTO ruleDTO,
            List<RuleVariableRowDTO> variableRows) {
        StringBuffer buffer = new StringBuffer();
        Map<String, String> variableTypes = ruleDTO.getVariableTypes();

        if (variableRows != null && variableRows.size() > 0) {
            Integer row = 0;
            for (RuleVariableRowDTO variableRow : variableRows) {

                StringBuffer ruleBuffer = new StringBuffer();

                ruleBuffer.append(
                        buildTitle(ruleDTO, variableRow.getName(), getRowName(variableRow.getVariables()), ruleSetId,
                                ruleSetRowId, ++row)).append("\"\n");

                ruleBuffer.append(buildWhen(ruleDTO, getSetOfVariableNamesForRow(variableRow.getVariables())));
                ruleBuffer.append(buildThen(ruleDTO));
                if (StringUtils.isNotBlank(ruleDTO.getId())) {
                    variableRow.getVariables().add(new RuleVariableDTO("rule_id", ruleDTO.getId()));
                }
                if (StringUtils.isNotBlank(ruleSetRowId)) {
                    variableRow.getVariables().add(new RuleVariableDTO("rule_set_row_id", ruleSetRowId));
                }
                if (StringUtils.isNotBlank(ruleSetId)) {
                    variableRow.getVariables().add(new RuleVariableDTO("rule_set_id", ruleSetId));
                }

                variableRow.getVariables().add(new RuleVariableDTO("row_number", row.toString()));
                StringBuffer updatedRuleBuffer = substitute(ruleBuffer, ruleDTO.getTitle(), variableTypes, variableRow);
                buffer.append(updatedRuleBuffer);
            }
        } else {
            buffer.append(buildTitle(ruleDTO)).append("\"\n");
            buffer.append(buildWhen(ruleDTO, null));
            buffer.append(buildThen(ruleDTO));
        }
        return buffer;
    }

    private String getRowName(List<RuleVariableDTO> variables) {
        for (RuleVariableDTO ruleVariable : variables) {
            if (ruleVariable.getName().equals("rule_name")) {
                return ruleVariable.getValue();
            }
        }
        return "";
    }

    private List<String> getSetOfVariableNamesForRow(List<RuleVariableDTO> variables) {
        return variables.stream().map(s -> s.getName()).collect(Collectors.toList());
    }

    public StringBuffer buildPackage() {
        StringBuffer buffer = new StringBuffer();        
//        buffer.append("package net.ccctechcenter.drools\n");
        buffer.append("package org.ccctc.common\n");
        buffer.append("\n");
        return buffer;
    }

    public StringBuffer buildHeader(RuleDTO ruleDTO) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("import java.util.Map\n");
        buffer.append("import java.util.List\n");
        buffer.append("import java.util.ArrayList\n");
        buffer.append("import org.ccctc.common.droolscommon.RulesAction\n");
        buffer.append("\n");
        for (String customImport : ruleDTO.getCustomImports()) {
            buffer.append("import " + customImport + "\n");
        }
        buffer.append("\n");
        return buffer;
    }

    public StringBuffer buildTitle(RuleDTO ruleDTO) {
        return new StringBuffer("rule \"").append(ruleDTO.getTitle() == null ? "" : ruleDTO.getTitle()).append(":")
                .append(ruleDTO.getId() == null ? "" : ruleDTO.getId());
    }

    public StringBuffer buildTitle(RuleDTO ruleDTO, String name) {
        return buildTitle(ruleDTO).append((StringUtils.isBlank(name) ? "" : ":" + name));
    }

    public StringBuffer buildTitle(RuleDTO ruleDTO, String name, String rowName, String ruleSetId, String ruleSetRowId) {
        return buildTitle(ruleDTO, name).append((StringUtils.isBlank(ruleSetRowId) ? "" : ":rowname-" + rowName))
                .append((StringUtils.isBlank(ruleSetRowId) ? "" : ":rulesetrowid-" + ruleSetRowId))
                .append((StringUtils.isBlank(ruleSetRowId) ? "" : ":rulesetid-" + ruleSetId));
    }

    public StringBuffer buildTitle(RuleDTO ruleDTO, String name, String rowName, String ruleSetId, String ruleSetRowId,
            Integer row) {
        return buildTitle(ruleDTO, name, rowName, ruleSetId, ruleSetRowId).append(row == null ? "" : ":" + row);
    }

    public StringBuffer buildWhen(RuleDTO ruleDTO, List<String> tokensWithValue) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("when\n");
        buffer.append(parseFreetext(ruleDTO.getConditionFreetext()));

        for (ConditionRowDTO ruleRowDTO : ruleDTO.getConditionRows()) {
            if (!CollectionUtils.isEmpty(ruleRowDTO.getTokensFromFreeText()) && tokensWithValue != null) {
                if (ruleRowDTO.getTokensFromFreeText().stream().anyMatch(t -> !tokensWithValue.contains(t))) {
                    continue;
                }
            }
            buffer.append("\t").append(buildConditionRow(ruleRowDTO)).append("\n");
        }
        log.debug("whenBuffer:[" + buffer.toString() + "]");
        return buffer;
    }

    public StringBuffer buildConditionRow(ConditionRowDTO conditionRowDTO) {
        StringBuffer buffer = new StringBuffer();
        if (!StringUtils.isBlank(conditionRowDTO.getPublishedName())) {
            buffer.append(conditionRowDTO.getPublishedName() + " : ");
        }
        if (!StringUtils.isBlank(conditionRowDTO.getFreetext())) {
            buffer.append(conditionRowDTO.getFreetext());
        } else {
            buffer.append(conditionRowDTO.getPublishedObject());
            if (!StringUtils.isBlank(conditionRowDTO.getPublishedObjectField())) {
                buffer.append("." + conditionRowDTO.getPublishedObjectField());
            }
            if (!StringUtils.isBlank(conditionRowDTO.getCondition())) {
                buffer.append(" " + conditionRowDTO.getCondition());
                buffer.append(" " + conditionRowDTO.getValue1());
            }
        }
        log.debug("conditionRowBuffer:[" + buffer.toString() + "]");
        return buffer;
    }

    public StringBuffer buildThen(RuleDTO ruleDTO) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("then\n");
        buffer.append(parseFreetext(ruleDTO.getActionFreetext()));

        // add actions
        for (ActionRowDTO actionRowDTO : ruleDTO.getActionRows()) {
            buffer.append(buildActionRow(actionRowDTO));
        }
        buffer.append("end\n");
        log.debug("thenBuffer:[" + buffer.toString() + "]");
        return buffer;
    }

    public StringBuffer buildActionRow(ActionRowDTO actionRowDTO) {
        StringBuffer buffer = new StringBuffer();
        String action = actionRowDTO.getActionType().toLowerCase() + "Action";
        buffer.append("\tRulesAction " + action + " = new RulesAction(\"" + actionRowDTO.getActionType() + "\");\n");
        for (ActionParameterDTO parameter : actionRowDTO.getParameters()) {
            buffer.append("\t" + action + ".addActionParameter(\"" + parameter.getName() + "\"," + parameter.getValue()
                    + ");\n");
        }
        buffer.append("\tinsert(" + action + ");\n");
        return buffer;
    }

    public StringBuffer parseFreetext(String freetext) {
        StringBuffer buffer = new StringBuffer();
        if (!StringUtils.isBlank(freetext)) {
            freetext = freetext.replace("\\n", "\n");
            String[] lines = freetext.split("\\n");
            for (String line : lines) {
                buffer.append("\t" + line + "\n");
            }
        }
        return buffer;
    }

    public StringBuffer substitute(StringBuffer originalBuffer, String ruleName, Map<String, String> variableTypes,
            RuleVariableRowDTO variableRow) {
        StringBuffer buffer = new StringBuffer();
        String updatedRule = RuleUtils.generateRuleFromVariables(originalBuffer.toString(), variableTypes, variableRow);
        buffer.append(updatedRule);
        return buffer;
    }
    
    
    public void sleep(int sleepDuration) {
        log.debug("Sleeping for [" + sleepDuration + "] milliseconds");
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
        log.debug("finished sleeping");        
    }
    
    private RuleSetDTO getRuleSet(String ruleSetId) {
        boolean success = false;
        int retryCount = 0;
        RuleSetDTO ruleSetDTO = null;
        while (!success && retryCount++ < MAX_RETRY_COUNT) {
            try{
                ruleSetDTO = ruleSetService.getRuleSet(ruleSetId);
                success = true;
            } catch(ProvisionedThroughputExceededException exception)  {
                sleep(RETRY_SLEEP_IN_MILLIS);
            }
        }
        if(ruleSetDTO == null && retryCount > 0) {
            throw new ObjectNotFoundException("Looks like a ProvisionedThroughputExceededException but unable to find ruleset with id: " + ruleSetId);
        }
        return ruleSetDTO;
    }
}
