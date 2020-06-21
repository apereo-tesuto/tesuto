alter table test_event add column uuid varchar(255);
create unique index test_event_uuid_idx on test_event(uuid);

