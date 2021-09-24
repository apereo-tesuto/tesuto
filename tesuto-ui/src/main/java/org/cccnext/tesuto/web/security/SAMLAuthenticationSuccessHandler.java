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
package org.cccnext.tesuto.web.security;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.util.UrlUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.SecurityGroupReader;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.user.service.UserContextService;
import org.cccnext.tesuto.web.enums.CommonErrors;
import org.cccnext.tesuto.web.security.exception.TesutoInvalidAccountException;
import org.cccnext.tesuto.web.service.StudentCollegeAffiliationService;
import org.cccnext.tesuto.web.service.UserAccountDtoUserIdentityAssembler;
import org.ccctc.common.commonidentity.domain.identity.UserIdentity;
import org.ccctc.common.commonidentity.saml.SpringSAMLHelper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml.SAMLCredential;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class SAMLAuthenticationSuccessHandler extends TesutoAuthenticationSuccessHandler {
	@Autowired UserContextService userContextService;
	@Autowired UserAccountDtoUserIdentityAssembler userAccountDtoUserIdentityAssembler;
	@Autowired SecurityGroupReader securityGroupReader;
	@Autowired UserAccountReader userAccountReader;
	@Autowired RequestForwardService requestForwardService;
	@Autowired StudentCollegeAffiliationService studentCollegeAffiliationService;
	@Autowired StudentReader studentReader;
	@Autowired UrlUtils urlUtils;

	@Override
	protected void clearFailedLogins(Authentication authentication){

	}

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, TesutoInvalidAccountException {
		UserIdentity userIdentity = SpringSAMLHelper.getUserIdentity();
        UserAccountDto userAccountDto = null;

		// Check the SAML Authentication user object from the Identity Provider
		SamlAuthResults samlAuthResults = samlAffiliationResult(userIdentity);

		/*
		 * TODO: Implement an exception that hold on to the CommonError and userAccountDto, move this code
		 * into a method throw the exception, catch the exception here and redirectToError in the catch block.
		 */
		// Add the proper permissions based on what we discovered above.
		if (samlAuthResults.isFacultyFlag()) {
			// Look up the extra permissions, this user should have, because this is not a student and presumably needs more.
			try {
				userAccountDto = userAccountReader.getUserAccountByUsername(userIdentity.getEppn());
				// Apply student permissions for a member affiliation.
				if (userAccountDto == null && samlAuthResults.isCccIdFlag() && samlAuthResults.isStudentFlag()) {
					userAccountDto = applyStudentPermissions(userIdentity);
				} else if (userAccountDto == null && !samlAuthResults.isCccIdFlag() && samlAuthResults.isStudentFlag()) {
					log.info("Invalid CCCID and No Faculty Account in Assess {}", userIdentity.getDisplayName());
					redirectToError(response, CommonErrors.NO_CCCID_EPPN, userAccountDto);
				} else if (userAccountDto == null) {
					log.info("No faculty Account in Assess {}", userIdentity.getDisplayName());
					redirectToError(response, CommonErrors.NOT_FOUND, userAccountDto);
				}
			} catch (Exception exception) {
				log.error("Error on login using SAML", exception);
			}
		} else if (samlAuthResults.isStudentFlag() && samlAuthResults.isCccIdFlag()){
			userAccountDto = applyStudentPermissions(userIdentity);
		} else if (samlAuthResults.isStudentFlag() && !samlAuthResults.isCccIdFlag()) {
			log.error("SSO Account is not in Access User Base and this user does not have a CCCID or EPPN");
			redirectToError(response, CommonErrors.NO_CCCID_EPPN, userAccountDto);
		} else {
			// This should not be possible, but if it happens, it means no affiliations matched up with a student type or a faculty type.
			log.error("Affiliations are invalid from the SAML Authentication Provider: for user cccid: {} with affiliations: {}", userIdentity.getCccId(), userIdentity.getAffiliations());
			redirectToError(response, CommonErrors.INVALID_AFFILIATIONS, userAccountDto);
		}

		// Faculty lookup error handling
		SecurityGroupDto studentSecurityGroupDto = securityGroupReader.getSecurityGroupByGroupName("STUDENT");
		if (samlAuthResults.isFacultyFlag() && userAccountDto != null && !userAccountDto.getSecurityGroupDtos().contains(studentSecurityGroupDto)) {
			if (userAccountDto.isAccountExpired()) {
				log.info("User Account, {}, is expired.", userAccountDto.getUsername());
				log.info("expired: {}", userAccountDto.isAccountExpired());
				redirectToError(response, CommonErrors.EXPIRED, userAccountDto);
			} else if (userAccountDto.isAccountLocked()) {
				log.info("User Account, {}, is locked.", userAccountDto.getUsername());
				log.info("locked: {}", userAccountDto.isAccountLocked());
				redirectToError(response, CommonErrors.LOCKED, userAccountDto);
			} else if (!userAccountDto.isEnabled()) {
				log.info("User Account, {}, is disabled.", userAccountDto.getUsername());
				log.info("enabled: {}", userAccountDto.isEnabled());
				redirectToError(response, CommonErrors.DISABLED, userAccountDto);
			} else if (userAccountDto.getColleges() == null || userAccountDto.getColleges().size() == 0) {
				log.info("User Account, {}, has no colleges.", userAccountDto.getUsername());
				redirectToError(response, CommonErrors.NO_COLLEGES, userAccountDto);
			}
		}

		String relayState = relayState(authentication);
		String targetUrl = null;
		if (relayState != null) {
			ImmutablePair<String, String> pair = null;
			String cccMisCode = null;
			try {
				pair = parseTargetUrlAndMisCodeFromRelayState(relayState);
				targetUrl = pair.getLeft();
				cccMisCode = pair.getRight();
			} catch (Exception e) {
				log.info("A request was made with an invalid relayState: " + relayState);
				redirectToError(response, CommonErrors.INVALID_REQUEST, userAccountDto);
			}
			if (cccMisCode != null) {
				studentCollegeAffiliationService.createIfNotExists(userIdentity.getEppn(), userIdentity.getCccId(), cccMisCode, userIdentity.getAuthSource());
			}
		}

		unionProfileAndInternalCollegeAffiliations(userIdentity, userAccountDto);

		if (userAccountDto != null) {
			userAccountDto.setAuthSource(userIdentity.getAuthSource());
			// Update the authentication in the session if it's not null.
			userContextService.updateUserAuthentication(userAccountDto);
		}

		// Detect the response committed error condition and bypass if needed.
		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect");
			return;
		}

		if (targetUrl != null) {
			response.sendRedirect(targetUrl);
		} else {
			targetUrl = urlUtils.determineTargetUrl(userAccountDto == null ? null : userAccountDto.getSecurityGroupDtos());
			String forwardUrl = requestForwardService.getForwardUrl(request, targetUrl);
			response.sendRedirect(forwardUrl);
		}
	}

	ImmutablePair<String, String> parseTargetUrlAndMisCodeFromRelayState(String relayState) {
		String targetUrl;
		String cccMisCode = null;
		int questionLoc = StringUtils.indexOf(relayState, "?");
		if (questionLoc != StringUtils.lastIndexOf(relayState, "?")) {
			throw new IllegalArgumentException("RelayState is invalid: " + relayState);
		}
		if (questionLoc < 0) {
			targetUrl = relayState;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(StringUtils.substring(relayState, 0, questionLoc));
			URI uri;
			try {
				uri = new URI(relayState);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException("RelayState is invalid: " + relayState);
			}
			List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
			boolean isFirstAppend = true;
			for (NameValuePair param : params) {
				if (param.getName().equalsIgnoreCase("cccMisCode")) {
					cccMisCode = param.getValue();
				} else {
					if (isFirstAppend) {
						stringBuilder.append("?").append(param.getName()).append("=").append(param.getValue());
						isFirstAppend = false;
					} else {
						stringBuilder.append("&").append(param.getName()).append("=").append(param.getValue());
					}
				}
			}
			targetUrl = stringBuilder.toString();
		}
		return new ImmutablePair<>(targetUrl, cccMisCode);
	}

	private String relayState(Authentication authentication) {
		Object credentials = authentication.getCredentials();
		if (credentials instanceof SAMLCredential) {
			SAMLCredential samlCredential = (SAMLCredential) credentials;
			return samlCredential.getRelayState();
		} else {
			return null;
		}
	}


	public UserAccountDto applyStudentPermissions(UserIdentity userIdentity) {
		// Assemble the DTO and add the GrantedAuthorities associated with students.
		Set<GrantedAuthority> grantedAuthorities;
		Set<SecurityGroupDto> securityGroupDtos = null;
		UserAccountDto userAccountDto = userAccountDtoUserIdentityAssembler.assembleStudent(userIdentity);
		securityGroupDtos = new HashSet<>();
		SecurityGroupDto securityGroupDto = securityGroupReader.getSecurityGroupByGroupName("STUDENT");
		securityGroupDtos.add(securityGroupDto);
		userAccountDto.setSecurityGroupDtos(securityGroupDtos);
		grantedAuthorities = securityGroupDto.getGrantedAuthorities();
		userAccountDto.setGrantedAuthorities(grantedAuthorities);
		return userAccountDto;
	}

	/**
	 * Process the possible outcomes of an IDP SAML based authentication.
	 *
	 * Based on http://software.internet2.edu/eduperson/internet2-mace-dir-eduperson-201602.html#eduPersonAffiliation
	 * List of possible faculty affiliations:
	 * <ul>
	 *     <li>employee</li>
	 *     <li>faculty</li>
	 *     <li>member</li>
	 *     <li>staff</li>
	 * </ul>
	 *
	 * List of possible student affiliations:
	 * <ul>
	 *     <li>affiliate</li>
	 *     <li>alum</li>
	 *     <li>library-walk-in</li>
	 *     <li>member</li>
	 *     <li>student</li>
	 * </ul>
	 *
	 * @param userIdentity
	 * @return
	 */
	public SamlAuthResults samlAffiliationResult(UserIdentity userIdentity) {
		SamlAuthResults samlAuthResults = new SamlAuthResults();

		// Hash it for efficiency
		HashSet<String> affiliationHash = new HashSet<>();
		if (userIdentity.getAffiliations() != null) {
			for (String affiliation : userIdentity.getAffiliations()) {
				affiliationHash.add(affiliation.toLowerCase());
			}

			// Check if this is a faculty user
			samlAuthResults.setFacultyFlag(
				affiliationHash.contains("staff")
					|| affiliationHash.contains("faculty")
					|| affiliationHash.contains("member")
					|| affiliationHash.contains("employee")

			);
			// Check if this is a student user
			samlAuthResults.setStudentFlag(
				affiliationHash.contains("student")
					|| affiliationHash.contains("affiliate")
					|| affiliationHash.contains("library-walk-in")
					|| affiliationHash.contains("alum")
					|| affiliationHash.contains("member")
			);
			// Minimal checks for a valid SAML user
			samlAuthResults.setCccIdFlag(
				userIdentity.getCccId() != null
			);
		}
		return samlAuthResults;
	}

	/**
	 * Container class for returning multiple values, in our case, the only ones we care about coming from the IDP
	 * to process inside of Assess.
	 */
	public class SamlAuthResults {
		private boolean studentFlag;
		private boolean facultyFlag;
		private boolean cccIdFlag;

		public boolean isStudentFlag() {
			return studentFlag;
		}

		public void setStudentFlag(boolean studentFlag) {
			this.studentFlag = studentFlag;
		}

		public boolean isFacultyFlag() {
			return facultyFlag;
		}

		public void setFacultyFlag(boolean facultyFlag) {
			this.facultyFlag = facultyFlag;
		}

		public boolean isCccIdFlag() {
			return cccIdFlag;
		}

		public void setCccIdFlag(boolean cccIdFlag) {
			this.cccIdFlag = cccIdFlag;
		}
	}

	private void redirectToError(HttpServletResponse response, CommonErrors error, UserAccountDto userAccountDto) throws IOException {
		StringBuilder errorRedirect = new StringBuilder(errorPageUrl);
		errorRedirect.append("?error=")
				.append(error.name());
		if (userAccountDto!= null) {
			errorRedirect.append("&expired=")
					.append(userAccountDto.isAccountExpired())
					.append("&locked=")
					.append(userAccountDto.isAccountLocked())
					.append("&enabled=")
					.append(userAccountDto.isEnabled());
		}

		// Detect the response committed error condition and bypass if needed.
		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + errorRedirect);
			return;
		}
		response.sendRedirect(errorRedirect.toString());
	}

	/**
	 * @param userIdentity parameter cannot be null
	 * @param userAccountDto parameter cannot be null
	 */
	public void unionProfileAndInternalCollegeAffiliations(UserIdentity userIdentity, UserAccountDto userAccountDto) {
		if (userAccountDto == null) {
			throw new IllegalArgumentException("userAccountDto cannot be null.");
		}
		/* Note: If the student profile service is not available, this causes a failure here because a user lookup will happen
		and fail. Turn it on in the application.properties file.  */
		StudentViewDto studentViewDto = studentReader.getStudentById(userIdentity.getCccId());
		Set<String> collegesFromStudentProfile = new HashSet<>();
		Map<String, Integer> collegeStatuses;
		if (studentViewDto == null) {
			collegeStatuses = new HashMap<>();
		} else {
			collegeStatuses = studentViewDto.getCollegeStatuses();
		}
		// It's not necessary to check the value of the key, but it it might be in the future.
		for (String collegeStatus : collegeStatuses.keySet()) {
			collegesFromStudentProfile.add(collegeStatus);
		}

		Set<String> collegesFromDatabase = userAccountDto.getCollegeIds();
		if (collegesFromDatabase == null) {
			collegesFromDatabase = new HashSet<>();
		}

		collegesFromDatabase.addAll(collegesFromStudentProfile);

		List<StudentCollegeAffiliationDto> studentCollegeAffiliationDtoList =
				studentCollegeAffiliationService.findByStudentCccId(userIdentity.getCccId());
		for (StudentCollegeAffiliationDto studentCollegeAffiliationDto : studentCollegeAffiliationDtoList) {
			collegesFromDatabase.add(studentCollegeAffiliationDto.getMisCode());
		}

		userAccountDto.setCollegeIds(collegesFromDatabase);
	}
}
