insert into security_permission
       (security_permission_id, created_on_date, last_updated_date, description)
 select 'GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER',
        current_timestamp, current_timestamp, 'Create a public passcode'
        where  not exists
               (select 1 from security_permission
                where security_permission_id='GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER');

insert into security_permission
       (security_permission_id, created_on_date, last_updated_date, description)
select 'GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER',
        current_timestamp, current_timestamp,'Create a private passcode'
where  not exists
       (select 1 from security_permission
        where security_permission_id='GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER');


insert into security_group_security_permission
       (security_permission_id, security_group_id)
select  'GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER', 3
where not exists (select 1 from security_group_security_permission
      where security_permission_id='GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER');

insert into security_group_security_permission
       (security_permission_id, security_group_id)
select 'GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER', 3
where not exists (select 1 from security_group_security_permission
      where security_permission_id='GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER');
