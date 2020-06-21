delete from test_event_assessment; -- there's no reasonable fix for any existing data
alter table test_event_assessment add namespace varchar(100);
alter table test_event_assessment drop constraint test_event_assessment_pk;
alter table test_event_assessment add constraint test_event_assessment_pk primary key (namespace, assessment_identifier);


