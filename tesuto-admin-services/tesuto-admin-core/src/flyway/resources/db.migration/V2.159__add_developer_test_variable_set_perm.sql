insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('CREATE_TEST_VARIABLE_SET', timestamp 'now', timestamp 'now', 'Allow user to create variable sets for students.');

insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_TEST_VARIABLE_SET', timestamp 'now', timestamp 'now', 'Allow user to view variable sets for students.');

insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_TEST_VARIABLE_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_TEST_VARIABLE_SET');

insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_TEST_VARIABLE_SET', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='VIEW_TEST_VARIABLE_SET');