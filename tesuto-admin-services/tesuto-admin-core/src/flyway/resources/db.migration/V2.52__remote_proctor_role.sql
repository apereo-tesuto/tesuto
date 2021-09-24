insert into security_group
(security_group_id, created_on_date, last_updated_date, description, group_name)
select 10, current_timestamp, current_timestamp, 'Role for creating passcodes', 'REMOTE_PROCTOR'
where not exists (select 1 from security_group where security_group_id=10);

insert into security_group_security_permission
       (security_permission_id, security_group_id)
select 'GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER', 10
where not exists (select 1 from security_group where security_group_id=10);

insert into security_group_security_permission
       (security_permission_id, security_group_id)
select 'GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER', 10
where not exists (select 1 from security_group where security_group_id=10);

insert into security_group_security_permission
       (security_permission_id, security_group_id)
select 'VIEW_PASSCODES_FOR_CURRENT_USER', 10
where not exists (select 1 from security_group where security_group_id=10);

