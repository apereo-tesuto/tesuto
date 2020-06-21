package org.ccctc.common.docker.spring;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ccctc.common.docker.DockerConfig;
import org.ccctc.common.docker.utils.NetworkUtil;
import org.ccctc.common.docker.utils.TestContextUtil;


import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DefaultDockerClient.Builder;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.LogMessage;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
// TODO see below
//import com.spotify.docker.client.messages.AuthConfig;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

// This is based on publicly available code at https://bitbucket.org/asimio/integration-testing-spring-boot-postgres-docker/src/master
// It has been modified to use the latest version of docker-client and to address some behavior regarding exposed ports.
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DockerizedTestExecutionListener extends AbstractTestExecutionListener {

	private static final int DEFAULT_PORT_WAIT_TIMEOUT_IN_MILLIS = 4000;
	private static final int DEFAULT_STOP_WAIT_BEFORE_KILLING_CONTAINER_IN_SECONDS = 2;
	private static final String HOST_PORT_SYS_PROPERTY_NAME_PATTERN = "HOST_PORT_FOR_%s";
    private long DEFAULT_WAIT_FOR_LOG_TIMEOUT = 30000;

	private DockerClient docker;
	private Set<String> containerIds = Sets.newConcurrentHashSet();

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		final DockerConfig dockerConfig = (DockerConfig) TestContextUtil.getClassAnnotationConfiguration(testContext, DockerConfig.class);
		this.validateDockerConfig(dockerConfig);

		final String image = dockerConfig.image();
		this.docker = this.createDockerClient(dockerConfig);
        log.debug("Pulling image '{}' from Docker registry ...", image);
		this.docker.pull(image);
		log.debug("Completed pulling image '{}' from Docker registry", image);

		if (DockerConfig.ContainerStartMode.ONCE == dockerConfig.startMode()) {
			this.startContainer(testContext);
		}

		super.beforeTestClass(testContext);
	}

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		final DockerConfig dockerConfig = (DockerConfig) TestContextUtil.getClassAnnotationConfiguration(testContext, DockerConfig.class);
		if (DockerConfig.ContainerStartMode.FOR_EACH_TEST == dockerConfig.startMode()) {
			this.startContainer(testContext);
		}
		super.prepareTestInstance(testContext);
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		try {
			super.afterTestClass(testContext);
			for (String containerId : this.containerIds) {
				log.debug("Stopping container: {}, timeout to kill: {}", containerId, DEFAULT_STOP_WAIT_BEFORE_KILLING_CONTAINER_IN_SECONDS);
				this.docker.stopContainer(containerId, DEFAULT_STOP_WAIT_BEFORE_KILLING_CONTAINER_IN_SECONDS);
				log.debug("Removing container: {}", containerId);
				this.docker.removeContainer(containerId, RemoveContainerParam.forceKill());
			}
		} finally {
			log.debug("Final cleanup");
			IOUtils.closeQuietly(this.docker);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

    private void validateDockerConfig(DockerConfig dockerConfig) {
        if (StringUtils.isBlank(dockerConfig.image())) {
            throw new RuntimeException("Cannot execute test, image is a required attribute in " + DockerConfig.class.getSimpleName());
        }
        if (dockerConfig.exposedPorts().length != dockerConfig.mappedPorts().length) {
            throw new RuntimeException("Cannot execute test, exposed ports must have the same number of mapped ports");
        }
    }

	private DockerClient createDockerClient(DockerConfig dockerConfig) throws DockerCertificateException {
        // TODO see below
//      final AuthConfig registryAuthConfig = this.getRegistryAuthConfig(dockerConfig.registry());
        Builder dockerBuilder = null;
        if (System.getenv("DOCKER_HOST") != null) {
            dockerBuilder = DefaultDockerClient.fromEnv();
        } else {
            dockerBuilder = DefaultDockerClient.builder().uri(URI.create(dockerConfig.host()));
        }
//        return dockerBuilder.authConfig(registryAuthConfig).build();
        return dockerBuilder.build();
    }

    // TODO AuthConfig is deprecated, not used in 8.13.0 of docker-client
    // replace with the new implementation
//  private AuthConfig getRegistryAuthConfig(RegistryConfig registryConfig) {
//      if (ObjectUtil.allNullOrEmpty(registryConfig.email(), registryConfig.userName(), registryConfig.passwd())) {
//          return null;
//      }
//      final AuthConfig result = AuthConfig.builder().
//          email(registryConfig.email()).
//          username(registryConfig.userName()).
//          password(registryConfig.passwd()).
//          serverAddress(registryConfig.host()).
//          build();
//      return result;
//  }

	private void startContainer(TestContext testContext) throws Exception {
		log.debug("Starting docker container in prepareTestInstance() to make System properties available to Spring context ...");
		final DockerConfig dockerConfig = (DockerConfig) TestContextUtil.getClassAnnotationConfiguration(testContext, DockerConfig.class);

		final ContainerConfig containerConfig = buildContainerConfig(dockerConfig);
		log.debug("Creating container for image: {}", containerConfig.image());

		final ContainerCreation creation = this.docker.createContainer(containerConfig);
		final String id = creation.id();
		log.debug("Created container [image={}, containerId={}]", containerConfig.image(), id);

		// Stores container Id to remove it for later removal
		this.containerIds.add(id);

		// Starts container
		this.docker.startContainer(id);
		log.debug("Started container: {}", id);

		Set<String> hostPorts = Sets.newHashSet();

		// Sets published host ports to system properties so that test method can connect through it
		// TODO this seems funky, need to check
        final int[] containerToHostRandomPorts = dockerConfig.exposedPorts();
		final ContainerInfo info = this.docker.inspectContainer(id);
		final Map<String, List<PortBinding>> infoPorts = info.networkSettings().ports();
		for (int port : containerToHostRandomPorts) {
            final String hostPort = infoPorts.get(String.format("%s/tcp", port)).iterator().next().hostPort();
			hostPorts.add(hostPort);
			final String hostToContainerPortMapPropName = String.format(HOST_PORT_SYS_PROPERTY_NAME_PATTERN, port);
            System.getProperties().put(hostToContainerPortMapPropName, hostPort);
            log.debug(String.format("Mapped ports host=%s to container=%s via System property=%s", hostPort, port, hostToContainerPortMapPropName));
		}

		if (dockerConfig.waitForPorts()) {
		    waitForPorts(hostPorts);
		}
		
		String waitForLogMessage = dockerConfig.waitForLogMessage();
		if (!StringUtils.isBlank(waitForLogMessage)) {
            waitForLog(id, waitForLogMessage);
		}
	}

    private ContainerConfig buildContainerConfig(DockerConfig dockerConfig) {
        final String image = dockerConfig.image();
        final int[] exposedPorts = dockerConfig.exposedPorts();

        // Bind container ports to automatically allocated available host ports
        final Map<String, List<PortBinding>> portBindings = this.bindContainerToHostRandomPorts(this.docker, 
                exposedPorts, 
                dockerConfig.mappedPorts());
        final HostConfig hostConfig = HostConfig.builder().
                portBindings(portBindings).
                publishAllPorts(true).
                build();
        final ContainerConfig containerConfig = ContainerConfig.builder().
                hostConfig(hostConfig).
                exposedPorts(portBindings.keySet()).
                image(image).
                build();
        return containerConfig;
    }

    private final Map<String, List<PortBinding>> bindContainerToHostRandomPorts(DockerClient docker, int[] exposedPorts, int[] mappedPorts) {
        final Map<String, List<PortBinding>> result = new HashMap<String, List<PortBinding>>();
        for (int i = 0; i < exposedPorts.length; i++) {
            result.put(String.valueOf(exposedPorts[i]), Arrays.asList(PortBinding.of("0.0.0.0", mappedPorts[i])));
        }
        return result;
    }

	private void waitForPorts(Set<String> hostPorts) {
        // Makes sure ports are LISTENing before giving running test
        log.debug("Waiting for host ports [{}] ...", StringUtils.join(hostPorts, ", "));
        final Collection<Integer> intHostPorts = Collections2.transform(hostPorts,
            new Function<String, Integer>() {

                @Override
                public Integer apply(String arg) {
                    return Integer.valueOf(arg);
                }
            }
        );
        NetworkUtil.waitForPort(this.docker.getHost(), intHostPorts, DEFAULT_PORT_WAIT_TIMEOUT_IN_MILLIS);
        log.debug("All ports are now listening");
	}
	
	// This source code was downloaded from the web
	// https://www.programcreek.com/java-api-examples/?api=com.spotify.docker.client.LogStream, example 7
	private void waitForLog(String id, String logMessage) throws DockerException, InterruptedException {
        LogStream logStream = this.docker.logs(id, LogsParam.follow(), LogsParam.stderr(), LogsParam.stdout());
        log.debug("Waiting for log snippet '{}'", logMessage);
        final Semaphore ready = new Semaphore(0);
        long waitForLogTimeout = DEFAULT_WAIT_FOR_LOG_TIMEOUT;

        new Thread(() -> {
            try {
                while (logStream.hasNext()) {
                    final LogMessage msg = logStream.next();

                    final String logStr = StandardCharsets.UTF_8.decode(msg.content()).toString();
                    log.error("CONTAINER: {}", logStr);
                    if (logStr.contains(logMessage)) {
                        ready.release();
                        return;
                    }
                }
            } finally {
                logStream.close();
            }
        }).start();

        if (!ready.tryAcquire(waitForLogTimeout, TimeUnit.MILLISECONDS)) {
            throw new DockerException(String.format(
                    "Did not observe desired Docker container log snippet within %d ms",
                    waitForLogTimeout));
        }
	}
}