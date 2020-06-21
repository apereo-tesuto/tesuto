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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.action.result.ErrorActionResult;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.IActionService;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.ccctc.common.droolsengine.utils.SecurityUtils;
import org.drools.core.SessionConfiguration;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;


import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DroolsRulesService implements IDroolsRulesService {
    @Autowired
    private Map<String, IActionService> availableActions;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;
    
    private IContainerService containerService;
    
    @Autowired
    private List<IFactsPreProcessor> factPreProcessors;
    
    private String familyCodeFactFieldName = FactsUtils.MISCODE_FIELD;
    
    private String name;
    
    /**
     * searchableActions are keyed by the service's "name" property, not by the classname
     */
    private final Map<String, IActionService> searchableActions = new HashMap<String, IActionService>();

    @Autowired
    private SecurityUtils securityUtils;

    private DroolsEngineStatus status;

    private List<ActionResult> applyFactPreProcessors(Map<String, Object> facts) {
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        log.debug("[" + this.getName() + "] looking through preProcessors:[" + factPreProcessors.size() + "]");
        for (IFactsPreProcessor preProcessor : factPreProcessors) {
            if (preProcessor.isEnabled(facts)) {
                log.debug("[" + this.getName() + "] IFactsPreProcessor:[" + preProcessor.getName() + "] enabled, processing");
                try {
                    List<String> results = preProcessor.processFacts(facts);
                    if (CollectionUtils.isNotEmpty(results)) {
                        for (String result : results) {
                            ActionResult actionResult = new ErrorActionResult("FAILED_ON_PRE_PROCESS", result);
                            actionResults.add(actionResult);
                        }
                    }
                } catch (Exception exception) {
                    ActionResult actionResult = new ErrorActionResult(exception, "FAILED_ON_PRE_PROCESS", exception.getMessage());
                    actionResults.add(actionResult);
                }
            }
        }
        return actionResults;
    }
    
    /**
     * Execute takes the list of facts, loads them into a RulesEngine stateless
     * session and fires all rules that have been loaded into the rules engine.
     * Rules that match add actions into Facts to a list under the key
     * "actions". Once all rules have been fired, all of the defined actions are
     * iterated over
     * <p>
     * Facts are validated against a list of preprocessors that implement the
     * IFactsPreProcessor interface. Classes that implement this interface verify
     * that the data needed is present. These classes can also add any missing
     * data if they know how to create that data.
     * <p>
     * All actions are executed independently A failure by any action will not
     * prevent other actions from being executed.
     * 
     * @param facts
     *            a Map of things known about the environment, including the
     *            current user, oauthToken, and other information added by the
     *            integrating module.
     * @return List<String> of errors that occurred during execution. The list
     *         will be empty if there are no problems during execution.
     */
    public List<ActionResult> execute(Map<String, Object> facts) {
        log.debug("[" + this.getName() + "] has incoming facts");
        log.trace("[" + this.getName() + "] incoming facts:[" + facts + "]");        
        List<ActionResult> results = new ArrayList<ActionResult>();

        if (!isEnabled()) {
            results.add(new ErrorActionResult("DroolsRulesService.execute(..)", "[" + this.getName() + "] Rules Engine status:[" + this.getStatus() + "]"));
            return results;
        }

        results.addAll(applyFactPreProcessors(facts));
        if (results.size() > 0) {
            return results;
        }

        String cookies = getCookies();
        facts.put(FactsUtils.COOKIES_FIELD, cookies);

        log.trace("[" + this.getName() + " ] facts added to rules engine session:[" + facts.toString() + "]");
        String familyCode = (String) facts.get(familyCodeFactFieldName);
        KieContainer kContainer = containerService.getContainer(familyCode);
        if (kContainer == null) {
            results.add(new ErrorActionResult("DroolsRulesService.execute(..)", "[" + this.getName() +"] No container found for family code [" + familyCode + "], rules ignored"));
            return results;
        }
        log.debug("[" + this.getName() + "] retrieved kContainer for [" + familyCode + "]");
        List<RulesAction> actions = new ArrayList<>();
        StatelessKieSession kSession = kContainer.newStatelessKieSession(SessionConfiguration.newInstance());
        log.debug("[" + this.getName() + "] Stateless Session created");
        if (config.getAddActionQuery()) {
            log.debug("[" + this.getName() + "] executeRuleActionQuery");
            actions.addAll(executeRuleActionQuery(kSession, facts));
        } else {
            log.debug("[" + this.getName() + "] executeRuleStandard");
            actions.addAll(executeStandard(kSession, facts));
        }

        List<ActionResult> actionResults = takeActions(actions, facts);
        results.addAll(actionResults);
        return results;
    }

    private List<RulesAction> executeRuleActionQuery(StatelessKieSession kSession, Map<String, Object> facts) {
        log.trace("[" + this.getName() + "] incoming facts:[" + facts + "]");
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(facts, "myfacts"));
        commands.add(CommandFactory.newFireAllRules());
        commands.add(CommandFactory.newQuery("actions", "actions"));
        ExecutionResults results = kSession.execute(CommandFactory.newBatchExecution(commands));
        log.debug("[" + this.getName() + "] execution succeeded");
        List<RulesAction> actions = new ArrayList<RulesAction>();
        QueryResults queryResults = (QueryResults) results.getValue("actions");
        log.debug("[" + this.getName() + "] QueryResults size:[" + queryResults.size() + "]");
        for (QueryResultsRow row : queryResults) {
            Object obj = row.get("rulesActions");
            if (obj instanceof RulesAction) {
                RulesAction action = (RulesAction) obj;
                actions.add(action);
            } else {
                log.warn("[" + this.getName() +"] [actions] query returned a value that was not a RulesAction, value is of type [" + obj.getClass().getName() + "], ignoring");
            }
        }
        log.debug("[" + this.getName() + "] returning [" + actions.size() + "] actions");
        return actions;
    }
    
    @SuppressWarnings("unchecked")
    private List<RulesAction> executeStandard(StatelessKieSession kSession, Map<String, Object> facts) {
        log.trace("[" + this.getName() + "] incoming facts:[" + facts + "]");
        kSession.addEventListener(new DebugAgendaEventListener());
        kSession.addEventListener(new DebugRuleRuntimeEventListener());
        facts.put("actions", new ArrayList<RulesAction>());

        kSession.execute(facts);
        return (List<RulesAction>) facts.get("actions");
    }
    
    private String getCookies() {
        String result = "";
        HttpServletRequest request = securityUtils.getHttpServletRequest();
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (i > 0) {
                        result += "; ";
                    }
                    result += cookie.getName() + "=" + cookie.getValue();
                }
            }
        }
        log.debug("[" + this.getName() + "] cookie string:[" + result + "]");
        return result;
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", getName());
        info.put("status", getStatus());
        info.put("containerSerivice", this.containerService.getInfo());
        info.put("actions", this.searchableActions.keySet());
        info.put("preprocessors", this.factPreProcessors.stream()
                .map(p -> p.getName())
                .collect(Collectors.toList()));
        return info;
    }

    public String getName() {
        if (StringUtils.isBlank(this.name)) {
            if (this.config == null) {
                log.error("DroolsRuleService does not have a config setting, returning an [ERROR_NAME]");
                this.name = "ERROR_NAME";
            } else {
                this.name = config.getDroolsRulesApplication();
            }
        }
        return this.name;
    }

    DroolsEngineStatus getStatus() {
        if (this.status == null) {
            if (this.config == null) {
                log.error("[" + this.getName() + "] DroolsRulesService config is not set, returning disabled status");
                // TODO this.status should already be set before we get here, figure out why it isn't
                status = DroolsEngineStatus.DISABLED;
            } else {
                status = config.getDroolsEngineStatus();
            }
        }
        return this.status;
    }

    @PostConstruct
    public void init() {
        if (availableActions == null) {
            log.error("[" + this.getName() + "] No available action services to process");
            availableActions = new HashMap<String, IActionService>();
        }
        
        for (IActionService service : availableActions.values()) {
            searchableActions.put(service.getName(), service);
        }

        if (!this.isEnabled()) {
            log.warn("[" + this.getName() + "] Rules Engine status on init is [" + status + "], rules engine will not process rules");
            return;
        }
        
        if (containerService == null) {
            status = DroolsEngineStatus.DISABLED_STARTUP_FAILED;
            log.error("[" + this.getName() + "] ContainerService not defined for DroolsRulesService.  Engine will be disabled");
            return;
        }
        
        if (containerService.getContainers().size() == 0) {
            log.warn("[" + this.getName() + "] has no containers");
            return;
        }

        if (StringUtils.isNotEmpty(config.getFamilyCodeFactFieldName())) {
            this.familyCodeFactFieldName = config.getFamilyCodeFactFieldName();
        }
        
        log.info("[" + this.getName() + "] status after initializing is [" + this.getStatus() + "]");
    }
    
    @Override
    public void initkContainers() {
        log.debug("$$$$[" + getName()+"] initializing kContainers via container service");
        containerService.createContainers();
        log.debug("$$$$[" + getName()+"] container service finished creating kContainers");
    }

    private boolean isEnabled() {
        log.debug("[" + this.getName() + "] Status:[" + this.getStatus() + "]");
        return DroolsEngineStatus.ENABLED.equals(this.getStatus());
    }

    public void setAvailableActions(Map<String, IActionService> availableActions) {
        this.availableActions = availableActions;
    }

    public void setConfiguration(DroolsEngineEnvironmentConfiguration config) {
        this.config = config;
    }

    public void setContainerService(IContainerService containerService) {
        this.containerService = containerService;
    }

    public void setFactPreProcessors(List<IFactsPreProcessor> factPreProcessors) {
        this.factPreProcessors = factPreProcessors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecurityUtils(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    List<ActionResult> takeAction(final RulesAction action, final Map<String, Object> facts) {
        List<ActionResult> actionResults = new ArrayList<ActionResult>();
        String actionName = action.getActionName();
        final IActionService service = searchableActions.get(actionName);
        if (service != null) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    log.debug("[Submitting [" + service.getName() + "] to new thread");
                    List<String> errors = service.execute(action, facts);
                    for (String error : errors) {
                        log.error(error);
                    }
                }
            });
            actionResults.add(new ActionResult("DroolsRulesService.takeAction(..)", "Service found and action taken for [" + actionName + "]"));
        } else {
            log.error("[" + this.getName() + "] Service not found for action [" + actionName + "]");
            actionResults.add(new ErrorActionResult("DroolsRulesService.takeAction(..)", "[" + this.getName() + "] Service not found for action [" + actionName + "]"));
        }
        return actionResults;
    }

    List<ActionResult> takeActions(List<RulesAction> actions, Map<String, Object> facts) {
        List<ActionResult> results = new ArrayList<ActionResult>();
        if (actions.size() > 0) {
            log.debug("[" + this.getName() + "] Taking [" + actions.size() + "] actions");
            for (RulesAction action : actions) {
                log.debug("[" + this.getName() +"] taking action[" + action.getActionName() + "]");
                List<ActionResult> actionErrors = takeAction(action, facts);
                results.addAll(actionErrors);
            }
        } else {
            log.debug("[" + this.getName() + "] No actions found based on current ruleset for this user");
            results.add(new ActionResult("DroolsRulesService.takeActions(..)", "NO_ACTIONS_FOUND"));
        }
        return results;
    }
}
