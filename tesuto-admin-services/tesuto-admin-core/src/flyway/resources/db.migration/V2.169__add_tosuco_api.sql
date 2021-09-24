-- Create the TOSUCO API Mitre ID
insert into "user_account_api" (user_account_api_id, created_on_date, last_updated_date, account_locked, display_name, enabled, expired, failed_logins, last_login_date, password, username)
values ('tesuto_api', timestamp 'now', timestamp 'now', false, 'Tesuto APIs', true, false, 0, null, null, 'tesuto_api');

-- Relate this user to a security group
insert into "user_account_api_security_group_api" (user_account_api_id, security_group_api_id)
values ('tesuto_api', 1);
