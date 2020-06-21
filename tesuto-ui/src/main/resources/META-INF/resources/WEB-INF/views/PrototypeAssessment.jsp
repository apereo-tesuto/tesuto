<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html ccc-lang>
    <head>

        <meta charset="utf-8" />
        <meta equiv="X-UA-Compatible" content="IE=edge, chrome=1" />
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title ccc-update-page-title>Prototype Assessment Player</title>

        <meta name="description" content="" />
        <meta name="keywords" content="" />

        <!-- Cross Site Request Forgery Protection -->
        <meta name="_csrf" content="${_csrf.token}"/>
        <!-- default header name is X-CSRF-TOKEN -->
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <!-- SET THE BASE PATH USED BY RELATIVE UI RESOURCES AND ANGULAR ROUTER -->
        <base href="${pageContext.servletContext.contextPath}/" />


        <!--============ SESSION CONFIGS ============-->

        <script>

            // load the server side configs so the config phase of application bootstrapping will have access
            var sessionConfigs = {

                uiIdleTimeoutDuration: ${uiIdleTimeoutDuration},

                assessmentSession: {
                    "assessmentSessionId":"ee415712-1ea5-40d2-af0a-60952972470c",
                    "userId":"9",
                    "assessmentId":"a7e8f124-b70a-430e-aeba-73267933f19f",
                    "title": "Prototype Assessment",
                    "language":"en-US",
                    "navigationMode": 'NONLINEAR',
                    "submissionMode":null,
                    "assessmentSettings":null,
                    "currentTaskSet": false,
                    "responses":{},
                    "completionDate":null,
                    "taskSetIndex":1,
                    "completed":false,
                    "startDate": new Date().getTime(),
                    "timeRemaining":1000000
                },
                user: {
                    "userAccountId":9,
                    "username":"prototype",
                    "displayName":"Prototype User"
                }
            };

        </script>


        <!--============ STYLES ============-->

        <!-- COMMON STYLES -->
        <jsp:include page="/WEB-INF/common/styles.common.jsp" />

        <!-- MODULE STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_assess.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_components.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_assessment.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_asmt_player.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_calculator.css' />
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/ccc_activity_monitor.css' />

        <!-- VIEW STYLES -->
        <link rel='stylesheet' href='${pageContext.request.contextPath}/ui/styles/css/views/view_assessment_player.css' />


        <!--============ LIBRARY SCRIPTS ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.common.jsp" />


        <!--============ SERVER API VERSION CONFIGS ============-->

        <jsp:include page="/WEB-INF/common/scripts.api_versions.jsp" />


        <!--============ MODULE DEPENDENCIES ============-->

        <!-- MODULE : common CCC Assess application module -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.assess.jsp" />
        <!-- MODULE : common CCC components -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.components.jsp" />
        <!-- MODULE : CCC Calculator -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.calculator.jsp" />
        <!-- MODULE : CCC.Math -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.math.jsp" />
        <!-- MODULE : CCC Assessment (for rendering assessment items) -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.assessment.jsp" />
        <!-- MODULE: ASMT PLAYER -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.asmt_player.jsp" />

        <!-- MODULE: CCC ACTIVITY MONITOR (used only for visually testing activity) -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.activityMonitor.jsp" />

        <!-- API MODULES -->
        <jsp:include page="/WEB-INF/common/dynamic/scripts.api.fakeData.jsp" />


        <!--============ VIEW MODULE ============-->

        <jsp:include page="/WEB-INF/common/dynamic/scripts.view.assessmentPrototype.jsp" />


        <!-- INITIALIZATION -->
        <script>

            (function () {

                // when we are ready then start up the main workflow
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['CCC.View.AssessmentPrototype']);
                });

            })();

        </script>

    </head>


    <!-- INITIAL MARKUP -->
    <body>

    	<ui-view></ui-view>
        <c:if test="${google.analytics.environment == 'prod'}" >
            <jsp:include page="/WEB-INF/common/scripts.common.jsp" />
        </c:if>
    </body>

</html>

