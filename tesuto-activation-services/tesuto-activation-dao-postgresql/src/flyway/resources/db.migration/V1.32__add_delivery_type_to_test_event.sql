alter table test_event add delivery_type varchar(100);
update test_event set delivery_type='ONLINE';
alter table test_event alter delivery_type set not null;
