package selenium.page.proctor;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class ProctorDashboardTestCenter extends PageObject {

    public static final String DASHBOARD_FACET_FIRST_ASSESSMENT = "ProctorDashboardFacetFirstAssessment";
    public static final String DASHBOARD_FACET_RESULT_COUNT = "ProctorDashboardFacetResultCount";
    public static final String DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_ID = "ProctorDashboardSearchResultGroup1Student1Id";
    public static final String DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_NAME = "ProctorDashboardSearchResultGroup1Student1Name";
    public static final String DASHBOARD_SEARCH_RESULT_FIRST_HEADER = "ProctorDashboardSearchResultHeader0";
    public static final String DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_ID = "ProctorDashboardSearchResultGroup2Student1Id";
    public static final String DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_NAME = "ProctorDashboardSearchResultGroup2Student1Name";
    public static final String DASHBOARD_SEARCH_RESULT_SECOND_HEADER = "ProctorDashboardSearchResultHeader1";
    @Resource
    @Value(value = "#{selenium['base.url']}")
    String baseUrl;
    private WebElement firstAssessmentFacetCheckBox;
    private WebElement studentSearchResultContainer;
    private WebElement studentSearchResultsDiv;
    private WebElement studentSearchTextBox;

    public ProctorDashboardTestCenter doStudentSearch(String searchName) {
        initializeWebElements();
        setSearchName(searchName);
        return this;
    }

    public ProctorDashboardTestCenter enableFirstStatusFacet() {
        pageElements.put(DASHBOARD_FACET_FIRST_ASSESSMENT, findElement(ElementBys.ProctorDashboardFacetFirstAssessment));
        pageElements.put(DASHBOARD_FACET_RESULT_COUNT, findElement(ElementBys.ProctorDashboardFacetFirstAssessmentResultCount));
        firstAssessmentFacetCheckBox = findElement(ElementBys.ProctorDashboardFacetFirstAssessmentCheckBox);
        click(firstAssessmentFacetCheckBox);
        return this;
    }

    public Map<Integer, String> getDashboardSearchResultHeaderTextValues(int headerIndex) {
        studentSearchResultsDiv = findElement(ElementBys.ProctorDashboardStudentSearchDiv(headerIndex));
        return getCollectionTextAttributesByCssSelector(studentSearchResultsDiv, "div.ccc-activation-details-student");
    }

    public Map<Integer, String> getDashboardSearchResultHeaders() {
        studentSearchResultContainer = findElement(ElementBys.ProctorDashboardSearchResultContainer);
        return getCollectionTextAttributesByCssSelector(studentSearchResultContainer, "div.ccc-grouped-items-group-title > h2:nth-of-type(1)");
    }

    private void getPageElementsForFirstHeaderOfSearchResults() {
        pageElements.put(DASHBOARD_SEARCH_RESULT_FIRST_HEADER, findElement(ElementBys.ProctorDashboardSearchResultHeader0));
        pageElements.put(DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_NAME, findElement(ElementBys.ProctorDashboardSearchResultGroup1Student1Name));
        pageElements.put(DASHBOARD_SEARCH_RESULT_FIRST_GROUP_FIRST_STUDENT_ID, findElement(ElementBys.ProctorDashboardSearchResultGroup1Student1Id));
    }

    public ProctorDashboardTestCenter getPageElementsForSearchWithOneHeaderOfResults() {
        getPageElementsForFirstHeaderOfSearchResults();
        return this;
    }

    public ProctorDashboardTestCenter getPageElementsForSearchWithTwoHeadersOfResults() {
        getPageElementsForFirstHeaderOfSearchResults();
        getPageElementsForSecondHeaderOfSearchResults();
        return this;
    }

    private void getPageElementsForSecondHeaderOfSearchResults() {
        pageElements.put(DASHBOARD_SEARCH_RESULT_SECOND_HEADER, findElement(ElementBys.ProctorDashboardSearchResultHeader1));
        pageElements.put(DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_NAME, findElement(ElementBys.ProctorDashboardSearchResultGroup2Student1Name));
        pageElements.put(DASHBOARD_SEARCH_RESULT_SECOND_GROUP_FIRST_STUDENT_ID, findElement(ElementBys.ProctorDashboardSearchResultGroup2Student1Id));
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/home/proctordashboard?dev=true").toString();
    }

    public void initializeWebElements() {
        studentSearchTextBox = findElement(ElementBys.ProctorDashboardSearch);
    }

    private ProctorDashboardTestCenter setSearchName(String searchName) {
        setText(studentSearchTextBox, searchName);
        return this;
    }

    @Override
    public ProctorDashboardTestCenter waitForDuration(int milliseconds) {
        super.waitForDuration(milliseconds);
        return this;
    }
}
