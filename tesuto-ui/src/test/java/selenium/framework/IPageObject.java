package selenium.framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
interface IPageObject {
    void click(WebElement webElement);

    void clickThenSwitchToNewWindow(WebElement webElement);

    void deselectByIndex(WebElement webElement, int index);

    void deselectByValue(WebElement webElement, String value);

    void deselectByVisibleText(WebElement webElement, String text);

    IPageObject executeJavascript(String javascript);

    WebElement findElement(By by);

    Map<Integer, String> getCollectionTextAttributesByCssSelector(WebElement collection, String cssSelector);

    Map<Integer, String> getCollectionValueAttributesByCssSelector(WebElement collection, String cssSelector);

    Map<String, String> getPageElementIdAttributes(Set<String> elementNames);

    Map<String, String> getPageElementTextAttributes(Set<String> elementNames);

    Map<String, String> getPageElementValueAttributes(Set<String> elementNames);

    Map<String, String> getElementCssValues(WebElement element, Set<String> styleAttributeNames);

    WebElement getTableElementByRowColumnAndSelector(WebElement table, int tableRow, int tableColumn, String cssSelector);

    String getUrl();

    IPageObject navigateTo();

    IPageObject refresh();

    void selectByIndex(WebElement webElement, int index);

    void selectByValue(WebElement webElement, String value);

    void selectByVisibleText(WebElement webElement, String text);

    void setText(WebElement webElement, String value);

    IPageObject waitForDuration(int milliseconds);
}
