insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_COMPETENCY_MAP_DISCIPLINES', timestamp 'now', timestamp 'now', 'View all competency map disciplines');

--ADMIN
insert into security_group_security_permission (security_group_id, security_permission_id) values (2, 'VIEW_COMPETENCY_MAP_DISCIPLINES');