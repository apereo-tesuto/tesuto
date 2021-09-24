<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html ccc-lang moznomarginboxes>

    <head>

        <meta charset="utf-8" />
        <meta equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title ccc-update-page-title>Printable Assessment</title>

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

        <!-- VIEW STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/views/view_assessment_print.css' />

    </head>

    <!-- INITIAL MARKUP -->
    <body>
        <ccc-view-assessment-print-detached></ccc-view-assessment-print-detached>
        <c:if test="${google.analytics.environment == 'prod'}" >
            <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
        </c:if>

        <!--============ SESSION CONFIGS ============-->

        <script>
            // load the server side configs so the config phase of application bootstrapping will have access
            var sessionConfigs = {
                uiIdleTimeoutDuration: ${uiIdleTimeoutDuration},
                miscode: "${misCode}",
                assessmentSession: ${assessmentSession}
            };
            // we conditionally may have a user object
            <c:if test="${not empty studentProfile}">
               sessionConfigs.user = ${studentProfile};
            </c:if>
        </script>

        <!--============ LIBRARY SCRIPTS ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.common.jsp" />


        <!--============ SERVER API VERSION CONFIGS ============-->

        <jsp:include page="/WEB-INF/common/scripts.api_versions.jsp" />

        <!-- API MODULES -->

        <!--============ MODULE DEPENDENCIES ============-->

        <!-- MODULE : common CCC Assess application module -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.assess.jsp" />
        <!-- MODULE : common CCC components -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.components.jsp" />
        <!-- MODULE : CCC.Math rendering -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.math.jsp" />
        <!-- MODULE : CCC.AssessmentPrint -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.assessmentPrint.jsp" />


        <!--============ VIEW MODULE ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.view.assessmentPrintDetached.jsp" />


        <!-- INITIALIZATION -->
        <script>

            (function () {

                // when we are ready then start up the main workflow
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['CCC.View.AssessmentPrintDetached']);
                });

            })();

        </script>
    </body>

</html>

