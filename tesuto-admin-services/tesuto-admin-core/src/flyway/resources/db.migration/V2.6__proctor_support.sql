-- Add Support for EPPN and CCCIDs

-- Remove constraints from only table with user_acount_id foreign keys
ALTER TABLE user_account_security_group DROP CONSTRAINT fk_7bxcp0xlw815mc0f81mojd0j1;

-- Adjust table to support new user account type
ALTER TABLE user_account_security_group ALTER COLUMN user_account_id TYPE character varying(255);
ALTER TABLE user_account_security_group ALTER COLUMN user_account_id SET NOT NULL;

-- user_account table 
ALTER TABLE user_account DROP CONSTRAINT user_account_pkey;
ALTER TABLE user_account ALTER COLUMN user_account_id TYPE character varying(256);

ALTER TABLE ONLY user_account ADD CONSTRAINT user_account_pkey PRIMARY KEY (user_account_id);

ALTER TABLE user_account ADD COLUMN last_name character varying(100);
-- 
--  ALTER TABLE ONLY user_account_security_group ADD CONSTRAINT user_account_security_group_pkey PRIMARY KEY (security_group_id, user_account_id);

-- restore foreign key constraint to user_account_security_group
ALTER TABLE user_account_security_group ADD CONSTRAINT fk_user_account_security_group_user_account_id FOREIGN KEY (user_account_id)
      REFERENCES user_account (user_account_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;