insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_ANY_COLLEGE', timestamp 'now', timestamp 'now', 'Allows the user to view any college.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id)
select 'VIEW_ANY_COLLEGE', security_group_id
from security_group where group_name in ('DEVELOPER');
