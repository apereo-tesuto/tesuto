alter table activation add test_event_id integer references test_event;
create index activation_test_event_idx on activation(test_event_id);
