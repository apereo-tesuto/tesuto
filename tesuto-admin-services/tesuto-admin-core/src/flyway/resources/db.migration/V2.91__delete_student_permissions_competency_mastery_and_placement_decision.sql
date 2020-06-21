delete from security_group_security_permission
    where security_permission_id = 'VIEW_MY_COMPETENCY_MASTERY' and
        security_group_id = 1;

delete from security_group_security_permission
    where security_permission_id = 'VIEW_CURRENT_USER_PLACEMENT_DECISION' and
        security_group_id = 1;