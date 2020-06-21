<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
	<title>Upload Assessment</title>
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

	<script language="javascript" type="text/javascript">
	    var baseUrl = "${pageContext.servletContext.contextPath}";
		var uploadUrl = baseUrl + 'qti-import-service/service/v1/competency-map/upload';
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
							url: uploadUrl,
							type: 'POST',
							data: new FormData(this),
							processData: false,
							contentType: false,
							dataType: false,
							//headers: {'X-CSRF-TOKEN': token},
							headers: headers,
							success: function (data, status, xhr) {
								var trHTML = "";
								if(data.validationWarnings != null && data.validationWarnings.length !== 0) {
									trHTML += '<div class="panel panel-warning">';
									trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Warnings</h3></div>';
									trHTML += '<div class="panel body">';
									trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Node</td><td>File</td><td>File Type</td><td>Line Number</td><td>Column Number</td></tr></thead>';
									$.each(data.validationWarnings, function (i, warning) {
										trHTML += '<tr><td>' + warning.message + '</td><td>' + warning.node + '</td><td>' + warning.file + '</td><td>' + warning.fileType + '</td><td>' + warning.line + '</td><td>' + warning.column + '</td></tr>';
									});
									trHTML += '</table>';
									trHTML += '</div></div>'; // close panel body  close panel
								}

								trHTML += '<div class="panel panel-success">';
								trHTML += '<div class="panel-heading"><h3 class="panel-title">Success</h3></div>';
								trHTML += '<div class="panel body">';
								trHTML += '<table class="table table-striped">';

								if(data.competencyMapUrls != null){
									$.each(data.competencyMapUrls, function (i, competencyMapUrl) {
										trHTML += '<tr><td>Competency Mpas: <a target="_blank" href=\"' + competencyMapUrl + '\">' + competencyMapUrl + '</a></tr></td>';
									});
								}
								if(data.competencyMapOrderUrls != null) {
									$.each(data.competencyMapOrderUrls, function (discipline, url) {
										trHTML += '<tr><td>Competency Map Order for discipline ' + discipline +' URL:<a target="_blank" href=\"' + url + '\">' + url + '</a></tr></td>';
									});
								} else {
									trHTML += '<tr><td>No competency maps are currently associated to this assessment, so no competency map order objects were created.</tr></td>';
								}
								trHTML += '</table>';
								trHTML += '</div></div>';

				                $('#import_results').append(trHTML);
				                },
				              error: function(data,status,message){
								  var trHTML = "";
								  var responseData = data.responseJSON;
								  $('#import_results').empty();
								  if(responseData.validationErrors !== null){
									  trHTML += '<div class="panel panel-danger">';
									  trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Errors</h3></div>';
									  trHTML += '<div class="panel body">';
									  trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Node</td><td>File</td><td>File Type</td><td>Line Number</td><td>Column Number</td></tr></thead>';
									  $.each(responseData.validationErrors, function (i, error) {
										  trHTML += '<tr><td>' + error.message + '</td><td>' + error.node + '</td><td>' + error.file + '</td><td>' + error.fileType + '</td><td>' + error.line + '</td><td>' + error.column + '</td></tr>';
									  });
									  trHTML += '</table>';
									  trHTML += '</div></div>'; // close panel body  close panel
								  }

								  if(responseData.validationWarnings !== null && responseData.validationWarnings.length !== 0) {
									  trHTML += '<div class="panel panel-warning">';
									  trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Warnings</h3></div>';
									  trHTML += '<div class="panel body">';
									  trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Node</td><td>File</td><td>File Type</td><td>Line Number</td><td>Column Number</td></tr></thead>';
									  $.each(responseData.validationWarnings, function (i, warning) {
										  trHTML += '<tr><td>' + warning.message + '</td><td>' + warning.node + '</td><td>' + warning.file + '</td><td>' + warning.fileType + '</td><td>' + warning.line + '</td><td>' + warning.column + '</td></tr>';
									  });
									  trHTML += '</table>';
									  trHTML += '</div></div>'; // close panel body  close panel
								  }

								  if(responseData.validationWarnings === null && responseData.validationErrors === null) {
									  alert("Unable to parse files sent to server:" + message);
								  }
								  $('#import_results').append(trHTML);
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
					<label>Upload by Selecting a Content Package ZIP file or Assessment Item XML file to upload and validate:</label>
					<div class="input-group">
						<label class="input-group-btn" for="file">
							<span class="btn btn-primary">Browse&hellip; <input id="file" type="file" name="file" style="display: none;" multiple></span>
						</label>
						<input id="name" type="text" name="text" class="form-control" readonly />
						<input id="contentType" name="contentType" type="hidden" value="" />
					</div>
					<div>
						<aside>
							<p>You may upload any of the following to the validator:</p>
							<ul>
								<li>Competency Maps and Competency schemas.</li>
								<li>File must be an single xml or entire package in zip format.</li>
							</ul>
						</aside>
					</div>
					<input id="submitFile" type="submit" class="btn btn-default" value="Click Here To Upload!" >
					<!-- Turns out IE 10/11 both have a bug. Go figure.
                        https://blog.yorkxin.org/2014/02/06/ajax-with-formdata-is-broken-on-ie10-ie11
                        The input with a value set at the end of the form magically fixes the bug.
                        DO NOT REMOVE input, it will break IE 10/11 -->
					<div>
						<input id="dummy" name="dummy" type="hidden" value="dummy"/>
					</div>
				</form>
			</div>
		</div>
	</div>

</div>
<div class="clear" style="padding-bottom: 1em"></div>
</div>
<div id="createdCompetencyMaps">
	<div id="import_results">
	</div>
</div>
		<div class="actions">
						<a href="${homePageUrl}" id="home-page"
							class="btn btn-primary btn-lg">Back To Home Page</a>
	</div>
<c:if test="${google.analytics.environment == 'prod'}" >
	<jsp:include page="/WEB-INF/common/scripts.common.jsp" />
</c:if>
</body>
</html>
