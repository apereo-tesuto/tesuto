alter table security_group_api_security_permission add constraint security_group_api_security_permission_permission_fk foreign key (security_permission_id) references security_permission;

alter table security_group_api add constraint security_group_api_pk primary key (security_group_api_id);

alter table security_group_api_security_permission add constraint security_group_api_security_permission_group_fk foreign key (security_group_api_id) references security_group_api;

-- Create the API role for server to server communication.
insert into "security_group_api" (security_group_api_id, created_on_date, last_updated_date, description, group_name)
values (1, timestamp 'now', timestamp 'now', 'API security group', 'API');

insert into security_permission values ('CREATE_PLACEMENT_DECISION', current_timestamp, current_timestamp, 'Create a placement decision');

-- Relate these permissions and groups
insert into "security_group_api_security_permission" (security_permission_id, security_group_api_id)
values ('CREATE_PLACEMENT_DECISION', 1);

-- Create the API Mitre ID
insert into "user_account_api" (user_account_api_id, created_on_date, last_updated_date, account_locked, display_name, enabled, expired, failed_logins, last_login_date, password, username)
values ('assess_api', timestamp 'now', timestamp 'now', false, 'Assess APIs', true, false, 0, null, null, 'assess_api');

-- Relate this user to a security group
insert into "user_account_api_security_group_api" (user_account_api_id, security_group_api_id)
values ('assess_api', 1);

-- Preview Upload Mitre user
-- Create the API role for server to server communication.
insert into "security_group_api" (security_group_api_id, created_on_date, last_updated_date, description, group_name)
values (2, timestamp 'now', timestamp 'now', 'Preview Upload security group', 'Assessment Author');

-- Relate these permissions and groups
insert into "security_group_api_security_permission" (security_permission_id, security_group_api_id)
values ('UPLOAD_PREVIEW_ASSESSMENT_PACKAGE', 2);

-- Create the API Mitre ID
insert into "user_account_api" (user_account_api_id, created_on_date, last_updated_date, account_locked, display_name, enabled, expired, failed_logins, last_login_date, password, username)
values ('lsi_preview_upload', timestamp 'now', timestamp 'now', false, 'LSI Authoring', true, false, 0, null, null, 'lsi_preview_upload');

-- Relate this user to a security group
insert into "user_account_api_security_group_api" (user_account_api_id, security_group_api_id)
values ('lsi_preview_upload', 2);
