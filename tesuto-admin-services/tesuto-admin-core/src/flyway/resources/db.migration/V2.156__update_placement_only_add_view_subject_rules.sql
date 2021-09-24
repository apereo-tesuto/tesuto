insert into "security_group_security_permission" (security_permission_id, security_group_id)
    select 'VIEW_SUBJECT_AREA_RULES', g.security_group_id from security_group g where g.group_name = 'PLACEMENT_READ_ONLY';
