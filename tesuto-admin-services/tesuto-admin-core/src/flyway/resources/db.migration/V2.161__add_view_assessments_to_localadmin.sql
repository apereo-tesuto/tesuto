insert into "security_group_security_permission" (security_permission_id, security_group_id)
    select 'VIEW_ASSESSMENTS', g.security_group_id from security_group g where g.group_name = 'LOCAL_ADMIN';
