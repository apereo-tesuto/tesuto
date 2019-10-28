CREATE TABLE report_assessment_structure (
  assessment_id character varying(100) NOT NULL,
  item_id character varying(100),
  assessment_scoped_identifier character varying(100) NOT NULL,
  assessment_scoped_id character varying(255) NOT NULL,
  item_scoped_identifier character varying(255) NOT NULL,
  parent_testlet_id character varying(100),
  report_order integer NOT NULL,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  response_structure character varying,
  CONSTRAINT report_assessment_structure_pkey PRIMARY KEY (assessment_id, item_id)
  
);

CREATE INDEX report_assessment_structure_assessment_scoped_identifier_idx
on report_assessment_structure(assessment_scoped_identifier);



CREATE TABLE report_attempt_record (
  attempt_id character varying(100) primary key,
  assessment_id character varying(100),
  cccid character varying(100) NOT NULL,
  test_location_id integer,
  test_location_label character varying(100),
  testlet_sequence character varying,
  start_date  timestamp  not null,
  completion_date  timestamp  not null,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone
);


CREATE TABLE report_response_record (
  attempt_id character varying(64) NOT NULL,
  item_id character varying(100),
  attempt_index integer,
  duration bigserial,
  responses character varying(50),
  responseIdentifiers character varying,
  single_character_response character(1),
  outcome_score double precision,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  
  CONSTRAINT report_response_record_pkey PRIMARY KEY (attempt_id, item_id, attempt_index),
  CONSTRAINT fk_attemtp_record_response_record FOREIGN KEY (attempt_id) REFERENCES report_attempt_record(attempt_id)
  
);

CREATE INDEX report_response_record_id_idx on report_response_record(attempt_id);
