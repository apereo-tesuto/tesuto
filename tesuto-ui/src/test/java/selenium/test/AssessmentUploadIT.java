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
import selenium.page.AssessmentPlayer;
import selenium.page.BrowserSession;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/selenium/selenium-context.xml"})
public class AssessmentUploadIT implements Loggable {

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

    @After
    public void after() {
        log("   ... done!");
    }

    @Before
    public void before() {
        login(adminUsername, adminPassword);
    }



    @Override
    public void log(String message) {
        System.out.println(message);
    }

    public void login(String username, String password) {
        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(username, password);
    }

    @Test
    public void uploadingAnAssessmentWithBranchingRulesNavigatesRulesProperly() throws FileNotFoundException {
        log("Testing if the assessment player can successfully navigate an assessment containing multiple branching rules...");

        String filename = "src/test/resources/selenium/assessmentItems/branch.full.zip";
        String assessmentTitle = "CCC Sample";

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
                .clickMultipleChoiceAnswers(3) // branch to section 3
                .clickNext()
                .clickMultipleChoiceAnswers(5) // branch to section 5
                .clickNext()
                .clickMultipleChoiceAnswers(1) // move to section 6
                .clickNext()
                .clickMultipleChoiceAnswers(1) // mote to section 7
                .clickNext()
                .clickMultipleChoiceAnswers(2) // branch to section 9
                .clickNext()
                .clickMultipleChoiceAnswers(1) // branch to exit
                .clickNext()
                .clickOkay();

        // No asserts necessary. If we failed somewhere along the way, the Okay would fail
    }

    @Test
    public void testIfAssessmentVersioningHandlesNewVersionsProperly() throws FileNotFoundException {
        log("Testing if the assessment player supports versioning");

        String filenameV1 = "src/test/resources/selenium/assessmentItems/sqe001-a.zip";
        String filenameV2 = "src/test/resources/selenium/assessmentItems/sqe001-b.zip";
        String assessmentTitle = "SQE Test 001";

        Set<String> elementNames = new HashSet<>();
        elementNames.add(AssessmentPlayer.ASSESSMENT_QUESTION_TEXT);

        // upload v1
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filenameV1)
                .clickUpload();

        // activate v1
        ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, studentId, assessmentTitle, accommodations);
        String proctorPublicPasscode = ActivationHelper.getProctorPublicPasscode(browserSession, proctorUsername, proctorPassword);

        // take v1, get question text
        login(studentUsername, studentPassword);
        int assessmentIndex = ActivationHelper.getAssessmentActivationByAssessmentTitle(browserSession, "SQE Test 001", "Ready");

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

        Map<String, String> elements = browserSession.asAssessmentPlayer()
                .getQuestionText()
                .getPageElementTextAttributes(elementNames);

        String questionTextV1 = elements.get(AssessmentPlayer.ASSESSMENT_QUESTION_TEXT);

        browserSession.asAssessmentPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickOkay();

        // upload v2
        login(adminUsername, adminPassword);
        browserSession.asAssessmentUploadPage()
                .navigateTo()
                .setUploadedFile(filenameV2)
                .clickUpload();

        // activate v2
        ActivationHelper.createStudentActivation(browserSession, proctorUsername, proctorPassword, studentId, assessmentTitle, accommodations);

        // take v2, get question text
        login(studentUsername, studentPassword);
        assessmentIndex = ActivationHelper.getAssessmentActivationByAssessmentTitle(browserSession, "SQE Test 001", "Ready");

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

        elements = browserSession.asAssessmentPlayer()
                .getQuestionText()
                .getPageElementTextAttributes(elementNames);

        String questionTextV2 = elements.get(AssessmentPlayer.ASSESSMENT_QUESTION_TEXT);

        browserSession.asAssessmentPlayer()
                .clickFirstResponse()
                .clickNext()
                .clickOkay();

        // compare v1, v2 question text
        Assert.assertNotEquals("The question text should differ", questionTextV1, questionTextV2);
    }
}
