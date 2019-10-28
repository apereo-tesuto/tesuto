package selenium.framework.helper.webdriver.window;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import selenium.framework.helper.webdriver.expectedconditions.NewWindowOpenedCondition;
import selenium.page.BrowserSession;

import javax.annotation.Resource;
import java.util.Set;

@Component(value = "windowSwitcher")
public class WindowSwitcher {
    @Resource
    BrowserSession browserSession;
    private Set<String> currentPageHandles;
    private Set<String> previousPageHandles;

    public void clickThenSwitchToNewWindow(WebElement webElement) {
        previousPageHandles = browserSession.getWebDriver().getWindowHandles();
        webElement.click();
        waitForWindowToOpen(previousPageHandles.size());
        currentPageHandles = browserSession.getWebDriver().getWindowHandles();
        switchToNewWindow();
    }

    public void closeWindow(String windowHandle) {
        browserSession.getWebDriver().switchTo().window(windowHandle);
        browserSession.getWebDriver().close();
    }

    public String getNewWindow() {
        if (currentPageHandles.size() > previousPageHandles.size()) {
            for (String handle : currentPageHandles) {
                if (!previousPageHandles.contains(handle)) {
                    return handle;
                }
            }
        }

        return null;
    }

    public String getPreviousWindow() {
        if (currentPageHandles.size() > previousPageHandles.size()) {
            String previousHandle = "";
            for (String handle : currentPageHandles) {

                if (previousPageHandles.contains(handle)) {
                    previousHandle = handle;
                } else if (!previousPageHandles.contains(handle)) {
                    return previousHandle;
                }
            }
        }

        return null;
    }

    public void switchToNewWindow() {
        browserSession.getWebDriver().switchTo().window(getNewWindow());
    }

    public void switchToPreviousWindow() {
        browserSession.getWebDriver().switchTo().window(getPreviousWindow());
    }

    public void waitForWindowToOpen(int currentHandles) {
    	//TODO UPDATE, selenium tests were not kept up to date, based on instructions from CCC Techcenter
        //browserSession.getWebDriverWait().until(new NewWindowOpenedCondition(currentHandles));
    }
}
