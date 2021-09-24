ALTER TABLE user_account_test_location
    DROP CONSTRAINT fk_user_account_test_location_test_location_id;
ALTER TABLE test_location_assessment
    DROP CONSTRAINT fk_test_location_assessment_test_location_id;
ALTER TABLE test_location
    ALTER COLUMN id TYPE character varying(256);
ALTER TABLE user_account_test_location
    ALTER COLUMN test_location_id TYPE character varying(256);
ALTER TABLE test_location_assessment
    ALTER COLUMN test_location_id TYPE character varying(256);
ALTER TABLE user_account_test_location
    ADD CONSTRAINT fk_user_account_test_location_test_location_id 
    FOREIGN KEY (test_location_id) 
    REFERENCES test_location (id) 
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE test_location_assessment 
    ADD CONSTRAINT fk_test_location_assessment_test_location_id 
    FOREIGN KEY (test_location_id) 
    REFERENCES test_location (id) 
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
