<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!doctype html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html ccc-lang>

    <head>
        <meta charset="utf-8" />
        <meta equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="apple-mobile-web-app-capable" content="yes" />

        <title>Healthcheck Dashboard</title>

        <meta name="description" content="" />
        <meta name="keywords" content="" />

        <!-- Cross Site Request Forgery Protection -->
        <meta name="_csrf" content="${_csrf.token}"/>
        <!-- default header name is X-CSRF-TOKEN -->
        <meta name="_csrf_header" content="${_csrf.headerName}"/>


        <!-- SET THE BASE PATH USED BY RELATIVE UI RESOURCES AND ANGULAR ROUTER -->
        <base href="${pageContext.servletContext.contextPath}/" />


        <!--============ STYLES ============-->

        <!-- COMMON STYLES -->
        <jsp:include page="/WEB-INF/common/styles.common.jsp" />
        

        <!-- MODULE STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_assess.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_components.css' />

        <!-- LIBRARY STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui_lib/c3/c3.min.css' />

        <!-- VIEW STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/views/view_dashboard.css' />

    </head>

    <!-- INITIAL MARKUP -->
    <body>

        <ccc-view-dashboard></ccc-view-dashboard>

        <c:if test="${google.analytics.environment == 'prod'}" >
            <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
        </c:if>

        <!-- SESSION CONFIGS -->
        <script>
            // load the server side configs so the config phase of application bootstrapping will have access
            var sessionConfigs = ${sessionConfigs};
        </script>


        <!--============ LIBRARY SCRIPTS ============-->

        <!-- GLOBAL LIBRARIES AND FRAMEWORK -->
        <jsp:include page="/WEB-INF/common/standardView.common.jsp" />
		<jsp:include page="/WEB-INF/common/dynamic/scripts.api.dashboard.jsp" />
	    <jsp:include page="/WEB-INF/common/dynamic/scripts.api.onboard.college.jsp" />
		

        <!--============ MODULE DEPENDENCIES ============-->

        <!--============ VIEW MODULE ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.view.dashboard.jsp" />
        

        <!-- INITIALIZATION -->
        <script>

            (function () {

                // when we are ready then start up the main workflow
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['CCC.View.Dashboard']);
                });

            })();

        </script>
    </body>

</html>

