package selenium.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import selenium.framework.PageObject;
import selenium.page.BrowserSession;
import selenium.page.StudentDashboard;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/selenium/selenium-context.xml"})
public class StudentIT implements Loggable {
    String accommodations = "Squirrels.";
    String adminPassword = "password";
    String adminUsername = "admin";
    @Resource
    BrowserSession browserSession;
    String location = "777";
    @Value(value = "#{selenium['login.proctor0.password']}")
    String proctorPassword;
    @Value(value = "#{selenium['login.proctor0.name']}")
    String proctorUsername;
    @Value(value = "#{selenium['student1.id']}")
    String studentId;
    @Value(value = "#{selenium['login.student1.password']}")
    String studentPassword;
    @Value(value = "#{selenium['login.student1.name']}")
    String studentUsername;

    private void activateAndLaunchPaperPencilAssessment(String studentId) throws FileNotFoundException {
        String filename = "src/test/resources/selenium/assessmentItems/sqe001-pp.zip";
        String assessmentTitle = "SQE Test 001";

        login(adminUsername, adminPassword);
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();

        login(proctorUsername, proctorPassword);

        browserSession.asLandingPage()
                .navigateTo()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(studentId)
                .clickSearch();

        browserSession.asProctorStudentSearchPage()
                .clickSearchResult(studentId);

        browserSession.asProctorStudentProfilePage()
                .clickNewActivation();

        browserSession.asProctorAssessmentActivationPage()
                .clickPaperAssessments()
                .activateAssessment(assessmentTitle, accommodations)
                .clickPrintLinkAndCloseNewWindow();
    }

    @Test
    public void blankProctorCodeShowsError() throws FileNotFoundException {
        log("Testing if student assessment proctor code screen displays errors for blank codes...");

        String assessmentTitle = "jca 1 test";
        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear.zip";

        int firstReadyAssessmentIndex = getFirstReadyAssessmentIndex(assessmentTitle, filename);

        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.STUDENT_DASHBOARD_PROCTOR_CODE_ERROR);

        Map<String, String> elements = browserSession.asStudentDashboard()
                .clickAssessment(firstReadyAssessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterBlankProctorCode()
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("An error message should be present.", elements.get(StudentDashboard.STUDENT_DASHBOARD_PROCTOR_CODE_ERROR));
    }

    private int getDeactivateButtonIndex(String assessmentTitle) {
        int deactivateButtonIndex = 0;

        Map<Integer, String> activationTitles = new TreeMap<>();
        try {
            activationTitles = browserSession.asProctorStudentSearchPage()
                    .getActivationTitles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Integer key : activationTitles.keySet()) {
            if (activationTitles.get(key).equals(assessmentTitle)) {
                deactivateButtonIndex = key;
                break;
            }
        }
        return deactivateButtonIndex;
    }

    private int getFirstAssessmentIndexForTitleWithStatus(String assessmentTitle, String status) {
        int firstReadyAssessmentIndex = 0;

        Map<Integer, String> assessmentLabels = new HashMap<>();
        Map<Integer, String> assessmentTitles = new HashMap<>();
        try {
            assessmentLabels = browserSession.asStudentDashboard()
                    .getActiveAssessmentLabels();
            assessmentTitles = browserSession.asStudentDashboard()
                    .getActiveAssessmentTitles();
        } catch (TimeoutException | NoSuchElementException e) {
            // do nothing
        }

        for (Integer key : assessmentLabels.keySet()) {
            if (assessmentLabels.get(key).equalsIgnoreCase(status) && assessmentTitles.get(key).trim().equals(assessmentTitle)) {
                firstReadyAssessmentIndex = key;
                break;
            }
        }

        return firstReadyAssessmentIndex;
    }

