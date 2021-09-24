# Common Docker
The purpose of Common Docker is to provide a way to integrate Docker Containers into the
Spring JUnit Testing Process.   I can imagine many scenarios where as part of an integration test,  it would be desirable to be able to spin up a new Docker container, rather than mock up any interfaces or access other full-featured services.  By allowing an integration test to spin up a docker container, we ensure that testing can occur entirely in a local environment and can guarantee the setup for each run.

## What You Get
This Maven project builds a Jar for inclusion in your Spring application.  It contains the classes necessary for you to start/stop a Docker container for a given test.

## Using this Package
This package supports the Localstack docker container, the docker-rest-test-service docker container, and other custom containers with a little additional effort.

### Localstack docker container
Localstack is a package for simulating AWS features locally.  It can be configured to run within a Docker container, and that's how we're using it because it's pretty simple.  There are actually two different ways to startup the container.

#### LocalstackDockerTestRunner
LocalstackDockerTestRunner is a JUnit Test Runner that integrates into Spring and JUnit's test framework.  To use it, use this in your JUnit test class:

```
@RunWith(LocalstackDockerTestRunner.class)
```

This will automatically startup and teardown the Localstack Docker Container.  Your tests will need to use DockerTestUtils to retrieve your AWS Clients (AmazonS3, AmazonSNS, AmazonSQS, etc.), and your code will need to be able to use this client for testing and a "real" client in production.  For an example of how this is used, refer to the common-aws project, in S3FileServiceImplTest.

Unfortunately, when using this test runner, you can't use the Spring JUnit Test Runner you're used to.  You can only use one Test Runner in a class.  Therefore, you may not want to use LocalstackDockerTestRunner to start the Localstack docker container.

#### LocalstackUtils
org.ccctc.common.docker.localstack.LocalstackUtils is a class that automatically starts the Localstack docker container and waits until it is started before continuing the test.  This one isn't really configurable, but it's very easy to use.  Just add this code to your test setup and teardown code for a clean docker container for each test.  For example:
```
private LocalstackUtils localstackUtils;

@Before
public void setUp() {
    localstackUtils = new LocalstackUtils();
    localstackUtils.startLocalstackContainer();
}

@After
public void teardown() {
    localstackUtils.stopLocalstackContainer();
}
```

Like above, you can't use a "real" AWS client; you'll need to use LocalstackEndpoints to get an AWS client that is configured to read/write to the Localstack endpoints.  For an example of how to use this, look at project Web-Monitor, org.ccctc.webmonitor.service.SwaggerServiceTest.

### docker-rest-test-service
This test starts up the Docker image, docker.dev.ccctechcenter.org/docker-rest-test-service:1.0.0-SNAPSHOT, which is a configurable Rest endpoint test package.  The actual test sends a REST call and confirms that the response is what is expected.  At the end of the test, the docker container is automatically stopped.

