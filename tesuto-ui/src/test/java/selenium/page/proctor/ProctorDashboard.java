package selenium.page.proctor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class ProctorDashboard extends PageObject {

    public static final String PROCTOR_DASHBOARD_LAST_PROCTORED_LOCATION = "proctorDashboardLastProctoredLocation";
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;
    private WebElement facet;
    private WebElement facetListDiv;
    private WebElement firstResultPrintLink;
    private WebElement firstTestingSite;
    private boolean initialized = false;
    private WebElement lastProctoredLabel;
    private WebElement testingSiteLocationsCards;

    public ProctorDashboard clickFacet(String facetName) {
        int facetIndex = getFacetIndex(facetName);
        facet = findElement(ElementBys.ProctorDashboardFacetByIndex(facetIndex));
        click(facet);
        return this;
    }

    public ProctorDashboard clickFirstResultPrintLinkAndCloseNewWindow() {
        firstResultPrintLink = findElement(ElementBys.ProctorDashboardSearchResultFirstPrintLink);
        clickThenSwitchToNewWindow(firstResultPrintLink);
        closeWindow(getNewWindow());
        switchToPreviousWindow();
        return this;
    }

    public ProctorDashboard clickFirstTestingSite() {
        firstTestingSite = findElement(ElementBys.ProctorDashboardTestSite(1));
        click(firstTestingSite);
        return this;
    }

    public Map<Integer, String> getDashboardCompletedActivationsByTestingLocation() {
        initializeWebElements();
        return getCollectionTextAttributesByCssSelector(testingSiteLocationsCards, "div.ccc-location-card-metric.completed > span:nth-of-type(1)");
    }

    public Map<Integer, String> getDashboardInProgressActivationsByTestingLocation() {
        initializeWebElements();
        return getCollectionTextAttributesByCssSelector(testingSiteLocationsCards, "div.ccc-location-card-metric.in-progress > span:nth-of-type(1)");
    }

    public Map<Integer, String> getDashboardReadyActivationsByTestingLocation() {
        initializeWebElements();
        return getCollectionTextAttributesByCssSelector(testingSiteLocationsCards, "div.ccc-location-card-metric.ready > span:nth-of-type(1)");
    }

    public Map<Integer, String> getDashboardTestingLocationNames() {
        initializeWebElements();
        return getCollectionTextAttributesByCssSelector(testingSiteLocationsCards, "h3.ccc-location-card-name");
    }

    public Map<Integer, String> getDashboardTotalActivationsByTestingLocation() {
        initializeWebElements();
        return getCollectionTextAttributesByCssSelector(testingSiteLocationsCards, "div.ccc-location-card-metric.activations > span:nth-of-type(1)");
    }

    private int getFacetIndex(String facetName) {
        int facetIndex = 0;
        Map<Integer, String> facetNames = getFacetNames();
        for (Integer key : facetNames.keySet()) {
            String currentFacet = facetNames.get(key);
            currentFacet = currentFacet.substring(0, currentFacet.lastIndexOf('(')).trim();
            if (facetName.equals(currentFacet)) {
                facetIndex = key;
                break;
            }
        }
        return facetIndex;
    }

    public Map<Integer, String> getFacetNames() {
        facetListDiv = findElement(ElementBys.ProctorDashboardFacetListContainer);
        return getCollectionTextAttributesByCssSelector(facetListDiv, "div.ccc-facet-list-facet-value");
    }

    public ProctorDashboard getLastProctoredLocation() {
        lastProctoredLabel = findElement(ElementBys.ProctorDashboardLastProctoredLocation);
        pageElements.put(PROCTOR_DASHBOARD_LAST_PROCTORED_LOCATION, lastProctoredLabel.findElement(By.xpath("../h3")));
        return this;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home/proctorlocation?dev=true").toString(); // used to be /home/proctordashboard
    }

    public void initializeWebElements() {
        waitForClickabilityOfElement(ElementBys.ProctorDashboardTestingSiteLocationFirstCard);
        testingSiteLocationsCards = findElement(ElementBys.ProctorDashboardTestingSiteLocationCardsContainer);
    }

    @Override
    public ProctorDashboard navigateTo() {
        super.navigateTo();
        return this;
    }
}
