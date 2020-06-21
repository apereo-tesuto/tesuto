<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<title><spring:message code="accessdenied.title" /></title>
<!-- COMMON STYLES -->
<jsp:include page="/WEB-INF/common/styles.common.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/ui/resources/css/static-pages.css" />
<meta charset="utf-8" />
<meta equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="apple-mobile-web-app-capable" content="yes" />

<!-- Cross Site Request Forgery Protection -->
<meta name="_csrf" content="${_csrf.token}"/>
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<script type="text/javascript">
    var errorCode = "<c:out value='${errorCode}' />";
    var expired = "<c:out value='${expired}' />";
    var locked = "<c:out value='${locked}' />";
    var enabled = "<c:out value='${enabled}' />";
    var orginalErrorMessage = "<c:out value='${orginalErrorMessage}' />";
    var errorList = [
        {
            'code': 'DISABLED',
            'title': 'Account Disabled',
            'message': 'Your user account is disabled.'
        },
        {
            'code': 'EXPIRED',
            'title': 'Account Expired',
            'message': 'Your account has expired.'
        },
        {
            'code': 'LOCKED',
            'title': 'Account Locked',
            'message': 'Your account is locked.'
        },
        {
            'code': 'NO_CCCID_EPPN',
            'title': 'Missing CCCID',
            'message': 'Your account has no CCCID or EPPN.'
        },
        {
            'code': 'NO_COLLEGES',
            'title': 'No College Association',
            'message': 'Your account is not associated with any colleges.'
        },
        {
            'code': 'NOT_FOUND',
            'title': 'Account Not Found',
            'message': 'No account was found for that username.'
        },
        {
            'code': 'TIMED_OUT',
            'title': 'Login Timeout',
            'message': 'Your login has timed out.'
        },
        {
            'code': 'UNAUTHORIZED',
            'title': 'Unauthorized',
            'message': 'You are unauthorized to do that.'
        },
        {
            'code': 'INVALID_AFFILIATIONS',
            'title': 'Invalid Affiliations',
            'message': 'User has invalid edu-person affiliations.'
        },
        {
            'code': 'INVALID_REQUEST',
            'title': 'Invalid Request',
            'message': 'The request contained an invalid Relay State.'
        },
        {
            'code': "423",
            'title': 'Account Expired',
            'message': 'Your account has expired.'
        },
        {
            'code': "404",
            'title': 'Page Not Found',
            'message': 'No page was found for that url.'
        },
        {
            'code': "598",
            'title': 'System Timeout',
            'message': 'System timed out.'
        },
        {
            'code': "500",
            'title': 'Internal Service Error',
            'message': 'Something happened on the server. Either try again or go to home page.'
        },
        {
            'code': "403",
            'title': 'Forbidden',
            'message': 'You are forbidden to do that.  You must login first.'
        },
        {
            'code': "401",
            'title': 'Unauthorized',
            'message': 'You are not allowed to do that.  Please go back or to your home page.'
        },
        {
            'code': "415",
            'title': 'Unsupported Media Time',
            'message': 'Unsupported Media Ty[pes].'
        },
        {
            'code': "400",
            'title': 'Invalid Request',
            'message': 'The request contained an invalid Relay State.'
        }
    ];
</script>
</head>

<body class="error">
    <header class="ccc-login-header">
        <div class="container">
            <div class="row">
                <div class="col-xs-12">
                    <img class="logo-mark"
                        src="${pageContext.request.contextPath}/ui/resources/images/ccc-logomark-small.png"
                        alt="CCC logo mark"> <span class="logo-text">CCC
                        Assess Beta</span>
                </div>
            </div>
        </div>
    </header>

    <div class="subhead">
        <div class="container">
            <div class="row">
                <div class="col-xs-12">
                    <h1 class="title">My Account</h1>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-xs-12 col-sm-8 col-md-6 col-lg-6">
                <h2 class="view-title">There is a problem with your account.</h2>
                <p>Looks like an important bit of account information did not come across the wire.</p>
                <h3>The problem:</h3>
                <p>The system is reporting this issue:</p>
                <div class="alert alert-info alert-with-icon">
                    <p><span class="icon fa fa-info-circle" role="presentation" aria-hidden="true"></span> <span id="errorMessage"></span></p>
                	<p><span class="icon fa fa-info-circle" role="presentation" aria-hidden="true"></span> <span id="orginalErrorMessage"></span></p>
                	
                </div>
                <h3>To fix the problem:</h3>
                <p><strong>Contact your administrator</strong> and specify the problem listed above. An administrator with the proper permission can get you back to working order.</p>
                <p>In the meantime, you will be unable to access the rest of this app. We apologize for the inconvenience.</p>
            </div>
            <div class="content">
            		 <c:if test="${not empty homePageUrl}" >
					<p>Feel free to return re-login </p>
					<div class="actions">
						<a href="${landingPageUrl}" id="sign-in"
							class="btn btn-primary btn-lg">Sign back in</a>
					</div>
					<p>Or you can return to your home page:</p>
					<div class="actions">
						<a href="${homePageUrl}" id="home-page"
							class="btn btn-primary btn-lg">Back To Home Page</a>
					</div>
					</c:if>
					<c:if test="${empty homePageUrl}" >
					  <p>You do not appear to have been authenticated, please sign-in</p>
					<div class="actions">
						<a href="${landingPageUrl}" id="sign-in"
							class="btn btn-primary btn-lg">Sign in</a>
					</div>
					</c:if>
				</div>
			</div>
        </div>
    </div>

    <footer class="ccc-login-footer">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <img class="footer-logo"
                        src="${pageContext.request.contextPath}/ui/resources/images/ccc-logo-gray.png"
                        alt="Apereo Open Access logo">
                </div>
                <div class="col-md-8">
                    <div class="footer-links">
                        <ul class="list-inline">
                            <li><a href="#">Terms of Use</a></li>
                            <li><a href="#">Privacy Statement</a></li>
                            <li><a href="#">Accessibility</a></li>
                            <li><a href="#">Contact Us</a></li>
                        </ul>
                        <p>Tesuto is licensed to the Apereo Foundation under the Creative Commons Attribution 3.0 United States License.</p>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <script>
        for (var i = 0; i < errorList.length; i++) {
            if (errorList[i].code === errorCode) {
                document.getElementById('errorMessage').innerHTML = errorList[i].message;
            }
        }
        document.getElementById('orginalErrorMessage').innerHTML = orginalErrorMessage;
    </script>
    <c:if test="${google.analytics.environment == 'prod'}" >
        <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
    </c:if>
</body>
</html>