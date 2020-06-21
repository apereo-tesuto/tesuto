package org.ccctc.common.datamodel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NonNull;

/**
 * Based on the JSON model at -
 * https://bitbucket.org/cccnext/younite-conductor-adaptor/src/2a45ab235206803cdb445cf9f1e04a5a3f73cdce/domains/src/test/resources/domainSchemas/student.demographics.json?at=develop
 */
@Data
public class StudentDemographics {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ISO 8601
    
    @NonNull
    private String birthdate; // MUST be yyyy-MM-dd format
    @NonNull
    private String cccId;
    @NonNull
    private String lastname;

    private String dependentStatus;
    private String firstname;    
    private String gender;
    private boolean hispanic;
    private String middlename;
    private String orientation;
    private String otherfirstname;
    private String otherlastname;
    private String othermiddlename;
    private String preferredFirstname;
    private String preferredLastname;
    private String preferredMiddlename;
    private String raceEthnic;
    private String raceGroup;
    private String suffix;
    private String transgender;
    
    /**
     * @return true if all the required fields are are non-null/non-empty and birthdate is valid
     */
    public boolean isValid() {
        return StringUtils.isNotEmpty(lastname) && StringUtils.isNotEmpty(cccId) && hasValidBirthdate();
    }

    private boolean hasValidBirthdate() {
        try {
            Date dob = dateFormat.parse(birthdate);
            return dob.before(new Date(System.currentTimeMillis()));
        }
        catch (ParseException e) {
            return false;
        }
    }
}
