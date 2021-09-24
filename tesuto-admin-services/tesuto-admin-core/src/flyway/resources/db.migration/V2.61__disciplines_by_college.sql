insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('GET_DISCIPLINE_BY_COLLEGE_ID', timestamp 'now', timestamp 'now', 'List disciplines for a college');

   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('GET_DISCIPLINE_BY_COLLEGE_ID', 2);

