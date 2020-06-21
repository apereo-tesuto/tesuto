-- FACULTY group users do not have permission to complete a subject area with multiple measures
-- without this permission.
insert into security_group_security_permission
(security_permission_id, security_group_id)
  select 'VIEW_SUBJECT_AREA_RULES', security_group_id
  from security_group where group_name in ('FACULTY');
