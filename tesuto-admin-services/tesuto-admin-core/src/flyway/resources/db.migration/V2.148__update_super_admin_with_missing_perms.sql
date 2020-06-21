insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'CREATE_COMPETENCY_MAP', g.security_group_id from security_group g where g.group_name = 'SUPER_ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'VIEW_COMPETENCY_MAP_UPLOAD_UI', g.security_group_id from security_group g where g.group_name = 'SUPER_ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'PREVIOUS_PREVIEW_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g where g.group_name = 'SUPER_ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'CREATE_PLACEMENT_DECISION', g.security_group_id from security_group g where g.group_name = 'SUPER_ADMIN';

delete from security_group_security_permission
    where security_permission_id = 'VIEW_CURRENT_USER_PLACEMENT_DECISION';
    
delete from security_permission
    where security_permission_id = 'VIEW_CURRENT_USER_PLACEMENT_DECISION';
