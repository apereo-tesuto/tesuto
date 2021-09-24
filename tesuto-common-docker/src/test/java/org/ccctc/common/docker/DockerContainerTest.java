package org.ccctc.common.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.ccctc.common.docker.DockerConfig;
import org.ccctc.common.docker.RegistryConfig;
import org.ccctc.common.docker.DockerConfig.ContainerStartMode;
import org.ccctc.common.docker.spring.DockerizedTestExecutionListener;
import org.ccctc.common.docker.utils.SetupUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.test.context.TestExecutionListeners.MergeMode;

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
        // TODO ContainerStartMode.FOR_EACH_TEST seems to have an error
        // if there are multiple tests, need to research
//        startMode = ContainerStartMode.FOR_EACH_TEST,
        startMode = ContainerStartMode.ONCE,
        host="unix:///var/run/docker.sock",
        waitForLogMessage = SetupUtils.WAIT_FOR_LOG_MESSAGE,
        registry = @RegistryConfig(email="", host="", userName="", passwd="")
)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DockerContainerTest {
    
    private RestTemplate rs;
    private SetupUtils setupUtils;
    
    @Before
    public void setup() {
        setupUtils = new SetupUtils();
        setupUtils.clear();
        rs = setupUtils.getRestTemplate();
    }

    @Test
    public void nominal() {
        URI url = URI.create("http://localhost:8088/answer/abc");
        HttpMethod method = HttpMethod.GET;
        HttpEntity<?> requestEntity = null;
        ResponseEntity<String> response = rs.exchange(url, method, requestEntity, String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void setupAndAnswer() {
        try {
            new SetupUtils().send("sampleSetup.json");
        } catch (JsonParseException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        URI url = URI.create("http://localhost:8088/answer/abc/def");
        HttpEntity<?> requestEntity = null;
        ResponseEntity<String> response = rs.exchange(url, HttpMethod.POST, requestEntity, String.class);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void getAndClear() {
        Map beginning = setupUtils.get();
        assertEquals(0, beginning.size());

        try {
            new SetupUtils().send("sampleSetup.json");
        } catch (JsonParseException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Map middle = setupUtils.get();
        assertEquals(1, middle.size());
        String key = "abc/def:POST::";
        assertTrue(middle.keySet().contains(key));
        Map<String, Object> map = (Map<String, Object>) middle.get(key);
        assertEquals("POST", map.get("httpMethod"));
        assertEquals("abc/def", map.get("path"));
        assertEquals(201, map.get("responseStatus"));
        setupUtils.clear();

        Map end = setupUtils.get();
        assertEquals(0, end.size());
    }
}
