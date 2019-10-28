insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('DELETE_DISCIPLINE', timestamp 'now', timestamp 'now', 'Delete a college discipline.');

   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('DELETE_DISCIPLINE', 2);
