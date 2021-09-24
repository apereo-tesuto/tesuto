insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_LOCATION_SUMMARY', timestamp 'now', timestamp 'now', 'Allow user to view location summary.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id)
select 'VIEW_LOCATION_SUMMARY', security_group_id
from security_group where group_name in ('PROCTOR');
    
