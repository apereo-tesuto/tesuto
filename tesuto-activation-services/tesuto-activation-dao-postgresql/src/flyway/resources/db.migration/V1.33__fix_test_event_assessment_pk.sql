alter table test_event_assessment drop constraint test_event_assessment_pk;
alter table test_event_assessment add constraint test_event_assessment_pk primary key (test_event_id, namespace, assessment_identifier);
