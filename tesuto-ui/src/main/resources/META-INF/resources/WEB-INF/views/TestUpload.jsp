<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
	<title>Upload Some File</title>
	<!-- COMMON STYLES -->
	<jsp:include page="/WEB-INF/common/styles.common.jsp"/>
	<jsp:include page="/WEB-INF/common/standardView.common.jsp"/>
	<script type="text/javascript"
			src="${pageContext.request.contextPath}/ui_lib/jquery-2.1.4/jquery-2.1.4.min.js"></script>

	<meta content="text/html"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Cache-control"
		  content="public, max-age=31536000, must-revalidate=false"/>

	<!-- Cross Site Request Forgery Protection -->
	<meta name="_csrf" content="${_csrf.token}"/>
	<!-- default header name is X-CSRF-TOKEN -->
	<meta name="_csrf_header" content="${_csrf.headerName}"/>

	<script type="text/javascript">
		var baseUrl = "${pageContext.servletContext.contextPath}";
		$(document).ready(function () {

			$(document).on('change', ':file', function() {
				var input = $(this),
						numFiles = input.get(0).files ? input.get(0).files.length : 1,
						label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
				input.trigger('fileselect', [numFiles, label]);
			});

			// We can watch for our custom `fileselect` event like this
			$(document).ready( function() {
				$(':file').on('fileselect', function(event, numFiles, label) {

					var input = $(this).parents('.input-group').find(':text'),
							log = numFiles > 1 ? numFiles + ' files selected' : label;

					if( input.length ) {
						input.val(log);
					} else {
						if( log ) alert(log);
					}

				});
			});

			$('#uploadForm')
				.submit(function (e) {
					$('#import_results').empty();
					var token = $("meta[name='_csrf']").attr("content");
					var headerKey = $("meta[name='_csrf_header']").attr("content");
					var headers = {};
					headers[headerKey] = token;
					$.ajax({
						url: baseUrl + '/service/v1/class-report/upload',
						type: 'POST',
						data: new FormData(this),
						processData: false,
						contentType: false,
						dataType: false,
						//headers: {'X-CSRF-TOKEN': token},
						headers: headers,
						success: function (data, status, xhr) {
							$("#importedFilename").val(data);
						},
						error: function(data,status,message){
							var trHTML = "";
							var responseData = data.responseJSON;
							console.log(responseData);
						}
					});
					e.preventDefault();
				});

			$('#generateForm')
				.submit(function (e) {
					var token = $("meta[name='_csrf']").attr("content");
					var headerKey = $("meta[name='_csrf_header']").attr("content");
					var headers = {};
					headers[headerKey] = token;
					$.ajax({
						url: baseUrl + '/service/v1/class-report/generate',
						type: 'POST',
						data: JSON.stringify({
								'collegeId': $('#collegeId').val(),
								'courseId': $('#courseId').val(),
								'importedFilename': $('#importedFilename').val()
						}),
						dataType: 'json',
						contentType : "application/json",
						//headers: {'X-CSRF-TOKEN': token},
						headers: headers,
						success: function (data, status, xhr) {
							console.log(date);
						},
						error: function(data,status,message){
							console.log(data);
						}
					});
					e.preventDefault();
				});

			$('#fullForm')
			.submit(function (e) {
				$('#import_results').empty();
				var token = $("meta[name='_csrf']").attr("content");
				var headerKey = $("meta[name='_csrf_header']").attr("content");
				var headers = {};
				headers[headerKey] = token;
				$.ajax({
					url: baseUrl + '/service/v1/class-report/validateAndGenerate',
					type: 'POST',
					data: new FormData(this),
					processData: false,
					contentType: false,
					dataType: false,
					//headers: {'X-CSRF-TOKEN': token},
					headers: headers,
					success: function (data, status, xhr) {
						$("#importedFilename").val(data);
					},
					error: function(data,status,message){
						var trHTML = "";
						var responseData = data.responseJSON;
						console.log(responseData);
					}
				});
				e.preventDefault();
			});

		});
	</script>
</head>

<body style="margin-left:70px;margin-top:70px;margin-right:70px;">

<div class="wrapper"></div>
<div class="main-content-wrapper">
	<div class="main-content">
		<div class="panel panel-default">
			<div class="panel-heading"><h2>Upload</h2></div>
			<div class="panel-body">
				<form id="uploadForm" action="javascript:;" enctype="multipart/form-data" method="post" accept-charset="utf-8">
					<label>Upload file :</label>
					<div class="input-group">
						<label class="input-group-btn" for="file">
							<span class="btn btn-primary">Browse&hellip; <input id="file" type="file" name="file" style="display: none;" multiple></span>
						</label>
						<input id="name" type="text" name="text" class="form-control" readonly />
						<input id="contentType" name="contentType" type="hidden" value="" />
					</div>
					<input id="submitFile" type="submit" class="btn btn-default" value="Click Here To Upload!" >
				</form>
			</div>
			<div class="panel-heading"><h2>Generate</h2></div>
			<div class="panel-body">
				<form id="generateForm" action="javascript:;" enctype="multipart/form-data" method="post" accept-charset="utf-8">
					<input id="collegeId" name="collegeId" type="text" value="ZZZ1">
					<input id="courseId" name="courseId" type="text" value="126376218-1287381">
					<input id="importedFilename" name="importedFilename" type="hidden">
					<input id="generateReport" type="submit" class="btn btn-default" value="Generate Report!" >
				</form>
			</div>
			<div class="panel-heading"><h2>Validate and Generate</h2></div>
			<div class="panel-body">
				<form id="fullForm" action="javascript:;" enctype="multipart/form-data" method="post" accept-charset="utf-8">
					<label>Upload file :</label>
					<div class="input-group">
						<label class="input-group-btn" for="file">
							<span class="btn btn-primary">Browse&hellip; <input id="file" type="file" name="file" style="display: none;" multiple></span>
						</label>
						<input id="name" type="text" name="text" class="form-control" readonly />
						<input id="collegeId" name="collegeId" type="text" value="ZZZ1">
						<input id="courseId" name="courseId" type="text" value="126376218-1287381">
						<input id="contentType" name="contentType" type="hidden" value="" />
					</div>
					<input id="submitFile" type="submit" class="btn btn-default" value="Click Here To Upload!" >
				</form>
			</div>
		</div>
	</div>
</div>
</body>
</html>
