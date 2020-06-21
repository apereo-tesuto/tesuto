insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('PREVIOUS_ASSESSMENT_SESSION_TASK_SET', timestamp 'now', timestamp 'now', 'Go back to previous task set in assessment player'),
  ('PREVIOUS_PREVIEW_ASSESSMENT_SESSION_TASK_SET', timestamp 'now', timestamp 'now', 'Go back to previous task set in assessment preview player');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'PREVIOUS_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g
  where g.group_name = 'STUDENT';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'PREVIOUS_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g
  where g.group_name = 'PROCESSOR';

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'PREVIOUS_PREVIEW_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g
  where g.group_name = 'ADMIN';


