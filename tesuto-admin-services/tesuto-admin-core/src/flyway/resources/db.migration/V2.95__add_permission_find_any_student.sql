insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('FIND_ANY_STUDENT', timestamp 'now', timestamp 'now', 'Allows user to find any student irrespective of application to college');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'FIND_ANY_STUDENT', g.security_group_id from security_group g
  where g.group_name = 'PROCTOR';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'FIND_ANY_STUDENT', g.security_group_id from security_group g
  where g.group_name = 'COUNSELOR';
