package org.ccctc.common.commonidentity.domain.identity;

import com.nimbusds.jwt.JWT;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.ccctc.common.commonidentity.domain.identity.JWTUserIdentity;
import org.ccctc.common.commonidentity.domain.identity.SecurityContextPreferrer;
import org.ccctc.common.commonidentity.domain.identity.ServiceAccountManager;

/**
 * Created by andrew on 2/10/17.
 */
public class SecurityContextPreferrerTest {

    private ServiceAccountManager sa = mock(ServiceAccountManager.class);
    private JWT mockJWT = mock(JWT.class);
    private SecurityContext ctx = mock(SecurityContext.class);
    private SecurityContextPreferrer pref = null;

    @Before
    public void setup() {
        pref = new SecurityContextPreferrer(sa);
        SecurityContextHolder.setContext(ctx);

        when(sa.getJWT()).thenReturn(mockJWT);
    }

    @Test
    public void getJWT() throws Exception {
        when(ctx.getAuthentication()).thenReturn(null);
        JWT j = pref.getJWT();
        assertEquals(j, mockJWT);
        verify(sa, times(1)).getJWT();
    }

    @Test
    public void testContextHasJWT() throws Exception {
        Authentication a = mock(Authentication.class);
        JWTUserIdentity id = mock(JWTUserIdentity.class);
        JWT testJWT = mock(JWT.class);

        when(ctx.getAuthentication()).thenReturn(a);
        when(a.getDetails()).thenReturn(id);
        when(id.getJwt()).thenReturn(testJWT);

        JWT userJWT = pref.getJWT();
        assertEquals(userJWT, testJWT);
        verify(sa, never()).getJWT();
    }
}