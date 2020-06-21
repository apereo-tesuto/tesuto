ALTER TABLE user_account ADD COLUMN deleted boolean;
UPDATE user_account SET deleted = false;
ALTER TABLE user_account ALTER COLUMN deleted SET NOT NULL;
ALTER TABLE user_account ALTER COLUMN deleted SET DEFAULT FALSE;

INSERT INTO security_permission (security_permission_id, created_on_date, last_updated_date, description) VALUES
    ('DELETE_USER', timestamp 'now', timestamp 'now', 'Allows user to mark a user as soft-deleted.');

INSERT INTO "security_group_security_permission" (security_permission_id, security_group_id)
    SELECT 'DELETE_USER', sg.security_group_id FROM security_group sg WHERE sg.group_name = 'LOCAL_ADMIN';
