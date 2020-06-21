
CREATE TABLE assessment_access (
  user_id character varying(100) NOT NULL,
  location_id int NOT NULL,
  assessment_identifier character varying(100) NOT NULL,
  assessment_namespace character varying(100) NOT NULL,
  active boolean NOT NULL,
  start_date timestamp without time zone,
  end_date timestamp without time zone,
  CONSTRAINT assessment_access_pkey PRIMARY KEY (user_id, location_id, assessment_identifier, assessment_namespace)
  
);

