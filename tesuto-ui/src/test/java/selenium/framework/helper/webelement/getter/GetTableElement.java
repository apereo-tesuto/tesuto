package selenium.framework.helper.webelement.getter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class GetTableElement {
    public static WebElement getTableElementByRowAndColumn(WebElement table, int rowIndex, int columnIndex, String cssSelector) {
        WebElement element = null;

        if (table.getTagName().equalsIgnoreCase("table")) {
            List<WebElement> tableRows = table.findElements(By.tagName("tr"));
            WebElement selectedRow = tableRows.get(rowIndex);
            List<WebElement> tableColumns = selectedRow.findElements(By.tagName("td"));
            element = tableColumns.get(columnIndex).findElement(By.cssSelector(cssSelector));
        }

        return element;
    }
}
