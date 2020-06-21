alter table passcode_validation_attempt add column result int not null default 1;
update passcode_validation_attempt set result=0 where is_successful;
alter table passcode_validation_attempt drop column is_successful;

