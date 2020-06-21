insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'VIEW_TEST_LOCATIONS_BY_USER', g.security_group_id from security_group g where g.group_name = 'REMOTE_PROCTORING_MANAGER';
