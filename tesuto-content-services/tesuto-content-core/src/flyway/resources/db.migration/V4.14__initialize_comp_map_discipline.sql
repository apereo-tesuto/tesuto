-- Table will hold the supported map disciplines.
CREATE TABLE competency_map_discipline (
  competency_map_discipline_id SERIAL,
  discipline_name character varying(100) NOT NULL,
  CONSTRAINT competency_map_discipline_pkey
  PRIMARY KEY (competency_map_discipline_id, discipline_name)
);

insert into competency_map_discipline (discipline_name)
values ('ESL');

insert into competency_map_discipline (discipline_name)
values ('ENGLISH');

insert into competency_map_discipline (discipline_name)
values ('MATH');