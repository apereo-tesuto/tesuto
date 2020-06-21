-- add new groups
insert into security_group (security_group_id, created_on_date, last_updated_date, description, group_name) values
    (13, timestamp 'now', timestamp 'now', 'Placement read-only role', 'PLACEMENT_READ_ONLY'),
    (14, timestamp 'now', timestamp 'now', 'Placement Manager role', 'PLACEMENT_MANAGER'),
    (15, timestamp 'now', timestamp 'now', 'Remote Proctoring Manager', 'REMOTE_PROCTORING_MANAGER');

-- renames
update security_group set group_name = 'LOCAL_ADMIN' where group_name = 'ADMIN';
update security_group set group_name = 'SUPER_ADMIN' where group_name = 'DEVELOPER';
update security_group set group_name = 'PAPER_PENCIL_SCORER' where group_name = 'PROCESSOR';

-- placement_read_only
insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name = 'PLACEMENT_READ_ONLY' and
          sp.security_permission_id in ('GET_DISCIPLINE',
                                        'GET_DISCIPLINE_BY_COLLEGE_ID',
                                        'VIEW_PROCTOR_DASHBOARD');
-- placement_manager
insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name = 'PLACEMENT_MANAGER' and
          sp.security_permission_id in ('GET_DISCIPLINE',
                                        'GET_DISCIPLINE_BY_COLLEGE_ID',
                                        'UPDATE_PLACEMENT',
                                        'CREATE_PLACEMENT',
                                        'VIEW_PROCTOR_DASHBOARD',
                                        'GET_COMPETENCY_GROUPS_BY_CLASS_ID',
                                        'VIEW_SUBJECT_AREA_RULES',
                                        'UPDATE_COMPETENCY_GROUP',
                                        'VIEW_COMPETENCY_MAP_DISCIPLINES',
                                        'GET_COMPETENCY_GROUP',
                                        'CREATE_COMPETENCY_GROUP',
                                        'CREATE_DISCIPLINE',
                                        'GET_DISCIPLINE',
                                        'UPDATE_DISCIPLINE',
                                        'GET_SEQUENCE',
                                        'UPDATE_SEQUENCE',
                                        'CREATE_COURSE',
                                        'DELETE_COURSE',
                                        'GET_ALL_COURSES_FOR_SEQUENCE',
                                        'UPDATE_SUBJECT_AREA_RULES',
                                        'DELETE_DISCIPLINE',
                                        'GET_ALL_COURSES_FOR_DISCIPLINE',
                                        'GET_DISCIPLINE_BY_COLLEGE_ID',
                                        'GET_MMAP_EQUIVALENTS',
                                        'UPDATE_COURSE',
                                        'DELETE_COMPETENCY_GROUP');

-- update counselor
delete from security_group_security_permission
    where security_group_id = 11
    and security_permission_id in ('UPDATE_PLACEMENT', 'CREATE_PLACEMENT');

-- update faculty
delete from security_group_security_permission
    where security_group_id = 5;
insert into security_group_security_permission (security_group_id, security_permission_id) values
    (5, 'VIEW_INSTRUCTOR_CLASS_REPORT'),
    (5, 'VIEW_ADMIN_DASHBOARD');

-- update local_admin
delete from security_group_security_permission
    where security_group_id = 2;
insert into security_group_security_permission (security_group_id, security_permission_id) values
    (2, 'VIEW_DISTRICTS_AND_COLLEGES_FOR_CURRENT_USER_WITH_ALL_TEST_LOCATIONS'),
    (2, 'VIEW_USERS_BY_TEST_LOCATION'),
    (2, 'VIEW_USERS_BY_COLLEGE'),
    (2, 'CREATE_TEST_LOCATION'),
    (2, 'VIEW_ADMIN_DASHBOARD'),
    (2, 'USER_SEARCH'),
    (2, 'VIEW_ROLES'),
    (2, 'CREATE_USER'),
    (2, 'VIEW_COLLEGES_WITH_ALL_LOCATIONS'),
    (2, 'EDIT_USER'),
    (2, 'ENABLE_TEST_LOCATION'),
    (2, 'VIEW_ASSESSMENTS_BY_LOCATION_AND_COLLEGE_AFFILIATION');

-- update proctor
delete from security_group_security_permission
    where security_group_id = 3
    and security_permission_id in ('GENERATE_REMOTE_PASSCOTE_FOR_TEST_EVENT',
                                   'TRACK_USER_HISTORY',
                                   'VIEW_ACCOMMODATIONS',
                                   'VIEW_TEST_EVENT',
                                   'VIEW_TEST_EVENT_BY_COLLEGE',
                                   'CREATE_TEST_EVENT',
                                   'UPDATE_TEST_EVENT',
                                   'CANCEL_TEST_EVENT',
                                   'VIEW_COMPETENCY_MASTERY',
                                   'VIEW_PLACEMENT_DECISION');

-- add remmote_proctoring_manager
insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name = 'REMOTE_PROCTORING_MANAGER' and
          sp.security_permission_id in ('USER_SEARCH',
                                        'VIEW_ASSESSMENTS_BY_LOCATION',
                                        'CREATE_TEST_EVENT',
                                        'VIEW_TEST_EVENT',
                                        'UPDATE_TEST_EVENT',
                                        'VIEW_TEST_EVENT_BY_COLLEGE',
                                        'CANCEL_TEST_EVENT',
                                        'GENERATE_REMOTE_PASSCODE_FOR_TEST_EVENT',
                                        'VIEW_REMOTE_PASSCODE');

-- update super_admin
insert into security_group_security_permission (security_group_id, security_permission_id) values
    (8, 'DELETE_STUDENT_COLLEGE_AFFILIATION'),
    (8, 'VIEW_ALL_STUDENT_COLLEGE_AFFILIATIONS'),
    (8, 'GENERATE_PLACEMENT_DECISION'),
    (8, 'VIEW_ASSESSMENT_ITEM_REVISIONS'),
    (8, 'VIEW_ASSESSMENT_REVISIONS');

-- remove update_placement and create_placement
delete from security_group_security_permission
    where security_permission_id in ('UPDATE_PLACEMENT', 'CREATE_PLACEMENT',
                                     'TRACK_USER_HISTORY', 'VIEW_ACCOMMODATIONS');
delete from security_permission
    where security_permission_id in ('UPDATE_PLACEMENT', 'CREATE_PLACEMENT',
                                     'TRACK_USER_HISTORY', 'VIEW_ACCOMMODATIONS');
