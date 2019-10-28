insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_COLLEGES_WITH_ALL_LOCATIONS', timestamp 'now', timestamp 'now', 'View colleges with all their locations, unfiltered by user location access.');

--ADMIN
insert into security_group_security_permission (security_group_id, security_permission_id) values (2, 'VIEW_COLLEGES_WITH_ALL_LOCATIONS');