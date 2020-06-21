-- Add only the subject area version endpoint to be able to be viewed by a student.
insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('GET_DISCIPLINE_FOR_STUDENT', timestamp 'now', timestamp 'now', 'Allows a student to view a particular version of a subject area');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'GET_DISCIPLINE_FOR_STUDENT', g.security_group_id from security_group g
  where g.group_name in ('STUDENT');
