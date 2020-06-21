package org.ccctc.common.datamodel;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NonNull;

/**
 * Based on the JSON model at -
 * https://bitbucket.org/cccnext/younite-conductor-adaptor/src/2a45ab235206803cdb445cf9f1e04a5a3f73cdce/domains/src/test/resources/domainSchemas/application.json?at=develop
 */
@Data
public class Application {
    @NonNull
    private String appId;
    @NonNull
    private String cccId;
    @NonNull
    private String collegeId; //aka mis code
    @NonNull
    private String termCode;
    
    private boolean ackFinAid;
    private String appLang;   
    private boolean caFosterYouth;
    private String confirmation;
    private boolean consentIndicator;
    private String eduGoal;
    private boolean finAidRef;
    private String highestEduLevel;
    private String intendedMajor;
    private String majorCategory;
    private String majorCode;
    private boolean under19Ind;

    /**
     * @return true if all the required fields are are non-null/non-empty
     */
    public boolean isValid() {
        return StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(cccId) && StringUtils.isNotEmpty(collegeId)
                        && StringUtils.isNotEmpty(termCode);
    }
}
