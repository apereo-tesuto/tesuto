-- fix the size problem we have for the permissions auditing
ALTER TABLE ONLY audit_event ALTER roles TYPE text;
ALTER TABLE ONLY audit_event RENAME roles TO permissions;

-- fixing permissions for the INSTRUCTOR CLASS REPORT
insert into "security_group_security_permission" (security_permission_id, security_group_id)
  select 'VIEW_INSTRUCTOR_CLASS_REPORT', g.security_group_id from security_group g where g.group_name in ('FACULTY');

delete from "security_group_security_permission" where security_permission_id = 'VIEW_INSTRUCTOR_CLASS_REPORT' and 
   security_group_id = (select g.security_group_id from security_group g where g.group_name in ('ADMIN'));
