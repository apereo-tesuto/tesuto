ALTER TABLE test_location_assessment DROP CONSTRAINT test_location_assessment_pkey;
ALTER TABLE test_location_assessment ADD COLUMN assessment_namespace character varying(100) NOT NULL;
ALTER TABLE test_location_assessment ADD CONSTRAINT test_location_assessment_pkey PRIMARY KEY (test_location_id, assessment_identifier, assessment_namespace);
