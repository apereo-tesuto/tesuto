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
package org.cccnext.tesuto.web.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.servlet.mvc.WebContentInterceptor;

/**
 * What happening here is similar to the GlobalUrlInterceptor, but it *does not* apply to controllers.
 * As opposed to an interceptor, it is a filter wired in through the deployment descriptor (web.xml).
 * As it stands right now, it only applies to files ending in ".woff" and ".woff2".  The last bit of configuration
 * is in the root-context.xml.  It cannot exist in the servlet-context.xml or things will fail because
 * it needs to be universally available to the whole Spring Context.  In that bean definition a
 * bunch of different caching options can be configured as needed.  I defaulted things to one day but
 * remember, it's only fired on ".woff" and ".woff2" files for now.
 *
 * There is a private inner class which rewrites header information.  It is a pass through for *every* header
 * value except the Pragma value.  The goal here is to remove "Pragma: no-cache" and only be left with
 * "Pragma: none". This can be verified by inspecting headers for the .woff* files that come from our project.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class ResponseCachingFilter extends WebContentInterceptor implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			this.preHandle((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		((HttpServletResponse) response).setHeader("Pragma", "none");
		filterChain.doFilter(request, new HeaderWackerHttpServletResponseWrapper(response));
	}

	@Override
	public void destroy() {
	}

	/**
	 * Removes problematic Pragma and Cache-Control headers for fonts and Internet Exploder.
	 */
	private final class HeaderWackerHttpServletResponseWrapper extends HttpServletResponseWrapper {

		private HeaderWackerHttpServletResponseWrapper(ServletResponse response) {
			super((HttpServletResponse) response);
		}

		@Override
		public void addHeader(String name, String value) {
			if ("Pragma".equals(name) || "Cache-Control".equals(name)) {
				return;
			}
			super.addHeader(name, value);
		}

		@Override
		public void setHeader(String name, String value) {
			if ("Pragma".equals(name) || "Cache-Control".equals(name)) {
				return;
			}
			super.setHeader(name, value);
		}
	}
}
