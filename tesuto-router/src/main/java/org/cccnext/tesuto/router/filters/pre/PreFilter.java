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

import javax.servlet.http.HttpServletRequest;




import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PreFilter  extends ZuulFilter {


    @Override
    public String filterType() {
      return "pre";
    }

    @Override
    public int filterOrder() {
      return 1;
    }

    @Override
    public boolean shouldFilter() {
      return true;
    }

    @Override
    public Object run() {
      RequestContext ctx = RequestContext.getCurrentContext();
      RequestContext.getCurrentContext().setDebugRouting(true);
      RequestContext.getCurrentContext().setDebugRequest(true);
      
      HttpServletRequest request = ctx.getRequest();

      log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
 
      return null;
    }

}
