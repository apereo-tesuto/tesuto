insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('CREATE_TEST_EVENT', timestamp 'now', timestamp 'now', 'Allows user to create test events');

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_TEST_EVENT', timestamp 'now', timestamp 'now', 'Allows user to view test events');

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('UPDATE_TEST_EVENT', timestamp 'now', timestamp 'now', 'Allows user to modify test events');


insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'CREATE_TEST_EVENT', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR', 'ADMIN');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_TEST_EVENT', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR', 'ADMIN');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'UPDATE_TEST_EVENT', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR', 'ADMIN');
