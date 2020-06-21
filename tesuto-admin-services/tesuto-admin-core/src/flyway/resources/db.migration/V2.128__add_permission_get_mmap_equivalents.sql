insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('GET_MMAP_EQUIVALENTS', timestamp 'now', timestamp 'now', 'Allows user to view list of mmap equivalents.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id)
select 'GET_MMAP_EQUIVALENTS', security_group_id
from security_group where group_name in ('ADMIN', 'FACULTY');
