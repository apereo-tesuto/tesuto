package selenium.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import selenium.page.BrowserSession;
import selenium.page.Landing;
import selenium.page.proctor.ProctorAssessmentActivation;
import selenium.page.proctor.ProctorAssessmentActivationSummary;
import selenium.page.proctor.ProctorDashboard;
import selenium.page.proctor.ProctorDashboardTestCenter;
import selenium.page.proctor.ProctorStudentProfile;
import selenium.page.proctor.ProctorStudentSearch;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/selenium/selenium-context.xml"})
public class ProctorIT implements Loggable {
    private static final String ACTIVATION_FAILED_MESSAGE = "Activation NOT CREATED due to an unexpected server error: Unexpected Server Error";
    private static final String ACTIVATION_SUCCESS_MESSAGE = "Activation created successfully";
    private static final String CHOSEN_ASSESSMENTS_ERROR = "Please select at least one assessment to activate.";
    private static final String LOCATION_ERROR = "A location is required. (locationId = integer)";
    String accommodations = "Squirrels.";
    String adminPassword = "password";
    String adminUsername = "admin";
    @Resource
    BrowserSession browserSession;
    String location = "Cuyamaca College Test Center";
    @Value(value = "#{selenium['login.proctor0.password']}")
    String proctorPassword;
    @Value(value = "#{selenium['login.proctor0.name']}")
    String proctorUsername;
    String simulateFailure = "FORCE_SERVER_ERROR = 500;";
    @Value(value = "#{selenium['student0.id']}")
    String student0Id;
    @Value(value = "#{selenium['student0.name']}")
    String student0Name;
    @Value(value = "#{selenium['student1.id']}")
    String student1Id;

