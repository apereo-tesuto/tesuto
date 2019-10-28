insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_INSTRUCTOR_CLASS_REPORT', timestamp 'now', timestamp 'now', 'Allows user generate and view the instructor class report.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_INSTRUCTOR_CLASS_REPORT', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
