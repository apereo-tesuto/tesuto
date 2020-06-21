insert into "security_group_security_permission" (security_permission_id, security_group_id)
    select 'LAUNCH_PAPER_ACTIVATION', g.security_group_id from security_group g where g.group_name = 'PAPER_PENCIL_SCORER';