    private Set<String> buildAssessmentSummaryElementNames(boolean doSuccess) {
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorAssessmentActivationSummary.ASSESSMENT_ACTIVATION_SUMMARY_ACTIVATED);
        elementNames.add(ProctorAssessmentActivationSummary.ASSESSMENT_ACTIVATION_SUMMARY_STUDENT_NAME);
        elementNames.add(ProctorAssessmentActivationSummary.ASSESSMENT_ACTIVATION_SUMMARY_LOCATION);
        return elementNames;
    }

    private Set<String> buildDashboardSearchElementNames() {
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_HEADER);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_NAME);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_ID);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_HEADER);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_NAME);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_ID);
        return elementNames;
    }

    @Test
    public void dateDropdownTodayProperlyFiltersActivations() {
        log("Testing if the date dropdown filters activations properly");

        // using default location and default date dropdown value, "Today"
        // if these change, this test will need to be updated to select the appropriate values.

        Map<Integer, String> activations = browserSession.asLandingPage()
                .getAllActivations();

        int preActivationCount = activations.keySet().size();

        proctorCanSuccessfullyActivateStudent();

        browserSession.asLandingPage().navigateTo();

        activations = browserSession.asLandingPage()
                .getAllActivations();

        int postActivationCount = activations.keySet().size();

        Assert.assertEquals("post count should match pre + 1", preActivationCount + 1, postActivationCount);
    }

    @Test
    public void emptyAssessmentActivationShowsRequiredFields() {
        log("Testing if an empty assessment activation shows required fields...");
        studentSearchByNameFindsSingleStudentForSpecificSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student0Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorAssessmentActivation.PROCTOR_ACTIVATION_CHOSEN_ASSESSMENTS_ERROR);
        elementNames.add(ProctorAssessmentActivation.PROCTOR_ACTIVATION_LOCATION_ERROR);

        Map<String, String> elements = browserSession.asProctorAssessmentActivationPage()
                .doBlankActivation()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Missing assessment error should match", CHOSEN_ASSESSMENTS_ERROR, elements.get(ProctorAssessmentActivation.PROCTOR_ACTIVATION_CHOSEN_ASSESSMENTS_ERROR));
    }

    @Test
    public void invalidStudentSearchNameShowsErrorMessages() {
        log("Testing if submitting an invalid student search name produces the expected messages...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_FIRST_NANE);
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_MIDDLE_INITIAL);
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_LAST_NAME);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doFailedSearchByInvalidName("", "aa", "")
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("A \"last name required\" error message is present", elements.get(ProctorStudentSearch.STUDENT_SEARCH_ERROR_LAST_NAME));
        Assert.assertNotNull("A \"middle initial length\" error message is present", elements.get(ProctorStudentSearch.STUDENT_SEARCH_ERROR_MIDDLE_INITIAL));
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Before
    public void login() {
        login(proctorUsername, proctorPassword);
        //browserSession.asLoginPage()
        //        .navigateTo()
        //        .completeLogin(proctorUsername, proctorPassword);
        browserSession.asLandingPage()
                .navigateTo();
    }

    public void login(String username, String password) {
        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(username, password);
    }

    @After
    public void logout() {
        log("   ... done!\n");
        browserSession.asLandingPage()
                .clickLogoutButton();
    }

    @Test
    public void proctorCanLaunchPaperPencilAssessmentFromActivationSummary() throws FileNotFoundException {
        log("Testing if a pencil/paper activation shows the reprint button when launched from activation summary...");

        String filename = "src/test/resources/selenium/assessmentItems/sqe001-pp.zip";
        String assessmentTitle = "SQE Test 001";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL);

        login(adminUsername, adminPassword);
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();

        login();

        browserSession.asLandingPage()
                .navigateTo()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(student1Id)
                .clickSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student1Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        browserSession.asProctorAssessmentActivationPage()
                .clickPaperAssessments()
                .activateAssessment(assessmentTitle, accommodations)
                .clickPrintLinkAndCloseNewWindow()
                .clickStudentProfileButton();

        Map<String, String> elements = browserSession.asProctorStudentProfilePage()
                .getFirstPrintButtonForAssessmentTitle(assessmentTitle)
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("print button should be \"RePrint\"", "RePrint", elements.get(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL));
    }

    @Test
    public void proctorCanLaunchPaperPencilAssessmentFromStudentProfile() throws FileNotFoundException {
        log("Testing if a pencil/paper activation shows the reprint button when launched from student profile...");

        String filename = "src/test/resources/selenium/assessmentItems/sqe001-pp.zip";
        String assessmentTitle = "SQE Test 001";

        final String expectedStatusReady = "READY";
        final String expectedStatusInProgress = "IN_PROGRESS";
        final String expectedRePrint = "RePrint";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL);
        elementNames.add(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_STATUS);

        login(adminUsername, adminPassword);
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();

        login();

        browserSession.asLandingPage()
                .navigateTo()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(student1Id)
                .clickSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student1Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        browserSession.asProctorAssessmentActivationPage()
                .clickPaperAssessments()
                .activateAssessment(assessmentTitle, accommodations)
                .clickStudentProfileButton();

        Map<String, String> elements = browserSession.asProctorStudentProfilePage()
                .getFirstAssessmentStatusForAssessmentTitle(assessmentTitle)
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Status should be " + expectedStatusReady, expectedStatusReady, elements.get(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_STATUS));

        elements = browserSession.asProctorStudentProfilePage()
                .clickFirstPrintButtonForAssessmentTitleAndCloseNewWindow(assessmentTitle)
                .getFirstPrintButtonForAssessmentTitle(assessmentTitle)
                .getFirstAssessmentStatusForAssessmentTitle(assessmentTitle)
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("print button should be " + expectedRePrint, expectedRePrint, elements.get(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL));
        Assert.assertEquals("Status should be " + expectedStatusInProgress, expectedStatusInProgress, elements.get(ProctorStudentProfile.STUDENT_PROFILE_ACTIVATION_STATUS));
    }

    @Test
    public void proctorCanRegenerateTheirPasscodes() {
        log("Testing if a proctor can regenerate their passcodes...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.PUBLIC_PASSCODE);
        Map<String, String> elements = browserSession.asLandingPage()
                .clickDropdown()
                .clickPasscodes()
                .getPublicPasscode()
                .getPageElementTextAttributes(elementNames);

        String publicPasscodePre = elements.get(Landing.PUBLIC_PASSCODE);

        elementNames = new HashSet<>();
        elementNames.add(Landing.PRIVATE_PASSCODE);

        elements = browserSession.asLandingPage()
                .clickPrivatePasscodeTab()
                .getPrivatePasscode()
                .getPageElementTextAttributes(elementNames);

        String privatePasscodePre = elements.get(Landing.PRIVATE_PASSCODE);

        elements = browserSession.asLandingPage()
                .clickGeneratePrivatePasscode()
                .clickGenerateNewPasscode()
                .getPrivatePasscode()
                .getPageElementTextAttributes(elementNames);

        String privatePasscodePost = elements.get(Landing.PRIVATE_PASSCODE);

        elementNames = new HashSet<>();
        elementNames.add(Landing.PUBLIC_PASSCODE);

        elements = browserSession.asLandingPage()
                .clickPublicPasscodeTab()
                .clickGeneratePublicPasscode()
                .clickGenerateNewPasscode()
                .getPublicPasscode()
                .getPageElementTextAttributes(elementNames);

        String publicPasscodePost = elements.get(Landing.PUBLIC_PASSCODE);

        Assert.assertNotEquals("Public keys before and after generation should not match", publicPasscodePre, publicPasscodePost);
        Assert.assertNotEquals("Private keys before and after generation should not match", privatePasscodePre, privatePasscodePost);
    }

    @Test
    @Ignore //TODO: Fix this once we are pointing at real/reasonable data
    public void proctorCanSearchForStudentOnDashboard() {
        log("Testing if a proctor can search for a student on the dashboard...");
        Set<String> elementNames = buildDashboardSearchElementNames();

        Map<Integer, String> searchResults = browserSession.asProctorDashboardTestCenter()
                .doStudentSearch("Sam")
                .getPageElementsForSearchWithTwoHeadersOfResults()
                .getDashboardSearchResultHeaderTextValues(1);

        Assert.assertEquals("There should be one result for the first header", 1, searchResults.size());

        searchResults = browserSession.asProctorDashboardTestCenter()
                .getDashboardSearchResultHeaderTextValues(2);

        Assert.assertEquals("There should be one result for the second header", 1, searchResults.size());

        Map<String, String> elements = browserSession.asProctorDashboardTestCenter()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("The first header title should match", "ENGLISH", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_HEADER));
        Assert.assertEquals("The first student in the first group should have expected name", "Samuelson, Sammy", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_NAME));
        Assert.assertEquals("The first student in the first group should have expected id", "AAA4315", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_ID));
        Assert.assertEquals("The second header title should match", "ESL", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_HEADER));
        Assert.assertEquals("The first student in the second group should have expected name", "Samuelson, Sammy", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_NAME));
        Assert.assertEquals("The first student in the second group should have expected id", "AAA4315", elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_ID));
    }

    @Test
    public void proctorCanSuccessfullyActivateStudent() {
        log("Testing if a Proctor can successfully activate an assessment for a student...");
        boolean doSuccessScenario = true;
        studentSearchByNameFindsSingleStudentForSpecificSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student0Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorAssessmentActivation.PROCTOR_ACTIVATION_ASSESSMENT_TITLE_1);

        Map<String, String> elements = browserSession.
                asProctorAssessmentActivationPage()
                .findFirstAssessmentTitle()
                .getPageElementTextAttributes(elementNames);

        String firstTestTitle = StringUtils.trim(elements.get(ProctorAssessmentActivation.PROCTOR_ACTIVATION_ASSESSMENT_TITLE_1));

        browserSession.asProctorAssessmentActivationPage().activateFirstAssessment(location, accommodations);

        elementNames = buildAssessmentSummaryElementNames(doSuccessScenario);

        elements = browserSession
                .asProctorAssessmentActivationSummaryPage()
                .getSummary(doSuccessScenario)
                .getPageElementTextAttributes(elementNames);

        Map<Integer, String> assessmentTitles = browserSession.asProctorAssessmentActivationSummaryPage().getActivatedAssessmentTitles();

        Map<Integer, String> actualAccommodations = browserSession.asProctorAssessmentActivationSummaryPage().getActivatedAccommodations();

        Assert.assertEquals("Student name should match", student0Name, elements.get(ProctorAssessmentActivationSummary.ASSESSMENT_ACTIVATION_SUMMARY_STUDENT_NAME));
        Assert.assertTrue("There should be only one activated assessment", assessmentTitles.size() == 1);
        Assert.assertEquals("The activated assessment title should match", firstTestTitle, assessmentTitles.get(1));
        Assert.assertEquals("The location should match", location, elements.get(ProctorAssessmentActivationSummary.ASSESSMENT_ACTIVATION_SUMMARY_LOCATION));
        Assert.assertTrue("There should be two accommodations (plus a description for \"Other\")", actualAccommodations.size() == 3);
        Assert.assertEquals("The \"Other\" description should match)", accommodations, actualAccommodations.get(actualAccommodations.size()));
    }

    @Test
    public void proctorDashboardAllowsProctorToPrintPaperAssessment() throws FileNotFoundException {
        log("Testing if proctor dashboard filters paper assessments and allows proctor to print");
        Set<String> elementNames = new HashSet<>();

        String filename = "src/test/resources/selenium/assessmentItems/sqe001-pp.zip";
        String assessmentTitle = "SQE Test 001";

        login(adminUsername, adminPassword);
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();
        login();

        browserSession.asLandingPage()
                .navigateTo()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(student1Id)
                .clickSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student1Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        browserSession.asProctorAssessmentActivationPage()
                .clickPaperAssessments()
                .activateAssessment(assessmentTitle, accommodations);

        browserSession.asProctorDashboard()
                .navigateTo()
                .clickFacet("Paper")
                .clickFirstResultPrintLinkAndCloseNewWindow();

        // TODO: 3/31/16 add code to "complete" assessment once we can score paper/pencil
    }

    @Ignore // TODO: Complete this test once we have real activation data
    @Test
    public void proctorDashboardDisplaysConsistentActivationCountsPerLocation() {
        log("Testing if the dashboard displays the right activation counts per test center");

        Map<Integer, String> testingLocationNames = browserSession.asProctorDashboard()
                .getDashboardTestingLocationNames();

        Map<Integer, String> totalActivations = browserSession.asProctorDashboard()
                .getDashboardTotalActivationsByTestingLocation();

        Map<Integer, String> readyActivations = browserSession.asProctorDashboard()
                .getDashboardReadyActivationsByTestingLocation();

        Map<Integer, String> inProgressActivations = browserSession.asProctorDashboard()
                .getDashboardInProgressActivationsByTestingLocation();

        Map<Integer, String> completedActivations = browserSession.asProctorDashboard()
                .getDashboardCompletedActivationsByTestingLocation();

        for (Integer key : testingLocationNames.keySet()) {
            System.out.println(testingLocationNames.get(key) + ":" +
                    " total: " + totalActivations.get(key) +
                    " ready: " + readyActivations.get(key) +
                    " inProgress: " + inProgressActivations.get(key) +
                    " completed: " + completedActivations.get(key));

            // for each location, compare activation stats with totals on Test Center view; they should match
        }

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorDashboard.PROCTOR_DASHBOARD_LAST_PROCTORED_LOCATION);

        Map<String, String> elements = browserSession.asProctorDashboard()
                .getLastProctoredLocation()
                .getPageElementTextAttributes(elementNames);

        for (String key : elements.keySet()) {
            System.out.println(key + " -> " + elements.get(key));
        }
    }

    @Test
    public void proctorDashboardStudentSearchFacetsProperlyFilterResults() {
        log("Testing if proctor dashboard facets properly filter results...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_HEADER);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_FACET_FIRST_ASSESSMENT);
        elementNames.add(ProctorDashboardTestCenter.DASHBOARD_FACET_RESULT_COUNT);

        ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, "A12345", "jca 1 test", "");

        login();

        Map<String, String> elements = browserSession.asProctorDashboardTestCenter()
                .doStudentSearch("Sam")
                .waitForDuration(2000)
                .enableFirstStatusFacet()
                .getPageElementsForSearchWithOneHeaderOfResults()
                .getPageElementTextAttributes(elementNames);

        String facetName = elements.get(ProctorDashboardTestCenter.DASHBOARD_FACET_FIRST_ASSESSMENT);
        String searchHeaderName = elements.get(ProctorDashboardTestCenter.DASHBOARD_SEARCH_RESULT_FIRST_HEADER);
        String facetResultCount = elements.get(ProctorDashboardTestCenter.DASHBOARD_FACET_RESULT_COUNT);
        Assert.assertEquals("Facet type and search result header should match", searchHeaderName.trim().toUpperCase(), facetName.trim().toUpperCase()); // TODO: fix me; headers don't always match chosen facets

        Map<Integer, String> searchResults = browserSession.asProctorDashboardTestCenter()
                .getDashboardSearchResultHeaders();

        Assert.assertEquals("There should only be one header in the search results", 1, searchResults.size());

        searchResults = browserSession.asProctorDashboardTestCenter()
                .getDashboardSearchResultHeaderTextValues(1);

        int facetResultCountInt = Integer.valueOf(facetResultCount.substring(2, StringUtils.lastIndexOf(facetResultCount, " ")));

        Assert.assertEquals("There number of results should match the facet result count", facetResultCountInt, searchResults.size());
    }

    @Test
    public void proctorPasscodesConformToPattern() {
        log("Testing if proctor passcodes conform to the appropriate patterns...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.PUBLIC_PASSCODE);
        Map<String, String> elements = browserSession.asLandingPage()
                .clickDropdown()
                .clickPasscodes()
                .getPublicPasscode()
                .getPageElementTextAttributes(elementNames);

        String publicPasscode = elements.get(Landing.PUBLIC_PASSCODE);

        elementNames = new HashSet<>();
        elementNames.add(Landing.PRIVATE_PASSCODE);
        elements = browserSession.asLandingPage()
                .clickPrivatePasscodeTab()
                .getPrivatePasscode()
                .getPageElementTextAttributes(elementNames);

        String privatePasscode = elements.get(Landing.PRIVATE_PASSCODE);

        final String publicPasscodeRegex = "^[\\d[A-Z]]{6}$";
        final String privatePasscodeRegex = "^PR[\\d[A-Z]]{6}$";

        Pattern publicPattern = Pattern.compile(publicPasscodeRegex);
        Pattern privatePattern = Pattern.compile(privatePasscodeRegex);

        Assert.assertTrue("Public key conforms to regex " + publicPasscodeRegex, publicPattern.matcher(publicPasscode).find());
        Assert.assertTrue("Private key conforms to regex " + privatePasscodeRegex, privatePattern.matcher(privatePasscode).find());
    }

    @Test
    public void searchByNameAndAgeProducesResultsWithMatchingAge() {
        log("Testing if searching by age in addition to name returns appropriate results...");

        String expectedAge = "19";
        String lastName = "a";

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> ages = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchByNameAndAge(lastName, expectedAge)
                .getSearchResultAges();

        for (Integer key : ages.keySet()) {
            Assert.assertEquals("age should match expected age", expectedAge, ages.get(key));
        }
    }

    @Test
    public void searchByNameAndEmailProducesResultsWithMatchingEmail() {
        log("Testing if searching by email in addition to name returns appropriate results...");

        String lastName = "a";
        String expectedEmail = "sammyrules@gmail.com";

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> emails = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchByNameAndEmail(lastName, expectedEmail)
                .getSearchResultEmails();

        for (Integer key : emails.keySet()) {
            Assert.assertEquals("email addresses should match", expectedEmail, emails.get(key));
        }
    }

    @Test
    public void searchByNameAndPhoneNumberProducesResultsWithMatchingPhoneNumbers() {
        log("Testing if searching by phone number in addition to name returns appropriate results...");

        String lastName = "a";
        String expectedPhoneNumber = "7187239876";
        String expectedPhoneNumberFormatted = "(718) 723-9876";

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> phoneNumbers = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchByNameAndPhoneNumber(lastName, expectedPhoneNumber)
                .getSearchResultPhoneNumbers();

        for (Integer key : phoneNumbers.keySet()) {
            Assert.assertEquals("phone numbers should match", expectedPhoneNumberFormatted, phoneNumbers.get(key));
        }
    }

    @Test
    public void studentAssessmentActivationShouldHandleFailuresGracefully() {
        log("Testing if a failure during activation causes the system to produce reasonable error messages...");
        boolean doSuccessScenario = false;
        studentSearchByNameFindsSingleStudentForSpecificSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(student0Id);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorAssessmentActivation.PROCTOR_ACTIVATION_SYSTEM_ERROR);

        Map<String, String> elements = browserSession.asProctorAssessmentActivationPage()
                .executeJavascript(simulateFailure)
                .activateFirstAssessment(location, accommodations)
                .getErrorMessage()
                .getPageElementTextAttributes(elementNames);

        String expectedErrorMessage = "An unexpected server error occurred.";
        Assert.assertEquals("The error message should match", expectedErrorMessage, elements.get(ProctorAssessmentActivation.PROCTOR_ACTIVATION_SYSTEM_ERROR));
    }

    @Test
    public void studentSearchByCCCIdFindsStudentSuccessfully() {
        log("Testing if searching by CCC ID results in finding the appropriate student...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_RESULT_NAME);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> searchResultNames = browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(student0Id)
                .clickSearch()
                .getSearchResultNames();

        Map<Integer, String> searchResultIds = browserSession.asProctorStudentSearchPage()
                .getSearchResultIds();

        Assert.assertTrue("There should only be one result", searchResultNames.keySet().size() == 1);
        Assert.assertEquals("Student id should match id from search", student0Id, StringUtils.trim(searchResultIds.get(1)));
        Assert.assertEquals("Student name should match name associated with student id " + student0Id, student0Name, StringUtils.trim(searchResultNames.get(1)));
    }

    @Test
    public void studentSearchByEmptyCCCIdDisplaysRequiredField() {
        log("Testing if searching for a blank CCC ID shows an error message...");
        String invalidCCCId = "";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_CCCID);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .doFailedSearchByCCCId(invalidCCCId)
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("A \"CCCID is required\" message should be present", elements.get(ProctorStudentSearch.STUDENT_SEARCH_ERROR_CCCID));
    }

    @Test
    public void studentSearchByInvalidCCCIdReturnsNoResults() {
        log("Testing if searching for an invalid CCC ID results in an appropriate message being shown...");
        String invalidCCCId = "invalidCCCId";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_NO_RESULTS);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .waitForDuration(2000)
                .doCCCIdSearchWithNoResults(invalidCCCId)
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("A \"no results found\" message should be present", elements.get(ProctorStudentSearch.STUDENT_SEARCH_NO_RESULTS));
    }

    @Test
    public void studentSearchByNameFindsMultipleStudentsForSimpleSearch() {
        log("Testing if searching for simple names produces multiple results...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_RESULT_NAME);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> searchResultNames = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchByName("a", "", "a")
                .clickSearch()
                .getSearchResultNames();

        Assert.assertTrue("There should be multiple student search results", searchResultNames.size() > 1);
    }

    @Test
    public void studentSearchByNameFindsNoResultsForCrazyName() {
        log("Testing if searching for unknown names produces no results...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_NO_RESULTS);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchWithNoResults("asdf", "", "asdf")
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("A \"no results found\" message should be present", elements.get(ProctorStudentSearch.STUDENT_SEARCH_NO_RESULTS));
    }

    @Test
    public void studentSearchByNameFindsSingleStudentForSpecificSearch() {
        log("Testing if searching for a specific valid student name produces a single accurate, clickable search result...");
        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_RESULT_NAME);

        browserSession.asLandingPage()
                .clickStudentLookup();

        Map<Integer, String> searchResultNames = browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab()
                .doSearchByName("Sammy", "", "Samuelson")
                .clickSearch()
                .getSearchResultNames();

        Map<Integer, String> searchResultIds = browserSession.asProctorStudentSearchPage()
                .getSearchResultIds();

        Assert.assertTrue("There should only be one result", searchResultNames.keySet().size() == 1);
        Assert.assertEquals("Student id should match id from search", student0Id, StringUtils.trim(searchResultIds.get(1)));
        Assert.assertEquals("Student name should match name associated with student id " + student0Id, student0Name, StringUtils.trim(searchResultNames.get(1)));
    }

    @Test(expected = org.openqa.selenium.TimeoutException.class)
    public void studentSearchEmailFieldRejectsMalformedEmailAddresses() {
        log("Testing if the email address field on student search shows errors for incomplete email addresses...");

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_EMAIL);

        String emailAddress = "a@a.aa";
        String expectedErrorMessage = "Please provide a valid email address.";

        browserSession.asLandingPage()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab();

        for (int index = 0; index < emailAddress.length(); index++) {
            char emailAddressCharacter = emailAddress.charAt(index);
            Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                    .setEmail(String.valueOf(emailAddressCharacter))
                    .getEmailError()
                    .getPageElementTextAttributes(elementNames);
            Assert.assertEquals("error message should match", expectedErrorMessage, elements.get(ProctorStudentSearch.STUDENT_SEARCH_ERROR_EMAIL));
        }
    }

    @Test(expected = org.openqa.selenium.TimeoutException.class)
    public void studentSearchPhoneNumberFieldRejectsShortPhoneNumbers() {
        log("Testing if the phone number field on student search shows errors for numbers shorter than 10 digits...");

        Set<String> elementNames = new HashSet<>();
        elementNames.add(ProctorStudentSearch.STUDENT_SEARCH_ERROR_PHONE_NUMBER);

        String phoneNumber = "1234567890";
        String expectedErrorMessage = "Please enter a valid 10 digit phone number";

        browserSession.asLandingPage()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickAdvancedSearchTab();

        for (int index = 0; index < phoneNumber.length(); index++) {
            char phoneNumberDigit = phoneNumber.charAt(index);
            Map<String, String> elements = browserSession.asProctorStudentSearchPage()
                    .setPhoneNumber(String.valueOf(phoneNumberDigit))
                    .getPhoneNumberError()
                    .getPageElementTextAttributes(elementNames);
            Assert.assertEquals("error message should match", expectedErrorMessage, elements.get(ProctorStudentSearch.STUDENT_SEARCH_ERROR_PHONE_NUMBER));
        }
    }
}