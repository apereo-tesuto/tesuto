insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('CREATE_COMPETENCY_GROUP', timestamp 'now', timestamp 'now', 'Create a competency group and associate competencies.'),
    ('GET_COMPETENCY_GROUP', timestamp 'now', timestamp 'now', 'Retrieve a competency group and associated competencies.'),
    ('UPDATE_COMPETENCY_GROUP', timestamp 'now', timestamp 'now', 'Update a competency group and associated competencies.'),
    ('DELETE_COMPETENCY_GROUP', timestamp 'now', timestamp 'now', 'Delete competency group and associated competencies.'),
    ('GET_COMPETENCY_GROUPS_BY_CLASS_ID', timestamp 'now', timestamp 'now', 'Get all competency groups and associate competencies for given course.');

   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('CREATE_COMPETENCY_GROUP', 2),
    ('GET_COMPETENCY_GROUP', 2),
    ('UPDATE_COMPETENCY_GROUP', 2),
    ('GET_COMPETENCY_GROUPS_BY_CLASS_ID', 2);