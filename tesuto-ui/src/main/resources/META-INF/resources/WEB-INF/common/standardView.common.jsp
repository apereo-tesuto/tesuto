<!--============ GLOBAL LIBRARIES AND FRAMEWORK ============-->

<jsp:include page="/WEB-INF/common/dynamic/scripts.common.jsp" />


<!--============ SERVER API VERSION CONFIGS ============-->

<jsp:include page="/WEB-INF/common/scripts.api_versions.jsp" />


<!--============ MODULE DEPENDENCIES ============-->

<!-- MODULE : common CCC Assess application module -->
<jsp:include page="/WEB-INF/common/dynamic/scripts.assess.jsp" />
<!-- MODULE : common CCC components -->
<jsp:include page="/WEB-INF/common/dynamic/scripts.components.jsp" />
<!-- MODULE : common CCC.View.Layout and CCC.View.Common -->
<jsp:include page="/WEB-INF/common/dynamic/scripts.viewStandardLayout.jsp" />
<jsp:include page="/WEB-INF/common/dynamic/scripts.viewStandardCommon.jsp" />