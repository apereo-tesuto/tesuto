-- delete all the VIEW_INSTRUCTOR_CLASS_REPORT permissions in case some were already created
delete from "security_group_security_permission" where security_permission_id = 'VIEW_INSTRUCTOR_CLASS_REPORT';

-- only faculty should have permissions
insert into "security_group_security_permission" (security_permission_id, security_group_id)
select 'VIEW_INSTRUCTOR_CLASS_REPORT', g.security_group_id from security_group g
where g.group_name in ('FACULTY');
