package selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class StudentDashboard extends PageObject {

    public static final String ASSESSMENT_INSTRUCTIONS_TITLE = "assessmentInstructionsTitle";
    public static final String MODAL_ERROR_MESSAGE = "modalErrorMessage";
    public static final String STUDENT_CCCID = "studentCCCID";
    public static final String STUDENT_DASHBOARD_PROCTOR_CODE_ERROR = "studentDashboardProctorCodeError";
    public static final String USER_FIRST_NAME_LAST_NAME = "userFirstNameLastName";
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;

    private WebElement activeAssessmentListUL;
    private WebElement assessment;
    private WebElement assessmentInstructionsTitleText;
    private WebElement assessmentReadyFlag;
    private WebElement beginAssessmentBackButton;
    private WebElement beginAssessmentButton;
    private WebElement continueButton;
    private WebElement firstAssessment;
    private WebElement historicalAssessmentListUL;
    private WebElement modalErrorMessageText;
    private WebElement myAssessmentsButton;
    private WebElement pilotAcceptanceCheckbox;
    private WebElement proctorCodeTextBox;
    private WebElement rulesReadyButton;
    private WebElement studentCCCID;
    private WebElement studentInstructionsNextButton;
    private WebElement userFirstNameLastNameButton;

    public StudentDashboard acknowledgePilot() {
        pilotAcceptanceCheckbox = findElement(By.id("step-2"));
        click(pilotAcceptanceCheckbox);
        studentInstructionsNextButton = findElement(ElementBys.StudentDashboardInstructionsNextButton);
        click(studentInstructionsNextButton);
        return this;
    }

    public StudentDashboard beginAssessment() {
        beginAssessmentButton = findElement(ElementBys.StudentDashboardBeginAssessment);
        clickThenSwitchToNewWindow(beginAssessmentButton);
        return this;
    }

    public StudentDashboard clickAssessment(int assessmentIndex) {
        assessment = findElement(ElementBys.StudentAssessment(assessmentIndex));
        click(assessment);
        return this;
    }

    public StudentDashboard clickBeginAssessmentBackButton() {
        beginAssessmentBackButton = findElement(ElementBys.StudentDashboardBeginAssessmentBackButton);
        click(beginAssessmentBackButton);
        return this;
    }

    public StudentDashboard clickFirstAssessment() {
        firstAssessment = findElement(ElementBys.StudentDashboardFirstAssessment);
        click(firstAssessment);
        return this;
    }

    public StudentDashboard clickNextOnDoNotExitPage() {
        studentInstructionsNextButton = findElement(ElementBys.StudentDashboardInstructionsNextButton);
        click(studentInstructionsNextButton);
        return this;
    }

    public StudentDashboard clickNextOnNavigationPage() {
        studentInstructionsNextButton = findElement(ElementBys.StudentDashboardInstructionsNextButton);
        click(studentInstructionsNextButton);
        return this;
    }

    public StudentDashboard clickNextOnStructurePage() {
        studentInstructionsNextButton = findElement(ElementBys.StudentDashboardInstructionsNextButton);
        click(studentInstructionsNextButton);
        return this;
    }

    public StudentDashboard clickNextOnWelcomePage() {
        studentInstructionsNextButton = findElement(ElementBys.StudentDashboardInstructionsNextButton);
        click(studentInstructionsNextButton);
        return this;
    }

    public StudentDashboard clickReadyOnRulesPage() {
        rulesReadyButton = findElement(ElementBys.StudentDashboardInstructionsReadyButton);
        click(rulesReadyButton);
        return this;
    }

    public StudentDashboard closeAssessment() {
        closeWindow(getNewWindow());
        switchToPreviousWindow();
        return this;
    }

    public StudentDashboard completeAssessment() {
        switchToPreviousWindow();
        myAssessmentsButton = findElement(ElementBys.StudentDashboardMyAssessmentsButton);
        click(myAssessmentsButton);
        return this;
    }

    public StudentDashboard enterBlankProctorCode() {
        proctorCodeTextBox = findElement(ElementBys.StudentDashboardProctorCode);
        setText(proctorCodeTextBox, "");
        continueButton = findElement(ElementBys.StudentDashboardContinue);
        click(continueButton);
        pageElements.put(STUDENT_DASHBOARD_PROCTOR_CODE_ERROR, findElement(ElementBys.StudentDashboardProctorCodeError));
        return this;
    }

    public StudentDashboard enterProctorCode(String proctorCode) {
        proctorCodeTextBox = findElement(ElementBys.StudentDashboardProctorCode);
        setText(proctorCodeTextBox, proctorCode);
        continueButton = findElement(ElementBys.StudentDashboardContinue);
        click(continueButton);
        return this;
    }

    public Map<Integer, String> getActiveAssessmentLabels() {
        activeAssessmentListUL = findElement(ElementBys.StudentDashboardCurrentAssessmentsTable);
        return getCollectionTextAttributesByCssSelector(activeAssessmentListUL, "ccc-student-activation > div.header > div > div > span > span.text");
    }

    public Map<Integer, String> getActiveAssessmentStatuses() {
        activeAssessmentListUL = findElement(ElementBys.StudentDashboardCurrentAssessmentsTable);
        return getCollectionTextAttributesByCssSelector(activeAssessmentListUL, "ccc-student-activation > span.label");
    }

    public Map<Integer, String> getActiveAssessmentTitles() {
        activeAssessmentListUL = findElement(ElementBys.StudentDashboardCurrentAssessmentsTable);
        return getCollectionTextAttributesByCssSelector(activeAssessmentListUL, "ccc-student-activation > div > div > div > h4");
    }

    public StudentDashboard getAssessmentInstructionsTitle() {
        assessmentInstructionsTitleText = findElement(ElementBys.StudentDashboardAssessmentInstructionsTitleText);
        pageElements.put(ASSESSMENT_INSTRUCTIONS_TITLE, assessmentInstructionsTitleText);
        return this;
    }

    public Map<Integer, String> getHistoricalAssessmentTitles() {
        historicalAssessmentListUL = findElement(ElementBys.StudentDashboardHistoricalAssessmentsTable);
        return getCollectionTextAttributesByCssSelector(historicalAssessmentListUL, "ccc-student-activation > a > h4");
    }

    public StudentDashboard getModalErrorMessage() {
        modalErrorMessageText = findElement(ElementBys.StudentDashboardModalErrorText);
        pageElements.put(MODAL_ERROR_MESSAGE, modalErrorMessageText);
        return this;
    }

    @Override
    public Map<String, String> getPageElementTextAttributes(Set<String> elementNames) {
        initializeWebElements();
        return super.getPageElementTextAttributes(elementNames);
    }

    public StudentDashboard getPrintIcon(int assessmentIndex) {
        findElement(ElementBys.StudentAssessmentPrintIcon(assessmentIndex));
        return this;
    }

    public StudentDashboard getProctorCodeError() {
        pageElements.put(STUDENT_DASHBOARD_PROCTOR_CODE_ERROR, findElement(ElementBys.StudentDashboardProctorCodeError));
        return this;
    }

    public StudentDashboard getStudentCCCID() {
        studentCCCID = findElement(ElementBys.StudentDashboardStudentCCCID);
        pageElements.put(StudentDashboard.STUDENT_CCCID, studentCCCID);
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/student").toString();
    }

    private void initializeWebElements() {
        userFirstNameLastNameButton = findElement(ElementBys.LandingFirstNameLastNameButton);
        pageElements.put(USER_FIRST_NAME_LAST_NAME, userFirstNameLastNameButton);
    }
}
