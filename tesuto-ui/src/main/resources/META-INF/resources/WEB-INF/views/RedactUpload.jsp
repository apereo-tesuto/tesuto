<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!doctype html>
<html>
<head>
<title>Redact Assessment</title>
<!-- COMMON STYLES -->
<jsp:include page="/WEB-INF/common/standardView.common.jsp" />
<script type="text/javascript" src="../../ui_lib/jquery-2.1.4/jquery-2.1.4.min.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/ui_lib/jquery-2.1.4/jquery-2.1.4.min.js"></script>

<meta content="text/html" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-control"
	content="public, max-age=31536000, must-revalidate=false" />

<!-- Cross Site Request Forgery Protection -->
<meta name="_csrf" content="${_csrf.token}"/>
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<script language="javascript" type="text/javascript">
    var baseUrl = "${pageContext.servletContext.contextPath}";
    
    </script>
</head>

<body  style="margin-left:70px;margin-top:70px">

	<div class="header">


		<div class="header-text">Redact Assessment</div>

	</div>

	<div class="wrapper"></div>
	<div class="main-content-wrapper">
		<div class="main-content">

			<h2>QTI Redact</h2>

			<div>
				<p>Use this form to redact an item or a assessment.</p>
			</div>


			<form id="uploadRedactForm"
				action="/service/v1/redact/upload"
				enctype="multipart/form-data" method="post" accept-charset="utf-8">

				<fieldset>
					<div>
						<label for="file">Redact by Selecting a Content Package
							ZIP file or Assessment Item XML file to upload and validate:</label> <br />
						<input id="file" name="file" type="file" />
					</div>
					<div>
						<input id="name" name="name" type="text" value="" />
					</div>
					<div>
						<input id="contentType" name="contentType" type="hidden" value="" />
					</div>
					<div>
						<aside>
							<p>You may upload any of the following to the validator:</p>
							<ul>
								<li>An IMS Content Package containing a QTI (2.0 - 2.20)
									Assessment Item(s), plus any related resources, such as images,
									response processing templates...</li>
								<li>An IMS Content Package containing a QTI 2.1 Assessment
									Test(s), its Assessment Items, plus any related resources.</li>
								<li>A self-contained QTI 2.1 (or 2.0) Assessment Item XML
									file(s).</li>
							</ul>
						</aside>
					</div>
				</fieldset>
				<div class="clear"></div>
				<fieldset>
					<div>
						<label for="submit">Hit "Upload and Run"</label> <br /> <input
							id="submitFile" type="submit" value="Click Here To Upload!" />
					</div>
				</fieldset>
			</form>


		</div>
	</div>
	<div class="clear"></div>
	</div>
	<div id="createdAssessmentSessions">
		<table id="records_table">
		</table>
	</div>

	<c:if test="${google.analytics.environment == 'prod'}" >
		<jsp:include page="/WEB-INF/common/scripts.common.jsp" />
	</c:if>
</body>