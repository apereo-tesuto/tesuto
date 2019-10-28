insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'FIND_ANY_STUDENT', g.security_group_id from security_group g where g.group_name = 'REMOTE_PROCTORING_MANAGER';
insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'FIND_ACTIVATIONS', g.security_group_id from security_group g where g.group_name = 'REMOTE_PROCTORING_MANAGER';
