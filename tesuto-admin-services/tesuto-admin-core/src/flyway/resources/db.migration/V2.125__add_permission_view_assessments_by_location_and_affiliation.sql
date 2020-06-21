insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_ASSESSMENTS_BY_LOCATION_AND_COLLEGE_AFFILIATION', timestamp 'now', timestamp 'now', 'Allows user to view assessments for a test location provided they are associated with that test locations college.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_ASSESSMENTS_BY_LOCATION_AND_COLLEGE_AFFILIATION', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
