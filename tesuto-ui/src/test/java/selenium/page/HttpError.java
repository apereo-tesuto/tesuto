package selenium.page;

import org.openqa.selenium.By;
import org.springframework.stereotype.Component;
import selenium.framework.PageObject;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class HttpError extends PageObject {
    public static final String ERROR_STATUS = "errorStatus";

    public HttpError getErrorStatus() {
        pageElements.put(ERROR_STATUS, findElement(By.cssSelector("h1")));
        return this;
    }

    @Override
    public String getUrl() {
        return null;
    }
}