    private int getFirstReadyAssessmentIndex(String assessmentTitle, String filename) throws FileNotFoundException {
        int firstReadyAssessmentIndex = getFirstAssessmentIndexForTitleWithStatus(assessmentTitle, "Ready");
        if (firstReadyAssessmentIndex == 0) {
            uploadAssessment(filename);
            ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, studentId, assessmentTitle, accommodations);
            login();
            firstReadyAssessmentIndex = getFirstAssessmentIndexForTitleWithStatus(assessmentTitle, "Ready");
        }
        return firstReadyAssessmentIndex;
    }

    @Test
    public void invalidProctorCodeShowsError() throws FileNotFoundException {
        log("Testing if student assessment proctor code screen displays errors for invalid codes...");

        String assessmentTitle = "jca 1 test";
        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear.zip";

        int firstReadyAssessmentIndex = getFirstReadyAssessmentIndex(assessmentTitle, filename);

        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.MODAL_ERROR_MESSAGE);

        Map<String, String> elements = browserSession.asStudentDashboard()
                .clickAssessment(firstReadyAssessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode("invalidCode")
                .getModalErrorMessage()
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("An error message should be present.", elements.get(StudentDashboard.MODAL_ERROR_MESSAGE));
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Before
    public void login() {
        login(studentUsername, studentPassword);
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
    public void paperPencilActivationDisplaysIconInProgressAndIsUnclickable() throws FileNotFoundException {
        log("Testing if student dashboard shows paper&pencil activation properly...");

        String assessmentTitle = "SQE Test 001";

        activateAndLaunchPaperPencilAssessment(studentId);

        login();

        int assessmentIndex = getFirstAssessmentIndexForTitleWithStatus(assessmentTitle, "In Progress");

        browserSession.asStudentDashboard()
                .clickAssessment(assessmentIndex)
                .getPrintIcon(assessmentIndex); // will fail if we navigate away or if the print icon isn't present
    }

    @Test
    public void repeatedInvalidProctorPasscodeLocksOutUser() throws FileNotFoundException {
        log("Testing if student assessment proctor code screen locks out user after repeated failed attempts...");

        String assessmentTitle = "jca 1 test";
        String tooManyAttemptsError = "Too many attempts. This activation has been locked for 3 minutes. Please try again later.";
        String invalidPasscodeError = "Invalid passcode. Please try again or ask the proctor to enter their private passcode.";
        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear.zip";

        int firstReadyAssessmentIndex = getFirstReadyAssessmentIndex(assessmentTitle, filename);

        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.STUDENT_DASHBOARD_PROCTOR_CODE_ERROR);

        browserSession.asStudentDashboard()
                .clickAssessment(firstReadyAssessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage();

        Map<String, String> elements;
        for (int badCodeCount = 1; badCodeCount <= 6; badCodeCount++) {
            elements = browserSession.asStudentDashboard()
                    .enterProctorCode("12345" + badCodeCount)
                    .getProctorCodeError()
                    .getPageElementTextAttributes(elementNames);
            if (badCodeCount < 6) {
                Assert.assertEquals("The error message should be \"invalid passcode\"", invalidPasscodeError, elements.get(StudentDashboard.STUDENT_DASHBOARD_PROCTOR_CODE_ERROR));
            } else {
                Assert.assertEquals("The error message should be \"too many attempts\"", tooManyAttemptsError, elements.get(StudentDashboard.STUDENT_DASHBOARD_PROCTOR_CODE_ERROR));
            }
        }

        // Deactivate the locked-out one so we don't try to use it in other tests

        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(proctorUsername, proctorPassword);

        browserSession.asLandingPage()
                .navigateTo()
                .clickStudentLookup();

        browserSession.asProctorStudentSearchPage()
                .clickCCCIdSearchTab()
                .enterStudentId(studentId)
                .clickSearch()
                .clickSearchResult(studentId);

        int deactivateButtonIndex = getDeactivateButtonIndex(assessmentTitle);

        browserSession.asProctorStudentSearchPage()
                .deactivateActivation(deactivateButtonIndex);
    }

    @Test
    public void studentCCIDDisplaysInHeader() {
        log("Testing if the student's CCCID displays properly in the header...");

        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.STUDENT_CCCID);

        Map<String, String> elements = browserSession.asStudentDashboard()
                .getStudentCCCID()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals("Displayed CCCID should match student's id", studentId, elements.get(StudentDashboard.STUDENT_CCCID));
    }

    @Test
    public void studentDashboardCanSuccessfullyCompleteAnAssessment() throws FileNotFoundException {
        log("Testing if student can successfully complete an assessment...");

        String assessmentTitle = "jca 1 test";
        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear.zip";

        int firstReadyAssessmentIndex = getFirstReadyAssessmentIndex(assessmentTitle, filename);
        String proctorPublicPasscode = ActivationHelper.getProctorPublicPasscode(browserSession, proctorUsername, proctorPassword);

        login();

        browserSession.asStudentDashboard()
                .clickAssessment(firstReadyAssessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode(proctorPublicPasscode)
                .beginAssessment();

        browserSession.asAssessmentPlayer()
                .completeBasicAssessment()
                .clickOkay();
    }

    @Test
    public void studentShouldNotHaveAccessToProctorActivationPage() {
        log("Testing if student can access proctor assessment activation...");

        testIfAccessIsForbiddenToResource(browserSession.asProctorAssessmentActivationPage());

        browserSession.asStudentDashboard()
                .navigateTo(); // so that we can log out in @After
    }

    @Test
    public void studentShouldNotHaveAccessToProctorAssessmentActivationSummaryPage() {
        log("Testing if student can access proctor activation summary...");

        testIfAccessIsForbiddenToResource(browserSession.asProctorAssessmentActivationSummaryPage());

        browserSession.asStudentDashboard()
                .navigateTo(); // so that we can log out in @After
    }

    @Test
    public void studentShouldNotHaveAccessToProctorDashboard() {
        log("Testing if student can access proctor dashboard...");

        testIfAccessIsForbiddenToResource(browserSession.asProctorDashboard());

        browserSession.asStudentDashboard()
                .navigateTo(); // so that we can log out in @After
    }

    @Test
    public void studentShouldNotHaveAccessToProctorStudentSearchPage() {
        log("Testing if student can access proctor student search page...");

        testIfAccessIsForbiddenToResource(browserSession.asProctorStudentSearchPage());

        browserSession.asStudentDashboard()
                .navigateTo(); // so that we can log out in @After
    }

    private void testIfAccessIsForbiddenToResource(PageObject resource) {
        resource.navigateTo();

        String actualURL = browserSession.getWebDriver().getCurrentUrl();
        String expectedURL = browserSession.asStudentDashboard().getUrl();

        Assert.assertEquals("The student should have been returned to the student dashboard", expectedURL, actualURL);
    }

    @Test
    public void testIfStudentCanCompleteDemoContent() throws FileNotFoundException {
        log("Testing if a student can complete demo content...");

        String filename = "src/test/resources/selenium/assessmentItems/demo.zip";
        String assessmentTitle = "CCC Sample Demo";

        login(adminUsername, adminPassword);

        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();

        ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, studentId, assessmentTitle, accommodations);
        String proctorPublicPasscode = ActivationHelper.getProctorPublicPasscode(browserSession, proctorUsername, proctorPassword);

        login(studentUsername, studentPassword);
        int assessmentIndex = ActivationHelper.getAssessmentActivationByAssessmentTitle(browserSession, assessmentTitle, "Ready");

        browserSession.asStudentDashboard()
                .clickAssessment(assessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode(proctorPublicPasscode)
                .beginAssessment();

        browserSession.asAssessmentPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickFirstCheckbox()
                .answerText()
                .clickNext()
                .answerText()
                .clickFirstResponse()
                .clickFirstInlineChoice()
                .clickNext()
                .checkFirstCheckableTableElement()
                .clickNext()
                .clickFirstInlineChoice()
                .clickNext()
                .answerTextarea()
                .clickNext()
                .clickOkay();
    }

    @Test
    public void testIfStudentCanPauseAndResumeDemoContent() throws FileNotFoundException {
        log("Testing if a student can pause and resume demo content...");

        String filename = "src/test/resources/selenium/assessmentItems/demo.zip";
        String assessmentTitle = "CCC Sample Demo";

        login(adminUsername, adminPassword);

        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();

        ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, studentId, assessmentTitle, accommodations);
        String proctorPublicPasscode = ActivationHelper.getProctorPublicPasscode(browserSession, proctorUsername, proctorPassword);

        login(studentUsername, studentPassword);
        int assessmentIndex = ActivationHelper.getAssessmentActivationByAssessmentTitle(browserSession, assessmentTitle, "Ready");

        browserSession.asStudentDashboard()
                .clickAssessment(assessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode(proctorPublicPasscode)
                .beginAssessment();

        browserSession.asAssessmentPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickPause()
                .clickEndSession();

        String proctorPrivatePasscode = ActivationHelper.getProctorPrivatePasscode(browserSession, proctorUsername, proctorPassword);

        login(studentUsername, studentPassword);
        assessmentIndex = ActivationHelper.getAssessmentActivationByAssessmentTitle(browserSession, assessmentTitle, "In Progress");

        browserSession.asStudentDashboard()
                .clickAssessment(assessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode(proctorPrivatePasscode)
                .beginAssessment();

        browserSession.asAssessmentPlayer()
                .clickFirstCheckbox()
                .answerText()
                .clickNext()
                .answerText()
                .clickFirstResponse()
                .clickFirstInlineChoice()
                .clickNext()
                .checkFirstCheckableTableElement()
                .clickNext()
                .clickFirstInlineChoice()
                .clickNext()
                .answerTextarea()
                .clickNext()
                .clickOkay();
    }

    private void uploadAssessment(String filename) throws FileNotFoundException {
        String adminPassword = "password";
        String adminUsername = "admin";

        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(adminUsername, adminPassword);

        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filename)
                .clickUpload();
    }

    @Test
    public void validProctorCodeAllowsStartingAnAssessment() throws FileNotFoundException {
        log("Testing if a valid proctor passcode allows student assessment access...");

        String assessmentTitle = "jca 1 test";
        String filename = "src/test/resources/selenium/assessmentItems/Simple3QuestionLinear.zip";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.ASSESSMENT_INSTRUCTIONS_TITLE);

        String proctorPublicCode = ActivationHelper.getProctorPublicPasscode(browserSession, proctorUsername, proctorPassword);
        browserSession.asLandingPage()
                .clickLogoutButton();

        login();
        int firstReadyAssessmentIndex = getFirstReadyAssessmentIndex(assessmentTitle, filename);
        Map<String, String> elements = browserSession.asStudentDashboard()
                .clickAssessment(firstReadyAssessmentIndex)
                .clickNextOnWelcomePage()
                .acknowledgePilot()
                .clickNextOnStructurePage()
                .clickNextOnNavigationPage()
                .clickNextOnDoNotExitPage()
                .clickReadyOnRulesPage()
                .enterProctorCode(proctorPublicCode)
                .getAssessmentInstructionsTitle()
                .getPageElementTextAttributes(elementNames);

        Assert.assertNotNull("The instructions title should not be empty", elements.get(StudentDashboard.ASSESSMENT_INSTRUCTIONS_TITLE));
    }
}
