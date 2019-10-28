insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('CREATE_TEST_LOCATION', timestamp 'now', timestamp 'now', 'Allows a user to create new test locations'),
  ('VIEW_USERS_BY_COLLEGE', timestamp 'now', timestamp 'now', 'Allows a user to view all users associated with a college'),
  ('VIEW_USERS_BY_TEST_LOCATION', timestamp 'now', timestamp 'now', 'Allows a user to view all users associated with a test location');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'CREATE_TEST_LOCATION', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_USERS_BY_COLLEGE', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_USERS_BY_TEST_LOCATION', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';
