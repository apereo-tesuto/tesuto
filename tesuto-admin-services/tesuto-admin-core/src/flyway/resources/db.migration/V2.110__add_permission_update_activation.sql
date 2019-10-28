insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('UPDATE_ACTIVATION', timestamp 'now', timestamp 'now', 'Allows user to update an activation.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'UPDATE_ACTIVATION', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR');