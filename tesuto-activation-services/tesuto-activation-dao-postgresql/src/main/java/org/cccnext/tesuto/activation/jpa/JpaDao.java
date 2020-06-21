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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by bruce on 1/27/16.
 */
@Slf4j 
public abstract class JpaDao {

    @Autowired
    @Qualifier(value = "entityManagerFactoryActivation")
    protected EntityManagerFactory emf;

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected <T> T execute(Function<EntityManager, T> f) {
        EntityManager manager = emf.createEntityManager();
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
