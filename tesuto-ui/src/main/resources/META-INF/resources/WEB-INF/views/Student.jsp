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

        <title ccc-update-page-title>Tesuto Student Home</title>

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
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_activations.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_placement.css' />

        <!-- VIEW STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/views/view_student.css' />




    </head>

    <!-- INITIAL MARKUP -->
    <body>
        <ccc-view-student></ccc-view-student>
        <c:if test="${google.analytics.environment == 'prod'}" >
            <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
        </c:if>

        <!-- SESSION CONFIGS -->
        <script>
            // load the server side configs so the config phase of application bootstrapping will have access
            var sessionConfigs = {
                uiIdleTimeoutDuration: ${uiIdleTimeoutDuration},
                student: ${user},
                studentCollegesInfo: ${studentCollegesInfo},
                disableAssessments: ${disableAssessments},
                disablePlacements: ${disablePlacements}
            };
        </script>


        <!--============ LIBRARY SCRIPTS ============-->

        <!-- GLOBAL LIBRARIES AND FRAMEWORK -->
        <jsp:include page="/WEB-INF/common/standardView.common.jsp" />


        <!--============ MODULE DEPENDENCIES ============-->

        <!-- MODULE : VIEW SERVER API MODULES -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.fakeData.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.activations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.activationSearch.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.assessmentSessions.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.assessmentMetadata.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.students.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.testlocations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.placement.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.placementcolleges.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.disciplines.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.collegedisciplines.jsp" />


        <!-- MODULE : ACTIVATIONS MODULE -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.activations.jsp" />

        <!-- MODULE : PLACEMENT MODULE -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.placement.jsp" />


        <!--============ VIEW MODULE ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.view.student.jsp" />

        <!-- INITIALIZATION -->
        <script>

            (function () {

                // when we are ready then start up the main workflow
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['CCC.View.Student']);
                });

            })();

        </script>
    </body>

</html>

