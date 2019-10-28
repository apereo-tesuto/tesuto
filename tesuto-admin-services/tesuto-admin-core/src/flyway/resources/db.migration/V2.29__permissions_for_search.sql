insert into security_permission (security_permission_id, created_on_date, last_updated_date, description)
select 'USER_SEARCH', current_timestamp, current_timestamp, 'User Search';

insert into security_group_security_permission (security_group_id, security_permission_id)
select sg.security_group_id, sp.security_permission_id
from security_group sg, security_permission sp
where sg.group_name in ('PROCTOR', 'ADMIN', 'PROCESSOR', 'DEVELOPER') and
      sp.security_permission_id = 'USER_SEARCH';
