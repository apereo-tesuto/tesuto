insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('CREATE_BATCH_ACTIVATION', timestamp 'now', timestamp 'now', 'Allow user to create a batch activaiton.');

insert into security_group_security_permission
(security_permission_id, security_group_id)
select 'CREATE_BATCH_ACTIVATION', security_group_id
from security_group where group_name in ('PROCTOR');
