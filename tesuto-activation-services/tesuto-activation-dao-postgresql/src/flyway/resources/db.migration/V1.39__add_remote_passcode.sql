alter table test_event add column remote_passcode varchar(100);
alter table passcode_validation_attempt add column event_id int;
