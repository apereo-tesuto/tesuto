insert into security_group_security_permission (security_group_id, security_permission_id)
    select sg.security_group_id, sp.security_permission_id
    from security_group sg, security_permission sp
    where sg.group_name in ('PLACEMENT_READ_ONLY', 'PLACEMENT_MANAGER') and
          sp.security_permission_id = 'VIEW_COMPETENCY_MAP';
