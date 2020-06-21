update user_account set username = user_account_id where username is null;
alter table user_account alter column username set not null;

