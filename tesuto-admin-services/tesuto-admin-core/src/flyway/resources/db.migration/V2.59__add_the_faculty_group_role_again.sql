-- Re-create the faculty group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name)
values (5, timestamp 'now', timestamp 'now', 'Faculty security group', 'FACULTY');

insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('FACULTY') and
          sp.security_permission_id in ('CREATE_PLACEMENT', 'UPDATE_PLACEMENT');
