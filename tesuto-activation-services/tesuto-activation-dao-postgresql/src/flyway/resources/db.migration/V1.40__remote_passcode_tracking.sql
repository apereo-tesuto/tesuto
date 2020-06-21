
alter table passcode_record add column test_event_id int references test_event
on delete cascade; 

alter table passcode_record alter column user_id drop not null;
alter table passcode_record alter column type drop not null;

