package org.ccctc.web.spring.actuator.extension;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * Creates an endpoint in the Spring Actuator at - /[actuator]/heapUsage which displays a 2 digit decimal value that is 
 * the percentage of the heap being used.
 */
@Component
@Endpoint(id = "heapUsage")
public class MemoryEndpoint {
    @ReadOperation
    public Map<String, String> getHeapUsage() {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        double heapUsed = (double)heapSize/heapMaxSize;
        String strDouble = String.format("%.2f", heapUsed); 
        
        Map<String, String> details = new HashMap<>();
        details.put("Heap_used", strDouble);
        return details;
    }

}
