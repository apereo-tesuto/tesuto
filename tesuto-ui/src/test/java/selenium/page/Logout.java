package selenium.page;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import selenium.framework.PageObject;

import javax.annotation.Resource;

/**
 * @author Eric Stockdale (estockdale@unicon.net))
 * @author Bill Smith (wsmith@unicon.net)
 */
@Component
public class Logout extends PageObject {
    @Resource
    @Value(value = "${base.url}")
    String baseUrl;

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(baseUrl);
        return sb.append("/logout?dev=true").toString();
    }
}
