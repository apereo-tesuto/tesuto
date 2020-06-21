insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_MY_COMPETENCY_MASTERY', timestamp 'now', timestamp 'now', 'Allow student to view mastery of discipline.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_MY_COMPETENCY_MASTERY', 1);
    
 insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_COMPETENCY_MASTERY', timestamp 'now', timestamp 'now', 'Allow user to view mastery of discipline.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_COMPETENCY_MASTERY', 2);
    
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('VIEW_COMPETENCY_MASTERY', 11);