insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('VIEW_COMPETENCY_MAP_DISCIPLINE_FROM_ENTRY_TESTLET', timestamp 'now', timestamp 'now', 'View the competency map discipline determined by the entry teslet.');

insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('ADMIN', 'COUNSELOR', 'STUDENT') and
          sp.security_permission_id in ('VIEW_COMPETENCY_MAP_DISCIPLINE_FROM_ENTRY_TESTLET');