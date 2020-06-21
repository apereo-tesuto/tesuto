package selenium.page.proctor;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class ProctorStudentProfile extends PageObject {

    public static final String STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL = "studentProfileActivationPrintButtonLabel";
    public static final String STUDENT_PROFILE_ACTIVATION_STATUS = "studentProfileActivationStatus";
    WebElement activeAssessmentListUL;
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    WebElement newActivationButton;

    public ProctorStudentProfile clickFirstPrintButtonForAssessmentTitleAndCloseNewWindow(String assessmentTitle) {
        WebElement printButton = findFirstPrintButtonForAssessmentTitle(assessmentTitle);
        clickThenSwitchToNewWindow(printButton);
        closeWindow(getNewWindow());
        switchToPreviousWindow();
        return this;
    }

    public ProctorStudentProfile clickNewActivation() {
        initializeWebElements();
        click(newActivationButton);
        return this;
    }

    private int findFirstAssessmentIndexForAssessmentTitle(String assessmentTitle) {
        Map<Integer, String> activeAssessmentsForStudent = getActiveAssessmentTitles();
        int activeAssessmentIndex = 0;
        for (Integer key : activeAssessmentsForStudent.keySet()) {
            if (activeAssessmentsForStudent.get(key).equals(assessmentTitle)) {
                activeAssessmentIndex = key;
                break;
            }
        }
        return activeAssessmentIndex;
    }

    private WebElement findFirstPrintButtonForAssessmentTitle(String assessmentTitle) {
        int activeAssessmentIndex = findFirstAssessmentIndexForAssessmentTitle(assessmentTitle);
        WebElement printButton = findElement(ElementBys.ProctorStudentProfileActivationPrintButton(activeAssessmentIndex));
        return printButton;
    }

    public Map<Integer, String> getActiveAssessmentTitles() {
        activeAssessmentListUL = findElement(ElementBys.ProctorStudentProfileActivationListUL);
        return getCollectionTextAttributesByCssSelector(activeAssessmentListUL, "div.title-row > h4.title");
    }

    public ProctorStudentProfile getFirstAssessmentStatusForAssessmentTitle(String assessmentTitle) {
        int assessmentIndex = findFirstAssessmentIndexForAssessmentTitle(assessmentTitle);
        WebElement assessmentStatus = findElement(ElementBys.ProctorStudentProfileActivationStatus(assessmentIndex));
        pageElements.put(STUDENT_PROFILE_ACTIVATION_STATUS, assessmentStatus);
        return this;

    }

    public ProctorStudentProfile getFirstPrintButtonForAssessmentTitle(String assessmentTitle) {
        WebElement printButton = findFirstPrintButtonForAssessmentTitle(assessmentTitle);
        pageElements.put(STUDENT_PROFILE_ACTIVATION_PRINT_BUTTON_LABEL, printButton);
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("home/studentlookup?dev=true").toString();
    }

    public void initializeWebElements() {
        newActivationButton = findElement(ElementBys.ProctorStudentProfileNewActivationButton);
    }
}
