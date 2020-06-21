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
package org.ccctc.common.droolscommon;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;



/**
 * This factory creates kie containers. As this operation can be expensive (time + processing), before attempting to create a kie 
 * container, a client should first check and see if an attempt is already being made to create the container by another process. If 
 * so, that process should wait.
 * 
 * Created containers should be cached and shared to avoid creation overhead when possible.
 * Log messages use '===' to prefix the messages to make searching for log items specific to this class easier to find.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class KieContainerFactory {
    private boolean addActionQuery = true;

    private static HashSet<String> kieContainersCurrentlyBeingBuilt = new HashSet<>();
    
    private StringBuffer buildActionQuery() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("import org.ccctc.common.droolscommon.RulesAction\n");
        buffer.append("query \"actions\"\n");
        buffer.append("\trulesActions : RulesAction();\n");
        buffer.append("end\n");
        return buffer;
    }

    public KieContainer createKieContainerFromDrlString(String drl) {
        return createKieContainerFromDrlStrings(Arrays.asList(drl));
    }

    public KieContainer createKieContainerFromDrlStrings(List<String> drls) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        for (int i = 0; i < drls.size(); i++) {
            String drl = drls.get(i);
            kfs.write("src/main/resources/rule-" + i + ".drl", ks.getResources().newReaderResource(new StringReader(drl)));
        }
        if (addActionQuery) {
            kfs.write("src/main/resources/rule-action-query.drl",
                            ks.getResources().newReaderResource(new StringReader(buildActionQuery().toString())));
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();

        // check there have been no errors for rule setup
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            final StringBuilder stringBuilder = new StringBuilder();
            results.getMessages(Message.Level.ERROR).forEach(m -> stringBuilder.append(m.getText()).append("\n"));
            throw new IllegalStateException("=== Errors in getContainerFromDrlStrings(): \n" + stringBuilder.toString());
        }
        KieContainer kieContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        return kieContainer;
    }

    /**
     * Because creating a new kieContainer is an expensive process, clients using this class should check iskieContainerBeingBuilt(...)
     * before calling this method. If the method returns true, the client should NOT call this method, instead, the calling method
     * sleep and wait, then recheck its cache.
     * </br>
     * This method will always try to create a new container.
     * @param groupId
     * @param artifactId
     * @param version
     * @return new kieContainer
     */
    public KieContainer createKieContainerFromMavenRepository(String groupId, String artifactId, String version) {
        kieContainersCurrentlyBeingBuilt.add(groupId + artifactId + version);
        log.debug("===KieContainerFactory.createKieContainerFromMavenRepository(...): retrieving [" + groupId + ":" + artifactId + ":" + version + "]");
        KieServices kieServices = KieServices.Factory.get();
        ReleaseId releaseId = kieServices.newReleaseId(groupId, artifactId, version);
        KieContainer kieContainer = null;
        try {
            log.debug("=====KieContainerFactory.createKieContainerFromMavenRepository(...): Start create kieContainer");
            KieRepository kieRepo = kieServices.getRepository();
            kieRepo.getKieModule(releaseId);
            kieContainer = kieServices.newKieContainer(releaseId);
            log.debug("=====KieContainerFactory.createKieContainerFromMavenRepository(...): kieContainer created for [" + groupId + ":" + artifactId + ":" + version + "]");
        }
        catch (RuntimeException e) {
            log.error("===KieContainerFactory failed to initialize container for [" + groupId + ":" + artifactId + ":" + version
                            + "], no container created and rules will not be applied");
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        log.debug("===KieContainerFactory.createKieContainerFromMavenRepository(...): returning kieContainer");
        kieContainersCurrentlyBeingBuilt.remove(groupId + artifactId + version);
        return kieContainer;
    }

    public boolean isKieContainerBeingBuilt(String groupId, String artifactId, String version) {
        return kieContainersCurrentlyBeingBuilt.contains(groupId + artifactId + version);
    }

    public void setAddActionQuery(boolean addActionQuery) {
        this.addActionQuery = addActionQuery;
    }

}
