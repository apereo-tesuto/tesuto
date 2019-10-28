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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.view.CB21ViewDto;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.MmapDataSourceType;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementRulesSourceDto;
import org.ccctc.common.droolscommon.RulesAction;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class PlacementFactsParser {

    public enum PlacementComponentValuePosition {
        ID,
        CCCID,
        CB21CODE,
        CB21LEVEL,
        CREATED_ON,
        COURSE_GROUP,
        COLLEGE_ID,
        SUBJECT_AREA_ID,
        SUBJECT_AREA_VERSION_ID,
        SELF_REPORTED_DATA,
        STANDALONE,
        ELA_INDICATOR,
        TRACKING_ID
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

    private PlacementActionResult addFactsMap(Map<String, Object> facts, Iterator<String> columnType, Iterator<String> headers, CSVRecord csvFacts) {
        CompetencyAttributesViewDto competencyAttributes = new CompetencyAttributesViewDto();
        PlacementRulesSourceDto placementRulesSourceDto = new PlacementRulesSourceDto();
        PlacementActionResult placement = null;
        Iterator<String> values = csvFacts.iterator();
        while (values.hasNext()) {
            if (columnType.next().equals("fact")) {
                PlacementComponentViewDto placementComponent;
                switch (headers.next()) {
                    case "ruleSetId":
                        facts.put("RULE_SET_ID", values.next());
                        break;
                    case "optInMultiMeasure":
                        competencyAttributes.setOptInMultiMeasure(Boolean.parseBoolean(values.next()));
                        break;
                    case "useSelfReportedData":
                        competencyAttributes.setUseSelfReportedDataForMM(Boolean.parseBoolean(values.next()));
                        break;
                    case "mmapComponent":
                        placementComponent = generatePlacementComponent(values.next());
                        if (placementComponent != null) {
                            placementRulesSourceDto.getMmapPlacementComponents().add(placementComponent);
                        }
                        break;
                    case "assessComponent":
                        placementComponent = generatePlacementComponent(values.next());
                        if (placementComponent != null) {
                            placementRulesSourceDto.getAssessmentPlacementComponents().add(placementComponent);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                placement = generatePlacementActionResult(headers, values);
            }
        }
        facts.put("COMPETENCY_ATTRIBUTES", competencyAttributes);
        facts.put("PLACEMENT_COMPONENTS", placementRulesSourceDto);
        return placement;
    }

    private PlacementActionResult generatePlacementActionResult(Iterator<String> headers, Iterator<String> values) {
        PlacementActionResult placement = new PlacementActionResult();
        while (values.hasNext()) {
            String value = values.next();
            if (StringUtils.isEmpty(value)) { return null; }
            switch (headers.next()) {
                case PlacementHeaderNames.CCCID:
                    placement.setCccid(value);
                    break;
                case PlacementHeaderNames.CB21_CODE:
                    placement.setCb21Code(value.charAt(0));
                    break;
                case PlacementHeaderNames.COURSE_GROUP:
                    placement.setCourseGroup(Integer.parseInt(value));
                    break;
                case PlacementHeaderNames.COLLEGE_ID:
                    placement.setCollegeId(value);
                    break;
                case PlacementHeaderNames.SUBJECT_AREA_ID:
                    placement.setSubjectAreaId(Integer.parseInt(value));
                    break;
                case PlacementHeaderNames.SUBJECT_AREA_VERSION:
                    placement.setSubjectAreaVersion(Integer.parseInt(value));
                    break;
                case PlacementHeaderNames.CREATED_ON:
                    placement.setCreatedOn(Date.from(
                            LocalDate.parse(value)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    break;
                case PlacementHeaderNames.RULE_SET_ID:
                    placement.setRuleSetId(value);
                    break;
                case PlacementHeaderNames.RULE_ID:
                    placement.setRuleId(value);
                    break;
                case PlacementHeaderNames.PLACEMENT_COMPONENT_IDS:
                    Set<String> placementComponentIds = new HashSet<>();
                    placementComponentIds.addAll(Arrays.asList(value.split(":")));
                    placement.setPlacementComponentIds(placementComponentIds);
                    break;
                case PlacementHeaderNames.ELA_INDICATOR:
                    placement.setElaIndicator(value);
                    break;
                case PlacementHeaderNames.TRACKING_ID:
                    placement.setTrackingId(value);
                    break;
                default:
                    break;
            }
        }
        return placement;
    }

    private PlacementComponentViewDto generatePlacementComponent(String componentValues) {
        PlacementComponentViewDto placementComponentViewDto = null;
        if (StringUtils.isNotBlank(componentValues)) {
            placementComponentViewDto = new PlacementComponentViewDto();
            List<String> componentValuesList = Arrays.asList(componentValues.split(":"));
            if (componentValuesList.size() != 13) {
                throw new RuntimeException("Could not parse placement component!");
            }
            placementComponentViewDto.setId(componentValuesList.get(PlacementComponentValuePosition.ID.ordinal()));
            placementComponentViewDto.setCccid(componentValuesList.get(PlacementComponentValuePosition.CCCID.ordinal()));
            CB21ViewDto cb21ViewDto = new CB21ViewDto();
            cb21ViewDto.setCb21Code(componentValuesList.get(PlacementComponentValuePosition.CB21CODE.ordinal()).charAt(0));
            cb21ViewDto.setLevel(Integer.parseInt(componentValuesList.get(PlacementComponentValuePosition.CB21LEVEL.ordinal())));
            placementComponentViewDto.setCb21(cb21ViewDto);
            placementComponentViewDto.setCreatedOn(Date.from(
                    LocalDate.parse(componentValuesList.get(PlacementComponentValuePosition.CREATED_ON.ordinal()))
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            placementComponentViewDto.setCourseGroup(Integer.valueOf(componentValuesList.get(PlacementComponentValuePosition.COURSE_GROUP.ordinal())));
            placementComponentViewDto.setCollegeId(componentValuesList.get(PlacementComponentValuePosition.COLLEGE_ID.ordinal()));
            placementComponentViewDto.setSubjectAreaId(Integer.valueOf(componentValuesList.get(PlacementComponentValuePosition.SUBJECT_AREA_ID.ordinal())));
            placementComponentViewDto.setSubjectAreaVersion(Integer.valueOf(componentValuesList.get(PlacementComponentValuePosition.SUBJECT_AREA_VERSION_ID.ordinal())));
            if (StringUtils.isNotBlank(componentValuesList.get(PlacementComponentValuePosition.SELF_REPORTED_DATA.ordinal()))) {
                boolean isSelfReported = Boolean.parseBoolean(componentValuesList.get(PlacementComponentValuePosition.SELF_REPORTED_DATA.ordinal()));
                placementComponentViewDto.setDataSourceType(isSelfReported ? MmapDataSourceType.SELF_REPORTED : MmapDataSourceType.STANDARD);
            }
            if (StringUtils.isNotBlank(componentValuesList.get(PlacementComponentValuePosition.STANDALONE.ordinal()))) {
                placementComponentViewDto.setStandalonePlacement(Boolean.parseBoolean(componentValuesList.get(PlacementComponentValuePosition.STANDALONE.ordinal())));
            }
            placementComponentViewDto.setElaIndicator(componentValuesList.get(PlacementComponentValuePosition.ELA_INDICATOR.ordinal()));
            placementComponentViewDto.setTrackingId(componentValuesList.get(PlacementComponentValuePosition.TRACKING_ID.ordinal()));
        }
        return placementComponentViewDto;
    }

    public Map<String, Object> initFacts() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("actions", new ArrayList<RulesAction>());
        return facts;
    }

    public class PlacementHeaderNames {
        public static final String CCCID = "cccid";
        public static final String CB21_CODE = "cb21Code";
        public static final String COURSE_GROUP = "courseGroup";
        public static final String COLLEGE_ID = "collegeId";
        public static final String SUBJECT_AREA_ID = "subjectAreaId";
        public static final String SUBJECT_AREA_VERSION = "subjectAreaVersion";
        public static final String CREATED_ON = "createdOn";
        public static final String RULE_SET_ID = "ruleSetId";
        public static final String RULE_ID = "ruleId";
        public static final String PLACEMENT_COMPONENT_IDS = "placementComponentIds";
        public static final String ELA_INDICATOR = "elaIndicator";
        public static final String TRACKING_ID = "trackingId";
    }
}
