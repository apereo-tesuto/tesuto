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
package org.cccnext.tesuto.content.service;

import static org.cccnext.tesuto.util.CCCAsssessListUtils.safeSubList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyRef;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedCompetencies;
import org.cccnext.tesuto.content.model.competency.CompetencyMapOrder;
import org.cccnext.tesuto.content.repository.mongo.CompetencyMapOrderRepository;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.util.TesutoUtils;
import org.hibernate.ObjectNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("competencyMapOrderService")
public class CompetencyMapOrderServiceImpl implements CompetencyMapOrderService {

    @Autowired
    AssessmentItemReader assessmentItemReader;

    @Autowired
    CompetencyMapOrderRepository repository;

    @Autowired
    @Qualifier("competencyService")
    CompetencyService competencyService;

    @Autowired
    @Qualifier("competencyMapService")
    CompetencyMapService competencyMapService;

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public CompetencyMapOrderRepository getRepository() {
        return repository;
    }

    public void setRepository(CompetencyMapOrderRepository repository) {
        this.repository = repository;
    }

    public CompetencyService getCompetencyService() {
        return competencyService;
    }

    public void setCompetencyService(CompetencyService competencyService) {
        this.competencyService = competencyService;
    }

    public CompetencyMapService getCompetencyMapService() {
        return competencyMapService;
    }

    public void setCompetencyMapService(CompetencyMapService competencyMapService) {
        this.competencyMapService = competencyMapService;
    }

    @Override
    public String create(CompetencyMapDto map) {

        if (StringUtils.isEmpty(map.getId())) {
            map.setId(TesutoUtils.newId());
        }
        List<CompetencyDifficultyRef> competencyDifficulties = new ArrayList<CompetencyDifficultyRef>();
        List<CompetencyRefDto> competencies = map.getCompetencyRefs();

        for (CompetencyRefDto ref : competencies) {
            List<String> parents = new ArrayList<String>();
            buildSubOrder(ref, competencyDifficulties, parents);
        }
        if (competencyDifficulties.size() > 0) {

            CompetencyMapOrder cmo = new CompetencyMapOrder();
            cmo.setId(TesutoUtils.newId());
            cmo.setCompetencies(competencyDifficulties);
            cmo.setCompetencyMapId(map.getId());
            cmo.setVersion(getNextVersion(map.getId()));

            sortCompetencies(cmo.getCompetencies());

            return repository.save(cmo).getId();
        }

        return null;
    }

    private void sortCompetencies(List<CompetencyDifficultyRef> competencies) {
        Collections.sort(competencies, new Comparator<CompetencyDifficultyRef>() {
            @Override
            public int compare(CompetencyDifficultyRef o1, CompetencyDifficultyRef o2) {
                return o2.getDifficulty().compareTo(o1.getDifficulty());
            }
        });
    }

    private int getNextVersion(String competencyMapId) {
        List<CompetencyMapOrder> mapOrder = repository
                .findByCompetencyMapIdOrderByVersionDesc(competencyMapId);
        int version = 1;
        if (CollectionUtils.isNotEmpty(mapOrder)) {
            version = mapOrder.get(0).getVersion() + 1;
        }
        return version;
    }

    private void buildSubOrder(CompetencyRefDto ref, List<CompetencyDifficultyRef> competencyDifficulties, List<String> parents) {
        List<AssessmentItemDto> items = assessmentItemReader.getItemsByCompetency(ref.getDiscipline(),
                ref.getCompetencyIdentifier());
        if (CollectionUtils.isNotEmpty(items)) {
            Double difficultyTotal = null;
            for (AssessmentItemDto item : items) {
                if (item.getItemMetadata() != null && item.getItemMetadata().getCalibratedDifficulty() != null) {
                    if (difficultyTotal == null) {
                        difficultyTotal = item.getItemMetadata().getCalibratedDifficulty();
                    } else {
                        difficultyTotal += item.getItemMetadata().getCalibratedDifficulty();
                    }

                }
            }
            if (difficultyTotal != null) {
                CompetencyDifficultyRef difficulty = new CompetencyDifficultyRef();
                difficulty.setDiscipline(ref.getDiscipline());
                difficulty.setVersion(ref.getVersion());
                difficulty.setCompetencyIdentifier(ref.getCompetencyIdentifier());
                difficulty.setAssessmentItemIds(items.stream().map(it -> it.getId()).collect(Collectors.toList()));
                difficulty.setDifficulty(difficultyTotal / items.size());
                difficulty.setParents(parents);
                competencyDifficulties.add(difficulty);
            }
        }
        CompetencyDto competency = competencyService.readByDisciplineIdentifierAndVersion(ref.getDiscipline(),
                ref.getCompetencyIdentifier(), ref.getVersion());
        if (competency != null && CollectionUtils.isNotEmpty(competency.getChildCompetencyDtoRefs())) {
            List<String> subparents = new ArrayList<String>(parents);
            subparents.add(competency.getId());
            competency.getChildCompetencyDtoRefs().forEach(cr -> buildSubOrder(cr, competencyDifficulties, subparents));
        } else if (competency == null) {
            log.error(ref.toString() + " returns a null competency.");
        }
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
        CompetencyMapOrder order = repository.findById(id).get();
        return getOrderedCompetencies( order.getCompetencies());
    }

    @Override
    public List<CompetencyDifficultyDto> getOrderedCompetencies(String competencyMapId, int version) {
        CompetencyMapOrder order = repository.findByCompetencyMapIdAndVersion(competencyMapId, version);
        return getOrderedCompetencies( order.getCompetencies());
    }

