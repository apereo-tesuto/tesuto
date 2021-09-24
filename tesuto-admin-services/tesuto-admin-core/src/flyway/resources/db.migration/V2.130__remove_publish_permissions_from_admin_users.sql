-- Remove the ability to publish assessments and assessment items from the ADMIN group users. It's too
-- dangerous.  Only the DEVELOPER group will have this ability.
delete from security_group_security_permission where security_permission_id = 'PUBLISH_ASSESSMENT' and security_group_id = (select g.security_group_id from security_group g where g.group_name = 'ADMIN');
delete from security_group_security_permission where security_permission_id = 'PUBLISH_ASSESSMENT_ITEM' and security_group_id = (select g.security_group_id from security_group g where g.group_name = 'ADMIN');
