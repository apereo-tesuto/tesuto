package selenium.framework.helper.webelement.getter;

import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GetElementValues {

    public static Map<String, String> getPageElementIdAttributes(Map<String, WebElement> pageElements, Set<String> elementNames) {
        return getPageElementValuesForAttribute(pageElements, elementNames, new GetElementId());
    }

    public static Map<String, String> getPageElementTextAttributes(Map<String, WebElement> pageElements, Set<String> elementNames) {
        return getPageElementValuesForAttribute(pageElements, elementNames, new GetElementText());
    }

    public static Map<String, String> getPageElementValueAttributes(Map<String, WebElement> pageElements, Set<String> elementNames) {
        return getPageElementValuesForAttribute(pageElements, elementNames, new GetElementValue());
    }

    private static Map<String, String> getPageElementValuesForAttribute(Map<String, WebElement> pageElements, Set<String> elementNames, GetElement getter) {
        Map<String, String> map = new HashMap<String, String>();

        elementNames.forEach(element -> {
            if (pageElements.containsKey(element)) {
                map.put(element, getter.getValue(pageElements.get(element)));
            }
        });

        return map;
    }
}
