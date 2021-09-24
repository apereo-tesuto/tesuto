<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!doctype html>
<html>
<head>
<title>Upload Assessment</title>
<!-- COMMON STYLES -->
<jsp:include page="/WEB-INF/common/styles.common.jsp" />
<jsp:include page="/WEB-INF/common/standardView.common.jsp" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ui_lib/jquery-2.1.4/jquery-2.1.4.min.js"></script>

<meta content="text/html" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-control"
	content="public, max-age=31536000, must-revalidate=false" />

<!-- Cross Site Request Forgery Protection -->
<meta name="_csrf" content="${_csrf.token}" />
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}" />
<script language="javascript" type="text/javascript">
		var uploadAssessmentUrl = "${uploadAssessmentUrl}";
		$(document).ready(function () {

			$(document).on('change', '#assessmentFile', function() {
				var input = $(this),
						numFiles = input.get(0).files ? input.get(0).files.length : 1,
						label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
				input.trigger('assessmentfileselect', [numFiles, label]);
			});

			// We can watch for our custom `fileselect` event like this
			$(document).ready( function() {
				$('#assessmentFile').on('assessmentfileselect', function(event, numFiles, label) {

					var input = $(this).parents('.input-group-assessment').find(':text'),
							log = numFiles > 1 ? numFiles + ' files selected' : label;

					if( input.length ) {
						input.val(log);
					} else {
						if( log ) alert(log);
					}

				});
			});

			$('#uploadAssessmentForm')
					.submit(function (e) {
						$('#import_assessment_results').empty();
						var token = $("meta[name='_csrf']").attr("content");
						var headerKey = $("meta[name='_csrf_header']").attr("content");
						var headers = {};
						$('#import_assessment_results')
						.append(
								"Posting Request To Upload Assessment.");
						headers[headerKey] = token;
						$.ajax({
							url: uploadAssessmentUrl,
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

								if(data.assessmentSessionUrls != null){
									$.each(data.assessmentSessionUrls, function (i, assessmentSessionUrl) {
										trHTML += '<tr><td>Assessment Session URL: <a target="_blank" href=\"' + assessmentSessionUrl + '\">' + assessmentSessionUrl + '</a></tr></td>';
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

				                $('#import_assessment_results').append(trHTML);
				                },
				              error: function(data,status,message){
								  var trHTML = "";
								  var responseData = data.responseJSON;
								  $('#import_assessment_results').empty();
									if(responseData == null) {
										trHTML += '<div class="panel panel-warning">';
										trHTML += '<div class="panel-heading"><h3 class="panel-title">Uploading Assessment Failed</h3></div>';
										trHTML += '<div class="panel body">';
										trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
									
										trHTML += '<tr ><td colspan="2">Uhoh something happened while uploading the assessment package.</td></tr>';
										trHTML += '<tr><td>Http Status</td><td>' + data.status + '</td></tr>';
										trHTML += '<tr><td>Error</td><td>' + data.statusText + '</td></tr>';
									trHTML += '</table>';
									trHTML += '</div></div>';

									$('#import_assessment_results')
											.append(
													trHTML);
										return;
									}
									if(responseData.validationErrors == null  && responseData.validationWarnings == null) {
										trHTML += '<div class="panel panel-warning">';
										trHTML += '<div class="panel-heading"><h3 class="panel-title">Uploading Assessment Failed</h3></div>';
										trHTML += '<div class="panel body">';
										trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
									
										trHTML += '<tr ><td colspan="2">Uhoh something happened while uploading the assessment package.</td></tr>';
										trHTML += '<tr><td>Http Status</td><td>' + responseData.status + '</td></tr>';
										trHTML += '<tr><td>Error</td><td>' + responseData.error + '</td></tr>';
										trHTML += '<tr><td>Message</td><td>' + responseData.message + '</td></tr>';
									trHTML += '</table>';
									trHTML += '</div></div>';

									$('#import_assessment_results')
											.append(
													trHTML);
										return;
									}
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
								  $('#import_assessment_results').append(trHTML);
				                }
						});
						e.preventDefault();
					});

		});
	</script>
<!-- Competency Map Upload -->
<script language="javascript" type="text/javascript">
	var baseUrl = "${pageContext.servletContext.contextPath}";
	var uploadCompetencyMapUrl = baseUrl
			+ 'qti-import-service/service/v1/competency-map/upload';
	$(document)
			.ready(
					function() {

						$(document)
								.on(
										'change',
										'#competencyMapFile',
										function() {
											var input = $(this), numFiles = input
													.get(0).files ? input
													.get(0).files.length : 1, label = input
													.val().replace(/\\/g, '/')
													.replace(/.*\//, '');
											input.trigger('competencymapfileselect', [
													numFiles, label ]);
										});

						// We can watch for our custom `fileselect` event like this
						$(document)
								.ready(
										function() {
											$('#competencyMapFile')
													.on(
															'competencymapfileselect',
															function(event,
																	numFiles,
																	label) {

																var input = $(
																		this)
																		.parents(
																				'.input-group-competency-map')
																		.find(
																				':text'), log = numFiles > 1 ? numFiles
																		+ ' files selected'
																		: label;

																if (input.length) {
																	input
																			.val(log);
																} else {
																	if (log)
																		alert(log);
																}

															});
										});

						$('#uploadCompetencyMapForm')
								.submit(
										function(e) {
											$('#import_competency_map_results').empty();
											$('#import_competency_map_results')
											.append("Uploading Competency Map, may take some time.");
											var token = $("meta[name='_csrf']")
													.attr("content");
											var headerKey = $(
													"meta[name='_csrf_header']")
													.attr("content");
											var headers = {};
											headers[headerKey] = token;
											$
													.ajax({
														url : uploadCompetencyMapUrl,
														type : 'POST',
														data : new FormData(
																this),
														processData : false,
														contentType : false,
														dataType : false,
														//headers: {'X-CSRF-TOKEN': token},
														headers : headers,
														success : function(
																data, status,
																xhr) {
															var trHTML = "";
															if (data.validationWarnings != null
																	&& data.validationWarnings.length !== 0) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Warnings</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Node</td><td>File</td><td>File Type</td><td>Line Number</td><td>Column Number</td></tr></thead>';
																$
																		.each(
																				data.validationWarnings,
																				function(
																						i,
																						warning) {
																					trHTML += '<tr><td>'
																							+ warning.message
																							+ '</td><td>'
																							+ warning.node
																							+ '</td><td>'
																							+ warning.file
																							+ '</td><td>'
																							+ warning.fileType
																							+ '</td><td>'
																							+ warning.line
																							+ '</td><td>'
																							+ warning.column
																							+ '</td></tr>';
																				});
																trHTML += '</table>';
																trHTML += '</div></div>'; // close panel body  close panel
															}

															trHTML += '<div class="panel panel-success">';
															trHTML += '<div class="panel-heading"><h3 class="panel-title">Success</h3></div>';
															trHTML += '<div class="panel body">';
															trHTML += '<table class="table table-striped">';

															if (data.competencyMapUrls != null) {
																$
																		.each(
																				data.competencyMapUrls,
																				function(
																						i,
																						competencyMapUrl) {
																					trHTML += '<tr><td>Competency Mpas: <a target="_blank" href=\"' + competencyMapUrl + '\">'
																							+ competencyMapUrl
																							+ '</a></tr></td>';
																				});
															}
															if (data.competencyMapOrderUrls != null) {
																$
																		.each(
																				data.competencyMapOrderUrls,
																				function(
																						discipline,
																						url) {
																					trHTML += '<tr><td>Competency Map Order for discipline '
																							+ discipline
																							+ ' URL:<a target="_blank" href=\"' + url + '\">'
																							+ url
																							+ '</a></tr></td>';
																				});
															} else {
																trHTML += '<tr><td>No competency maps are currently associated to this assessment, so no competency map order objects were created.</tr></td>';
															}
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_competency_map_results')
																	.append(
																			trHTML);
														},
														error : function(data,
																status, message) {
															var trHTML = "";
															var responseData = data.responseJSON;
															$('#import_competency_map_results')
																	.empty();
															if(responseData == null) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Uploding Completency Map Failed</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
															
																trHTML += '<tr ><td colspan="2">Uhoh something happened while uploading competency maps.</td></tr>';
																trHTML += '<tr><td>Http Status</td><td>' + data.status + '</td></tr>';
																trHTML += '<tr><td>Error</td><td>' + data.statusText + '</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_competency_map_results')
																	.append(
																			trHTML);
																return;
															}
															if(responseData.validationErrors == null  && responseData.validationWarnings == null) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Uploding Completency Map Failed</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
															
																trHTML += '<tr ><td colspan="2">Uhoh something happened while uploading competency map.</td></tr>';
																trHTML += '<tr><td>Http Status</td><td>' + responseData.status + '</td></tr>';
																trHTML += '<tr><td>Error</td><td>' + responseData.error + '</td></tr>';
																trHTML += '<tr><td>Message</td><td>' + responseData.message + '</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_competency_map_results')
																	.append(
																			trHTML);
																return;
															}
															

																
															if (responseData.validationErrors !== null && responseData.validationErrors.length > 0) {
																trHTML += '<div class="panel panel-danger">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Errors</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Node</td><td>File</td><td>File Type</td><td>Line Number</td><td>Column Number</td></tr></thead>';
																$
																		.each(
																				responseData.validationErrors,
																				function(
																						i,
																						error) {
																					trHTML += '<tr><td>'
																							+ error.message
																							+ '</td><td>'
																							+ error.node
																							+ '</td><td>'
																							+ error.file
																							+ '</td><td>'
																							+ error.fileType
																							+ '</td><td>'
																							+ error.line
																							+ '</td><td>'
																							+ error.column
																							+ '</td></tr>';
																				});
																trHTML += '</table>';
																trHTML += '</div></div>'; // close panel body  close panel
															}

															if (responseData.validationWarnings !== null
																	&& responseData.validationWarnings.length !== 0) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Validation Warnings</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Message</td><td>Message</td></tr></thead>';
																$
																		.each(
																				responseData.validationWarnings,
																				function(
																						i,
																						warning) {
																					trHTML += '<tr><td>'
																							+ warning.message
																							+ '</td><td>'
																							+ warning.node
																							+ '</td><td>'
																							+ warning.file
																							+ '</td><td>'
																							+ warning.fileType
																							+ '</td><td>'
																							+ warning.line
																							+ '</td><td>'
																							+ warning.column
																							+ '</td></tr>';
																				});
																trHTML += '</table>';
																trHTML += '</div></div>'; // close panel body  close panel
															}

															if (responseData.validationWarnings === null
																	&& responseData.validationErrors === null) {
																alert("Unable to parse files sent to server:"
																		+ message);
															}
															$('#import_competency_map_results')
																	.append(
																			trHTML);
														}
													});
											e.preventDefault();
										});

					});
