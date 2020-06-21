insert into "security_group_api" (security_group_api_id, created_on_date, last_updated_date, description, group_name) values
    (3, timestamp 'now', timestamp 'now', 'Preview Player Viewer Security Group', 'PREVIEW_PLAYER');

insert into "security_group_api_security_permission" (security_permission_id, security_group_api_id) values 
    ('VIEW_PREVIEW_ASSESSMENT_SESSION', 3),
    ('UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE', 3),
    ('COMPLETE_PREVIEW_ASSESSMENT_SESSION_TASK_SET', 3),
    ('PREVIOUS_PREVIEW_ASSESSMENT_SESSION_TASK_SET', 3),
    ('CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SET_SCORE', 3);
