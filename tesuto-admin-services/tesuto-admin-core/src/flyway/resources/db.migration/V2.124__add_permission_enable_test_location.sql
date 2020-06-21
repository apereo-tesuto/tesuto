insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('ENABLE_TEST_LOCATION', timestamp 'now', timestamp 'now', 'Allows user to enable or disable a test location.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'ENABLE_TEST_LOCATION', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
