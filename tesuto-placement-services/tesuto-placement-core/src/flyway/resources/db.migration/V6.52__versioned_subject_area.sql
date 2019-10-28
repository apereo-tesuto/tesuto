create table versioned_subject_area (
       college_discipline_id int references college_discipline,
       version int,
       published boolean,
       json text
);

alter table versioned_subject_area add constraint versioned_subject_area_pk
primary key (college_discipline_id, version);

alter table college_discipline add column max_subject_area_version int;

alter table history_college_discipline add column max_subject_area_version int;
alter table history_college_discipline add column maxsubjectareaversion_mod boolean;

alter table college_discipline add constraint
college_discipline_versioned_subject_area_fk foreign key
(college_discipline_id, max_subject_area_version) references versioned_subject_area;

