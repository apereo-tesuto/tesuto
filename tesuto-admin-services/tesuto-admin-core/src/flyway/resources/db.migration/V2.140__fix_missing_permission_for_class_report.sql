insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('STUDENT_VIEW_PLACEMENT_DECISION', timestamp 'now', timestamp 'now', 'permission for student view of placements');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
select 'STUDENT_VIEW_PLACEMENT_DECISION', g.security_group_id from security_group g
where g.group_name in ('STUDENT');
