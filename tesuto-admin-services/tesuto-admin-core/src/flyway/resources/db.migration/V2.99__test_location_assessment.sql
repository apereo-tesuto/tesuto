CREATE TABLE test_location_assessment (
    test_location_id integer NOT NULL,
    assessment_identifier character varying(100) NOT NULL,
    CONSTRAINT test_location_assessment_pkey PRIMARY KEY (test_location_id, assessment_identifier),
    CONSTRAINT fk_test_location_assessment_test_location_id FOREIGN KEY (test_location_id)
        REFERENCES test_location (id)
);