</script>
<!-- Onboard College -->
<script language="javascript" type="text/javascript">
	var baseUrl = "${pageContext.servletContext.contextPath}";
	var onboardCollege = baseUrl
			+ 'placement-service/service/v1/onboard';
	$(document)
			.ready(
					function() {
						$('#onboardCollegeForm')
								.submit(
										function(e) {
											$('#import_onboard_results').empty();
											$('#import_onboard_results')
											.append("Boarding College posted, waiting for response");
											var token = $("meta[name='_csrf']")
													.attr("content");
											var headerKey = $(
													"meta[name='_csrf_header']")
													.attr("content");
											var headers = {};
											headers[headerKey] = token;
											$
													.ajax({
														url : onboardCollege,
														type : 'POST',
														data : new FormData(
																this),
														processData : false,
														contentType : false,
														dataType : false,
														//headers: {'X-CSRF-TOKEN': token},
														headers : headers,
														success : function(
																data, status,
																xhr) {
															var trHTML = "";
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Onboarding College Results</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Message</td></td></tr></thead>';
															
																trHTML += '<tr><td>Congratulations College has been onboarded sucessfully.</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_onboard_results')
																	.append(
																			trHTML);
														},
														error : function(data,
																status, message) {
															var trHTML = "";
															var responseData = data.responseJSON;
															$('#import_onboard_results')
																	.empty();
															
															if(responseData == null) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Errors</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
															
																trHTML += '<tr ><td colspan="2">Uhoh something happened during onboarding.</td></tr>';
																trHTML += '<tr><td>Http Status</td><td>' + data.status + '</td></tr>';
																trHTML += '<tr><td>Error</td><td>' + data.statusText + '</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_onboard_results')
																	.append(
																			trHTML);
																return;
															}
															trHTML += '<div class="panel panel-warning">';
															trHTML += '<div class="panel-heading"><h3 class="panel-title">Errors</h3></div>';
															trHTML += '<div class="panel body">';
															trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
														
															trHTML += '<tr ><td colspan="2">Uhoh something happened during onboarding.</td></tr>';
															trHTML += '<tr><td>Http Status</td><td>' + responseData.status + '</td></tr>';
															trHTML += '<tr><td>Error</td><td>' + responseData.error + '</td></tr>';
															trHTML += '<tr><td>Message</td><td>' + responseData.message + '</td></tr>';
														trHTML += '</table>';
														trHTML += '</div></div>';

														$('#import_onboard_results')
																.append(
																		trHTML);
														}
													});
											e.preventDefault();
										});

					});
