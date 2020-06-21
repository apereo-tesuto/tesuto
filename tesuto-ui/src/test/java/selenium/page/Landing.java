package selenium.page;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class Landing extends PageObject {
    public static final String PRIVATE_PASSCODE = "privatePasscode";
    public static final String PUBLIC_PASSCODE = "publicPasscode";
    public static final String USER_FIRST_NAME_LAST_NAME = "userFirstNameLastName";
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    private WebElement activationCollection;
    private WebElement generateNewPasscodeButton;
    private WebElement generateNewPrivatePasscodeButton;
    private WebElement generateNewPublicPasscodeButton;
    private WebElement logoutButton;
    private WebElement passcodesLink;
    private WebElement privatePasscode;
    private WebElement privatePasscodeTab;
    private WebElement proctorDashboardButton;
    private WebElement publicPasscode;
    private WebElement publicPasscodeTab;
    private WebElement studentLookupButton;
    private WebElement userFirstNameLastNameButton;

    public Landing clickDropdown() {
        userFirstNameLastNameButton = findElement(ElementBys.LandingFirstNameLastNameButton);
        click(userFirstNameLastNameButton);
        return this;
    }

    public Landing clickGenerateNewPasscode() {
        generateNewPasscodeButton = findElement(ElementBys.LandingGenerateNewPasscodeButton);
        click(generateNewPasscodeButton);
        return this;
    }

    public Landing clickGeneratePrivatePasscode() {
        generateNewPrivatePasscodeButton = findElement(ElementBys.LandingGenerateNewPrivatePasscodeButton);
        click(generateNewPrivatePasscodeButton);
        return this;
    }

    public Landing clickGeneratePublicPasscode() {
        generateNewPublicPasscodeButton = findElement(ElementBys.LandingGenerateNewPublicPasscodeButton);
        click(generateNewPublicPasscodeButton);
        return this;
    }

    public Landing clickLogoutButton() {
        userFirstNameLastNameButton = findElement(ElementBys.LandingFirstNameLastNameButton);
        click(userFirstNameLastNameButton);
        logoutButton = findElement(ElementBys.LandingLogout);
        click(logoutButton);
        return this;
    }

    public Landing clickPasscodes() {
        passcodesLink = findElement(ElementBys.LandingPasscodesLink);
        click(passcodesLink);
        return this;
    }

    public Landing clickProctorDashboardButton() {
        initializeWebElements();
        click(proctorDashboardButton);
        return this;
    }

    public Landing clickStudentLookup() {
        initializeWebElements();
        click(studentLookupButton);
        return this;
    }

    public Map<Integer, String> getAllActivations() {
        initializeWebElements();
        Map<Integer, String> activationsMap = new HashMap<>();
        try {
            activationCollection = findElement(ElementBys.ProctorDashboardSearchResultContainer);
            activationsMap = getCollectionTextAttributesByCssSelector(activationCollection, "ccc-activation > div > div.ccc-activation-details > div.ccc-activation-details-student");
        } catch (TimeoutException | NoSuchElementException e) {
            // do nothing
        }
        return activationsMap;
    }

    @Override
    public Map<String, String> getPageElementTextAttributes(Set<String> elementNames) {
        initializeWebElements();
        return super.getPageElementTextAttributes(elementNames);
    }

    public Landing getPrivatePasscode() {
        privatePasscode = findElement(ElementBys.ProctorPrivatePasscode);
        pageElements.put(PRIVATE_PASSCODE, privatePasscode);
        return this;
    }

    public Landing getPublicPasscode() {
        publicPasscode = findElement(ElementBys.ProctorPublicPasscode);
        pageElements.put(PUBLIC_PASSCODE, publicPasscode);
        return this;
    }

    public Landing clickPrivatePasscodeTab() {
        privatePasscodeTab = findElement(ElementBys.ProctorPrivatePasscodeTab);
        privatePasscodeTab.click();
        return this;
    }

    public Landing clickPublicPasscodeTab() {
        publicPasscodeTab = findElement(ElementBys.ProctorPublicPasscodeTab);
        publicPasscodeTab.click();
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home?dev=true").toString();
    }

    public void initializeWebElements() {
        userFirstNameLastNameButton = findElement(ElementBys.LandingFirstNameLastNameButton);
        studentLookupButton = findElement(ElementBys.LandingStudentLookup);
        pageElements.put(USER_FIRST_NAME_LAST_NAME, userFirstNameLastNameButton);
        //proctorDashboardButton = findElement(ElementBys.LandingProctorDashboardButton);
    }

    @Override
    public Landing navigateTo() {
        super.navigateTo();
        initializeWebElements();
        return this;
    }
}