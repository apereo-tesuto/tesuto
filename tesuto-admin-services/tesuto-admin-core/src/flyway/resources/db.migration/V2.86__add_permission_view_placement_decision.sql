insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_CURRENT_USER_PLACEMENT_DECISION', timestamp 'now', timestamp 'now', 'Allow student to view his/her placement decisions.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_CURRENT_USER_PLACEMENT_DECISION', 1);
    
 insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_PLACEMENT_DECISION', timestamp 'now', timestamp 'now', 'Allow user to view placement decisions of others.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_PLACEMENT_DECISION', 2);
    
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_PLACEMENT_DECISION', 11);