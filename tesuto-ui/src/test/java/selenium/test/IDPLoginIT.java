package selenium.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import selenium.page.BrowserSession;
import selenium.page.Landing;
import selenium.page.StudentDashboard;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/selenium/selenium-context.xml"})
public class IDPLoginIT implements Loggable {
    @Resource
    BrowserSession browserSession;
    @Value(value = "#{selenium['login.idp.proctor0.full.name']}")
    String proctorFirstNameLastName;
    @Value(value = "#{selenium['login.idp.proctor0.password']}")
    String proctorPassword;
    @Value(value = "#{selenium['login.idp.proctor0.name']}")
    String proctorUsername;
    @Value(value = "${login.idp.student0.full.name}")
    String studentFirstNameLastName;
    @Value(value = "${login.idp.student0.password}")
    String studentPassword;
    @Value(value = "${login.idp.student0.name}")
    String studentUsername;

    @Ignore // TODO: Fix this when we know how to handle multiple sessions
    @Test
    public void LoginWithValidProctorCredentialsThatAuthenticatesSuccessfully() throws InterruptedException {
        log("Testing if a proctor user with valid IDP credentials can authenticate...");
        String expectedSessionPageTitlePostLogin = "CCC Assessment Home";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.USER_FIRST_NAME_LAST_NAME);

        browserSession.asIDPLoginPage()
                .navigateTo()
                .completeLogin(proctorUsername, proctorPassword)
                .waitForDuration(5000);

        Assert.assertEquals(expectedSessionPageTitlePostLogin, browserSession.currentSessionPageTitle());

        Map<String, String> elements = browserSession.asLandingPage()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals(proctorFirstNameLastName.toUpperCase(), StringUtils.trim(elements.get(Landing.USER_FIRST_NAME_LAST_NAME)));

    }

    @Test
    public void LoginWithValidStudentCredentialsThatAuthenticatesSuccessfully() throws InterruptedException {
        log("Testing if a student user with valid IDP credentials can authenticate...");
        String expectedSessionPageTitlePostLogin = "My Assessments";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(StudentDashboard.USER_FIRST_NAME_LAST_NAME);

        browserSession.asIDPLoginPage()
                .navigateTo()
                .completeLogin(studentUsername, studentPassword)
                .waitForDuration(5000); // Give the UI a chance to update the page title

        Assert.assertEquals(expectedSessionPageTitlePostLogin, browserSession.currentSessionPageTitle());

        Map<String, String> elements = browserSession.asStudentDashboard()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals(studentFirstNameLastName.toUpperCase(), StringUtils.trim(elements.get(StudentDashboard.USER_FIRST_NAME_LAST_NAME)));

    }

    @After
    public void after() {
        browserSession.asLandingPage()
                .clickLogoutButton();
        log("   ... done!");
    }

    private void debugCookies() {
        Set<Cookie> cookies = browserSession.getWebDriver().manage().getCookies();
        if (cookies.size() == 0) {
            System.out.println("NO COOKIES!");
        }
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
            System.out.println(cookie.getPath());
            System.out.println(cookie.getValue());
        }
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}