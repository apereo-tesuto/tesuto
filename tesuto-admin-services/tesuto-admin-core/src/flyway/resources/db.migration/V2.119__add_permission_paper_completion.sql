insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('COMPLETE_PAPER_ASSESSMENT_SESSION_TASK_SET', timestamp 'now', timestamp 'now', 'Allows user to complete a paper assessment session task set.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'COMPLETE_PAPER_ASSESSMENT_SESSION_TASK_SET', g.security_group_id from security_group g
  where g.group_name in ('PROCESSOR');
