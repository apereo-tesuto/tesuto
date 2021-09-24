-- associate new permissions with existing roles

-- STUDENT
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_SESSION', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_CURRENT_USER_ACTIVE_ACTIVATIONS', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_CURRENT_USER_ACTIVATIONS', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'AUTHORIZE_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'LAUNCH_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PAUSE_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_DELIVERY_ASSESSMENT_SESSION', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ASSESSMENT_SESSION_ACTIVITY_ENTRY', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'COMPLETE_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_ASSESSMENT_SESSION_TASK_SET_RESPONSE', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_STUDENT_DASHBOARD', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVITY_LOG', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_METADATA', g.security_group_id from security_group g where g.group_name = 'STUDENT';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVITY_LOG_ENTRY', g.security_group_id from security_group g where g.group_name = 'STUDENT';

-- PROCTOR
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PROCTOR_DASHBOARD', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PRINT_ASSESSMENT_SESSION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACCOMMODATIONS', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVATION_SEARCH_SET', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'FIND_ACTIVATIONS', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'DELETE_ACTIVATION_SEARCH_SET', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATION_SEARCH_SET', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_ACTIVATION_SEARCH_SET', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CANCEL_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'LAUNCH_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENTS', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENTS_BY_LOCATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PASSCODES_FOR_CURRENT_USER', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PRIVATE_PASSCODE_FOR_CURRENT_USER', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PUBLIC_PASSCODE_FOR_CURRENT_USER', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'FIND_STUDENT', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_TEST_LOCATIONS_BY_USER', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'LAUNCH_PAPER_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PROCTOR_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'TRACK_USER_HISTORY', g.security_group_id from security_group g where g.group_name = 'PROCTOR';

-- ADMIN
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PUBLISH_ASSESSMENT', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PUBLISH_ASSESSMENT_ITEM', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PREVIEW_ASSESSMENT_SESSION', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'COMPLETE_PREVIEW_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SET_SCORE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SCORE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_PREVIEW_ASSESSMENT_PACKAGE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_ASSESSMENT_PACKAGE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_ASSESSMENT_PACKAGE_UI', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_UPLOADED_ASSESSMENT_PACKAGE', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ADMIN_DASHBOARD', g.security_group_id from security_group g where g.group_name = 'ADMIN';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'TRACK_USER_HISTORY', g.security_group_id from security_group g where g.group_name = 'ADMIN';

-- PROCESSOR
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ADMIN_DASHBOARD', g.security_group_id from security_group g where g.group_name = 'PROCESSOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PRINT_ASSESSMENT_SESSION', g.security_group_id from security_group g where g.group_name = 'PROCESSOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'COMPLETE_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g where g.group_name = 'PROCESSOR';
