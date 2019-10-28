insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_DISTRICTS_WITH_ALL_COLLEGES_AND_LOCATIONS', timestamp 'now', timestamp 'now', 'Allows user to find all districts with colleges and test locations');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_DISTRICTS_WITH_ALL_COLLEGES_AND_LOCATIONS', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';
