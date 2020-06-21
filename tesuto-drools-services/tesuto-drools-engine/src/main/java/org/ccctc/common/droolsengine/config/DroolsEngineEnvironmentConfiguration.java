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
package org.ccctc.common.droolsengine.config;

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolsengine.engine.service.DroolsEngineStatus;
import org.ccctc.common.droolsengine.utils.FactsUtils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * DroolsEngineEnvironmentConfiguration is a centralized place for storing all configurations
 * for the Drools Engine.  Centralization helps to ensure that all variables are named consistently
 * throughout the application.  In addition, upon all startup, all relevant configurations can
 * be logged for easier troubleshooting.
 * @author mgillian
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class DroolsEngineEnvironmentConfiguration implements InitializingBean {

    // Environment variable key names
    // OAUTH Configurations, all engines
    public static final String OAUTH_CLIENT_ID_KEY = "oauth.clientId";
    public static final String OAUTH_CLIENT_SECRET_KEY = "oauth.clientSecret";
    public static final String OAUTH_BASE_URL_KEY = "oauth.base.url";
    
    // OAuth Default Values
    private static final String OAUTH_CLIENT_ID_DEFAULT = "DUMMY_ID";
    private static final String OAUTH_CLIENT_SECRET_DEFAULT = "DUMMY_SECRET";
    private static final String OAUTH_BASE_URL_DEFAULT = "https://login.ci.cccmypath.org/f/";
    
    // Drools Engine Configuration, all engines
    public static final String DROOLS_ENGINE_STATUS_KEY = "DROOLS_ENGINE_STATUS";
    public static final String DROOLS_ENGINE_SCANNER_REFRESH_AUTOMATIC_KEY = "DROOLS_SCANNER_REFRESH_AUTOMATIC";
    public static final String DROOLS_ENGINE_SCANNER_REFERSH_INMILLIS_KEY = "DROOLS_SCANNER_REFERSH_INMILLIS";
    public static final String DROOLS_RULES_APPLICATION_KEY = "DROOLS_RULES_APPLICATION";

    // IActionService configurations, all engines
    public static final String PROFILE_URL_KEY = "PROFILE_URL";
    public static final String APPLICATION_LAYOUT_URL_KEY = "APPLICATION_LAYOUT_URL";
    public static final String MESSAGING_URL_KEY = "MESSAGING_URL";
    public static final String PINBOARD_FAVORITES_URL_KEY = "PINBOARD_FAVORITES_URL";
    public static final String ADVISORCARD_URL_KEY = "ADVISORCARD_URL";
    
    // College Configurations, only if configuration source is environment
    public static final String MISCODE_COUNT_KEY = "DROOLS_RULES_MISCODE_COUNT";
    public static final String MISCODE_PREFIX = "DROOLS_RULES_MISCODE_";
    public static final String MISCODE_SOURCE_KEY = "DROOLS_RULES_SOURCE_";
    
    // Maven Configurations, only if Rules are stored in maven
    public static final String MISCODE_GROUPID_KEY = "DROOLS_RULES_GROUPID_";
    public static final String MISCODE_ARTIFACTID_KEY = "DROOLS_RULES_ARTIFACTID_";
    public static final String MISCODE_VERSIONID_KEY = "DROOLS_RULES_VERSIONID_";
    public static final String GROUPID_PREFIX_KEY = "DROOLS_RULES_GROUPID_PREFIX";
    public static final String ARTIFACTID_PREFIX_KEY = "DROOLS_RULES_ARTIFACTID_PREFIX";
    public static final String VERSIONID_DEFAULT_KEY = "DROOLS_RULES_VERSIONID_DEFAULT";
    
    // Family Configuration: required only if family configuration source is editor
    public static final String DROOLS_EDITOR_URL_KEY = "DROOLS_EDITOR_URL";
    
    //Configuration Telling KieContainerService to Add Action Query to Rules
    public static final String DROOLS_ADD_ACTION_QUERY = "DROOLS_ADD_ACTION_QUERY";

    public static final String CONTAINER_SERVICE_TYPE_KEY = "container.factory.type";
    public static final String CONTAINER_SERVICE_TYPE_DEFAULT = "default";
    public static final String CONTAINER_SERVICE_TYPE_POLLING = "polling";
        
    // Drools Engine Configuration defaults
    private static final DroolsEngineStatus DROOLS_ENGINE_STATUS_DEFAULT = DroolsEngineStatus.ENABLED;
    private static final Integer MISCODE_COUNT_DEFAULT = 0;    
    public static final String RULES_SOURCE_MAVEN = "maven";
    public static final String RULES_SOURCE_EDITOR = "editor";
    public static final String RULES_SOURCE_DEFAULT = RULES_SOURCE_MAVEN;
        
    // Maven defaults
    private static final String GROUPID_PREFIX_DEFAULT = "org.jasig.portlet.rules";
    private static final String ARTIFACTID_PREFIX_DEFAULT = "drools-rules-survey-portlet";
    private static final String VERSIONID_DEFAULT_DEFAULT = "0.0.1";
    private static final Boolean DROOLS_ENGINE_SCANNER_REFRESH_AUTOMATIC_DEFAULT = false;
    private static final Integer DROOLS_ENGINE_SCANNER_REFERSH_INMILLIS_DEFAULT = 10000;
    public static final String DROOLS_RULES_APPLICATION_DEFAULT = "sns-listener";
    private static final Boolean DROOLS_ADD_ACTION_QUERY_DEFAULT=true;
    
    // Family Configuration defaults, only if configuration source is editor
    private static final String DROOLS_EDITOR_URL_DEFAULT = "http://portal.test:8081";

    // which IFamilyReader to use
    public static final String FAMILY_SOURCE_KEY = "family.source";
    public static final String FAMILY_SOURCE_ENVIRONMENT = "environment";
    public static final String FAMILY_SOURCE_EDITOR = "editor";
    public static final String FAMILY_SOURCE_FILE = "file";    
    public static final String FAMILY_SOURCE_DEFAULT = FAMILY_SOURCE_ENVIRONMENT;
    
    // Family Configuration defaults, only if configuration source is file
    public static final String FAMILY_SOURCE_FILEPATH_KEY = "family.source.filepath";
    private static final String FAMILY_SOURCE_FILEPATH_DEFAULT = "/opt/ccc/config";    
    public static final String FAMILY_SOURCE_FILENAME_KEY = "family.source.filename";
    private static final String FAMILY_SOURCE_FILENAME_DEFAULT = "families.json";
    
    // Service - configurations for how DroolsRulesServices are defined
    // which DroolsRulesServiceReader to use - currently only a file version is implemented
    public static final String SERVICE_SUPPORT_SOURCE_KEY = "rules.service.source";
    public static final String SERVICE_SUPPORT_SOURCE_FILE = "file";
    public static final String SERVICE_SUPPORT_SOURCE_DEFAULT = SERVICE_SUPPORT_SOURCE_FILE;
    
    // location of rulesServices source if SERVICE_SUPPORT_SOURCE_KEY=SERVICE_SUPPORT_SOURCE_FILE
    public static final String SERVICE_SUPPORT_SOURCE_FILEPATH_KEY = "service.source.filepath";
    private static final String SERVICE_SUPPORT_SOURCE_FILEPATH_DEFAULT = "/opt/ccc/config";
    public static final String SERVICE_SUPPORT_SOURCE_FILENAME_KEY = "service.source.filename";
    private static final String SERVICE_SUPPORT_SOURCE_FILENAME_DEFAULT = "rulesServices.json";
    
    // No default mis code by default, we only want a value here if it is configured
    public static final String DEFAULT_MIS_KEY = "default.mis";
    private static final String DEFAULT_MIS_CODE = "";
    private String defaultMis = ""; 
    
    public static final String CONTAINER_REFRESH_POLLING_RATEINSECONDS_KEY = "engine.refresh.polling.rate";
    private static final int CONTAINER_REFRESH_POLLING_RATEINSECONDS_DEFAULT = 60;
    
    public static final String PRELOAD_MAVEN_ARTIFACTS = "preload.maven.artifacts";
    public static final Boolean PRELOAD_MAVEN_ARTIFACTS_DEFAULT = Boolean.TRUE;
    
    public static final String FAMILY_CODE_FACT_FIELD_NAME = "family.code.fact.field.name";
    
    /**
     * The default mis (if used) will be used in the DroolsRulesService - when looking up the container for an mis code, if none is
     * found, a check can be made against the default value. 
     */
    public String getDefaultFamilyCode() {
        defaultMis = env.getProperty(DEFAULT_MIS_KEY, String.class, DEFAULT_MIS_CODE);
        return defaultMis;
    }

    /**
     * The default mis (if used) will be used in the DroolsRulesService - when looking up the container for an mis code, if none is
     * found, a check can be made against the default value. 
     */
    @Deprecated
    public String getDefaultMis() {
        defaultMis = env.getProperty(DEFAULT_MIS_KEY, String.class, DEFAULT_MIS_CODE);
        return defaultMis;
    }
    
    @Autowired
    private Environment env;

    public void setEnvironment(Environment env) {
        this.env = env;
    }

    public Integer getMisCodeCount() {
        Integer cccMisCodeCount = env.getProperty(MISCODE_COUNT_KEY, Integer.class, MISCODE_COUNT_DEFAULT);
        return cccMisCodeCount;
    }
    
    /**
     * For each college that uses the rules engine, they will have their own drools
     * session and maven package loaded for their rules.  This returns a list of 
     * colleges that are to be defined.
     * @return List<String> of misCodes that will have a set of drools loaded
     */
    public List<String> getMisCodes() {
        Integer cccMisCodeCount = getMisCodeCount();
        List<String> misCodes = new ArrayList<String>();
        for (int i = 1; i <= cccMisCodeCount; i++) {
            String cccMisCode = env.getProperty(MISCODE_PREFIX + i);
            misCodes.add(cccMisCode);
        }
        return misCodes;
    }

    /**
     * By default, colleges rules packages groupIds are named with this prefix, an "_", and the college miscode.
     * @return
     */
    private String getDefaultDroolsRulesGroupIdPrefix() {
        String groupIdBase = env.getProperty(GROUPID_PREFIX_KEY, GROUPID_PREFIX_DEFAULT);
        return groupIdBase;
    }

    /**
     * By default, colleges rules packages artifactIds are named with this prefix, an "_", and the college miscode.
     * @return
     */
    private String getDefaultDroolsRulesArtifactIdPrefix() {
        String artifactIdBase = env.getProperty(ARTIFACTID_PREFIX_KEY, ARTIFACTID_PREFIX_DEFAULT);
        return artifactIdBase;
    }
    
    /**
     * By default, college rules packages versionIds are named "0.0.1".
     * @return
     */
    private String getDefaultDroolsRulesVersionId() {
        String versionIdDefault = env.getProperty(VERSIONID_DEFAULT_KEY, VERSIONID_DEFAULT_DEFAULT);
        return versionIdDefault;
    }

    
    public String getDroolsRulesGroupIdKey(String cccMisCode) {
        String groupIdKey = MISCODE_GROUPID_KEY + cccMisCode;
        return groupIdKey;
    }
    
    public String getDroolsRulesGroupId(String cccMisCode) {
        String groupId = env.getProperty(getDroolsRulesGroupIdKey(cccMisCode), getDefaultDroolsRulesGroupIdPrefix() + "." + cccMisCode);
        return groupId;
    }

    public String getDroolsRulesArtifactIdKey(String cccMisCode) {
        String artifactIdKey = MISCODE_ARTIFACTID_KEY + cccMisCode;
        return artifactIdKey;
    }
    
    /**
     * Each college specifies what maven group, artifact, and version denotes its rules set.  
     * The group, artifact, and version can be explicitly set or will fall back to a default
     * naming convention.
     * @param cccMisCode
     * @return
     */
    public String getDroolsRulesArtifactId(String cccMisCode) {
        String artifactId = env.getProperty(getDroolsRulesArtifactIdKey(cccMisCode), getDefaultDroolsRulesArtifactIdPrefix() + "." + cccMisCode);
        return artifactId;
    }

    public String getDroolsRulesVersionIdKey(String cccMisCode) {
        String versionIdKey = MISCODE_VERSIONID_KEY + cccMisCode;
        return versionIdKey;
    }
    /**
     * Each college specifies what maven group, artifact, and version denotes its rules set.  
     * The group, artifact, and version can be explicitly set or will fall back to a default
     * naming convention.
     * @param cccMisCode
     * @return
     */
    public String getDroolsRulesVersionId(String cccMisCode) {
        String versionId = env.getProperty(getDroolsRulesVersionIdKey(cccMisCode), 
                getDefaultDroolsRulesVersionId());
        return versionId;
    }

    private String getDroolsRulesSourceKey(String cccMisCode) {
        return MISCODE_SOURCE_KEY + cccMisCode;
    }
    
    public String getDroolsRulesSource(String cccMisCode) {
        String collegeSourceKey = this.getDroolsRulesSourceKey(cccMisCode);
        String ruleSource = env.getProperty(collegeSourceKey, RULES_SOURCE_DEFAULT);
        return ruleSource;
    }
    
    /**
     * Each college specifies what maven group, artifact, and version denotes its rules set.  
     * The group, artifact, and version can be explicitly set or will fall back to a default
     * naming convention.
     * @param cccMisCode
     * @return
     */
    public DroolsEngineStatus getDroolsEngineStatus() {
        DroolsEngineStatus status = env.getProperty(DROOLS_ENGINE_STATUS_KEY, DroolsEngineStatus.class,
                DROOLS_ENGINE_STATUS_DEFAULT);        
        return status;
    }

    /**
     * By default, drools rules are only loaded once, at startup.  However, by setting this
     * property to true, rules will be reloaded on a refresh interval.  Warning: refreshing on a
     * schedule has a memory leak.  Refreshing every 10000 ms will crash the docker containere in
     * about 10 hours.  This is only relevant if rules are defined as SNAPSHOT in their versionID.
     * @return
     */
    public Boolean getDroolsAutomaticRefresh() {
        Boolean refreshAutomatic = env.getProperty(DROOLS_ENGINE_SCANNER_REFRESH_AUTOMATIC_KEY, Boolean.class, DROOLS_ENGINE_SCANNER_REFRESH_AUTOMATIC_DEFAULT);
        return refreshAutomatic;
    }

    /**
     * If the rules engine is set to reload, this is the frequency (in ms) for reload.
     * @return
     */
    public Integer getDroolsAutomaticRefreshInterval() {
        Integer refreshInMillis = env.getProperty(DROOLS_ENGINE_SCANNER_REFERSH_INMILLIS_KEY, Integer.class, DROOLS_ENGINE_SCANNER_REFERSH_INMILLIS_DEFAULT);
        return refreshInMillis;
    }
    
    /**
     * Validators can be disabled for all colleges.  By default they are enabled.
     * @param validatorName
     * @return
     */
    public Boolean isValidatorEnabled(String validatorName) {
        Boolean enabled = env.getProperty(validatorName + "_VALIDATOR_ENABLED", Boolean.class, true);
        return enabled;
    }

    /**
     * Validators can be applied to specific colleges.  Use the id of "ALL' to apply to all
     * colleges.
     * @param validatorName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getValidatorMisCodes(String validatorName) {
        List<String> misCodes = env.getProperty(validatorName + "_VALIDATOR_MISCODES", List.class, new ArrayList<String>());
        return misCodes;
    }

    /**
     * The Host URL of the Student Profile Server
     * @return
     */
    public String getProfileUrl() {
        String profileURL = env.getProperty(PROFILE_URL_KEY);
        return profileURL;
    }

    /**
     * The Host URL of the uPortal Application Layout REST Endpoiint.
     * @return
     */
    public String getEngineLayoutUrl() {
        String layoutUrl = env.getProperty(APPLICATION_LAYOUT_URL_KEY);
        return layoutUrl;
    }

    /**
     * The Host URL of the Messaging Server
     * @return
     */
    public String getEmailHost() {
        String emailHost = env.getProperty(MESSAGING_URL_KEY);
        return emailHost;
    }

    /**
     * The Host URL of the uPortal Pinboard Favorites REST Endpoint
     * @return
     */
    public String getFavoritesUrl() {
        String favoritesUrl = env.getProperty(PINBOARD_FAVORITES_URL_KEY);
        return favoritesUrl;
    }
    
    public String getAdvisorcardUrl() {
        String advisorcardUrl = env.getProperty(ADVISORCARD_URL_KEY);
        return advisorcardUrl;
    }

    /**
     * Client Key for OAuth2 Security (assuming statically defined
     * @return
     */
    public String getConsumerKey() {
        String consumerKey = env.getProperty(OAUTH_CLIENT_ID_KEY, OAUTH_CLIENT_ID_DEFAULT);
        return consumerKey;
    }

    /**
     * Shared Client Secret for OAuth2 Security (assuming statically defined
     * @return
     */
    public String getConsumerSecret() {
        String sharedSecret = env.getProperty(OAUTH_CLIENT_SECRET_KEY, OAUTH_CLIENT_SECRET_DEFAULT);
        return sharedSecret;
    }

    public String getConsumerBaseUrl() {
        String url = env.getProperty(OAUTH_BASE_URL_KEY, OAUTH_BASE_URL_DEFAULT);
        return url;
    }
    
    public String getDroolsRulesApplication() {
        String datasource = env.getProperty(DROOLS_RULES_APPLICATION_KEY, DROOLS_RULES_APPLICATION_DEFAULT);
        return datasource;
    }
    
    public String getFamilySource() {
        String source = env.getProperty(FAMILY_SOURCE_KEY, FAMILY_SOURCE_DEFAULT);
        return source;
    }

    public String getFamilySourceFilePath() {
        String path = env.getProperty(FAMILY_SOURCE_FILEPATH_KEY, FAMILY_SOURCE_FILEPATH_DEFAULT);
        return path;
    }

    public String getFamilySourceFileName() {
        String fileName = env.getProperty(FAMILY_SOURCE_FILENAME_KEY, FAMILY_SOURCE_FILENAME_DEFAULT);
        return fileName;
    }
    
    public String getEngineSource() {
        String source = env.getProperty(SERVICE_SUPPORT_SOURCE_KEY, SERVICE_SUPPORT_SOURCE_DEFAULT);
        return source;
    }
    
    public String getEngineSourceFilePath() {
        String path = env.getProperty(SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, SERVICE_SUPPORT_SOURCE_FILEPATH_DEFAULT);
        return path;
    }
    
    public String getEngineSourceFileName() {
        String path = env.getProperty(SERVICE_SUPPORT_SOURCE_FILENAME_KEY, SERVICE_SUPPORT_SOURCE_FILENAME_DEFAULT);
        return path;        
    }
    
    public String getDroolsEditorURL() {
        String url = env.getProperty(DROOLS_EDITOR_URL_KEY, DROOLS_EDITOR_URL_DEFAULT);
        return url;
    }
    
    public Boolean getAddActionQuery() {
        Boolean addActionQuery = env.getProperty(DROOLS_ADD_ACTION_QUERY, Boolean.class,  DROOLS_ADD_ACTION_QUERY_DEFAULT);
        return addActionQuery;
    }

    /**
     * getContainerServiceType refers to which type of ServiceContainer the ServiceContainerFactory will return.
     * @return "default" means load once, never refreshed until docker container is restarted.  "polling" means that
     * the container will be refreshed if the locally cached container 
     */
    public String getContainerServiceType() {
        String containerServiceType = env.getProperty(CONTAINER_SERVICE_TYPE_KEY, CONTAINER_SERVICE_TYPE_DEFAULT);
        return containerServiceType;
    }

    public int getContainerPollingRefreshRateInSeconds() {
        int refreshPollingRate = env.getProperty(CONTAINER_REFRESH_POLLING_RATEINSECONDS_KEY, Integer.class, CONTAINER_REFRESH_POLLING_RATEINSECONDS_DEFAULT);
        return refreshPollingRate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Drools Engine Environemt Configuration");
            log.debug("\tConsumer Key(" + OAUTH_CLIENT_ID_KEY + "): [" + this.getConsumerKey() + "]");
            log.debug("\tConsumere Secret(" + OAUTH_CLIENT_SECRET_KEY + "): [********]");
            log.debug("\tOauth BaseURL(" + OAUTH_BASE_URL_KEY + "): [" + this.getConsumerBaseUrl() + "]");
            log.debug("\tAutomatic Refresh?(" + DROOLS_ENGINE_SCANNER_REFRESH_AUTOMATIC_KEY + "): [" + this.getDroolsAutomaticRefresh() + "]");
            log.debug("\tAutomatic Refresh Interval(" + DROOLS_ENGINE_SCANNER_REFERSH_INMILLIS_KEY + "): [" + this.getDroolsAutomaticRefreshInterval() + "]");
            log.debug("\tDrools Rules Application(" + DROOLS_RULES_APPLICATION_KEY + "): [" + this.getDroolsRulesApplication() + "]");
            log.debug("\tCollege Source(" + FAMILY_SOURCE_KEY + "): [" + this.getFamilySource() + "]");
            log.debug("\tEngine Support Source[" + SERVICE_SUPPORT_SOURCE_KEY + "]: [" + this.getEngineSource() + "]");
            log.debug("\tDrools Editor URL(" + DROOLS_EDITOR_URL_KEY + "): [" + this.getDroolsEditorURL() + "]");
            log.debug("\tDefault Drools Rules Group Prefix(" + GROUPID_PREFIX_KEY + "): [" + this.getDefaultDroolsRulesGroupIdPrefix() + "]");
            log.debug("\tDefault Drools Rules Artifact Prefix(" + ARTIFACTID_PREFIX_KEY + "): [" + this.getDefaultDroolsRulesArtifactIdPrefix() + "]");
            log.debug("\tDefault Drools Rules Version(" + VERSIONID_DEFAULT_KEY + "): [" + this.getDefaultDroolsRulesVersionId() + "]");
            log.debug("\tMIS Code Count(" + MISCODE_COUNT_KEY + "): [" + this.getMisCodeCount() + "]");
            log.debug("\tDefault Add Action Query(" + DROOLS_ADD_ACTION_QUERY_DEFAULT + "): [" + this.getAddActionQuery() + "]");
            log.debug("\tNumber of Colleges assigned to rules engine:[" + getMisCodes().size() + "]");
            for (String cccMisCode : getMisCodes()) {
                log.debug("\tCollege Rules Source(" + getDroolsRulesSourceKey(cccMisCode) + "): [" + this.getDroolsRulesSource(cccMisCode) + "]");
                if (RULES_SOURCE_MAVEN.equals(this.getDroolsRulesSource(cccMisCode))) {
                    log.debug("\tCollege Rules Package ["+ cccMisCode + "]");
                    log.debug("\t\t(" + this.getDroolsRulesGroupIdKey(cccMisCode) + ":" + 
                            this.getDroolsRulesArtifactIdKey(cccMisCode) + ":" + 
                            this.getDroolsRulesVersionIdKey(cccMisCode) + ")");
                    log.debug("\t\t[" + this.getDroolsRulesGroupId(cccMisCode) + ":" + 
                            this.getDroolsRulesArtifactId(cccMisCode) + ":" + 
                            this.getDroolsRulesVersionId(cccMisCode) + "]");
                }
            }
            log.debug("\tApplication Layout URL(" + APPLICATION_LAYOUT_URL_KEY + "): [" + this.getEngineLayoutUrl() + "]");
            log.debug("\tFavorites URL(" + PINBOARD_FAVORITES_URL_KEY + "): [" +  this.getFavoritesUrl() + "]");
            log.debug("\tEmail Host URL(" + MESSAGING_URL_KEY + "): [" + this.getEmailHost() + "]");
            log.debug("\tStudent Profile URL(" + PROFILE_URL_KEY + "): [" + this.getProfileUrl() + "]");
            log.debug("\tAdvisorcard URL(" + ADVISORCARD_URL_KEY + "): [" + this.getAdvisorcardUrl() + "]");
            log.debug("\tDefault MIS CODE (" + DEFAULT_MIS_KEY + "): [" + this.getDefaultMis() + "]");
            log.debug("\tContainer Service Type (" + CONTAINER_SERVICE_TYPE_KEY + "); [" + this.getContainerServiceType() + "]");
            log.debug("\tEngine Refresh Polling RateInSeconds (" + CONTAINER_REFRESH_POLLING_RATEINSECONDS_KEY + "): [" + this.getContainerPollingRefreshRateInSeconds() + "]");
        }
    }

    public boolean getPreloadMavenArtifacts() {
        Boolean result = env.getProperty(PRELOAD_MAVEN_ARTIFACTS, Boolean.class,  PRELOAD_MAVEN_ARTIFACTS_DEFAULT);
        return result;
    }

    public String getFamilyCodeFactFieldName() {
        return env.getProperty(FAMILY_CODE_FACT_FIELD_NAME, String.class, FactsUtils.MISCODE_FIELD);
    }
}
