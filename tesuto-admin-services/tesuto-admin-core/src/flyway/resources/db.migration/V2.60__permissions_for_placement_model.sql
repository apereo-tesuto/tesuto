insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('CREATE_DISCIPLINE', timestamp 'now', timestamp 'now', 'Create a college discipline'),
    ('GET_DISCIPLINE', timestamp 'now', timestamp 'now', 'Retrieve a college discipline'),
    ('UPDATE_DISCIPLINE', timestamp 'now', timestamp 'now', 'Update a college discipline'),
    ('GET_SEQUENCE', timestamp 'now', timestamp 'now', 'Get the sequences for a college discipline.'),
    ('UPDATE_SEQUENCE', timestamp 'now', timestamp 'now', 'Update a college discipline sequence.'),
    ('CREATE_COURSE', timestamp 'now', timestamp 'now', 'Update a college discipline sequence.'),
   ('GET_ALL_COURSES_FOR_SEQUENCE', timestamp 'now', timestamp 'now', 'Get a list of courses for a college discipline sequence.'),
   ('DELETE_COURSE', timestamp 'now', timestamp 'now', 'Delete a course'),
   ('UPDATE_COURSE', timestamp 'now', timestamp 'now', 'Update a course'),
   ('GET_ALL_COURSES_FOR_DISCIPLINE', timestamp 'now', timestamp 'now', 'Get all the courses for a college discipline.');


   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('CREATE_DISCIPLINE', 2),
    ('GET_DISCIPLINE', 2),
    ('UPDATE_DISCIPLINE', 2),
    ('GET_SEQUENCE', 2),
    ('UPDATE_SEQUENCE', 2),
    ('CREATE_COURSE', 2),
   ('GET_ALL_COURSES_FOR_SEQUENCE', 2),
   ('DELETE_COURSE', 2),
   ('UPDATE_COURSE', 2),
   ('GET_ALL_COURSES_FOR_DISCIPLINE', 2);


