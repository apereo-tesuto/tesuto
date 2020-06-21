package org.ccctc.web.cors;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

public class CorsConfigurationSourceImplTest {
    
    @Test
    public void validateDefaultConfig() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        CorsConfiguration config = new CorsConfigurationSourceImpl().getCorsConfiguration(httpServletRequest);
        ArrayList<String> allowedHeaders = new ArrayList<String>();
        allowedHeaders.add("Origin");
        allowedHeaders.add("X-Requested-With");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Accept");
        allowedHeaders.add("Authorization");
        
        Assert.assertTrue("Expected default headers present", config.getAllowedHeaders().containsAll(allowedHeaders));
        Assert.assertTrue("Contains no other allowed headers", allowedHeaders.size() == config.getAllowedHeaders().size());
        
        Assert.assertEquals("Expected all origins allowed", "*", config.getAllowedOrigins().get(0));
        
        Assert.assertEquals("Expected default timeout", new Long(1800), config.getMaxAge());
        
        ArrayList<String> allowedMethods = new ArrayList<String>();
        allowedMethods.add(HttpMethod.PUT.toString());
        allowedMethods.add(HttpMethod.OPTIONS.toString());
        allowedMethods.add(HttpMethod.DELETE.toString());
        allowedMethods.add(HttpMethod.PATCH.toString());
        allowedMethods.add(HttpMethod.GET.toString());
        allowedMethods.add(HttpMethod.HEAD.toString());
        allowedMethods.add(HttpMethod.POST.toString());
        
        Assert.assertTrue("Expected default methods present", config.getAllowedMethods().containsAll(allowedMethods));
        Assert.assertTrue("Contains no other allowed methods", allowedMethods.size() == config.getAllowedMethods().size());
    }
}
