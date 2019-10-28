package selenium.page;

import org.springframework.stereotype.Component;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class AssessmentPreviewUpload extends AssessmentUpload {

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/preview/upload?dev=true").toString();
    }

    @Override
    public AssessmentPreviewUpload navigateTo() {
        super.navigateTo();
        return this;
    }
}
