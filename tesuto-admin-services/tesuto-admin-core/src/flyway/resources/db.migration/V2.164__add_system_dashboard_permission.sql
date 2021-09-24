insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values
        ('VIEW_SYSTEM_DASHBOARD', timestamp 'now', timestamp 'now', 'View the system dashboard');

insert into "security_group_security_permission" (security_permission_id, security_group_id) select 'VIEW_SYSTEM_DASHBOARD', g.security_group_id from security_group g where g.group_name = 'SUPER_ADMIN';        
