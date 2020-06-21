package selenium.page;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class AssessmentUpload extends PageObject {

    WebElement assessmentUploadButton;
    WebElement assessmentUploadFile;
    WebElement assessmentUploadSessionLink;
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;

    public AssessmentUpload clickSessionLink() {
        assessmentUploadSessionLink = findElement(ElementBys.AssessmentUploadSessionLink);
        clickThenSwitchToNewWindow(assessmentUploadSessionLink);
        return this;
    }

    public AssessmentUpload clickUpload() {
        assessmentUploadButton = findElement(ElementBys.AssessmentUploadButton);
        click(assessmentUploadButton);
        waitForVisibilityOfElement(ElementBys.AssessmentUploadSessionLink);
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/upload?dev=true").toString();
    }

    @Override
    public AssessmentUpload navigateTo() {
        super.navigateTo();
        return this;
    }

    public AssessmentUpload setUploadedFile(String fileName) throws FileNotFoundException {
        assessmentUploadFile = findElement(ElementBys.AssessmentUploadFile);
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Sample assessment item file not found!");
        }
        setText(assessmentUploadFile, file.getAbsolutePath());
        return this;
    }
}
