package org.ccctc.common.commonidentity.domain.identity.enhancers;

import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.domain.identity.enhancers.AdminAuthorityGranter;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdminAuthorityGranterTest {

    private AdminAuthorityGranter granter = new AdminAuthorityGranter(Arrays.asList("FOO", "BAR"));;

    @Test
    public void enhance() throws Exception {
        UserIdentity id = mock(UserIdentity.class);
        when(id.getPrimaryAffiliation()).thenReturn("staff");

        final HashSet<GrantedAuthority> authorities = new HashSet<>();

        assertEquals(0, authorities.size());

        final Answer<Object> answerAuthorities = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return authorities;
            }
        };
        //invocationOnMock -> authorities;
        when(id.getAuthorities()).thenAnswer(answerAuthorities);

        assertEquals(0, authorities.size());

        UserIdentity id2 = granter.enhance(id);

        assert (id2 == id);
        assertEquals(2, authorities.size());
        verify(id).setAuthorities(authorities);

        assert (authorities.contains(new SimpleGrantedAuthority("FOO")));
        assert (authorities.contains(new SimpleGrantedAuthority("BAR")));
    }
}