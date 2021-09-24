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
package org.cccnext.tesuto.placement.service;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.cccnext.tesuto.placement.model.PlacementRevision;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.NotAuditedException;
import org.hibernate.envers.query.AuditEntity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("placementAuditService")
public class PlacementAuditService {

    @Autowired
    @Qualifier("sqlDataSourcePlacement")
    private DataSource dataSource;

    @Autowired
    @Qualifier("entityManagerFactoryPlacement")
    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void revertByDate(Date date) {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        final Number revision = auditReader.getRevisionNumberForDate(date);
        List<Object> test = auditReader.getCrossTypeRevisionChangesReader().findEntities(revision);

        merge(test);
    }

    public void revertByRevisionNumber(Integer revision) {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        List<Object> test = auditReader.getCrossTypeRevisionChangesReader().findEntities(revision);
        merge(test);
    }

    public Object getByIdAndRevision(Class requestedClass, Object id, int revision) throws NotAuditedException, IllegalArgumentException, IllegalStateException, ClassNotFoundException {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        Object object = auditReader.find(requestedClass, id, revision);
        return object;
    }

    public List<Object> getEntitiesByRevisionByPropertyValue(Class requestedClass, int revision, String properyName, Object propertyValue) throws NotAuditedException, IllegalArgumentException, IllegalStateException, ClassNotFoundException {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        List<Object> objects = auditReader.createQuery().forEntitiesAtRevision(requestedClass, revision).add(AuditEntity.property(properyName).eq(propertyValue)).getResultList();
        return objects;
    }

    public List<Number> getRevisionsForClass(Class requestedClass, Object object) throws NotAuditedException, IllegalArgumentException, IllegalStateException, ClassNotFoundException {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        return auditReader.getRevisions(requestedClass, object);
    }

    public PlacementRevision getCurrentRevision() {
        AuditReader auditReader = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
        return auditReader.getCurrentRevision(PlacementRevision.class, true);
    }

    public void deleteAuditRows(String historyTableName, String id_name, Object id) {

        String revchangesQuery =  "delete from revchanges where rev in (select rev from " + historyTableName + " where " + id_name + "= " + getFormattedId(id) + ")";
        runQuery(revchangesQuery);

        String historyPlacementRevisionQuery =  "delete from history_placement_revision where id in (select rev from " + historyTableName + " where " + id_name + "= " + getFormattedId(id) + ")";
        runQuery(historyPlacementRevisionQuery);

        String audit_history =  "delete from " + historyTableName + " where " + id_name + "  = " + getFormattedId(id);
        runQuery(audit_history);

    }

    private String getFormattedId(Object id) {
        if( id instanceof String ) {
            return "'" + id + "'";
        } else if(id instanceof Integer) {
            return id.toString();
        }

        return id.toString();
    }


    public void runQuery(String query) {
        try (Connection connection = dataSource.getConnection();) {
                connection.setAutoCommit(false);
                Statement stmt = connection.createStatement();
                stmt.execute(query);
                connection.commit();
            } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void merge(List<Object> objects) {
        try {
            execute(manager -> {
                objects.forEach(o -> manager.persist(o));
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error trying to restore revision ", e);
        }
    }

    protected <T> T execute(Function<EntityManager, T> f) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        try {
            manager.getTransaction().begin();
            T val = f.apply(manager);
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().commit();
            }
            return val;
        } finally {
            try {
                manager.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
