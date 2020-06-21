package org.ccctc.common.commonidentity.openidc.validator;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;



import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class SignatureValidator implements TokenValidator {
    private JWTSigningAndValidationService validator = null;

    public SignatureValidator(String jwkURL) throws JWKSetLoadException {
        try {
            JWKSet set = JWKSet.load(new URL(jwkURL));
            this.validator = new DefaultJWTSigningAndValidationService(new JWKSetKeyStore(set));
        } catch (Exception ex) {
            throw new JWKSetLoadException("Could not load JWK set from " + jwkURL, ex);
        }
    }

    @Override
    public boolean validate(String token) {
        try {
            final SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (new Date().after(claims.getExpirationTime())) {
                log.debug("An expired token was encountered for {}", claims.getSubject());
                return false;
            }

            if (!validator.validateSignature(jwt)) {
                log.debug("A token with an invalid signature was encountered for {}", claims.getSubject());
                return false;
            }

            return true;
        } catch (ParseException ex) {
            log.error("Encountered a parsing exception while attempting to validate a JWT", ex);
            return false;
        }
    }
}
