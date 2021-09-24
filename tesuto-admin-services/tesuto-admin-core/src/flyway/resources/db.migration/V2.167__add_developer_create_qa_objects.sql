insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
  ('CREATE_QA_OBJECTS', timestamp 'now', timestamp 'now', 'Allows user to seed data, should only be available on test and ci environments');

insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'CREATE_QA_OBJECTS', 8 
    where not exists (select * from security_group_security_permission where security_group_id = 8 and security_permission_id='CREATE_QA_OBJECTS');
