<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!doctype html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html ccc-lang>
    <head>
        <meta charset="utf-8" />
        <meta equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="apple-mobile-web-app-capable" content="yes" />

        <title>Tesuto Home</title>

        <meta name="description" content="" />
        <meta name="keywords" content="" />
		
        <sec:csrfMetaTags/>

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
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/views/view_home.css' />
    </head>


    <!-- INITIAL MARKUP -->
    <body>
        <ccc-view-home></ccc-view-home>
        <c:if test="${google.analytics.environment == 'prod'}" >
            <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
        </c:if>

        <!-- SESSION CONFIGS -->
        <script>
            // load the server side configs so the config phase of application bootstrapping will have access
            var sessionConfigs = {
                uiIdleTimeoutDuration: ${uiIdleTimeoutDuration},
                user: ${user},
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
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.students.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.activations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.activationSearch.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.assessments.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.accommodations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.passcodes.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.districts.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.colleges.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.assessmentSessions.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.users.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.roles.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.batchActivations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.disciplines.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.mmsubjectareas.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.courses.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.competencymapdiscipline.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.competencymaps.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.competencygroups.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.collegeAttributes.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.testlocations.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.placement.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.placementcolleges.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.placementrequest.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.collegedisciplines.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.events.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.competencydisciplines.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.rulescolleges.jsp" />
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.classreport.jsp" />

        <!-- MODULE : ACTIVATIONS MODULE -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.activations.jsp" />

        <!-- MODULE : PLACEMENT MODULE -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.placement.jsp" />


        <!--============ VIEW MODULE ==============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.view.home.jsp" />

        <!-- INITIALIZATION -->
        <script>

            (function () {

                // when we are ready then start up the main workflow
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['CCC.View.Home']);
                });

            })();

        </script>
    </body>

</html>

