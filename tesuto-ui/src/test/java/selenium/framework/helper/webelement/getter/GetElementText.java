package selenium.framework.helper.webelement.getter;

import org.openqa.selenium.WebElement;

public class GetElementText implements GetElement {
    @Override
    public String getValue(WebElement webElement) {
        return webElement.getText();
    }
}