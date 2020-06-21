create sequence test_event_sequence;

create table test_event (
   test_event_id int default nextval('test_event_sequence') primary key,
   start_date timestamp not null,
   end_date timestamp not null,
   college_id varchar(100) not null,
   test_location_id int not null,
   proctor_first_name varchar(64) not null,
   proctor_last_name varchar(64) not null,
   proctor_email varchar(100) not null,
   proctor_phone  varchar(100),
   create_date timestamp not null,
   created_by varchar(100),
   update_date timestamp not null,
   updated_by varchar(100)
);

create table test_event_assessment (
  test_event_id int references test_event,
  assessment_identifier varchar(100)
);

alter table test_event_assessment add
constraint test_event_assessment_pk primary key (test_event_id, assessment_identifier);
