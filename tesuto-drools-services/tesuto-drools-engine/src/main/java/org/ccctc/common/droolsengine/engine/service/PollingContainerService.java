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
package org.ccctc.common.droolsengine.engine.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.kie.api.runtime.KieContainer;



/**
 * PollingContainerService provides Drools KieContainers. Due to the expense of creating containers, a cache of the created kieContainers
 * is kept. Periodic polling of the data source that defines which rule set to use is done to determine if there is a change (also 
 * if the cache becomes stale). 
 * Logging prefixed with ***
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PollingContainerService implements IContainerService {
    static final Map<String, KieContainerLog> kContainerLogs = new HashMap<>();

    private IContainerConfiguratorFactory containerConfiguratorFactory;

    private DroolsEngineEnvironmentConfiguration envConfig;

    private IFamilyConfigurator familyConfigurator;

    private String name;

    public PollingContainerService(IFamilyConfigurator familyConfig, IContainerConfiguratorFactory containerConfiguratorFactory,
                                   DroolsEngineEnvironmentConfiguration config, String name) {
        this.containerConfiguratorFactory = containerConfiguratorFactory;
        this.envConfig = config;
        this.name = name;
        this.familyConfigurator = familyConfig;
    }

    /**
     * WARNING: This method is going to attempt to create all the kContainers.
     */
    @Override
    public void createContainers() {
        log.debug("***START PollingContainerService - createContainers()");
        List<FamilyDTO> familyDTO = familyConfigurator.getFamilies(false);
        familyDTO = familyDTO == null ? new ArrayList<>() : familyDTO;
        log.debug("******family config supplied " + familyDTO.size() + " engine configurations for engine [" + this.name + "]");
        familyDTO.forEach(familydto -> {
            getContainer(familydto.getFamilyCode());
        });
        log.debug("***END PollingContainerService - createContainers()");
    }

    /**
     * getContainer returns a KieContainer for this family and engine. The KieContainer has all of the rules
     * that will be compared to the passed in facts.
     * <p>
     * This implementation can return null if there is no family and engine combo stored in the family datasource.
     * In addition, since the data can change, it is entirely possible for a family/engine combo to exist at one point
     * and not exist in the future.
     * </p>
     * <p>
     * KieContainers that are created are cached in a local, in-memory map for retrieval. When a calling engine tries
     * to retrieve a container, it checks to see if that container has already been created and stored. If it has, it checks
     * to see if that family/engine combo's settings are different (i.e. the maven address has changed). If the container
     * hasn't been created before, or if the container's Maven address has changed, the container is rebuilt. Rebuilding a
     * container is very expensive and takes many seconds, so we create a container only when necessary.
     * </p>
     * <p>
     * If the container has been created recently, we don't go back to the datasource immediately. This helps cut down on
     * database calls. We'll only look for changes to the container definition after an interval. Once that interval has been
     * exceeded, we check the container's configuration against the family's definition. If the definition has changed, we
     * recreate the container, otherwise, we reset the interval for checking the database again.
     * </p>
     */
    @Override
    public KieContainer getContainer(String familyCode) {
        log.debug("***getContainer(): [" + this.getName() + "] Retrieving container [" + familyCode + "]");
        if (isCacheStale(familyCode)) {
            log.debug("*****getContainer(): [" + this.getName() + "] - [" + familyCode + "], " + "retrieving family from configurator");
            FamilyDTO familyDTO = this.familyConfigurator.getFamily(familyCode);
            if (familyDTO == null && envConfig != null) {
                log.debug("*****getContainer(): [" + this.getName() + "] none found for [" + familyCode + "], checking default ["
                                + envConfig.getDefaultFamilyCode() + "]");
                familyDTO = this.familyConfigurator.getFamily(envConfig.getDefaultFamilyCode());
            }

            if (familyDTO == null) {
                log.warn("*****[" + this.getName() + "] family [" + familyCode + "] not found in datastore; Container is null.");
                return null;
            }
            ;
            // Now check that there is an engine that matches, if not, then we can try to get one from a default (if exists)
            EngineDTO engineDTO = familyDTO.getEngineDTO(this.getName());
            if (engineDTO == null && envConfig != null) {
                log.debug("*****getContainer(): [" + this.getName() + "] no engine found for [" + familyCode
                                + "], checking default [" + envConfig.getDefaultFamilyCode() + "]");
                FamilyDTO familyDto = this.familyConfigurator.getFamily(envConfig.getDefaultFamilyCode());
                if (familyDto != null && familyDto.getEngineDTO(this.getName()) != null) {
                    familyDTO = familyDto;
                }
            }

            // if the container is stale, rebuild
            // if not, don't rebuild but update the cache date so we won't check again for the refresh rate duration
            if (isContainerStale(familyDTO)) {
                PollingContainerService.kContainerLogs.remove(this.name + familyCode);
                log.debug("[" + this.getName() + "] Container for [" + familyCode + "] needs to be refreshed");
                KieContainerLog kContainerLog = refreshContainer(familyDTO);
                if (kContainerLog != null) {
                    PollingContainerService.kContainerLogs.put(this.name + familyCode, kContainerLog);
                } else {
                    log.warn("[" + this.getName() + "] Unable to refresh container for [" + familyCode + "]");
                }
            } else {
                KieContainerLog kContainerLog = PollingContainerService.kContainerLogs.get(this.name + familyCode);
                if (kContainerLog != null) {
                    kContainerLog.cacheDate = new Date();
                }
            }
        }

        KieContainerLog kContainerLog = PollingContainerService.kContainerLogs.get(this.name + familyCode);
        // the kContainerLog will be null if the family and engine doesn't exist and there is no default
        if (kContainerLog == null) {
            return null;
        }
        return kContainerLog.kContainer;
    }

    @Override
    public Map<String, KieContainer> getContainers() {
        Map<String, KieContainer> kContainers = new HashMap<>();
        for (Entry<String, KieContainerLog> entry : kContainerLogs.entrySet()) {
            kContainers.put(entry.getKey(), entry.getValue().kContainer);
        }
        return kContainers;
    }

    @SuppressWarnings({ "deprecation" })
    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("class", this.getClass().getName());
        info.put("name", getName());
        Map<String, Object> containers = new HashMap<>();
        for (Entry<String, KieContainerLog> entry : kContainerLogs.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(getName())) {
                KieContainerLog kContainerLog = entry.getValue();
                Map<String, String> containerMap = new HashMap<>();
                containerMap.put("Container Key", key);
                containerMap.put("groupId", kContainerLog.groupId);
                containerMap.put("artifactId", kContainerLog.artifactId);
                containerMap.put("version", kContainerLog.version);
                containerMap.put("dataSource", kContainerLog.dataSource);
                Date cacheDate = kContainerLog.cacheDate;
                containerMap.put("cacheDate", cacheDate == null ? "" : cacheDate.toGMTString());
                Date editDate = kContainerLog.editDate;
                containerMap.put("editDate", (editDate == null) ? "" : editDate.toGMTString());
                containers.put(key, containerMap);
            }
        }
        info.put("containers", containers);
        return info;
    }

    protected Map<String, KieContainerLog> getKieContainerLogs() {
        return PollingContainerService.kContainerLogs;
    }

    private String getName() {
        return this.name;
    }

    private IEngineFactory getEngineFactory(FamilyDTO familyDTO) {
        return StringUtils.isBlank(name) ? containerConfiguratorFactory.getEngineFactory(familyDTO)
                        : containerConfiguratorFactory.getEngineFactory(familyDTO, name);
    }

    @Override
    public List<String> getRuleSetDrls(String familyCode) {
        List<FamilyDTO> familyDTO = familyConfigurator.getFamilies(true);
        if (familyDTO.size() < 1) {
            log.error("[" + this.getName() + "] DROOLS_RULES_MISCODE_COUNT is 0 or not found, no rules loaded.");
            return Arrays.asList("[" + this.getName() + "] DROOLS_RULES_MISCODE_COUNT is 0 or not found, no rules loaded.");
        }

        for (FamilyDTO familyDto : familyDTO) {
            if (familyCode.equals(familyDto.getFamilyCode())) {
                IEngineFactory engineFactory = getEngineFactory(familyDto);
                return engineFactory.getDrls(familyDto);
            }
        }
        return Arrays.asList(String.format("[" + this.getName() + "] No drools rules found for familyCode %s", familyCode,
                        familyDTO.size()));
    }

    private boolean isCacheDateWithinThreshold(KieContainerLog kContainerLog) {
        Date cacheDate = kContainerLog.cacheDate;
        Date currentDate = new Date();
        int refreshPollingRateInSeconds = envConfig.getContainerPollingRefreshRateInSeconds();
        long millisSinceLastRefresh = currentDate.getTime() - cacheDate.getTime();
        if (millisSinceLastRefresh < refreshPollingRateInSeconds * 1000) {
            log.debug("[" + this.getName() + "] Cached copy has been cached with the last [" + refreshPollingRateInSeconds
                            + "] seconds.");
            return true;
        } else {
            log.debug("[" + this.getName() + "] Cached copy was cached more than [" + refreshPollingRateInSeconds + "] ago");
            return false;
        }
    }

    /**
     * isCacheState checks the local in-memory cache to see if a record exists for this family. If the record does exist
     * it checks to see when the last time the container properties were checked. This purpose of this check is to make sure
     * that every request for a container doesn't require a database call.
     * <p>
     * The cache period is configured with the config's CONTAINER_REFRESH_POLLING_RATEINSECONDS_KEY. Here are the scenarios:
     * <ol>
     * <li>If the family isn't in the cache, return true</li>
     * <li>If the family is found but the refresh date older than the rate in seconds, return true</li>
     * <li>If the family is found, and the refresh date is newer than the rate in seconds, return false</li>
     * </ol>
     * </p>
     * @return true if stale, false if not stale
     */
    boolean isCacheStale(String familyCode) {
        if (!PollingContainerService.kContainerLogs.containsKey(this.name + familyCode)) {
            log.debug("[" + this.getName() + "] Container [" + familyCode + "] has never been cached before.");
            return true;
        }

        KieContainerLog kContainerLog = kContainerLogs.get(this.name + familyCode);
        if (isCacheDateWithinThreshold(kContainerLog)) {
            log.debug("[" + this.getName() + "] Cache for [" + familyCode + "] is newer than ["
                            + envConfig.getContainerPollingRefreshRateInSeconds() + "]; Container is fresh");
            return false;
        }
        return true;
    }

    /**
     * isContainerStale() looks in the datasource for the container definition (FamilyDTO's). It checks the configuration of the
     * family against what is stored. If the values are different, it returns stale. If the values are the same, meaning the
     * container, if rebuilt would look exactly the same, it returns false.
     * 
     * @param familyDTO
     * @return true if the KieContainer needs to be rebuilt, false if the container doesn't need to be rebuilt.
     */
    boolean isContainerStale(FamilyDTO familyDTO) {
        String familyCode = familyDTO.getFamilyCode();
        KieContainerLog kContainerLog = PollingContainerService.kContainerLogs.get(this.name + familyCode);
        if (kContainerLog == null) {
            log.debug("***No existing kContainerLog for [" + familyDTO + "]");
            return true;
        }
        EngineDTO engineDTO = familyDTO.getEngineDTO(this.getName());
        if (engineDTO == null) {
            log.warn("[" + this.getName() + "] not associated with family [" + familyCode + "], Container is always stale.");
            return true;
        }

        String dataSource = engineDTO.getDataSource();
        if (DroolsEngineEnvironmentConfiguration.RULES_SOURCE_MAVEN.equals(dataSource)) {
            log.debug("***isStale? Checking mavenData for change");
            return isMavenDataChanged(kContainerLog, engineDTO);
        } else if (DroolsEngineEnvironmentConfiguration.RULES_SOURCE_EDITOR.equals(dataSource)) {
            log.debug("***isStale? Checking editor for change");
            return isEditorDataChanged(kContainerLog, engineDTO);
        } else {
            // Assume the container is fresh if datasource is anything else, as those
            // won't change dynamically
            log.debug("[" + this.getName() + "] Datasource is [" + dataSource + "]; Container is always assumed fresh.");
            return false;
        }
    }

    boolean isEditorDataChanged(KieContainerLog kContainerLog, EngineDTO engineDTO) {
        Date persistedEdited = engineDTO.getEdited();
        if (persistedEdited == null) {
            log.debug("***[" + this.getName()
                            + "] Database does not have an edited date.  Assume the date hasn't changed; Container is fresh");
            return false;
        }
        long persistedEditTime = persistedEdited.getTime();
        Date cachedEdited = kContainerLog.editDate;
        if (cachedEdited == null) {
            cachedEdited = new Date();
        }
        long cachedEditTime = cachedEdited.getTime();
        boolean isCachedNewer = persistedEditTime <= cachedEditTime;
        if (isCachedNewer) {
            log.debug("***[" + this.getName() + "] Database edited date is the same or earlier than cached date; Container is fresh");
            return false;
        } else {
            log.debug("***[" + this.getName() + "] Database edited date is after cachedDate; Container is stale");
            return true;
        }
    }

    private boolean isMavenDataChanged(KieContainerLog kContainerLog, EngineDTO engineDTO) {
        boolean isSame = (kContainerLog.groupId.equals(engineDTO.getGroupId())
                        && kContainerLog.artifactId.equals(engineDTO.getArtifactId())
                        && kContainerLog.version.equals(engineDTO.getVersion()));
        if (isSame) {
            log.debug("[" + this.getName() + "] GroupId:[" + kContainerLog.groupId + "], " + "ArtifactId:["
                            + kContainerLog.artifactId + "], " + "Version:[" + kContainerLog.version + "] "
                            + "have not changed; Container is fresh");
            return false;
        } else {
            log.debug("[" + this.getName() + "] CachedGroupId:[" + kContainerLog.groupId + "], " + "CachedArtifactId:["
                            + kContainerLog.artifactId + "], " + "CachedVersion:[" + kContainerLog.version + "] "
                            + "are different than StoredGroupId:[" + engineDTO.getGroupId() + "], " + "StoredArtifactId:["
                            + engineDTO.getArtifactId() + "], " + "StoredVersion:[" + engineDTO.getVersion() + "]; "
                            + "Container is stale");
            return true;
        }
    }

    /**
     * It is possible another thread has started the process of building the container. If this is the case, we will just wait and
     * then return the newly built kContainer.
     * 
     * @return updated KieContainerLog
     */
    private KieContainerLog refreshContainer(FamilyDTO familyDTO) {
        IEngineFactory engineFactory = getEngineFactory(familyDTO);
        log.debug("*****refreshContainer(): [" + this.getName() + "] Retrieved engine factory for [" + familyDTO.getFamilyCode()
                        + "] [" + engineFactory.getClass() + "]");
        KieContainerLog kContainerLog = null;
        boolean isBeingBuilt = engineFactory.iskContainerBeingBuilt(familyDTO);
        if (isBeingBuilt) {
            while (isBeingBuilt) {
                try {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {
                    log.error("Sleep Interrupted while waiting for kContainer to be built", e);
                }
                isBeingBuilt = engineFactory.iskContainerBeingBuilt(familyDTO);
            }
            kContainerLog = PollingContainerService.kContainerLogs.get(this.name + familyDTO.getFamilyCode());
        } else {
            KieContainer kContainer = engineFactory.getContainer(familyDTO);
            log.debug("*****refreshContainer(): [" + this.getName() + "] Retrieved kContainer for [" + familyDTO.getFamilyCode()
                            + "] [" + engineFactory.getClass() + "]");
            kContainerLog = new KieContainerLog();
            kContainerLog.kContainer = kContainer;
            kContainerLog.cacheDate = new Date();
            EngineDTO engineDTO = familyDTO.getEngineDTO(this.getName());
            if (engineDTO == null) {
                return null;
            }
            kContainerLog.editDate = engineDTO.getEdited();
            kContainerLog.dataSource = engineDTO.getDataSource();
            kContainerLog.groupId = engineDTO.getGroupId();
            kContainerLog.artifactId = engineDTO.getArtifactId();
            kContainerLog.version = engineDTO.getVersion();
        }

        return kContainerLog;
    }
    
    protected class KieContainerLog {
        public String artifactId;
        public Date cacheDate;
        public String dataSource;
        public Date editDate;
        public String groupId;
        public KieContainer kContainer;
        public String version;
    }
}
