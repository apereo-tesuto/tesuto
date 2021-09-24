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

import org.cccnext.tesuto.activation.Passcode;
import org.cccnext.tesuto.activation.PasscodeDao;
import org.cccnext.tesuto.activation.PasscodeValidationAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by bruce on 1/27/16.
 */
@Service("passcodeDao")
public class JpaPasscodeDao extends JpaDao implements PasscodeDao {

    @Autowired JpaPasscodeAssembler assembler;
    @Autowired JpaPasscodeValidationAttemptAssembler attemptAssembler;

    private boolean doDelete(String value, boolean isRecord) {
        String table = (isRecord ? "JpaPasscodeRecord" : "JpaPasscode");
        String queryString = "from " + table + " where value = :passcode";
        return execute(manager -> {
            Query query = manager.createQuery(queryString);
            query.setParameter("passcode", value);
            query.setMaxResults(1);
            if (query.getResultList().size() == 0) {
                return false;
            } else {
                String deleteQueryString = "delete " + queryString;
                Query deleteQuery = manager.createQuery(deleteQueryString);
                deleteQuery.setParameter("passcode", value);
                deleteQuery.executeUpdate();
                return true;
            }
        });

    }

    @Override
    public boolean delete(String value) {
        doDelete(value, true);
        return doDelete(value, false);
    }

    @Override
    public Set<Passcode> findAllPasscodesByUser(String userId) {
        return execute(manager -> {
            Query query = manager.createQuery("from JpaPasscode where userId = :userId");
            query.setParameter("userId", userId);
            Stream<Passcode> passcodes = query.getResultList().stream().map(row -> assembler.doAssemble((JpaPasscode) row));
            return passcodes.collect(Collectors.toSet());
        });
    }

    @Override
    public Passcode findPasscodeByUser(String userId, Passcode.PasscodeType type) {
        return execute(manager -> {
            Query query = manager.createQuery("from JpaPasscode where userId=:userId and type=:type");
            query.setParameter("userId", userId);
            query.setParameter("type", type);
            JpaPasscode jpaPasscode = null;
            try {
                return assembler.doAssemble((JpaPasscode) query.getSingleResult());
            } catch (NoResultException | EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public Set<Passcode> findPasscodesByValue(String value) {
        return execute(manager -> {
            Query query = manager.createQuery("from JpaPasscode where value = :value");
            query.setParameter("value", value);
            Stream<Passcode> passcodes = query.getResultList().stream().map(row -> assembler.doAssemble((JpaPasscode) row));
            return passcodes.collect(Collectors.toSet());
        });
    }

    @Override
    // TODO: It might be better to make the attempts a property of Activation
    // and return them in a join
    public Set<PasscodeValidationAttempt> findValidationAttempts(String activationId) {
        return execute(manager -> {
            Query query = manager
                    .createQuery("from JpaPasscodeValidationAttempt where jpaActivation.activationId = :activationId");
            query.setParameter("activationId", activationId);
            Stream<PasscodeValidationAttempt> attempts = query.getResultList().stream()
                    .map(row -> attemptAssembler.assembleDto((JpaPasscodeValidationAttempt) row));
            return attempts.collect(Collectors.toSet());
        });

    }

    @Override
    public void savePasscode(Passcode passcode) {
        JpaPasscode jpa = assembler.disassembleDto(passcode);
        try {
            execute(manager -> {
                Passcode oldPasscode = findPasscodeByUser(passcode.getUserId(), passcode.getType());
                if (oldPasscode == null) {
                    manager.persist(jpa);
                } else {
                    manager.merge(jpa);
                }
                JpaPasscodeRecord record = new JpaPasscodeRecord(passcode);
                manager.persist(record);
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error trying to create activation " + passcode.toString(), e);
        }
    }

    @Override
    public void saveLatestValidationAttempt(PasscodeValidationAttempt attempt) {
        if (attempt.getPasscode().length() > 100) {
            attempt.setPasscode("<passcode too long>");
        }
        try {
            execute(manager -> {
                // another database lookup that could be optimized away, but
                // it's hard to do so while keeping the DAO
                // interface independent of JPA
                JpaPasscodeValidationAttempt jpaAttempt = attemptAssembler.disassembleDto(attempt);
                manager.persist(jpaAttempt);
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error trying to save latest Validation Attempt " + attempt, e);
        }
    }
}