</script>

<!-- Seeding Data -->
<script language="javascript" type="text/javascript">
	var baseUrl = "${pageContext.servletContext.contextPath}";
	var seeddataUrl = baseUrl + 'service/v1/qa';
	$(document)
			.ready(
					function() {

						

						$('#seedDataForm')
								.submit(
										function(e) {
											$('#seed_data_results').empty();
											var token = $("meta[name='_csrf']")
													.attr("content");
											var headerKey = $(
													"meta[name='_csrf_header']")
													.attr("content");
											var headers = {};
											$('#seed_data_results')
											.append(
													"Posting Request For Seed Data Generation");
											headers[headerKey] = token;
											$
													.ajax({
														url : seeddataUrl,
														type : 'POST',
														processData : false,
														contentType : false,
														dataType : false,
														headers : headers,
														success : function(
																data, status,
																xhr) {
															var trHTML = "";
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Seeding Data Results</td></tr></thead>';
															
																trHTML += '<tr><td>Congratulations you have successfully seeded a demo set of data.</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';
															$('#seed_data_results').empty();
															$('#seed_data_results')
																	.append(
																			trHTML);
														},
														error : function(data,
																status, message) {
															var trHTML = "";
															var responseData = data.responseJSON;
															$('#seed_data_results')
																	.empty();
															
															if(responseData == null) {
																trHTML += '<div class="panel panel-warning">';
																trHTML += '<div class="panel-heading"><h3 class="panel-title">Errors</h3></div>';
																trHTML += '<div class="panel body">';
																trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
															
																trHTML += '<tr ><td colspan="2">Uhoh something happened while seeding data.</td></tr>';
																trHTML += '<tr><td>Http Status</td><td>' + data.status + '</td></tr>';
																trHTML += '<tr><td>Error</td><td>' + data.statusText + '</td></tr>';
															trHTML += '</table>';
															trHTML += '</div></div>';

															$('#import_onboard_results')
																	.append(
																			trHTML);
																return;
															}
															trHTML += '<div class="panel panel-warning">';
															trHTML += '<div class="panel-heading"><h3 class="panel-title">Errors</h3></div>';
															trHTML += '<div class="panel body">';
															trHTML += '<table class="table table-striped"><thead><tr><td>Component</td><td>Message</td></tr></thead>';
														
															trHTML += '<tr><td colspan="2">Uhoh something happened while seeding data.</td></tr>';
															trHTML += '<tr><td>Http Status</td><td>' + responseData.status + '</td></tr>';
															trHTML += '<tr><td>Error</td><td>' + responseData.error + '</td></tr>';
															trHTML += '<tr><td>Message</td><td>' + responseData.message + '</td></tr>';
														trHTML += '</table>';
														trHTML += '</div></div>';

														$('#seed_data_results')
																.append(
																		trHTML);
														}
													});
											e.preventDefault();
										});

					});
