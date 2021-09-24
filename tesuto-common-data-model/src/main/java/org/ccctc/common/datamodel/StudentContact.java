package org.ccctc.common.datamodel;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NonNull;

@Data
public class StudentContact {
    @NonNull
    private String cccId;

    private String appId; // Not needed unless this information is considered part of the application data
    private String city;
    private String country;
    private String email;
    private String mainphone;
    private boolean mainphoneAuthText;
    private String mainphoneExt;
    private String mainphoneintl;
    private boolean noMailingAddressHomeless;
    private boolean noPermAddressHomeless;
    private boolean nonUsAddress;
    private String nonusaprovince;
    private String permCity;
    private String permCountry;
    private String permNonusaprovince;
    private String permPostalcode;
    private String permState;
    private String permStreetaddress1;
    private String permStreetaddress2;
    private String postalcode;
    private String secondphone;
    private boolean secondphoneAuthText;
    private String secondphoneExt;
    private String secondphoneintl;
    private String state;
    private String streetaddress1;
    private String streetaddress2;
    private String zip4;

    /**
     * @return true if all the required fields are are non-null/non-empty
     */
    public boolean isValid() {
        return StringUtils.isNotEmpty(cccId);
    }
}
