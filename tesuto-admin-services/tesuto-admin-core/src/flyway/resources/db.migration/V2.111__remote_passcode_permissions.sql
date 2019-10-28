insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_REMOTE_PASSCODE', timestamp 'now', timestamp 'now', 'Allow user to view passcodes for remote test events.'),
    ('GENERATE_REMOTE_PASSCODE_FOR_TEST_EVENT', timestamp 'now', timestamp 'now', 'Allow user to generate new passcodes for remote test events.');

insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_REMOTE_PASSCODE', g.security_group_id from security_group g where g.group_name = 'REMOTE_PROCTOR';
insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'GENERATE_REMOTE_PASSCODE_FOR_TEST_EVENT', g.security_group_id from security_group g where g.group_name = 'PROCTOR';
