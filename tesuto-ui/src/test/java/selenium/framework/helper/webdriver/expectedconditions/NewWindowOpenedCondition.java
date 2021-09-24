package selenium.framework.helper.webdriver.expectedconditions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.Nullable;

public class NewWindowOpenedCondition implements ExpectedCondition<Boolean> {

    private int currentWindowHandleCount;

    public NewWindowOpenedCondition(int currentWindowHandleCount) {
        this.currentWindowHandleCount = currentWindowHandleCount;
    }

    @Nullable
    @Override
    public Boolean apply(
            @Nullable
            WebDriver webDriver) {
        return webDriver.getWindowHandles().size() > currentWindowHandleCount;
    }
}
