package org.ccctc.web.spring.actuator.extension;

import org.ccctc.common.commonidentity.openidc.CryptoBearerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Creates an extension/addition for the Spring Actuator Health endpoint
 * 
 * The CryptoBearerFilter itself, sets up by trying to fetch a cert for each of the auth sources it has been configured. 
 * If it can't fetch a cert, it can start up without a validator for a certain source, which can then result in a 
 * micro-service starting, but unable to accept anything coming in. This health indicator will indicate DOWN when we 
 * don't have a validator configured for each auth endpoint we were given.
 */
@Component
public class CryptoBearerFilterHealthIndicator implements HealthIndicator {
    private final String message_key = "CryptoBearerFilter";
    @Autowired
    private CryptoBearerFilter cbf;

    @Override
    public Health health() {
        if (cbf.isHealthy()) {
            return Health.up().withDetail(message_key, "Configured for all JWK URLs").build();
        }
        return Health.down().withDetail(message_key, "Unable to fetch certificates for all JWK URLs").build();
    }

}