</script>
</head>

<body style="margin-left: 70px; margin-top: 70px; margin-right: 70px;">

	<div class="wrapper"></div>
	<div class="main-content-wrapper">
		<div class="main-content">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h2>Upload Assessments</h2>
				</div>
				<div class="panel-body">
					<form id="uploadAssessmentForm" action="javascript:;"
						enctype="multipart/form-data" method="post" accept-charset="utf-8">
						<label>Upload by Selecting a Content Package ZIP file or
							Assessment Item XML file to upload and validate:</label>
						<div class="input-group-assessment">
							<label class="input-group-btn" for="assessmentFile"> 
								<span class="btn btn-primary">
								Browse&hellip; 	
								<input id="assessmentFile" type="file" name="file" style="display: none;" multiple>
								</span>								
							</label> 
							<input id="assessmentFileName" type="text" name="text" class="form-control" readonly style="width:70%" /> 
							<input id="contentType" name="contentType" type="hidden" value="" />
						</div>
						<div>
							<aside>
								<p>You may upload any of the following to the validator:</p>
								<ul>
									<li>An IMS Content Package containing a QTI (2.0 - 2.2)
										Assessment Item(s), plus any related resources, such as
										images, response processing templates...</li>
									<li>An IMS Content Package containing a QTI (2.0 - 2.2)
										Assessment Test(s), its Assessment Items, plus any related
										resources.</li>
									<li>A self-contained Assessment Item XML file - QTI (2.0 -
										2.2).</li>
								</ul>
							</aside>
						</div>
						<input id="submitAssessmentFile" type="submit" class="btn btn-default"
							value="Click Here To Upload an Assessment!">
						<!-- Turns out IE 10/11 both have a bug. Go figure.
                        https://blog.yorkxin.org/2014/02/06/ajax-with-formdata-is-broken-on-ie10-ie11
                        The input with a value set at the end of the form magically fixes the bug.
                        DO NOT REMOVE input, it will break IE 10/11 -->
						<div>
							<input id="dummy" name="dummy" type="hidden" value="dummy" />
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<div class="clear" style="padding-bottom: 1em"></div>
	<div id="createdAssessmentSessions">
		<div id="import_assessment_results"></div>
	</div>
	<div class="wrapper"></div>
