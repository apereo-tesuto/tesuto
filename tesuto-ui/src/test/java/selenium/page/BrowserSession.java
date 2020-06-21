package selenium.page;

import org.springframework.stereotype.Component;
import selenium.framework.BaseBrowserSession;
import selenium.page.proctor.ProctorAssessmentActivation;
import selenium.page.proctor.ProctorAssessmentActivationSummary;
import selenium.page.proctor.ProctorDashboard;
import selenium.page.proctor.ProctorDashboardTestCenter;
import selenium.page.proctor.ProctorStudentProfile;
import selenium.page.proctor.ProctorStudentSearch;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component(value = "browserSession")
public class BrowserSession extends BaseBrowserSession {
    @Resource
    AssessmentPlayer assessmentPlayer;
    @Resource
    AssessmentPreviewPlayer assessmentPreviewPlayer;
    @Resource
    AssessmentPreviewUpload assessmentPreviewUpload;
    @Resource
    AssessmentUpload assessmentUpload;
    @Resource
    HttpError httpError;
    @Resource
    IDPLogin idpLogin;
    @Resource
    Landing landing;
    @Resource
    Login login;
    @Resource
    Logout logout;
    @Resource
    ProctorAssessmentActivation proctorAssessmentActivation;
    @Resource
    ProctorAssessmentActivationSummary proctorAssessmentActivationSummary;
    @Resource
    ProctorDashboard proctorDashboard;
    @Resource
    ProctorStudentProfile proctorStudentProfile;
    @Resource
    ProctorStudentSearch proctorStudentSearch;
    @Resource
    StudentDashboard studentDashboard;
    @Resource
    ProctorDashboardTestCenter testCenter;

    public AssessmentPlayer asAssessmentPlayer() {
        return assessmentPlayer;
    }

    public AssessmentPreviewPlayer asAssessmentPreviewPlayer() {
        return assessmentPreviewPlayer;
    }

    public AssessmentPreviewUpload asAssessmentPreviewUploadPage() {
        return assessmentPreviewUpload;
    }

    public AssessmentUpload asAssessmentUploadPage() {
        return assessmentUpload;
    }

    public HttpError asErrorPage() {
        return httpError;
    }

    public IDPLogin asIDPLoginPage() {
        return idpLogin;
    }

    public Landing asLandingPage() {
        return landing;
    }

    public Login asLoginPage() {
        return login;
    }

    public Logout asLogoutPage() {
        return logout;
    }

    public ProctorAssessmentActivation asProctorAssessmentActivationPage() {
        return proctorAssessmentActivation;
    }

    public ProctorAssessmentActivationSummary asProctorAssessmentActivationSummaryPage() {
        return proctorAssessmentActivationSummary;
    }

    public ProctorDashboard asProctorDashboard() {
        return proctorDashboard;
    }

    public ProctorDashboardTestCenter asProctorDashboardTestCenter() {
        return testCenter;
    }

    public ProctorStudentProfile asProctorStudentProfilePage() {
        return proctorStudentProfile;
    }

    public ProctorStudentSearch asProctorStudentSearchPage() {
        return proctorStudentSearch;
    }

    public StudentDashboard asStudentDashboard() {
        return studentDashboard;
    }
}