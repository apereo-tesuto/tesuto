alter table test_event drop column acknowledged;

create table test_event_acknowledgement (
	   acknowledgement_id int primary key,
	   test_event_id int not null references test_event,
	   acknowledgement_date timestamp
);

create index acknowledegment_test_event_idx on test_event_acknowledgement(test_event_id);
