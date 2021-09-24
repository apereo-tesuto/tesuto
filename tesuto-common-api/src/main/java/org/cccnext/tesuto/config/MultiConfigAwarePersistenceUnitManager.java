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
package org.cccnext.tesuto.config;

import java.net.URL;

import javax.persistence.spi.PersistenceUnitInfo;



import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * An extension to the {@link DefaultPersistenceUnitManager} that is able to
 * merge multiple <tt>persistence.xml</tt> associated to the same persistence
 * unit name.
 * <p />
 * If a module persistence unit defines managed classes explicitly, only adds
 * the specified classes. If the module persistence unit does not define any
 * managed classes, module scanning is assumed: any entity classes defined in
 * the module holding the persistence unit will be associated to the main one.
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiConfigAwarePersistenceUnitManager extends DefaultPersistenceUnitManager {

    /** The strict. */
    private transient boolean strict = false;

    /** The persistence unit name. */
    private transient String persistenceUnitName = "default";

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        super.postProcessPersistenceUnitInfo(pui);

        // This our template persistence unit that I am post-processing
        // so let's just skip.
        if (isApplicationPersistenceUnit(pui)) {
            return;
        }

        final MutablePersistenceUnitInfo mainPui = getMainPersistenceUnitInfo(pui);

        if (strict) {
            pui.addJarFileUrl(pui.getPersistenceUnitRootUrl());
        }

        if (mainPui != null) {
            if (strict) {
                if (log.isDebugEnabled()) {
                    log.debug("Merging existing jar file urls " + mainPui.getJarFileUrls() + " to persistence unit ["
                            + pui.getPersistenceUnitName() + "]");
                }
                copyPersistenceUnit(mainPui, pui);
                if (log.isDebugEnabled()) {
                    log.debug("Persistence unit[" + pui.getPersistenceUnitName() + "] has now "
                            + "the following jar file urls " + pui.getJarFileUrls() + "");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Merging information from [" + pui.getPersistenceUnitName() + "] " + "to ["
                            + mainPui.getPersistenceUnitName() + "]");
                }
                mergePersistenceUnit(pui, mainPui);
                if (log.isDebugEnabled()) {
                    log.debug("Persistence unit[" + mainPui.getPersistenceUnitName() + "] has now "
                            + "the following jar file urls " + mainPui.getJarFileUrls());
                }
            }
        } else if (!pui.getPersistenceUnitName().equals(persistenceUnitName)) {
            log.debug("Adding persistence unit [" + pui.getPersistenceUnitName() + "] as is (no merging)");
        }
    }

    /**
     * Specifies if the manager should process the persistence units in strict
     * mode. When enabled, only the persistence unit that have the exact same
     * names as an existing one are merged. When disabled (the default), if the
     * name of the persistence unit matches the prefix, it is merged with the
     * persistence unit defined by the prefix.
     * 
     * @param strict
     *            if merging occurs on an exact match or on the prefix only
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Sets the name of the persistence unit that should be used. If no such
     * persistence unit exists, an exception will be thrown, preventing the
     * factory to be created.
     * <p />
     * When the <tt>strict</tt> mode is disabled, this name is used to find all
     * matching persistence units based on a prefix. Say for instance that the
     * <tt>persistenceUnitName</tt> to use is <tt>pu</tt>, the following
     * applies:
     * 
     * pu-base will be merged pufoo will be merged base-pu will <b>not</b> be
     * merged
     * 
     * Make sure to configure your entity manager factory to use this name as
     * the persistence unit
     * 
     * @param persistenceUnitName
     *            the name of the persistence unit to use
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * Merges a persistence unit to another one. Takes care of handling both
     * managed classes and urls. If the persistence unit has managed classes,
     * only merge these and prevents scanning. If no managed classes are
     * available, add the url of the module for entity scanning.
     * 
     * @param from
     *            the persistence unit to handle
     * @param to
     *            the target (merged) persistence unit
     */
    protected void mergePersistenceUnit(MutablePersistenceUnitInfo from, MutablePersistenceUnitInfo to) {
        if (from.getMappingFileNames().size() != 0) {
            for (String s : from.getMappingFileNames()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding entity [" + s + "]");
                }
                to.addMappingFileName(s);
            }
            if (log.isDebugEnabled()) {
                log.debug("Added [" + from.getMappingFileNames().size() + "] mapping file to " + "persistence unit["
                        + to.getPersistenceUnitName() + "]");
            }
        } else if (from.getManagedClassNames().size() != 0) {
            for (String s : from.getManagedClassNames()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding entity [" + s + "]");
                }
                to.addManagedClassName(s);
            }
            if (log.isDebugEnabled()) {
                log.debug("Added [" + from.getManagedClassNames().size() + "] managed classes to "
                        + "persistence unit[" + to.getPersistenceUnitName() + "]");
            }
        } else {
            to.addJarFileUrl(from.getPersistenceUnitRootUrl());
            if (log.isDebugEnabled()) {
                log.debug("Added [" + from.getPersistenceUnitRootUrl() + "] for entity scanning "
                        + "to persistence unit[" + to.getPersistenceUnitName() + "]");
            }
        }
    }

    /**
     * Copies a persistence unit to another one. Takes care of copying both
     * managed classes and urls.
     * 
     * @param from
     *            the persistence unit to copy
     * @param to
     *            the target (copied) persistence unit
     */
    protected void copyPersistenceUnit(MutablePersistenceUnitInfo from, MutablePersistenceUnitInfo to) {
        for (String s : from.getMappingFileNames()) {
            to.addMappingFileName(s);
        }
        for (String s : from.getManagedClassNames()) {
            to.addManagedClassName(s);
        }
        // Copy jar file urls
        for (URL url : from.getJarFileUrls()) {
            to.addJarFileUrl(url);
        }
    }

    /**
     * Specifies whether the specified persistence unit is the template one we
     * use to merge.
     * 
     * @param pui
     *            the persistence unit to test
     * @return <tt>true</tt> if the persistence unit is the target, template
     *         persistence unit
     */
    private boolean isApplicationPersistenceUnit(MutablePersistenceUnitInfo pui) {
        return (!strict && persistenceUnitName.equals(pui.getPersistenceUnitName()));
    }

    /**
     * Returns the main {@link MutablePersistenceUnitInfo} to use, based on the
     * given {@link MutablePersistenceUnitInfo} and the settings of the manager.
     * <p />
     * In strict mode, returns the declared persistence unit with the specified
     * name. In non strict mode and if the specified <tt>pui</tt> starts with
     * the configured <tt>persistenceUnitName</tt>, returns the template
     * persistence unit info used for the merging.
     * 
     * @param pui
     *            the persistence unit info to handle
     * @return the persistence unit info to use for the merging
     */
    private MutablePersistenceUnitInfo getMainPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        MutablePersistenceUnitInfo result = null;
        if (strict) {
            return getPersistenceUnitInfo(pui.getPersistenceUnitName());
        }
        if (pui.getPersistenceUnitName().startsWith(persistenceUnitName)) {
            // We have a match, retrieve our persistence unit name then
            result = getPersistenceUnitInfo(persistenceUnitName);
            // Sanity check
            if (result == null) {
                throw new IllegalStateException("No persistence unit found with name [" + persistenceUnitName + "] "
                        + "so no merging is possible. It usually means that the bootstrap-persistence.xml has not been "
                        + "included in the list of persistence.xml location(s). Check your configuration as it "
                        + "should be the first in the list!");
            }
        }
        // Nothing has been found
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String puName) {
        PersistenceUnitInfo persistenceUnitInfo = super.obtainPersistenceUnitInfo(puName);
        persistenceUnitInfo.getJarFileUrls().remove(persistenceUnitInfo.getPersistenceUnitRootUrl());
        return persistenceUnitInfo;
    }

}
