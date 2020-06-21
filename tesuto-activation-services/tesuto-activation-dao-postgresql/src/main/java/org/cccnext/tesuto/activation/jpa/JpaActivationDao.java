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
package org.cccnext.tesuto.activation.jpa;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.activation.ActivationCount;
import org.cccnext.tesuto.activation.ActivationDao;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
/**
 * Created by bruce on 10/14/15.
 */
@Slf4j 
@Service("activationDao")
public class JpaActivationDao extends JpaDao implements ActivationDao {

    @Autowired private JpaActivationAssembler assembler;
    @Autowired private JpaTestEventActivationAssembler testEventActivationAssembler;
    @Autowired private JpaActivationViewAssembler viewAssembler;

    /**
     * Persist a set of activations
     *
     * @param activations a set of almostValid activation object (an activation object with
     *                    a possibly null activationId)
     */
    @Override
    public void create(Collection<Activation> activations) {
        Set<JpaActivation> jpa = activations.stream().map(act -> assembler.disassembleDto(act)).collect(Collectors.toSet());
        try {
            execute(manager -> {
                jpa.forEach( act -> {
                    manager.persist(act);
                    act.getStatusChangeHistory().forEach(change -> manager.persist(change));
                });
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error trying to create activations " + jpa.toString(), e);
        }
    }

    /**
     * Persist an Activation
     *
     * @param activation
     *            -- an almostValid activation object (an activation object with
     *            a possibly null activationId)
     * @return the activationId
     */
    @Override
    public String create(Activation activation) {
        JpaActivation jpa = assembler.disassembleDto(activation);
        try {
            execute(manager -> {
                manager.persist(jpa);
                jpa.getStatusChangeHistory().forEach(change -> manager.persist(change));
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error trying to create activation " + activation.toString(), e);
        }
        return activation.getActivationId();
    }

    /**
     * delete an activation
     *
     * @param activationId
     */
    @Override
    public boolean delete(String activationId) {
        return execute(manager -> {
            JpaActivation activation = manager.find(JpaActivation.class, activationId);
            if (activation != null) {
                manager.remove(activation);
                return true;
            } else {
                return false;
            }
        });
    }


    /**
     * @param activationIds
     * @return A set of activations with the desired activationIds
     */
    @Override
    public Set<Activation> find(Set<String> activationIds) {
        if (activationIds.size() == 0) {
            return new HashSet<>(0);
        } else {
        	try {
            return execute(manager -> {
                List<Object> results = manager.createQuery("from JpaActivation where id in :activationIds")
                        .setParameter("activationIds", activationIds)
                        .getResultList();
                return results.stream().map(row -> assembler.doAssemble((JpaActivation) row)).collect(Collectors.toSet());
            });
        	} catch(JpaSystemException exception) {
        		log.error(exception.getMessage());
        		return new HashSet<>(0);
        	}
        }
    }

    @Override
    public Set<Activation> findActivationsByAssessmentSessionId(String assessmentSessionId) {
        Collection<JpaActivation> results = execute(manager -> {
            Query query = manager.createQuery("from JpaActivation where currentAssessmentSessionId = :sessionId");
            query.setParameter("sessionId", assessmentSessionId);
            return query.getResultList();
        });
        return results.stream().map(jpa -> assembler.doAssemble(jpa)).collect(Collectors.toSet());
    }

    /**
     * Retrieve activations for a particular testEvent
     *
     * @param testEventId
     */
    @Override
    public Set<Activation> findActivationsByTestEventId(int testEventId) {
        Collection<JpaTestEventActivation> results = execute(manager -> {
            Query query = manager.createQuery("from JpaTestEventActivation where testEvent.testEventId = :testEventId");
            query.setParameter("testEventId", testEventId);
            return query.getResultList();
        });
        return results.stream().map(jpa -> testEventActivationAssembler.doAssemble(jpa)).collect(Collectors.toSet());
    }

    /**
     * Persist a set of new and a set of existing activations
     *
     * @param newActivations
     * @param updatedActivations
     */
    @Override
    public void persist(Set<Activation> newActivations, Set<Activation> updatedActivations) {
        Set<JpaActivation> newJpa = assembler.disassembleDto(newActivations);
                //newActivations.stream().map(act -> assembler.disassembleDto(act)).collect(Collectors.toSet());
        Set<JpaActivation> updated = updatedActivations.stream().map(act -> assembler.disassembleDto(act)).collect(Collectors.toSet());
        execute(manager -> {
            newJpa.forEach( act -> {
                manager.persist(act);
                act.getStatusChangeHistory().forEach(change -> manager.persist(change));
            });
            updated.forEach(act -> {
                manager.merge(act);
                act.getStatusChangeHistory().forEach(item -> manager.merge(item));
            });
            return true; // must return something
        });
    }

    private <T> List<T> mapToList(Map<?, Optional<T>> map) {
        return map.entrySet().stream().map(entry -> entry.getValue().get()).collect(Collectors.toList());
    }

    private List<ActivationCount> doRollup(List<ActivationCount> list,
            Function<ActivationCount, ActivationCount> grouping) {
        // First group the list by key
        Map<ActivationCount, List<ActivationCount>> counts = list.stream().collect(Collectors.groupingBy(grouping));
        // Merge each group into the key (which is, itself, an ActivationCount
        // record)
        return counts.entrySet().stream().map(entry -> entry.getKey().merge(entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivationCount> report(Calendar from, Calendar to) {

        List<ActivationCount> activationCounts = (List<ActivationCount>) execute(manager -> {
            return manager.createNamedQuery("activation.report")
                    .setParameter("from", new Date(from.getTimeInMillis()))
                    .setParameter("to", new Date(to.getTimeInMillis()))
                    .getResultList();
        });

        List<ActivationCount> counts = doRollup(activationCounts, ActivationCount::getKey);
        List<ActivationCount> assessmentRollups = doRollup(counts, count -> {
            ActivationCount key = count.getKey();
            key.setProctor(null);
            return key;
        });
        List<ActivationCount> locationRollups = doRollup(assessmentRollups, count -> {
            ActivationCount key = count.getKey();
            key.setProctor(null);
            key.setAssessment(null);
            return key;
        });
        return Stream.concat(Stream.concat(counts.stream(), assessmentRollups.stream()), locationRollups.stream())
                .sorted().collect(Collectors.toList());
    }


    //return None if the where clause cannot be satisfied
    private Optional<Expression<Boolean>> constructWhereClause(SearchParameters parameters, CriteriaBuilder builder, Root<JpaActivationView> activation) {
        ArrayList<Expression<Boolean>> where = new ArrayList<>(); // we'll and these together to make where clause

        if (parameters.getUserIds() != null) {
            if (parameters.getUserIds().size() == 0) {
                return Optional.empty();
            }
            where.add(activation.get("userId").in(parameters.getUserIds()));
        }
        if (parameters.getLocationIds() != null) {
            if (parameters.getLocationIds().size() == 0) {
                return Optional.empty();
            }
            where.add(activation.get("locationId").in(parameters.getLocationIds()));
        }

        if (parameters.getCurrentAssessmentSessionIds() != null) {
            if (parameters.getCurrentAssessmentSessionIds().size() == 0) {
                return Optional.empty();
            }
            where.add(activation.get("currentAssessmentSessionId").in(parameters.getCurrentAssessmentSessionIds()));
        }

        if (parameters.getCreatorIds() != null) {
            if (parameters.getCreatorIds().size() == 0) {
                return Optional.empty();
            }
            where.add(activation.get("creatorId").in(parameters.getCreatorIds()));
        }

        if (parameters.getDeliveryType() != null) {
            where.add(builder.equal(activation.get("deliveryType"), parameters.getDeliveryType()));
        }

        if (!parameters.isIncludeCanceled()) {
            where.add(builder.notEqual(activation.get("statusForSearch"), Activation.Status.DEACTIVATED));
        }
        if (parameters.getMaxStartDate() != null) {
            where.add(builder.lessThanOrEqualTo(activation.get("startDate"), parameters.getMaxStartDate()));
        }
        if (parameters.getMinStartDate() != null) {
            where.add(builder.greaterThanOrEqualTo(activation.get("startDate"), parameters.getMinStartDate()));
        }
        if (parameters.getMaxEndDate() != null) {
            where.add(builder.lessThanOrEqualTo(activation.get("endDate"), parameters.getMaxEndDate()));
        }
        if (parameters.getMinEndDate() != null) {
            where.add(builder.greaterThanOrEqualTo(activation.get("endDate"), parameters.getMinEndDate()));
        }
        if (parameters.getMaxCreateDate() != null) {
            where.add(builder.lessThanOrEqualTo(activation.get("createDate"), parameters.getMaxCreateDate()));
        }
        if (parameters.getMinCreateDate() != null) {
            where.add(builder.greaterThanOrEqualTo(activation.get("createDate"), parameters.getMinCreateDate()));
        }
        if (parameters.getMinStatusUpdateDate() != null) {
            where.add(builder.greaterThanOrEqualTo(activation.get("statusUpdateDate"), parameters.getMinStatusUpdateDate()));
        }
        if (parameters.getMaxStatusUpdateDate() != null) {
            where.add(builder.lessThanOrEqualTo(activation.get("statusUpdateDate"), parameters.getMaxStatusUpdateDate()));
        }
        if (parameters.getCurrentStatus() != null) {
            where.add(builder.equal(activation.get("statusForSearch"), parameters.getCurrentStatus()));
        }

        Expression<Boolean> whereClause = builder.conjunction();
        for (Expression<Boolean> conjunct : where) {
            whereClause = builder.and(whereClause, conjunct);
        }
        return Optional.of(whereClause);
    }


    @Override
    public Set<Activation> search(SearchParameters parameters, Optional<SearchParameters> userParameters) {
        CriteriaBuilder builder = emf.getCriteriaBuilder();
        CriteriaQuery<JpaActivationView> criteria = builder.createQuery(JpaActivationView.class);
        Root<JpaActivationView> activation = criteria.from(JpaActivationView.class);
        activation.fetch("statusChangeHistory", JoinType.LEFT);
        activation.fetch("assessmentSessionIds", JoinType.LEFT);
        Optional<Expression<Boolean>> clause = constructWhereClause(parameters, builder, activation);
        if (!clause.isPresent()) {
            return new HashSet<>(0);
        }
        Expression<Boolean> whereClause = clause.get();
        if (userParameters.isPresent()) {
            Subquery<String> subquery = criteria.subquery(String.class);
            Root<JpaActivationView> subActivation = subquery.from(JpaActivationView.class);
            subquery.select(subActivation.get("userId"));
            Optional<Expression<Boolean>> subclause = constructWhereClause(userParameters.get(), builder, subActivation);
            if (!subclause.isPresent()) {
                return new HashSet<>(0);
            }
            subquery.where(subclause.get());
            Expression<Boolean> existsClause = builder.in(activation.get("userId")).value(subquery);
            whereClause = builder.and(whereClause, existsClause);
        }
        final Expression<Boolean> finalWhereClause = whereClause;
        return execute(manager -> {
            TypedQuery<JpaActivationView> query = manager.createQuery(criteria.distinct(true).where(finalWhereClause));
            List<JpaActivationView> jpaActivationViews = query.getResultList();
            return jpaActivationViews.stream().map(jpa -> viewAssembler.doAssemble(jpa)).collect(Collectors.toSet());
        });
    }


    @Override
    public Set<String> searchForUserIds(SearchParameters parameters) {
        CriteriaBuilder builder = emf.getCriteriaBuilder();
        CriteriaQuery<String> criteria = builder.createQuery(String.class);
        Root<JpaActivationView> activation = criteria.from(JpaActivationView.class);
        Optional<Expression<Boolean>> clause = constructWhereClause(parameters, builder, activation);
        if (!clause.isPresent()) {
            return new HashSet<>(0);
        } else {
            criteria.select(activation.get("userId"));
            criteria.distinct(true);

            return execute(manager -> {
                TypedQuery<String> query = manager.createQuery(criteria.where(clause.get()));
                return new HashSet<>(query.getResultList());
            });
        }
    }


    /**
     * Retrieve a count of activations for each status that meet the given search parameters.
     *
     * @param parameters
     * @return a map of maps: locationId -> activation status -> count
     */
    @Override
    public Map<String, Map<Activation.Status, Integer>> summarizeByStatus(SearchParameters parameters) {
            CriteriaBuilder builder = emf.getCriteriaBuilder();
        CriteriaQuery<SummaryResultHolder> criteria = builder.createQuery(SummaryResultHolder.class);
        Root<JpaActivationView> activation = criteria.from(JpaActivationView.class);
        Optional<Expression<Boolean>> clause = constructWhereClause(parameters, builder, activation);
        if (!clause.isPresent()) {
            return new HashMap<>(0);
        } else {
            criteria.multiselect(activation.get("locationId"), activation.get("statusForSearch"), builder.count(activation));
            criteria.groupBy(activation.get("locationId"), activation.get("statusForSearch"));
            List<SummaryResultHolder> rows = execute(manager -> {
                TypedQuery<SummaryResultHolder> query = manager.createQuery(criteria.where(clause.get()));
                return query.getResultList();
            });

            Map<String, Map<Activation.Status, Integer>> statusCountsByLocation = new HashMap<>();
            rows.forEach(row -> {
                Map<Activation.Status, Integer> countByStatus
                        = statusCountsByLocation.computeIfAbsent(row.getLocationId(), status -> new HashMap<>());
                int count = countByStatus.getOrDefault(row.getStatus(), 0);
                countByStatus.put(row.getStatus(), count+row.getCount());
            });

        return statusCountsByLocation;
        }
    }

    /**
     * Retrieve a summary of today's activations at a set of locations.
     *
     * @param locationIds
     * @return a map of maps: locationId -> activation status -> count
     */
    public Map<String,Map<Activation.Status, Integer>> summarizeByLocation(Set<Integer> locationIds) {
        if (CollectionUtils.isEmpty(locationIds)) {
            return new HashMap<>(0);
        }
        List<SummaryResultHolder> rows = (List<SummaryResultHolder>) execute(manager -> {
            return manager.createNamedQuery("activation.summarizeByLocation")
                    .setParameter("now", new Date())
                    .setParameter("locationIds", locationIds)
                    .getResultList();
        });

        Map<String, Map<Activation.Status, Integer>> statusCountsByLocation = new HashMap<>();
        rows.forEach(row -> {
            Map<Activation.Status, Integer> countByStatus
                    = statusCountsByLocation.computeIfAbsent(row.getLocationId(), status -> new HashMap<>());
            int count = countByStatus.getOrDefault(row.getStatus(), 0);
            countByStatus.put(row.getStatus(), count+row.getCount());
        });

        return statusCountsByLocation;
    }




    @Override
    public void update(Collection<Activation> activations) {
        execute(manager -> {
            activations.forEach(activation -> {
                JpaActivation jpa = assembler.disassembleDto(activation);
                manager.merge(jpa);
                // These merges are unfortunate, since they probably don't
                // change. An optimized activation system
                // should probably move away from JPA
                jpa.getStatusChangeHistory().forEach(item -> manager.merge(item));
            });
            return true; // must return something
        });
    }
}
