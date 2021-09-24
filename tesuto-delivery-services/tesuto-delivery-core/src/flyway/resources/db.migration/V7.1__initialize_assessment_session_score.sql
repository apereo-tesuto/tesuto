create table assessment_session_score (
  assessment_session_id varchar (100) not null PRIMARY KEY,
  points_earned double PRECISION NOT NULL,
  points_possible double precision NOT NULL,
  user_id varchar (100) not null,
  assessment_id varchar (100) not null,
  created_date TIMESTAMP NOT NULL
);