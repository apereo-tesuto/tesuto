insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('EDIT_USER', timestamp 'now', timestamp 'now', 'Edit an existing user in the system');

insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('ADMIN', 'DEVELOPER')
    and sp.security_permission_id = 'EDIT_USER';
