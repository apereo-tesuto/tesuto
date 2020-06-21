package selenium.page.proctor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class ProctorAssessmentActivation extends PageObject {
    public static final String PROCTOR_ACTIVATION_ASSESSMENT_TITLE_1 = "proctorActivationAssessmentTitle1";
    public static final String PROCTOR_ACTIVATION_CHOSEN_ASSESSMENTS_ERROR = "chosenAssessmentsError";
    public static final String PROCTOR_ACTIVATION_LOCATION_ERROR = "locationError";
    public static final String PROCTOR_ACTIVATION_SYSTEM_ERROR = "systemError";

    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    private WebElement proctorActivationAccommodationsTextBox;
    private WebElement proctorActivationAddAccommodationsButton;
    private WebElement proctorActivationAssessmentTypeButton;
    private WebElement proctorActivationCancelButton;
    private WebElement proctorActivationCreateButton;
    private WebElement proctorActivationFirstAccommodation;
    private WebElement proctorActivationFirstAssessmentCheckBox;
    private WebElement proctorActivationLocationTextBox;
    private WebElement proctorActivationOtherAccommodation;
    private WebElement proctorActivationOtherAccommodationTextBox;
    private WebElement proctorActivationPaperAssessmentTab;
    private WebElement proctorActivationStudentProfileButton;
    private WebElement printLink;

    public ProctorAssessmentActivation activateAssessment(String assessment, String accommodations) {
        initializeWebElements();

        selectAssessment(assessment);
        return createActivation();
    }

    public ProctorAssessmentActivation activateFirstAssessment(String location, String accommodations) {
        initializeWebElements();
        selectFirstAssessment();
        //selectOnlineAssessment();
        //setLocation(location);
        setAccommodations(accommodations);
        return createActivation();
    }

    public ProctorAssessmentActivation clickPaperAssessments() {
        proctorActivationPaperAssessmentTab = findElement(ElementBys.ProctorActivationPaperTab);
        click(proctorActivationPaperAssessmentTab);
        return this;
    }

    public ProctorAssessmentActivation clickStudentProfileButton() {
        proctorActivationStudentProfileButton = findElement(ElementBys.ProctorActivationStudentProfileButton);
        click(proctorActivationStudentProfileButton);
        return this;
    }

    private ProctorAssessmentActivation createActivation() {
        click(proctorActivationCreateButton);
        return this;
    }

    public ProctorAssessmentActivation doBlankActivation() {
        initializeWebElements();
        click(proctorActivationCreateButton);
        pageElements.put(PROCTOR_ACTIVATION_CHOSEN_ASSESSMENTS_ERROR, findElement(ElementBys.ProctorActivationAssessmentErrorText));
        return this;
    }

    @Override
    public ProctorAssessmentActivation executeJavascript(String javascript) {
        return (ProctorAssessmentActivation) super.executeJavascript(javascript);
    }

    public ProctorAssessmentActivation findFirstAssessmentTitle() {
        initializeWebElements();
        pageElements.put(PROCTOR_ACTIVATION_ASSESSMENT_TITLE_1, findElement(ElementBys.ProctorActivationFirstAssessmentTitle));
        return this;
    }

    public ProctorAssessmentActivation getErrorMessage() {
        pageElements.put(PROCTOR_ACTIVATION_SYSTEM_ERROR, findElement(ElementBys.ProctorActivationSystemError));
        return this;
    }

    public ProctorAssessmentActivation clickPrintLinkAndCloseNewWindow() {
        printLink = findElement(ElementBys.ProctorActivationPrintLink);
        clickThenSwitchToNewWindow(printLink);
        closeWindow(getNewWindow());
        switchToPreviousWindow();
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home/proctor?dev=true").toString();
    }

    public void initializeWebElements() {
        proctorActivationFirstAssessmentCheckBox = findElement(ElementBys.ProctorActivationFirstAssessment);
        proctorActivationCreateButton = findElement(ElementBys.ProctorActivationCreate);
    }

    private void selectAssessment(String assessmentToActivate) {
        WebElement assessmentList = findElement(By.cssSelector("ccc-assessments-list"));
        for (WebElement assessment : assessmentList.findElements(By.tagName("ccc-radio-box"))) {
            String currentAssessment = assessment.findElement(By.cssSelector("label")).getText();
            if (assessmentToActivate.equals(currentAssessment)) {
                assessment.findElement(By.cssSelector("input")).click();
                break;
            }
        }
    }

    private ProctorAssessmentActivation selectFirstAssessment() {
        click(proctorActivationFirstAssessmentCheckBox);
        return this;
    }

    private ProctorAssessmentActivation selectOnlineAssessment() {
        click(proctorActivationAssessmentTypeButton);
        return this;
    }

    private ProctorAssessmentActivation setAccommodations(String accommodations) {
        proctorActivationAddAccommodationsButton = findElement(ElementBys.ProctorActivationAddAccommodationsButton);
        proctorActivationAddAccommodationsButton.click();
        proctorActivationFirstAccommodation = findElement(ElementBys.ProctorActivationFirstAccommodation);
        proctorActivationFirstAccommodation.click();
        proctorActivationOtherAccommodation = findElement(ElementBys.ProctorActivationOtherAccommodation);
        proctorActivationOtherAccommodation.click();
        proctorActivationOtherAccommodationTextBox = findElement(ElementBys.ProctorActivationOtherAccommodationTextBox);
        setText(proctorActivationOtherAccommodationTextBox, accommodations);
        return this;
    }

    private ProctorAssessmentActivation setLocation(String location) {
        setText(proctorActivationLocationTextBox, location);
        return this;
    }
}