package org.ccctc.common.commonidentity.saml;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.util.SAMLUtil;

/**
 * Created by Parker Neff - Unicon Inc on 12/2/15.
 */
@Deprecated
public class SpringSAMLHelper {

    public static String getSAMLAssertion() throws SAMLParsingException {
        SAMLCredential creds = (SAMLCredential)SecurityContextHolder.getContext().getAuthentication().getCredentials();

        try {
            return  XMLHelper.nodeToString(SAMLUtil.marshallMessage(creds.getAuthenticationAssertion()));
        } catch (MessageEncodingException e) {
            throw new SAMLParsingException(e.getMessage());
        }

    }
    public static UserIdentity getUserIdentity() {
        return (UserIdentity)SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
