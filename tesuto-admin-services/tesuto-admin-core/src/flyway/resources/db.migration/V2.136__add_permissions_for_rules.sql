insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_SUBJECT_AREA_RULES', timestamp 'now', timestamp 'now', 'Allows user to find all rule set rows for college''s competency'),
  ('UPDATE_SUBJECT_AREA_RULES', timestamp 'now', timestamp 'now', 'Allows user to update rule set row for college''s competency');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_SUBJECT_AREA_RULES', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'UPDATE_SUBJECT_AREA_RULES', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';
