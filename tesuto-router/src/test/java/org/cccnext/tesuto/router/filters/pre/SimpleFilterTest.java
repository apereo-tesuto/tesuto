/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.cccnext.tesuto.router.filters.pre;

import com.netflix.zuul.context.RequestContext;
import org.cccnext.tesuto.router.filters.pre.PreFilter;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

public class SimpleFilterTest {

    private PreFilter filter;

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Before
    public void setup() {
        this.filter = new PreFilter();
    }

    @Test
    public void testFilterType() {
        assertThat(filter.filterType()).isEqualTo("pre");
    }

    @Test
    public void testFilterOrder() {
        assertThat(filter.filterOrder()).isEqualTo(1);
    }

    @Test
    public void testShouldFilter() {
        assertThat(filter.shouldFilter()).isTrue();
    }

    @Test
    public void testRun() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURL()).thenReturn(new StringBuffer("http://foo"));
        RequestContext context = mock(RequestContext.class);
        when(context.getRequest()).thenReturn(req);
        RequestContext.testSetCurrentContext(context);
        filter.run();
        this.outputCapture.expect(Matchers.containsString("GET request to http://foo"));
    }
}
