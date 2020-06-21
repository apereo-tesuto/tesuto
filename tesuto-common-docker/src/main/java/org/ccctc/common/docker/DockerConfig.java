package org.ccctc.common.docker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@Documented
// Uses DOCKER_HOST and DOCKER_CERT_PATH env vars to instantiate docker client
// or uri attribute
public @interface DockerConfig {

    RegistryConfig registry();

    // TODO if hosts is blank, get an NPE. having a scheme of unix:// seems to
    // work but I don't know why. Need to research
    String host() default "";
    String image();
    int[] exposedPorts();
    int[] mappedPorts();
    boolean waitForPorts() default false;
    String waitForLogMessage() default "";
    ContainerStartMode startMode() default ContainerStartMode.FOR_EACH_TEST;
    enum ContainerStartMode {
        ONCE, FOR_EACH_TEST
    }
}
