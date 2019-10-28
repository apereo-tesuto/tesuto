insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('ASSIGN_STUDENT_ROLE', timestamp 'now', timestamp 'now', 'Allows user to assign student role to database user account.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'ASSIGN_STUDENT_ROLE', g.security_group_id from security_group g
  where g.group_name in ('DEVELOPER');