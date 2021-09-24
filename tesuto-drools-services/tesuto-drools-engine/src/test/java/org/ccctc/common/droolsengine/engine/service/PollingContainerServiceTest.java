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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;
import org.ccctc.common.droolsengine.engine.service.PollingContainerService.KieContainerLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class })
@TestPropertySource("classpath:application-test.properties")
public class PollingContainerServiceTest {
    private static final String DEFAULT_ENGINE_NAME = DroolsEngineEnvironmentConfiguration.DROOLS_RULES_APPLICATION_DEFAULT;
    private static final String DEFAULT_CCC_MIS_CODE = "ZZ1";

    @Autowired
    private IFamilyConfiguratorFactory familyConfiguratorFactory;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private IContainerConfiguratorFactory containerConfiguratorFactory;

    private EngineDTO buildEngineDTO(String dataSource) {
        if (StringUtils.isBlank(dataSource)) {
            return null;
        }
        Date edited = new Date();
        EngineDTO engineDTO = new EngineDTO();
        engineDTO.setName(DEFAULT_ENGINE_NAME);
        engineDTO.setDataSource(dataSource);
        engineDTO.setEdited(edited);
        if ("maven".equals(dataSource)) {
            String groupId = "org.ccctc.rules";
            String artifactId = "defaultrule";
            String version = "1.0.0";
            engineDTO.setArtifactId(artifactId);
            engineDTO.setGroupId(groupId);
            engineDTO.setVersion(version);
        }
        return engineDTO;
    }

    private FamilyDTO buildFamilyDTO(String dataSource) {
        FamilyDTO collegeDTO = new FamilyDTO();
        collegeDTO.setFamilyCode(DEFAULT_CCC_MIS_CODE);
        Map<String, EngineDTO> engines = new HashMap<>();
        EngineDTO engineDTO = buildEngineDTO(dataSource);
        if (engineDTO != null) {
            engines.put(DEFAULT_ENGINE_NAME, engineDTO);
        }
        collegeDTO.setEngines(engines);
        return collegeDTO;
    }

