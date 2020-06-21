insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('UPDATE_DISCIPLINE_COMPETENCY_MAP_VERSION', timestamp 'now', timestamp 'now', 'Allows user to update a disciplines competency map version.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'UPDATE_DISCIPLINE_COMPETENCY_MAP_VERSION', g.security_group_id from security_group g
  where g.group_name in ('DEVELOPER');
