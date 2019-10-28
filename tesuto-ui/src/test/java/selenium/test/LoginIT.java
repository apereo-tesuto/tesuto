package selenium.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import selenium.page.BrowserSession;
import selenium.page.Landing;

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
public class LoginIT implements Loggable {
    @Resource
    BrowserSession browserSession;

    @Value(value = "#{selenium['login.proctor0.password']}")
    String password;
    @Value(value = "#{selenium['login.proctor0.full.name']}")
    String userFirstNameLastName;
    @Value(value = "#{selenium['login.proctor0.name']}")
    String username;

    @Test
    public void LoginWithValidProctorCredentialsThatAuthenticatesSuccessfully() throws InterruptedException {
        log("Testing if a user with valid credentials can authenticate...");
        String expectedSessionPageTitlePostLogin = "Proctor";
        Set<String> elementNames = new HashSet<>();
        elementNames.add(Landing.USER_FIRST_NAME_LAST_NAME);

        browserSession.asLoginPage()
                .navigateTo()
                .completeLogin(username, password);
        Map<String, String> elements = browserSession.asLandingPage()
                .getPageElementTextAttributes(elementNames);

        Assert.assertEquals(expectedSessionPageTitlePostLogin, browserSession.currentSessionPageTitle());
        Assert.assertEquals(userFirstNameLastName.toUpperCase(), StringUtils.trim(elements.get(Landing.USER_FIRST_NAME_LAST_NAME)));

        browserSession.asLandingPage().clickLogoutButton();
    }

    @After
    public void after() {
        log("   ... done!");
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}