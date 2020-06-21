insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_ALL_STUDENT_COLLEGE_AFFILIATIONS', timestamp 'now', timestamp 'now', 'Allows user to view all student college affiliations.'), 
  ('DELETE_STUDENT_COLLEGE_AFFILIATION', timestamp 'now', timestamp 'now', 'Allows user to delete a student college affiliation.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_ALL_STUDENT_COLLEGE_AFFILIATIONS', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'DELETE_STUDENT_COLLEGE_AFFILIATION', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