The code:
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={ App.class})
@TestExecutionListeners(value = {DockerizedTestExecutionListener.class}, 
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS )
@DockerConfig(
        image = SetupUtils.DOCKER_IMAGE,
        exposedPorts = { SetupUtils.EXPOSED_PORT },
        mappedPorts = { SetupUtils.MAPPED_PORT },
        waitForPorts = true,
        startMode = ContainerStartMode.FOR_EACH_TEST,
        host="unix:///var/run/docker.sock",
        waitForLogMessage=SetupUtils.WAIT_FOR_LOG_MESSAGE,
        registry = @RegistryConfig(email="", host="", userName="", passwd="")
)
```

Let's go over each line individually

```
@RunWith(SpringJUnit4ClassRunner.class)
```
This is a Spring JUnit annotation.  It makes sure that Spring capabilities are correctly
initialized for any test

```
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={ App.class})
```
This is also a Spring JUnit annocation.  It makes sure that any annotations are processed, including @Configuration,
@Bean, @Autowired, etc.

Both of these annoations are part of Spring.

```
@TestExecutionListeners(value = {DockerizedTestExecutionListener.class}, 
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS )
```
This is the first class that is part of common-docker.  This annotation implements TestExecutionListener, which is a Spring interface.  Spring by default has many TestExecutionListeners which manage building the infrastructure for a Spring JUnit test.  This additional TestExecutionListener is what actually starts, stops, and manages the docker container during execution.

The interesting attribute for this annotation is mergeMode.  By default (without this attribute), you need to explicitly list all TestExecutionListeners that will be run.  There are several.  Setting mergeMode to MERGE_WITH_DEFAULTS means that this listener will be added to all of the default listeners without explicitly adding them yourself.

```
@DockerConfig(
        image = SetupUtils.DOCKER_IMAGE,
        exposedPorts = { SetupUtils.EXPOSED_PORT },
        mappedPorts = { SetupUtils.MAPPED_PORT },
        waitForPorts = true,
        startMode = ContainerStartMode.FOR_EACH_TEST,
        host="unix:///var/run/docker.sock",
        waitForLogMessage=SetupUtils.WAIT_FOR_LOG_MESSAGE,
        registry = @RegistryConfig(email="", host="", userName="", passwd="")
)
```
This annotation is where you configure the docker container that you want to run.  It has several interesting attributes, some of which I don't think are necessary at this point.  I copied this code from another project, so not all of them may be relevant

```
        image = "docker.dev.ccctechcenter.org/docker-rest-test-service:1.0.0-SNAPSHOT",
```

This one should be obvious.  You need to explicitly say which docker container to start


```
        exposedPorts = { SetupUtils.EXPOSED_PORT },
```

This one is required if you need to expose ports outside of the container.  This makes internal ports available to be mapped

```
        mappedPorts = { SetupUtils.MAPPED_PORT },
```

This one is required if you have exposed ports.  For each exposed port there must be one mapped port (the lists should be the same length).  Your application should access the mappedPort, which will then be redirected by Docker to the exposed port.

```
        waitForPorts = true, 
```

This one makes the test wait until the docker container ports are available.  There is still work to do here.


```
        startMode = ContainerStartMode.FOR_EACH_TEST,
```

If set to ContainerStartMode.FOR_EACH_TEST, a new container is started for each test
If set to ContainerStartMode.ONCE, a new container is started once for all tests.

```
        host="unix:///var/run/docker.sock",
```
Not sure if this is required, there is still work to do here

```
        waitForLogMessage=SetupUtils.WAIT_FOR_LOG_MESSAGE,
```
By default this is blank.  If you set it to a value, test won't run until
this message is found in the Docker container's log file.  It will fail
if the message isn't found in 30 seconds.

```
        registry = @RegistryConfig(email="", host="", userName="", passwd="")
```
This one gives location of the docker registry where the docker image is located,
if it can't be found automatically.

Refer to org.ccctc.common.DockerContainerTest in this package for an example of how to use this.

### Other Docker containers
So far, we've discussed starting both the Localstack docker container and the docker-rest-test-service.  You probably noticed that docker-rest-test-service is just a docker container; you could easily use any docker container in this space; you'll just need to put the appropriate parameters in place for @DockerConfig.  I wrote SetupUutil as an easy way to keep those parameters handy.  Use discretion when starting other docker containers here.

## Updating this Project

To build this package:


```
mvn clean install
```
## Still To Do
Docker-Rest-Test-Service can and should have a similar setup as Localstack has.  Create a RestTestUtils class that can start and stop the container.  Use the logic embedded in the DockerizedTestExecutionListener class, combined with the configurations from SetupUtils.

Localstack should also have a similar setup that DockerizedTestExecutionListener provides.

LocalstackEndpoints should be able to provide more AWS clients.  It currently only provides SNS and SQS; it should also provide S3 and any other clients supported by common-aws.

## Resources
https://bitbucket.org/asimio/integration-testing-spring-boot-postgres-docker/src/master/ - original source code repository
https://bitbucket.org/cccnext/docker-rest-test-service/src/develop/ - Docker Rest Test Service
https://github.com/localstack/localstack - Localstsack source code