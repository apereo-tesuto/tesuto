create table item_session_score (
  item_session_id varchar (100) not null,
  assessment_session_id VARCHAR (100) not null,
  item_id varchar (100) not null,
  points_earned double PRECISION NOT NULL,
  points_possible double precision NOT NULL,
  created_date TIMESTAMP NOT NULL,
  CONSTRAINT item_session_score_pkey PRIMARY KEY (item_session_id),
  CONSTRAINT item_session_score_fkey FOREIGN KEY (assessment_session_id) REFERENCES assessment_session_score (assessment_session_id)
);