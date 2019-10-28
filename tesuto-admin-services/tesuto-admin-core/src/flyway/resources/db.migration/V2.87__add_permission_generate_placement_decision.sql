insert into security_permission (security_permission_id, created_on_date, last_updated_date, description) values
    ('GENERATE_PLACEMENT_DECISION', timestamp 'now', timestamp 'now', 'Allow user request a placement decision be generate for an assessment session.');
   
insert into security_group_security_permission
(security_permission_id, security_group_id) values
    ('GENERATE_PLACEMENT_DECISION', 2);
    