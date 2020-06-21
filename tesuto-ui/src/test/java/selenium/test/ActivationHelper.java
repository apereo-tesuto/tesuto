package selenium.test;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import selenium.page.BrowserSession;
import selenium.page.Landing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class ActivationHelper {
    /**
     * For a given student and assessment, go through the steps necessary to create an activation.
     *
     * @param browserSession
     * @param proctorUsername
     * @param proctorPassword
     * @param studentId
     * @param assessmentName
     * @param accommodations
     */
    public static void createStudentActivation(BrowserSession browserSession,
                                               String proctorUsername,
                                               String proctorPassword,
                                               String studentId,
                                               String assessmentName,
                                               String accommodations) {
        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(proctorUsername, proctorPassword);

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
                .activateAssessment(assessmentName, accommodations);

        browserSession.asLandingPage()
                .clickLogoutButton();
    }

    /**
     * Given an existing browser session, get the first activation for an assessment title
     *
     * @param browserSession
     * @param assessmentTitle
     * @return
     */
    public static int getAssessmentActivationByAssessmentTitle(BrowserSession browserSession,
                                                               String assessmentTitle,
                                                               String assessmentStatus) {
        int assessmentIndex = 0;

        Map<Integer, String> assessmentTitles = new HashMap<>();
        Map<Integer, String> assessmentLabels = new HashMap<>();
        try {
            assessmentLabels = browserSession.asStudentDashboard()
                    .getActiveAssessmentLabels();
            assessmentTitles = browserSession.asStudentDashboard()
                    .getActiveAssessmentTitles();
        } catch (TimeoutException | NoSuchElementException e) {
            // do nothing
        }

        for (Integer key : assessmentTitles.keySet()) {
            if (assessmentLabels.get(key).equalsIgnoreCase(assessmentStatus) && assessmentTitles.get(key).trim().equals(assessmentTitle)) {
                assessmentIndex = key;
                break;
            }
        }

        return assessmentIndex;
    }

    /**
     * For a given proctor, get their latest private passcode
     *
     * @param browserSession
     * @param proctorUsername
     * @param proctorPassword
     * @return
     */
    public static String getProctorPrivatePasscode(BrowserSession browserSession,
                                                   String proctorUsername,
                                                   String proctorPassword) {
        String privatePasscode = null;
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.PRIVATE_PASSCODE);

        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(proctorUsername, proctorPassword);

        Map<String, String> elements = browserSession.asLandingPage()
                .clickDropdown()
                .clickPasscodes()
                .clickPrivatePasscodeTab()
                .getPrivatePasscode()
                .getPageElementTextAttributes(elementNames);

        privatePasscode = elements.get(Landing.PRIVATE_PASSCODE);

        return privatePasscode;
    }

    /**
     * For a given proctor, get their latest public passcode
     *
     * @param browserSession
     * @param proctorUsername
     * @param proctorPassword
     * @return
     */
    public static String getProctorPublicPasscode(BrowserSession browserSession,
                                                  String proctorUsername,
                                                  String proctorPassword) {
        String publicPasscode = null;
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.PUBLIC_PASSCODE);

        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(proctorUsername, proctorPassword);

        Map<String, String> elements = browserSession.asLandingPage()
                .clickDropdown()
                .clickPasscodes()
                .getPublicPasscode()
                .getPageElementTextAttributes(elementNames);

        publicPasscode = elements.get(Landing.PUBLIC_PASSCODE);

        return publicPasscode;
    }
}