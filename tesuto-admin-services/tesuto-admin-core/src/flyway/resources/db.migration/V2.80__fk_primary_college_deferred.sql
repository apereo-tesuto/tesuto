ALTER TABLE user_account DROP CONSTRAINT fk_primary_college;

ALTER TABLE user_account ADD CONSTRAINT fk_primary_college
    FOREIGN KEY (user_account_id, primary_college_id)
    REFERENCES user_account_college(user_account_id, college_ccc_id)
    deferrable initially deferred;
