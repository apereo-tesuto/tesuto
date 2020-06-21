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
package org.cccnext.tesuto.rules.validation;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.placement.view.AssignedPlacementRulesSourceDto;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.ccctc.common.droolscommon.RulesAction;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class AssignedPlacementFactsParser {

    public enum PlacementValuePosition {
        ID,
        CB21CODE,
        CB21LEVEL,
        CREATED_ON,
    }

    public Pair<List<Map<String, Object>>, List<Object>> getTestFactsExpectedResults(String csvFacts) {
        if (StringUtils.isBlank(csvFacts)) {
            throw new RuntimeException("Validation CSV string was empty, required to perform required validation");
        }
        Reader in = new StringReader(csvFacts);
        List<Map<String, Object>> factsSet = new ArrayList<>();
        List<Object> placements = new ArrayList<>();
        try {
            CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
            Iterator<CSVRecord> rows = parser.getRecords().iterator();
            CSVRecord columnType = rows.next();
            CSVRecord headersRecord = rows.next();
            while (rows.hasNext()) {
                Map<String, Object> facts = initFacts();
                placements.add(addFactsMap(facts, columnType.iterator(), headersRecord.iterator(), rows.next()));
                factsSet.add(facts);
            }
            parser.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("Unable to parse facts for testing", exception);
        }

        if(factsSet.isEmpty() && placements.isEmpty()) {
            throw new RuntimeException("No fact sets and no placements results were parsed. ");
        }

        if(factsSet.size() != placements.size()) {
            throw new RuntimeException(String.format("Number of fact sets and number placements results were different so validation will not be true.  "
                    + "Number of Factsets: %d vs Number of Placements: %d", factsSet.size(), placements.size()));
        }
        return Pair.of(factsSet, placements);
    }

    private PlacementViewDto addFactsMap(Map<String, Object> facts, Iterator<String> columnType, Iterator<String> headers, CSVRecord csvFacts) {
        AssignedPlacementRulesSourceDto assignedPlacementRulesSourceDto = new AssignedPlacementRulesSourceDto();
        PlacementViewDto assignedPlacement = null;
        Iterator<String> values = csvFacts.iterator();
        while (values.hasNext()) {
            if (columnType.next().equals("fact")) {
                PlacementViewDto placement;
                switch (headers.next()) {
                    case "ruleSetId":
                        facts.put("RULE_SET_ID", values.next());
                        break;
                    case "placement":
                        placement = generatePlacement(values.next());
                        if (placement != null) {
                            assignedPlacementRulesSourceDto.getPlacements().add(placement);
                        }
                    default:
                        break;
                }
            } else {
                assignedPlacement = generateAssignedPlacement(headers, values);
            }
        }
        facts.put("PLACEMENTS", assignedPlacementRulesSourceDto);
        return assignedPlacement;
    }

    private PlacementViewDto generateAssignedPlacement(Iterator<String> headers, Iterator<String> values) {
        PlacementViewDto placement = new PlacementViewDto();
        while (values.hasNext()) {
            String value = values.next();
            if (StringUtils.isEmpty(value)) { return null; }
            CB21ViewDto cb21ViewDto;
            switch (headers.next()) {
                case AssignedPlacementHeaderNames.ID:
                    placement.setId(value);
                    break;
                case AssignedPlacementHeaderNames.CB21_CODE:
                    if (placement.getCb21() != null) {
                        cb21ViewDto = placement.getCb21();
                    } else {
                        cb21ViewDto = new CB21ViewDto();
                    }
                    cb21ViewDto.setCb21Code(value.charAt(0));
                    placement.setCb21(cb21ViewDto);
                    break;
                case AssignedPlacementHeaderNames.CB21_LEVEL:
                    if (placement.getCb21() != null) {
                        cb21ViewDto = placement.getCb21();
                    } else {
                        cb21ViewDto = new CB21ViewDto();
                    }
                    cb21ViewDto.setLevel(Integer.parseInt(value));
                    placement.setCb21(cb21ViewDto);
                    break;
                case AssignedPlacementHeaderNames.CREATED_ON:
                    placement.setCreatedOn(Date.from(
                            LocalDate.parse(value)
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    break;
                case AssignedPlacementHeaderNames.IS_ASSIGNED:
                    placement.setAssigned(Boolean.parseBoolean(value));
                    break;
                case AssignedPlacementHeaderNames.ASSIGNED_RULE_SET_ID:
                    placement.setAssignedRuleSetId(value);
                    break;
                default:
                    break;
            }
        }
        return placement;
    }

    private PlacementViewDto generatePlacement(String placementValues) {
        PlacementViewDto placementViewDto = null;
        if (StringUtils.isNotBlank(placementValues)) {
            placementViewDto = new PlacementViewDto();
            List<String> componentValuesList = Arrays.asList(placementValues.split(":"));
            if (componentValuesList.size() != 4) {
                throw new RuntimeException("Could not parse placement!");
            }
            placementViewDto.setId(componentValuesList.get(PlacementValuePosition.ID.ordinal()));
            CB21ViewDto cb21ViewDto = new CB21ViewDto();
            cb21ViewDto.setCb21Code(componentValuesList.get(PlacementValuePosition.CB21CODE.ordinal()).charAt(0));
            cb21ViewDto.setLevel(Integer.parseInt(componentValuesList.get(PlacementValuePosition.CB21LEVEL.ordinal())));
            placementViewDto.setCb21(cb21ViewDto);
            placementViewDto.setCreatedOn(Date.from(
                    LocalDate.parse(componentValuesList.get(PlacementValuePosition.CREATED_ON.ordinal()))
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return placementViewDto;
    }

    public Map<String, Object> initFacts() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("actions", new ArrayList<RulesAction>());
        return facts;
    }

    public class AssignedPlacementHeaderNames {
        public static final String ID = "id";
        public static final String CB21_CODE = "cb21Code";
        public static final String CB21_LEVEL = "cb21Level";
        public static final String CREATED_ON = "createdOn";
        public static final String IS_ASSIGNED = "isAssigned";
        public static final String ASSIGNED_RULE_SET_ID = "assignedRuleSetId";
    }
}
