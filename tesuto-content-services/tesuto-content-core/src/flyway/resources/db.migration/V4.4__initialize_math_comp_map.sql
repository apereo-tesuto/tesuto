-- Competency maps. Unknown usage at this time.  Requirement that this data is stored in a database by pilot.
-- Added to content module under the belief that will eventually be used in conjunction with assessment data.

-- Inserts do not correspond to TEST data and are included here.

-- Attack plane for description is rather large at 1000 characters. However LSI descriptions are greater than 750 chars
-- May need to investigate.

CREATE TABLE math_comp_map (
  class_id character varying(100) NOT NULL,
  description character varying(1000) NOT NULL,
  CONSTRAINT math_comp_map_pkey PRIMARY KEY (class_id)
);