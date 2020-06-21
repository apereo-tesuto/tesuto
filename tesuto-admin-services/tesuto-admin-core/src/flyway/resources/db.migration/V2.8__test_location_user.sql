-- CREATE A TABLE TO HOLD USER_LOCATION ASSOCIATION 
-- USER KEY IS NOT CREATED AS FOREIGN

CREATE TABLE user_account_test_location (
	user_account_id character varying(256) NOT NULL,
  	test_location_id integer NOT NULL,
  CONSTRAINT user_account_test_location_pkey PRIMARY KEY (test_location_id, user_account_id),
  CONSTRAINT fk_user_account_test_location_test_location_id FOREIGN KEY (test_location_id)
      REFERENCES test_location (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_user_account_test_location_user_account_id FOREIGN KEY (user_account_id)
      REFERENCES user_account (user_account_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