<div class="main-content-wrapper">
	<div class="main-content">
		<div class="panel panel-default">
			<div class="panel-heading"><h2>Upload Competency Maps</h2></div>
			<div class="panel-body">
				<form id="uploadCompetencyMapForm" action="javascript:;" enctype="multipart/form-data" method="post" accept-charset="utf-8">
					<label>Upload by Selecting a Competency Package ZIP file or Competency Map XML file to upload and validate:</label>
					<div class="input-group-competency-map">
						<label class="input-group-btn" for="competencyMapFile">
							<span class="btn btn-primary">Browse&hellip; <input id="competencyMapFile" type="file" name="file" style="display: none;" multiple></span>
						</label>
						<input id="competencyMapFileName" type="text" name="text" class="form-control" readonly style="width:70%" />
						<input id="contentType" name="contentType" type="hidden" value="" />
					</div>
					<div>
						<aside>
							<p>You may upload any of the following to the validator:</p>
							<ul>
								<li>Competency Maps and Competency schemas.</li>
								<li>File must be a single xml or set of competency maps in zip format.</li>
							</ul>
						</aside>
					</div>
					<input id="submitCompetencyMapFile" type="submit" class="btn btn-default" value="Click Here To Upload A competency map!" >
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
<div id="importedCompetenceyMaps">
	<div id="import_competency_map_results">
	</div>
</div>
<div class="main-content-wrapper">
	<div class="main-content">
		<div class="panel panel-default">
			<div class="panel-heading"><h2>On Board College Rules Sets and Basic Placement Subject Areas</h2></div>
			<div class="panel-body">
				<form id="onboardCollegeForm" action="javascript:;" enctype="application/json" method="post" accept-charset="utf-8">
					<label></label>
					<div class="input-group">
						<label class="input-group-btn" for="college">
							<span class="btn btn-primary">College Miscode</span>
						</label>
						<input id="college" type="text" name="college" class="form-control" style="width:70%" />
						</div>
						<div class="input-group">
						<label class="input-group-btn" for="description">
							<span class="btn btn-primary">College Description</span>
						</label>
						<input id="description" type="text" name="description" class="form-control" style="width:70%" />
					</div>
					<div>
						<aside>
							<p>Onboard a College :</p>
							<ul>
								<li>Enter a College id </li>
								<li>Enter a College description</li>
							</ul>
						</aside>
					</div>
					<input id="submitOnboardCollege" type="submit" class="btn btn-default" value="Click Here to Onboard College" >
					<!-- Turns out IE 10/11 both have a bug. Go figure.
                        https://blog.yorkxin.org/2014/02/06/ajax-with-formdata-is-broken-on-ie10-ie11
                        The input with a value set at the end of the form magically fixes the bug.
                        DO NOT REMOVE input, it will break IE 10/11 -->
					<div>
						<input id="dummy2" name="dummy2" type="hidden" value="dummy"/>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="clear" style="padding-bottom: 1em"></div>
<div id="onboardedCollege">
	<div id="import_onboard_results">
	</div>
</div>
	<table>
	<tr><td>
	<div class="actions">
		<a href="${homePageUrl}" id="home-page" class="btn btn-primary btn-lg">Back
			To Home Page</a>
	</div>
	</td><td style="padding-left:3em"">
	
	<form id="seedDataForm" action="javascript:;"  method="get" accept-charset="utf-8">
			<input id="submitSeedData" type="submit" id="seed-data" class="btn btn-primary btn-lg"" value="Generate Seed Data" >
			<input id="dummysd" name="dummy" type="hidden" value="dummy"/>
	</form>
	</td>
	</tr>
	</table>
	<div class="clear" style="padding-bottom: 1em"></div>
<div id="seed_data_generate">
	<div id="seed_data_results">
	</div>
</div>
	<c:if test="${google.analytics.environment == 'prod'}">
		<jsp:include page="/WEB-INF/common/scripts.common.jsp" />
	</c:if>
</body>
</html>
