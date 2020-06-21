-- Create the configurator group role
insert into security_group (security_group_id, created_on_date, last_updated_date, description, group_name)
values (11, timestamp 'now', timestamp 'now', 'Counselor security group', 'COUNSELOR');

insert into security_group_security_permission (security_permission_id, security_group_id)
values ('FIND_STUDENT', 11),
  ('VIEW_STUDENT_DASHBOARD', 11),
  ('CREATE_PLACEMENT', 11),
  ('UPDATE_PLACEMENT', 11);

