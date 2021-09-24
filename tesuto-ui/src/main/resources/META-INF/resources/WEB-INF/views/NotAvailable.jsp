<%--
  User: scott.smith@isostech.com
  Date: 5/19/15
  Time: 11:56 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<title><spring:message code="notfound.title" /></title>
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
</head>

<body class="not-found">
	<header class="ccc-login-header">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<img class="logo-mark"
                    src="${pageContext.request.contextPath}/ui/resources/images/multiple_measures_white.png"
                    alt="CCC logo mark beta">
				</div>
			</div>
		</div>
	</header>

	<div class="container">
		<div class="row">
			<div
				class="col-xs-10 col-xs-offset-1 col-md-6 col-md-offset-3 ccc-login-card text-center">
				<div class="titlebar">
					<h1>Oops!</h1>
				</div>
				<div class="content">
					<p>This functionality is not available because supporting microservices are not activated.</p>
					<p>
						<strong>Error code 404.</strong>
					</p>
					<p>Maybe you'd like to:</p>
					<hr>
					<div class="actions">
						<a href="${landingPageUrl}" id="sign-in"
							class="btn btn-primary btn-lg">Sign back in</a>
					</div>
					<c:if test="${not empty homePageUrl}" >
					<p>Or you can return to your home page:</p>
					<div class="actions">
						<a href="${homePageUrl}" id="home-page"
							class="btn btn-primary btn-lg">Back To Home Page</a>
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
		document.getElementById("sign-in").focus();
	</script>
	<c:if test="${google.analytics.environment == 'prod'}" >
		<jsp:include page="/WEB-INF/common/scripts.common.jsp" />
	</c:if>

</body>
</html>