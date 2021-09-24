insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('PROCESSOR', timestamp 'now', timestamp 'now', 'Prccessor for paper and pencil scoring members');

-- Create the faculty group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (9, timestamp 'now', timestamp 'now', 'Prccessor for paper and pencil scoring security group', 'PROCESSOR');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('PROCESSOR', 9);
