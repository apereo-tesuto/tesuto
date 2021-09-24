insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('UPDATE_COLLEGE_ATTRIBUTE', timestamp 'now', timestamp 'now', 'Update a college attribute'),
    ('VIEW_COLLEGE_ATTRIBUTE', timestamp 'now', timestamp 'now', 'View a college attribute');

insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('ADMIN', 'FACULTY') and
          sp.security_permission_id in ('VIEW_COLLEGE_ATTRIBUTE', 'UPDATE_COLLEGE_ATTRIBUTE');
