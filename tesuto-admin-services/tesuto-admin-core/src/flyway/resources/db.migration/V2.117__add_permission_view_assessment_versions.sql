insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_ASSESSMENT_VERSIONS', timestamp 'now', timestamp 'now', 'Allows user to view a list of versions for an assessment.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_ASSESSMENT_VERSIONS', g.security_group_id from security_group g
  where g.group_name in ('PROCESSOR');
