insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_DISTRICTS_AND_COLLEGES_FOR_CURRENT_USER_WITH_ALL_TEST_LOCATIONS', timestamp 'now', timestamp 'now', 'View districts and colleges for a user including all test loctions');
update security_group_security_permission set security_permission_id = 'VIEW_DISTRICTS_AND_COLLEGES_FOR_CURRENT_USER_WITH_ALL_TEST_LOCATIONS'
    where security_permission_id = 'VIEW_DISTRICTS_WITH_ALL_COLLEGES_AND_LOCATIONS';
delete from security_permission where security_permission_id = 'VIEW_DISTRICTS_WITH_ALL_COLLEGES_AND_LOCATIONS';    
