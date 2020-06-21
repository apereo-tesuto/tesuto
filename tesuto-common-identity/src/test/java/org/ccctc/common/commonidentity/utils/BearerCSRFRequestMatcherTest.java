package org.ccctc.common.commonidentity.utils;

import org.ccctc.common.commonidentity.utils.BearerCSRFRequestMatcher;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by andrew on 2/21/17.
 */
public class BearerCSRFRequestMatcherTest {
    private BearerCSRFRequestMatcher matcher = new BearerCSRFRequestMatcher();

    private HttpServletRequest req = mock(HttpServletRequest.class);

    @Test
    public void matches() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer foobar");
        assertTrue(!matcher.matches(req));
    }

    @Test
    public void testGetRequest() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        assertTrue(!matcher.matches(req));
    }

    @Test
    public void postWithNoBearer() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        assertTrue(matcher.matches(req));
    }
}