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
package org.cccnext.tesuto.web.controller.ui;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.util.UrlUtils;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.SecurityGroupReader;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.user.service.UserContextService;
import org.cccnext.tesuto.web.enums.CommonErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SimpleController extends BaseController implements ErrorController {

	@Autowired
	protected UserContextService userContextService;
	@Autowired
	protected UserAccountReader userAccountService;
	@Autowired
	private SecurityGroupReader securityGroupService;
	@Autowired
	protected RequestForwardService requestForwardService;
	@Autowired
	private UrlUtils urlUtils;
	
	private final static String ERROR_PATH = "/error";

	@Value("${assess.environment}")
	String assessEnvironmentUrl;

	@Value("${assess.spentityid}")
	String assessSpentityid;

	@Value("${tesuto.local.login.only}")
	Boolean localLoginOnly;

	@Value("${tesuto.login.url}")
	String localLoginUrl;

	@Value("${tesuto.admin.homepage.url}")
	String adminHomePageUrl;

	@Value("${tesuto.student.homepage.url}")
	String studentHomePageUrl;

	@RequestMapping(value = "login", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String login(Model model) {
		return "Login";
	}
	
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String loggedOut(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "sessionTimeout", required = false) Boolean sessionTimeout) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		addUrls(model, request);
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		if (sessionTimeout != null && sessionTimeout) {

			return "TimedOut";
		}

		return "LoggedOut";
	}

	@RequestMapping(value = "timedout", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String timedOut(Model model, HttpServletRequest request) {
		addUrls(model, request);
		return "TimedOut";
	}

	@RequestMapping(value = "unauthorized", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String unauthorized(Model model, HttpServletRequest request) {
		addUrls(model, request);
		return "Unauthorized";
	}

	@RequestMapping(value = ERROR_PATH, method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String error(@RequestParam(required = false) String error, @RequestParam(required = false) boolean expired,
			@RequestParam(required = false) boolean locked, @RequestParam(required = false) boolean enabled,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		if (error != null) {
			CommonErrors commonError = CommonErrors.valueOf(error);
			if (commonError != null) {
				model.addAttribute("errorMessage", commonError.toString());
				model.addAttribute("errorCode", commonError.name());
				model.addAttribute("orginalErrorMessage", error);
			} else {
				model.addAttribute("errorCode", response.getStatus());
				model.addAttribute("orginalErrorMessage", error);
				model.addAttribute("errorMessage", "HTTP Status:"  + response.getStatus());
				model.addAttribute("orginalErrorMessage", "HTTP Status:"  + response.getStatus());
			}
		} else {
			model.addAttribute("errorCode", response.getStatus());
			model.addAttribute("orginalErrorMessage", error);
			model.addAttribute("errorMessage", "HTTP Status:"  + response.getStatus());
			model.addAttribute("orginalErrorMessage", "HTTP Status:"  + response.getStatus());
		}
		
		model.addAttribute("expired", expired);
		model.addAttribute("locked", locked);
		model.addAttribute("enabled", enabled);
		addUrls(model, request);
		return "Error";
	}

	@RequestMapping(value = "notfound", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String notFound(Model model, HttpServletRequest request) {
		addUrls(model, request);
		return "NotFound";
	}

	@RequestMapping(value = "redirect-on-role-change", method = RequestMethod.GET)
	@PreAuthorize("permitAll()")
	public String redirectOnRoleChange(HttpServletRequest request) {
		Authentication authentication = userContextService.getCurrentAuthentication();
		UserAccountDto userAccountDto = (UserAccountDto) authentication.getPrincipal();
		Set<SecurityGroupDto> securityGroupDtos = userAccountDto.getSecurityGroupDtos();
		SecurityGroupDto studentSecurityGroupDto = securityGroupService.getSecurityGroupByGroupName("STUDENT");
		if (!securityGroupDtos.contains(studentSecurityGroupDto)) {
			securityGroupDtos = userAccountService.getUserAccount(userAccountDto.getUserAccountId())
					.getSecurityGroupDtos();
		}

		String targetUrl = requestForwardService.getForwardUrl(request, urlUtils.determineTargetUrl(securityGroupDtos));
		StringBuilder sb = new StringBuilder("redirect:");
		sb.append(targetUrl);
		return sb.toString();
	}

	private void addUrls(Model model, HttpServletRequest request) {
		model.addAttribute("landingPageUrl", buildLoginUrl(request.getSession()));
		String homePageUrl = buildHomeUrl();
		if(StringUtils.isNotBlank(homePageUrl))
			model.addAttribute("homePageUrl", homePageUrl);
	}

	private String buildLoginUrl(HttpSession session) {
		String authSource = null;

		if (localLoginOnly)
			return localLoginUrl;

		if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
			SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
			if (securityContext.getAuthentication() != null
					&& securityContext.getAuthentication().getPrincipal() instanceof UserAccountDto) {
				authSource = ((UserAccountDto) securityContext.getAuthentication().getPrincipal()).getAuthSource();
			}
		}

		StringBuilder idpUrlBuilder = new StringBuilder().append(assessEnvironmentUrl).append("?");
		if (authSource != null) {
			idpUrlBuilder.append("source=").append(authSource).append("&");
		}
		idpUrlBuilder.append("spentityid=").append(assessSpentityid);
		return idpUrlBuilder.toString();
	}

	private String buildHomeUrl() {

		UserAccountDto user = getUser();
		if(user == null) {
			return "";
		}
		
		if (user.isStudent()) {
			return studentHomePageUrl;
		}

		return adminHomePageUrl;

	}
}