    // TODO These tests still depend upon application-test.properties. Should change to use
    // the pattern where the environment can be updated locally through the
    // DroolsEngineEnvironmentConfig
    @SuppressWarnings("unchecked")
    @Test
    public void getInfo() {
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        String engineName = DEFAULT_ENGINE_NAME;
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        engineName);
        PollingContainerService.kContainerLogs.clear();
        containerService.getContainer(DEFAULT_CCC_MIS_CODE);
        Map<String, Object> info = containerService.getInfo();
        assertEquals(info.get("class"), "org.ccctc.common.droolsengine.engine.service.PollingContainerService");
        assertEquals(info.get("name"), DEFAULT_ENGINE_NAME);
        Map<String, Object> containers = (Map<String, Object>) info.get("containers");
        assertEquals(1, containers.size());
        Map<String, String> defaultContainer = (Map<String, String>) containers.get(DEFAULT_ENGINE_NAME + DEFAULT_CCC_MIS_CODE);
        assertTrue(defaultContainer != null);
        assertEquals(defaultContainer.get("groupId"), "org.cccmypath.rules.default");
        assertEquals(defaultContainer.get("artifactId"), "drools-rules-listener-rules");
        assertEquals(defaultContainer.get("version"), "unit-test");
        assertEquals(defaultContainer.get("Container Key"), DEFAULT_ENGINE_NAME + DEFAULT_CCC_MIS_CODE);
        assertEquals(defaultContainer.get("dataSource"), "maven");
        System.out.println("info:[" + info + "]");
    }

    @Test
    public void isCacheStale() {
        String engineName = DEFAULT_ENGINE_NAME;
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        List<FamilyDTO> colleges = collegeReader.getFamilies(true);
        assertTrue(colleges != null);
        assertEquals(2, colleges.size()); // ZZ1 and 261
        FamilyDTO collegeDTO = colleges.get(0);
        String cccMisCode = collegeDTO.getFamilyCode();
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        engineName);

        // stale because there is no record in the cache
        assertEquals(true, containerService.isCacheStale(cccMisCode));

        // build a kContainerLog to play with
        Map<String, KieContainerLog> kContainerLogs = containerService.getKieContainerLogs();
        KieContainerLog kContainerLog = containerService.new KieContainerLog();
        kContainerLogs.put(engineName + cccMisCode, kContainerLog);

        // stale because the record is 5 minutes old and default cache refresh is 1 minute
        LocalDateTime localDateTime = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        Date oldDate = java.sql.Timestamp.valueOf(localDateTime);
        kContainerLog.cacheDate = oldDate;
        assertEquals(true, containerService.isCacheStale(cccMisCode));

        // stale because the record is now less than a minute old
        kContainerLog.cacheDate = new Date();
        assertEquals(false, containerService.isCacheStale(cccMisCode));
    }

    @Test
    public void isContainerStale() {
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        String engineName = DEFAULT_ENGINE_NAME;
        FamilyDTO collegeDTO = buildFamilyDTO("maven");
        String cccMisCode = "ZZ1";
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        engineName);
        PollingContainerService.kContainerLogs.clear();

        // stale because there is no record in the cache
        assertEquals(true, containerService.isContainerStale(collegeDTO));

        // build a kContainerLog to play with
        Map<String, KieContainerLog> kContainerLogs = containerService.getKieContainerLogs();
        KieContainerLog kContainerLog = containerService.new KieContainerLog();
        kContainerLogs.put(engineName + cccMisCode, kContainerLog);

        // stale because maven group, artifact, and version don't match engineDTO
        kContainerLog.groupId = "groupId";
        kContainerLog.artifactId = "artifactId";
        kContainerLog.version = "version";
        assertEquals(true, containerService.isContainerStale(collegeDTO));

        // fresh because maven group, artifact, and version match engineDTO
        kContainerLog.groupId = "org.ccctc.rules";
        kContainerLog.artifactId = "defaultrule";
        kContainerLog.version = "1.0.0";
        assertEquals(false, containerService.isContainerStale(collegeDTO));

        // stale because his containerService's name isn't found in FamilyDTO
        FamilyDTO collegeDTO2 = buildFamilyDTO(null);
        assertEquals(true, containerService.isContainerStale(collegeDTO2));

        // always stale because environment never refreshes once loaded
        FamilyDTO collegeDTO3 = buildFamilyDTO("environment");
        assertEquals(false, containerService.isContainerStale(collegeDTO3));

        // stale because engineDTO's edited date is newer than cached date
        FamilyDTO collegeDTO4 = buildFamilyDTO("editor");
        LocalDateTime localDateTime = LocalDateTime.now();
        Date nowMinus5Minutes = java.sql.Timestamp.valueOf(localDateTime.minus(5, ChronoUnit.MINUTES));
        kContainerLog.editDate = nowMinus5Minutes;
        EngineDTO engineDTO4 = collegeDTO4.getEngineDTO(engineName);
        engineDTO4.setEdited(new Date()); // now
        assertEquals(true, containerService.isContainerStale(collegeDTO4));
    }

    @Test
    public void isEditorDataChanged() {
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        DEFAULT_ENGINE_NAME);
        KieContainerLog kContainerLog = containerService.new KieContainerLog();
        EngineDTO engineDTO = new EngineDTO();

        // engineDTO editedDate is blank, assume false
        assertEquals(false, containerService.isEditorDataChanged(kContainerLog, engineDTO));

        // engineDTO edited date is old, and cached date is blank, which defaults to now//
        // so engineDTO is older than cached so false
        LocalDateTime localDateTime = LocalDateTime.now();
        Date nowMinus5Minutes = java.sql.Timestamp.valueOf(localDateTime.minus(5, ChronoUnit.MINUTES));
        engineDTO.setEdited(nowMinus5Minutes);
        assertEquals(false, containerService.isEditorDataChanged(kContainerLog, engineDTO));

        // cached date is older than engineDTO date
        Date nowMinus10Minutes = java.sql.Timestamp.valueOf(localDateTime.minus(10, ChronoUnit.MINUTES));
        kContainerLog.editDate = nowMinus10Minutes;
        assertEquals(true, containerService.isEditorDataChanged(kContainerLog, engineDTO));
    }

    /**
     * This is a little bit of a wonky test in that previously we wanted to "start clean". That's not really valid, but since we
     * don't control the test order in Junit, this test is slightly modified to produce the same previous behavior. In reality, we
     * want to use a cached container if we can given the cost of creating containers.
     */
    @Test
    public void noCachedVersion() {
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        String engineName = DEFAULT_ENGINE_NAME;
        FamilyDTO collegeDTO = buildFamilyDTO("maven");
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        engineName);
        PollingContainerService.kContainerLogs.clear();
        assertEquals(true, containerService.isContainerStale(collegeDTO));
    }

    @Test
    public void nominal() {
        IFamilyConfigurator collegeReader = familyConfiguratorFactory.getConfigurator();
        List<FamilyDTO> colleges = collegeReader.getFamilies(true);
        assertTrue(colleges != null);
        assertEquals(2, colleges.size()); // ZZ1 and 261

        FamilyDTO collegeDTO = colleges.get(0);
        String cccMisCode = collegeDTO.getFamilyCode();
        PollingContainerService containerService = new PollingContainerService(collegeReader, containerConfiguratorFactory, config,
                        DEFAULT_ENGINE_NAME);

        // container has never been pulled before, so cache is not fresh, need to check database
        assertEquals(true, containerService.isCacheStale(cccMisCode));
        // since no record in cache to find, have to rebuild
        assertEquals(true, containerService.isContainerStale(collegeDTO));
        containerService.getContainer("ZZ1");
        // now we have a record in cache that is recent, no need to check
        assertEquals(false, containerService.isCacheStale(cccMisCode));
        // record in cache editTime in engineDTO same as previous time, so don't repull
        // TODO not the correct result
        assertEquals(false, containerService.isContainerStale(collegeDTO));
    }
}
