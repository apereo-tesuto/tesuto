insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_TEST_EVENT_BY_COLLEGE', timestamp 'now', timestamp 'now', 'Allows user to create test events');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_TEST_EVENT_BY_COLLEGE', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR', 'ADMIN');

