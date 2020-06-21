insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('VIEW_ASSESSMENT_REVISIONS', timestamp 'now', timestamp 'now', 'Allows user to view all assessment revisions.'),
  ('VIEW_ASSESSMENT_ITEM_REVISIONS', timestamp 'now', timestamp 'now', 'Allows user to view all assessment item revisions.');

insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_ASSESSMENT_REVISIONS', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');
insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_ASSESSMENT_ITEM_REVISIONS', g.security_group_id from security_group g
  where g.group_name in ('ADMIN');