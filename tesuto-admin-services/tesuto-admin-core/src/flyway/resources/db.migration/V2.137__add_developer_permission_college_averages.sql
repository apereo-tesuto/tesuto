insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('GET_COLLEGE_COMPETENCY_AVERAGES', timestamp 'now', timestamp 'now', 'Allows user to retrieve competency difficulty averages.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id)
select 'GET_COLLEGE_COMPETENCY_AVERAGES', security_group_id
from security_group where group_name in ('DEVELOPER');
