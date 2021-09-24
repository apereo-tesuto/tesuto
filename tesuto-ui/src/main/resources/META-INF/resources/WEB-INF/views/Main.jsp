<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<h1>${attribute}</h1>

<h2>
	<spring:message code="display.name" />
</h2>

<h3>Users, if they exist:</h3>
<ol>
	<c:forEach var="userAccountDto" items="${userAccountDtoList}">
		<li>${userAccountDto.username}
			(${userAccountDto.authorities.size()})</li>
		<c:forEach var="authority" items="${userAccountDto.authorities}">
			<li>Granted Authority ${authority.authority}</li>
		</c:forEach>
	</c:forEach>
</ol>

<h3>A single user pulled from cache.</h3>
A single cached user: ${user.username}

<c:if test="${google.analytics.environment == 'prod'}">
	<jsp:include page="/WEB-INF/common/scripts.common.jsp"/>
</c:if>
</body>
</html>
