package selenium.framework.helper.webelement.getter;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCollectionValues {
    public static Map<Integer, String> getCollectionTextAttributesByCssSelector(WebElement collection, String cssSelector) {
        return getCollectionValuesForAttributeByCssSelector(collection, cssSelector, new GetElementText());
    }

    public static Map<Integer, String> getCollectionValueAttributesByCssSelector(WebElement collection, String cssSelector) {
        return getCollectionValuesForAttributeByCssSelector(collection, cssSelector, new GetElementValue());
    }

    private static Map<Integer, String> getCollectionValuesForAttributeByCssSelector(WebElement collection, String cssSelector, GetElement getter) {
        Map<Integer, String> map = new HashMap<Integer, String>();

        Integer index = 1;
        for (WebElement element : getRecordsFromCollection(collection, cssSelector)) {
            map.put(index, getter.getValue(element));
            index += 1;
        }
        ;

        return map;
    }

    private static List<WebElement> getRecordsFromCollection(WebElement collection, String cssSelector) {
        List<WebElement> results = new ArrayList<>();
        if (!getRecordsFromCollectionGeneric(results, collection, cssSelector) && !getRecordsFromCollectionCustom(results, collection, cssSelector)) {
            throw new UnsupportedOperationException("getRecordsFromCollection does not support collection type " + collection.getTagName());
        }

        return results;
    }

    private static Boolean getRecordsFromCollectionCustom(List<WebElement> results, WebElement collection, String cssSelector) {
        if (collection.getTagName().equalsIgnoreCase("ccc-grouped-items")) {
            List<WebElement> collectionChildren = collection.findElements(By.className("ccc-grouped-items-group"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }

            return true;
        } else if (collection.getTagName().equalsIgnoreCase("ccc-view-manager-view")) {
            List<WebElement> collectionChildren = collection.findElements(By.tagName("ccc-location-card"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }

            return true;
        } else if (collection.getTagName().equalsIgnoreCase("ccc-facet-list")) {
            List<WebElement> collectionChildren = collection.findElements(By.cssSelector("div.ccc-facet-list-facets > div.ccc-facet-list-facet"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }
            return true;
        } else {
            return false;
        }
    }

    private static Boolean getRecordsFromCollectionGeneric(List<WebElement> results, WebElement collection, String cssSelector) {
        if (collection.getTagName().equalsIgnoreCase("ul") || collection.getTagName().equalsIgnoreCase("ol")) {
            List<WebElement> collectionChildren = collection.findElements(By.tagName("li"));
            if (StringUtils.isNotBlank(cssSelector)) {
                for (WebElement element : collectionChildren) {
                    results.addAll(element.findElements(By.cssSelector(cssSelector)));
                }
            } else {
                results.addAll(collectionChildren);
            }

            return true;
        } else if (collection.getTagName().equalsIgnoreCase("table")) {
            List<WebElement> tableRows = collection.findElements(By.tagName("tr"));
            for (WebElement row : tableRows) {
                List<WebElement> tableEntries = row.findElements(By.tagName("td"));
                for (WebElement tableEntry : tableEntries) {
                    results.addAll(tableEntry.findElements(By.cssSelector(cssSelector)));
                }
            }
            return true;
        } else if (collection.getTagName().equalsIgnoreCase("tr")) {
            List<WebElement> collectionChildren = collection.findElements(By.tagName("td"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }
            return true;
        } else if (collection.getTagName().equalsIgnoreCase("thead")) {
            List<WebElement> collectionChildren = collection.findElements(By.tagName("tr"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }
            return true;
        } else if (collection.getTagName().equalsIgnoreCase("div")) {
            List<WebElement> collectionChildren = collection.findElements(By.tagName("div"));

            for (WebElement element : collectionChildren) {
                results.addAll(element.findElements(By.cssSelector(cssSelector)));
            }
            return true;
        } else {
            return false;
        }
    }
}