    private List<CompetencyDifficultyDto> getOrderedCompetencies( List<CompetencyDifficultyRef> competenciesRefs) {
        if(CollectionUtils.isEmpty(competenciesRefs)) {
            return Collections.emptyList();
        }
        List<CompetencyDifficultyDto> competencies = competenciesRefs.stream()
                .map(cp -> buildCompetencyDifficulty(cp)).collect(Collectors.toList());
        return competencies;
    }



    private CompetencyDifficultyDto buildCompetencyDifficulty(CompetencyDifficultyRef cp) {
        return new CompetencyDifficultyDto(competencyService.readByDisciplineIdentifierAndVersion(cp.getDiscipline(),
                cp.getCompetencyIdentifier(), cp.getVersion()), cp.getDifficulty());
    }

    @Override
    public Integer findPositionByAbility(String id, Double studentDificulty) {
        CompetencyMapOrder order = repository.findById(id).get();
        if (order == null) {
            throw new ObjectNotFoundException(id, "CompetencyMapOrder");
        }
        List<CompetencyDifficultyRef> difficulties = order.getCompetencies().stream()
                .filter(c -> studentDificulty > c.getDifficulty()).collect(Collectors.toList());
        int position = -1; // Student Ability higher than any competencyDifficulty
        if (difficulties.size() == order.getCompetencies().size()) {
            position = 0; // Student Ability lower than any competencyDifficulty
        } else if (difficulties.size() == 0) {
            position = order.getCompetencies().size();
        }  else {
            position = order.getCompetencies().indexOf(difficulties.get(0)); // First competencyDificulty that is lower the Student Ability, if equal will select first lower value
        }
        return position;
    }

    @Override
    public OrderedCompetencySet selectOrganizeByAbility(String id, Double studentAbility, Integer parentLevel, Integer competencyRange) {
        CompetencyMapOrder competencyMapOrder = repository.findById(id).get();
        if (competencyMapOrder == null) {
            throw new NotFoundException("Unable to find competencyMapOrder with id: " + id);
        }

        Map<String, List<CompetencyDifficultyRef>> byParent =
                competencyMapOrder.getCompetencies().stream().filter(ref -> ref.getParents().size() > parentLevel)
                        .collect(Collectors.groupingBy(
                                (ref -> ref.getParents().get(parentLevel)),
                                Collectors.toList()
                        ));

        Map<String, SelectedOrderedCompetencies> sublists = new HashMap<String,  SelectedOrderedCompetencies>();
        for(String key:byParent.keySet()){
            SelectedOrderedCompetencies selectCompetencies = organizeByAbilityTopic( byParent.get(key), studentAbility, competencyRange);

            selectCompetencies.setParent(competencyService.read(key));
            sublists.put(key, selectCompetencies);
        }

        SelectedOrderedCompetencies all = organizeByAbilityTopic(competencyMapOrder.getCompetencies(), studentAbility, competencyRange);

        return new OrderedCompetencySet(all, sublists);
    }

    public SelectedOrderedCompetencies organizeByAbilityTopic(List<CompetencyDifficultyRef> competencies, Double studentAbility, Integer competencyRange) {

        List<CompetencyDifficultyRef> difficulties = competencies.stream()
                .filter(c -> studentAbility > c.getDifficulty()).collect(Collectors.toList());
        int position = -1; // Student Ability higher than any competencyDifficulty
        if (difficulties.size() == competencies.size()) {
            position = 0; // Student Ability lower than any competencyDifficulty
        } else if (difficulties.size() == 0) {
            position = competencies.size();
        }  else {
            position = competencies.indexOf(difficulties.get(0)); // First competencyDificulty that is lower the Student Ability, if equal will select first lower value
        }

        List<CompetencyDifficultyDto> mastered = getOrderedCompetencies(safeSubList(competencies, position, position + competencyRange));
        List<CompetencyDifficultyDto> tolearn =  getOrderedCompetencies(safeSubList(competencies, position - competencyRange, position));

        SelectedOrderedCompetencies selectedCompetencies = new SelectedOrderedCompetencies(mastered, tolearn);
        if(CollectionUtils.isNotEmpty(competencies)) {
            selectedCompetencies.setMaximumDifficulty(competencies.get(0).getDifficulty());
            selectedCompetencies.setMinimumDifficulty(competencies.get(competencies.size() - 1).getDifficulty());
            selectedCompetencies.setStudentAbility(studentAbility);
        }
        return selectedCompetencies;
    }
    
    @Override
    public String getCompetencyMapOrderId(String id) {
    	 CompetencyMapOrder order = repository.findById(id).get();
    	 if(order != null) {
    		 return order.getCompetencyMapId();
    	 }
    	 return "";
    }

    @Override
    public String findLatestPublishedIdByCompetencyMapDiscipline(String discipline) {
        CompetencyMapDto map = competencyMapService.readLatestPublishedVersion(discipline);
        if(map == null) {
            return null;
        }
        List<CompetencyMapOrder> orderedMaps = repository
                .findByCompetencyMapIdOrderByVersionDesc(map.getId());
        if (CollectionUtils.isNotEmpty(orderedMaps)) {
            return orderedMaps.get(0).getId();
        }
        return null;
    }

    @Override
    public Map<String, String> createForDisciplines(Set<String> disciplines) {
        Map<String, String> competencyMapOrders = new HashMap<String, String>();
        for (String dicipline : disciplines) {
            CompetencyMapDto cm = competencyMapService.readLatestPublishedVersion(dicipline);
            if (cm != null) {
                String competencyMapOrderId = create(cm);
                if(competencyMapOrderId != null) {
                    competencyMapOrders.put(dicipline, competencyMapOrderId);
                }
            }
        }
        return competencyMapOrders;
    }

    @Override
    public void delete(String competencyMapOrderId) {
        repository.deleteById(competencyMapOrderId);
    }



}
