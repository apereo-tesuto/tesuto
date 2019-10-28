package selenium.framework;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import selenium.framework.helper.webdriver.window.WindowSwitcher;
import selenium.framework.helper.webelement.getter.GetCollectionValues;
import selenium.framework.helper.webelement.getter.GetElementValues;
import selenium.framework.helper.webelement.getter.GetTableElement;
import selenium.page.BrowserSession;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
public abstract class PageObject implements IPageObject {
    protected Map<String, String> elementStyle;
    protected boolean initialized;
    protected Map<String, WebElement> pageElements;
    @Resource
    BrowserSession browserSession;

    @Resource
    WindowSwitcher windowSwitcher;

    public PageObject() {
        pageElements = new HashMap<>();
    }

    @Override
    public void click(WebElement webElement) {
        webElement.click();
    }

    @Override
    public void clickThenSwitchToNewWindow(WebElement webElement) {
        windowSwitcher.clickThenSwitchToNewWindow(webElement);
    }

    protected void closeWindow(String windowHandle) {
        windowSwitcher.closeWindow(windowHandle);
    }

    @Override
    public void deselectByIndex(WebElement webElement, int index) {
        new Select(webElement).deselectByIndex(index);
    }

    @Override
    public void deselectByValue(WebElement webElement, String value) {
        new Select(webElement).deselectByValue(value);
    }

    @Override
    public void deselectByVisibleText(WebElement webElement, String text) {
        new Select(webElement).deselectByVisibleText(text);
    }

    @Override
    public IPageObject executeJavascript(String javascript) {
        if (browserSession.getWebDriver() instanceof JavascriptExecutor) {
            ((JavascriptExecutor) browserSession.getWebDriver()).executeScript(javascript);
        }

        return this;
    }

    @Override
    public WebElement findElement(By by) {
        waitForVisibilityOfElement(by);
        return getWebDriver().findElement(by);
    }

    @Override
    public Map<Integer, String> getCollectionTextAttributesByCssSelector(WebElement collection, String cssSelector) {
        return GetCollectionValues.getCollectionTextAttributesByCssSelector(collection, cssSelector);
    }

    @Override
    public Map<Integer, String> getCollectionValueAttributesByCssSelector(WebElement collection, String cssSelector) {
        return GetCollectionValues.getCollectionValueAttributesByCssSelector(collection, cssSelector);
    }

    @Override
    public Map<String, String> getElementCssValues(WebElement element, Set<String> styleAttributeNames) {
        Map<String, String> map = new HashMap<>();

        styleAttributeNames.forEach(styleAttribute -> map.put(styleAttribute, element.getCssValue(styleAttribute)));

        return map;
    }

    protected String getNewWindow() {
        return windowSwitcher.getNewWindow();
    }

    @Override
    public Map<String, String> getPageElementIdAttributes(Set<String> elementNames) {
        return GetElementValues.getPageElementIdAttributes(pageElements, elementNames);
    }

    @Override
    public Map<String, String> getPageElementTextAttributes(Set<String> elementNames) {
        return GetElementValues.getPageElementTextAttributes(pageElements, elementNames);
    }

    @Override
    public Map<String, String> getPageElementValueAttributes(Set<String> elementNames) {
        return GetElementValues.getPageElementValueAttributes(pageElements, elementNames);
    }

    @Override
    public WebElement getTableElementByRowColumnAndSelector(WebElement table, int tableRow, int tableColumn, String cssSelector) {
        return GetTableElement.getTableElementByRowAndColumn(table, tableRow, tableColumn, cssSelector);
    }

    public WebDriver getWebDriver() {
        return browserSession.getWebDriver();
    }

    @Override
    public IPageObject navigateTo() {
        getWebDriver().get(getUrl());
        initialized = false;
        return this;
    }

    @Override
    public IPageObject refresh() {
        getWebDriver().navigate().refresh();
        return this;
    }

    @Override
    public void selectByIndex(WebElement webElement, int index) {
        new Select(webElement).selectByIndex(index);
    }

    @Override
    public void selectByValue(WebElement webElement, String value) {
        new Select(webElement).selectByValue(value);
    }

    @Override
    public void selectByVisibleText(WebElement webElement, String text) {
        new Select(webElement).selectByVisibleText(text);
    }

    @Override
    public void setText(WebElement webElement, String value) {
        if (StringUtils.isNotBlank(value)) {
            webElement.sendKeys(value);
        } else {
            webElement.clear();
        }
    }

    protected void switchToPreviousWindow() {
        windowSwitcher.switchToPreviousWindow();
    }

    public void waitForClickabilityOfElement(By webElementBy) {
    	//TODO UPDATE, selenium tests were not kept up to date, based on instructions from CCC Techcenter
        //browserSession.getWebDriverWait().until(ExpectedConditions.elementToBeClickable(webElementBy));
    }

    public IPageObject waitForDuration(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void waitForVisibilityOfElement(By webElementBy) {
    	//TODO UPDATE, selenium tests were not kept up to date, based on instructions from CCC Techcenter
        //browserSession.getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(webElementBy));
    }
}