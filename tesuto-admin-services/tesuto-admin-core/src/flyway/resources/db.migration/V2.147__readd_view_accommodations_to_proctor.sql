insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_ACCOMMODATIONS', timestamp 'now', timestamp 'now', 'Retrieve a list of supported accommodations');

insert into "security_group_security_permission" (security_permission_id, security_group_id) 
    select 'VIEW_ACCOMMODATIONS', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
