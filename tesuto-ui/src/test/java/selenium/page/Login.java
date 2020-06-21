package selenium.page;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class Login extends PageObject {
    @Resource
    //@Value(value = "#{selenium['base.url']}")
    // Allows you to run selenium tests with something like this: mvn -Pselenium -Dspring.profiles.active=local test -Dbase.url.secure="https://ccc-assess-dev-lb-793631592.us-west-2.elb.amazonaws.com" -Dbase.url="http://ccc-assess-dev-lb-793631592.us-west-2.elb.amazonaws.com" -Dit.test=LoginIT
    //@Value(value = "#{systemProperties['base.url']}")
    @Value(value = "${base.url}")
    String baseUrl;

    private WebElement loginSubmit;
    private WebElement passwordTextBox;
    private WebElement rememberMeCheckBox;
    private WebElement usernameTextBox;

    public Login completeLogin(String username, String password) {
        initializeWebElements();
        setUsername(username);
        setPassword(password);
        return submitLogin();
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/login?dev=true").toString();
    }

    public void initializeWebElements() {
        usernameTextBox = findElement(ElementBys.LoginUsername);
        passwordTextBox = findElement(ElementBys.LoginPassword);
        loginSubmit = findElement(ElementBys.LoginSubmit);
    }

    @Override
    public Login navigateTo() {
        super.navigateTo();
        initializeWebElements();
        return this;
    }

    private Login setPassword(String password) {
        setText(passwordTextBox, password);
        return this;
    }

    private Login setUsername(String username) {
        setText(usernameTextBox, username);
        return this;
    }

    private Login submitLogin() {
        click(loginSubmit);
        return this;
    }
}