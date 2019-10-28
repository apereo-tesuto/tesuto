package selenium.framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
public abstract class BaseBrowserSession {
    @Resource
    private WebDriver webDriver;

    @Resource
    private WebDriverWait webDriverWait;

    public String currentSessionPageContent() {
        return getWebDriver().getPageSource();
    }

    public String currentSessionPageTitle() {
        return getWebDriver().getTitle();
    }

    public void endSession() {
        webDriver.close();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public WebDriverWait getWebDriverWait() {
        return webDriverWait;
    }
}
