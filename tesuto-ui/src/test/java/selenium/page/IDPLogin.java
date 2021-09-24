package selenium.page;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.config.ElementBys;
import selenium.framework.PageObject;

import javax.annotation.Resource;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class IDPLogin extends PageObject {
    @Resource
    @Value(value = "${idp.url}")
    String idpURL;

    @Resource
    @Value(value = "${provider.id}")
    String providerId;

    private WebElement loginSubmit;
    private WebElement passwordTextBox;
    private WebElement usernameTextBox;

    public IDPLogin completeLogin(String username, String password) {
        initializeWebElements();
        setUsername(username);
        setPassword(password);
        return submitLogin();
    }

    private void forgotPassword(String username) {
        // TODO once supported
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(idpURL);
        return sb.append("?providerId=").append(providerId).toString();
    }

    public void initializeWebElements() {
        usernameTextBox = findElement(ElementBys.IDPLoginUsername);
        passwordTextBox = findElement(ElementBys.IDPLoginPassword);
        loginSubmit = findElement(ElementBys.IDPLoginSubmitButton);
    }

    @Override
    public IDPLogin navigateTo() {
        super.navigateTo();
        initializeWebElements();
        return this;
    }

    private IDPLogin setPassword(String password) {
        setText(passwordTextBox, password);
        return this;
    }

    private IDPLogin setUsername(String username) {
        setText(usernameTextBox, username);
        return this;
    }

    private IDPLogin submitLogin() {
        click(loginSubmit);
        return this;
    }
}