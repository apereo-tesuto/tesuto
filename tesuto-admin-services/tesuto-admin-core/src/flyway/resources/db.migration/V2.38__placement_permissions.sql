insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values 
    ('CREATE_PLACEMENT', timestamp 'now', timestamp 'now', 'Create a placement'),
    ('UPDATE_PLACEMENT', timestamp 'now', timestamp 'now', 'Update a placement');

insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('ADMIN') and
          sp.security_permission_id in ('CREATE_PLACEMENT', 'UPDATE_PLACEMENT');