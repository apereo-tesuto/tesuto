package selenium.page.proctor;

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
public class ProctorAssessmentActivationSummary extends PageObject {
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_ACCOMMODATIONS = "summaryAccommodations";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_ACTIVATED = "activated";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_ASSESSMENT_TYPE = "summaryAssessmentType";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_FIRST_ASSESSMENT_FAILURE_TITLE = "summaryFirstAssessmentFailureTitle";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_FIRST_ASSESSMENT_MESSAGE_TEXT = "summaryFirstAssessmentMessage";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_FIRST_ASSESSMENT_SUCCESS_TITLE = "summaryFirstAssessmentSuccessTitle";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_LOCATION = "summaryLocation";
    public static final String ASSESSMENT_ACTIVATION_SUMMARY_STUDENT_NAME = "studentName";
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    private WebElement proctorActivationSummaryAccommodationsContainer;
    private WebElement proctorActivationSummaryAssessmentSuccessText;
    private WebElement proctorActivationSummaryAssessmentTitleText;
    private WebElement proctorActivationSummaryAssessmentTitlesContainer;

    public Map<Integer, String> getActivatedAccommodations() {
        proctorActivationSummaryAccommodationsContainer = findElement(ElementBys.ProctorActivationSummaryAccommodationsContainer);
        return getCollectionTextAttributesByCssSelector(proctorActivationSummaryAccommodationsContainer, "");
    }

    public Map<Integer, String> getActivatedAssessmentTitles() {
        proctorActivationSummaryAssessmentTitlesContainer = findElement(ElementBys.ProctorActivationSummaryAssessmentTitlesContainer);
        return getCollectionTextAttributesByCssSelector(proctorActivationSummaryAssessmentTitlesContainer, "");
    }

    public ProctorAssessmentActivationSummary getSummary(boolean doSuccess) {
        pageElements.put(ASSESSMENT_ACTIVATION_SUMMARY_ACTIVATED, findElement(ElementBys.ProctorActivationSummaryActivatedState));
        pageElements.put(ASSESSMENT_ACTIVATION_SUMMARY_LOCATION, findElement(ElementBys.ProctorActivationSummaryLocation));
        pageElements.put(ASSESSMENT_ACTIVATION_SUMMARY_STUDENT_NAME, findElement(ElementBys.ProctorActivationSummaryStudentName));
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home/proctor?dev=true").toString();
    }
}