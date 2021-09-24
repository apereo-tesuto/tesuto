insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('PRINT_ASSESSMENT', timestamp 'now', timestamp 'now', 'Allows user to see the print view for an assessment.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'PRINT_ASSESSMENT', g.security_group_id from security_group g
  where g.group_name in ('PROCTOR', 'REMOTE_PROCTOR');
