-- admin

-- Add developer permissions

-- Create the developer group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) select 8, timestamp 'now', timestamp 'now', 'Developer security group', 'DEVELOPER' where not exists (select * from security_group_security_permission where security_group_id = 8);

-- DEVELOPER
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_SERVER_CONFIG', 8
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_SERVER_CONFIG');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_CODE_VERSION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_CODE_VERSION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PRINT_ASSESSMENT_SESSION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='PRINT_ASSESSMENT_SESSION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_SESSION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENT_SESSION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PROTOTYPE_ASSESSMENT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PROTOTYPE_ASSESSMENT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_REDACT_ASSESSMENT_UI', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_REDACT_ASSESSMENT_UI');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACCOMMODATIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACCOMMODATIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVATION_SEARCH_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_ACTIVATION_SEARCH_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'FIND_ACTIVATIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='FIND_ACTIVATIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'DELETE_ACTIVATION_SEARCH_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='DELETE_ACTIVATION_SEARCH_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATION_SEARCH_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACTIVATION_SEARCH_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_ACTIVATION_SEARCH_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPDATE_ACTIVATION_SEARCH_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_CURRENT_USER_ACTIVE_ACTIVATIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_CURRENT_USER_ACTIVE_ACTIVATIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_CURRENT_USER_ACTIVATIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_CURRENT_USER_ACTIVATIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATIONS_REPORT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACTIVATIONS_REPORT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATION_STATUS_CHANGE_REPORT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACTIVATION_STATUS_CHANGE_REPORT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'AUTHORIZE_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='AUTHORIZE_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CANCEL_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CANCEL_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'LAUNCH_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='LAUNCH_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PAUSE_ACTIVATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='PAUSE_ACTIVATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ACTIVITY_LOG_ENTRY', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_ACTIVITY_LOG_ENTRY');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ACTIVITY_LOG', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ACTIVITY_LOG');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_METADATA', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENT_METADATA');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENTS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENTS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENTS_BY_LOCATION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENTS_BY_LOCATION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PUBLISH_ASSESSMENT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='PUBLISH_ASSESSMENT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'PUBLISH_ASSESSMENT_ITEM', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='PUBLISH_ASSESSMENT_ITEM');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_ITEM', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENT_ITEM');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ALL_ASSESSMENT_SESSIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ALL_ASSESSMENT_SESSIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_DELIVERY_ASSESSMENT_SESSION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_DELIVERY_ASSESSMENT_SESSION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_ASSESSMENT_SESSION_ACTIVITY_ENTRY', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_ASSESSMENT_SESSION_ACTIVITY_ENTRY');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'COMPLETE_ASSESSMENT_SESSION_TASK_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='COMPLETE_ASSESSMENT_SESSION_TASK_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_ASSESSMENT_SESSION_TASK_SET_RESPONSE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPDATE_ASSESSMENT_SESSION_TASK_SET_RESPONSE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PASSCODES_FOR_CURRENT_USER', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PASSCODES_FOR_CURRENT_USER');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PRIVATE_PASSCODE_FOR_CURRENT_USER', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PRIVATE_PASSCODE_FOR_CURRENT_USER');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PUBLIC_PASSCODE_FOR_CURRENT_USER', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PUBLIC_PASSCODE_FOR_CURRENT_USER');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_PREVIEW_ASSESSMENT_SESSION', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_PREVIEW_ASSESSMENT_SESSION');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'COMPLETE_PREVIEW_ASSESSMENT_SESSION_TASK_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='COMPLETE_PREVIEW_ASSESSMENT_SESSION_TASK_SET');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SET_SCORE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SET_SCORE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SCORE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SCORE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_PREVIEW_ASSESSMENT_PACKAGE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPLOAD_PREVIEW_ASSESSMENT_PACKAGE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'REDACT_ASSESSMENT_PACKAGE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='REDACT_ASSESSMENT_PACKAGE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'DOWNLOAD_RESPONSE_REPORT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='DOWNLOAD_RESPONSE_REPORT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ASSESSMENT_SESSIONS_SEQUENCE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ASSESSMENT_SESSIONS_SEQUENCE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'GENERATE_SAMPLE_ASSESSMENT_SESSIONS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='GENERATE_SAMPLE_ASSESSMENT_SESSIONS');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'STORE_RESPONSE_REPORT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='STORE_RESPONSE_REPORT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'DOWNLOAD_STATIC_REPORT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='DOWNLOAD_STATIC_REPORT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'FIND_STUDENT', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='FIND_STUDENT');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_ASSESSMENT_PACKAGE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPLOAD_ASSESSMENT_PACKAGE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_TEST_LOCATIONS_BY_USER', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_TEST_LOCATIONS_BY_USER');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'UPLOAD_ASSESSMENT_PACKAGE_UI', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='UPLOAD_ASSESSMENT_PACKAGE_UI');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_UPLOADED_ASSESSMENT_PACKAGE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_UPLOADED_ASSESSMENT_PACKAGE');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_ADMIN_DASHBOARD', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_ADMIN_DASHBOARD');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'TRACK_USER_HISTORY', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='TRACK_USER_HISTORY');
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'ASSIGN_STUDENT_ROLE', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='ASSIGN_STUDENT_ROLE');