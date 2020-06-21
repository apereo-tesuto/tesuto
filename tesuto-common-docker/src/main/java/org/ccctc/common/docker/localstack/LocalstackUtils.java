package org.ccctc.common.docker.localstack;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;




import cloud.localstack.docker.Container;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class LocalstackUtils {
    private static final Pattern READY_TOKEN = Pattern.compile("Ready\\.");

    private static Container localStackContainer;

    public void startLocalstackContainer() {
        // now create the container
        String externalHostName = "localhost";
        boolean pullNewImage = true;
        boolean randomizePorts = false;
        Map<String, String> environmentVariables = new HashMap<>();
        // TODO make adding environment stuff configurable
        // optional debugging inside the localstack docker container
//        environmentVariables.put("SERVICES", "sqs,sns");
//        environmentVariables.put("DEBUG", "sns,sqs");

        localStackContainer = Container.createLocalstackContainer(externalHostName, pullNewImage, randomizePorts, environmentVariables );
        log.info("Waiting for localstack container to be ready...");
        localStackContainer.waitForLogToken(READY_TOKEN);
    }
    public void stopLocalstackContainer() {
        localStackContainer.stop();
    }
}
