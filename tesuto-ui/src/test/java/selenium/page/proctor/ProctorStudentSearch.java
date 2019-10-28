package selenium.page.proctor;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class ProctorStudentSearch extends PageObject {
    public static final String STUDENT_SEARCH_ERROR_BIRTHDATE = "errorBirthdate";
    public static final String STUDENT_SEARCH_ERROR_CCCID = "errorCCCId";
    public static final String STUDENT_SEARCH_ERROR_EMAIL = "errorEmail";
    public static final String STUDENT_SEARCH_ERROR_FIRST_NANE = "errorFirstName";
    public static final String STUDENT_SEARCH_ERROR_LAST_NAME = "errorLastName";
    public static final String STUDENT_SEARCH_ERROR_MIDDLE_INITIAL = "errorMiddleInitial";
    public static final String STUDENT_SEARCH_ERROR_PHONE_NUMBER = "errorPhoneNumber";
    public static final String STUDENT_SEARCH_NO_RESULTS = "studentSearchNoResults";
    public static final String STUDENT_SEARCH_RESULT = "studentSearchResult";
    public static final String STUDENT_SEARCH_RESULT_ID = "studentSearchResultID";
    public static final String STUDENT_SEARCH_RESULT_NAME = "studentSearchResultName";
    private static final String ACTIVE_TAB_ADVANCED = "advanced";
    private static final String ACTIVE_TAB_CCCID = "CCCId";
    private static final int TIME_TO_WAIT_FOR_BUTTON_ENABLE = 1000;
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    private WebElement activationListUL;
    private String activeTab;
    private WebElement deactivateButton;
    private WebElement studentSearchAdvancedTab;
    private WebElement studentSearchAgeTextBox;
    private WebElement studentSearchByCCCIdTab;
    private WebElement studentSearchCCCIdTextBox;
    private WebElement studentSearchEmailTextBox;
    private WebElement studentSearchFirstNameTextBox;
    private WebElement studentSearchLastNameTextBox;
    private WebElement studentSearchMiddleInitialTextBox;
    private WebElement studentSearchPhoneNumberTextBox;
    private WebElement studentSearchResultContainer;
    private WebElement studentSearchResultID;
    private WebElement studentSearchResultNameText;
    private WebElement studentSearchSubmitButton;

    public ProctorStudentSearch clickAdvancedSearchTab() {
        studentSearchAdvancedTab = findElement(ElementBys.ProctorStudentSearchAdvancedTab);
        click(studentSearchAdvancedTab);
        activeTab = ACTIVE_TAB_ADVANCED;
        return this;
    }

    public ProctorStudentSearch clickCCCIdSearchTab() {
        studentSearchByCCCIdTab = findElement(ElementBys.ProctorStudentSearchByCCCIdTab);
        click(studentSearchByCCCIdTab);
        activeTab = ACTIVE_TAB_CCCID;
        return this;
    }

    public ProctorStudentSearch clickSearch() {
        waitForClickabilityOfElement(ElementBys.ProctorStudentSearchSubmit);
        click(studentSearchSubmitButton);
        return this;
    }

    public ProctorStudentSearch clickSearchResult(String studentId) {
        Map<Integer, String> searchResultIds = getSearchResultIds();
        int index = 0;
        for (Integer key : searchResultIds.keySet()) {
            if (studentId.equals(searchResultIds.get(key))) {
                index = key;
            }
        }
        WebElement searchResult = findElement(ElementBys.ProctorStudentSearchResult(Integer.toString(index)));
        pageElements.put(STUDENT_SEARCH_RESULT, searchResult);
        click(searchResult);
        return this;
    }

    public ProctorStudentSearch deactivateActivation(int deactivateButtonIndex) {
        deactivateButton = findElement(ElementBys.DeactivateButton(deactivateButtonIndex));
        click(deactivateButton);
        deactivateButton = findElement(By.cssSelector("ccc-modal-deactivate-activation > div > button.btn-primary"));
        click(deactivateButton);
        return this;
    }

    public ProctorStudentSearch doCCCIdSearchWithNoResults(String searchTerm) {
        initializeWebElements();
        setCCCId(searchTerm);
        waitForClickabilityOfElement(ElementBys.ProctorStudentSearchSubmit);
        click(studentSearchSubmitButton);
        pageElements.put(STUDENT_SEARCH_NO_RESULTS, findElement(ElementBys.ProctorStudentSearchNoResults));
        return this;
    }

    public ProctorStudentSearch doFailedSearchByCCCId(String CCCId) {
        initializeWebElements();
        setCCCId(CCCId);
        return submitFailingSearchByCCCId(CCCId);
    }

    public ProctorStudentSearch doFailedSearchByInvalidName(String firstName, String middleInitial, String lastName) {
        initializeWebElements();
        setName(firstName, middleInitial, lastName);
        return submitFailingSearchByName(StringUtils.isEmpty(lastName), middleInitial.length());
    }

    public ProctorStudentSearch doSearchByName(String firstName, String middleInitial, String lastName) {
        initializeWebElements();
        setName(firstName, middleInitial, lastName);
        return this;
    }

    public ProctorStudentSearch doSearchByNameAndAge(String lastName, String age) {
        initializeWebElements();
        setLastName(lastName);
        setAge(age);
        click(studentSearchSubmitButton);

        return this;
    }

    public ProctorStudentSearch doSearchByNameAndEmail(String lastName, String email) {
        initializeWebElements();
        setLastName(lastName);
        setEmail(email);
        click(studentSearchSubmitButton);

        return this;
    }

    public ProctorStudentSearch doSearchByNameAndPhoneNumber(String lastName, String expectedPhoneNumber) {
        initializeWebElements();
        setLastName(lastName);
        setPhoneNumber(expectedPhoneNumber);
        click(studentSearchSubmitButton);

        return this;
    }

    public ProctorStudentSearch doSearchWithNoResults(String firstName, String middleInitial, String lastName) {
        initializeWebElements();
        setName(firstName, middleInitial, lastName);
        click(studentSearchSubmitButton);
        pageElements.put(STUDENT_SEARCH_NO_RESULTS, findElement(ElementBys.ProctorStudentSearchNoResults));
        return this;
    }

    public ProctorStudentSearch enterStudentId(String CCCId) {
        initializeWebElements();
        setCCCId(CCCId);
        return this;
    }

    @Override
    public ProctorStudentSearch executeJavascript(String javascript) {
        return (ProctorStudentSearch) super.executeJavascript(javascript);
    }

    public Map<Integer, String> getActivationStudentSearchResults() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultContainer);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "h3.name");
    }

    public Map<Integer, String> getActivationTitles() {
        activationListUL = findElement(By.cssSelector("ul.ccc-student-profile-activation-list"));
        return getCollectionTextAttributesByCssSelector(activationListUL, "h4.title");
    }

    public ProctorStudentSearch getEmailError() {
        pageElements.put(STUDENT_SEARCH_ERROR_EMAIL, findElement(ElementBys.ProctorStudentSearchErrorEmailText));
        return this;
    }

    public ProctorStudentSearch getPhoneNumberError() {
        pageElements.put(STUDENT_SEARCH_ERROR_PHONE_NUMBER, findElement(ElementBys.ProctorStudentSearchErrorPhoneNumberText));
        return this;
    }

    public Map<Integer, String> getSearchResultAges() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultList);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "ccc-user > div > div > div.ccc-user-data-age > span.ccc-user-data-value");
    }

    public Map<Integer, String> getSearchResultEmails() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultList);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "ccc-user > div > div > div.ccc-user-data-email > span.ccc-user-data-value");
    }

    public Map<Integer, String> getSearchResultIds() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultList);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "ccc-user > div > div > div.ccc-user-data-id > span.ccc-user-data-value");
    }

    public Map<Integer, String> getSearchResultNames() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultList);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "ccc-user > div > h3");
    }

    public Map<Integer, String> getSearchResultPhoneNumbers() {
        studentSearchResultContainer = findElement(ElementBys.ProctorStudentSearchResultList);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "ccc-user > div > div > div.ccc-user-data-phone > span.ccc-user-data-value");
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home/proctor?dev=true").toString();
    }

    private boolean hasEmptyDateField(String birthMonth, String birthDay, String birthYear) {
        return (StringUtils.isEmpty(birthMonth)
                || StringUtils.isEmpty(birthDay)
                || StringUtils.isEmpty(birthYear));
    }

    public void initializeWebElements() {
        if (activeTab != null && activeTab.equals(ACTIVE_TAB_ADVANCED)) {
            studentSearchFirstNameTextBox = findElement(ElementBys.ProctorStudentSearchFirstName);
            studentSearchLastNameTextBox = findElement(ElementBys.ProctorStudentSearchLastName);
            studentSearchMiddleInitialTextBox = findElement(ElementBys.ProctorStudentSearchMiddleInitial);
            studentSearchAgeTextBox = findElement(ElementBys.ProctorStudentSearchAge);
            studentSearchEmailTextBox = findElement(ElementBys.ProctorStudentSearchEmail);
            studentSearchPhoneNumberTextBox = findElement(ElementBys.ProctorStudentSearchPhoneNumber);
            studentSearchSubmitButton = findElement(ElementBys.ProctorStudentSearchSubmit);
        } else {
            studentSearchCCCIdTextBox = findElement(ElementBys.ProctorStudentSearchCCCId);
            studentSearchSubmitButton = findElement(ElementBys.ProctorStudentSearchSubmit);
        }
    }

    private ProctorStudentSearch setAge(String age) {
        setText(studentSearchAgeTextBox, age);
        return this;
    }

    private ProctorStudentSearch setCCCId(String cccId) {
        setText(studentSearchCCCIdTextBox, cccId);
        return this;
    }

    public ProctorStudentSearch setEmail(String email) {
        initializeWebElements();
        setText(studentSearchEmailTextBox, email);
        return this;
    }

    private ProctorStudentSearch setFirstName(String firstName) {
        setText(studentSearchFirstNameTextBox, firstName);
        return this;
    }

    private ProctorStudentSearch setLastName(String lastName) {
        setText(studentSearchLastNameTextBox, lastName);
        return this;
    }

    private ProctorStudentSearch setMiddleInitial(String middleInitial) {
        setText(studentSearchMiddleInitialTextBox, middleInitial);
        return this;
    }

    private ProctorStudentSearch setName(String firstName, String middleInitial, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
        setMiddleInitial(middleInitial);
        return this;
    }

    public ProctorStudentSearch setPhoneNumber(String phoneNumber) {
        initializeWebElements();
        setText(studentSearchPhoneNumberTextBox, phoneNumber);
        return this;
    }

    private ProctorStudentSearch submitFailingSearchByCCCId(String searchTerm) {
        click(studentSearchSubmitButton);
        pageElements.put(STUDENT_SEARCH_ERROR_CCCID, findElement(ElementBys.ProctorStudentSearchByCCCIdErrorText));
        return this;
    }

    private ProctorStudentSearch submitFailingSearchByName(boolean emptyLastName, int middleInitialLength) {
        if (middleInitialLength > 1) {
            pageElements.put(STUDENT_SEARCH_ERROR_MIDDLE_INITIAL, findElement(ElementBys.ProctorStudentSearchErrorMiddleInitialText));
        }
        if (emptyLastName) {
            click(studentSearchSubmitButton);
            pageElements.put(STUDENT_SEARCH_ERROR_LAST_NAME, findElement(ElementBys.ProctorStudentSearchErrorLastNameText));
        }
        return this;
    }

    private ProctorStudentSearch submitSearchByCCCId(String studentId) {
        waitForClickabilityOfElement(ElementBys.ProctorStudentSearchSubmit);
        click(studentSearchSubmitButton);
        pageElements.put(STUDENT_SEARCH_RESULT_NAME, findElement(ElementBys.ProctorStudentSearchResultName(studentId)));
        pageElements.put(STUDENT_SEARCH_RESULT_ID, findElement(ElementBys.ProctorStudentSearchResultId(studentId)));
        return this;
    }

    private ProctorStudentSearch submitSearchByName(String studentId) {
        waitForClickabilityOfElement(ElementBys.ProctorStudentSearchSubmit);
        click(studentSearchSubmitButton);
        pageElements.put(STUDENT_SEARCH_RESULT_NAME, findElement(ElementBys.ProctorStudentSearchResultName(studentId)));
        pageElements.put(STUDENT_SEARCH_RESULT_ID, findElement(ElementBys.ProctorStudentSearchResultId(studentId)));
        return this;
    }

    @Override
    public ProctorStudentSearch waitForDuration(int milliseconds) {
        super.waitForDuration(milliseconds);
        return this;
    }
}