ALTER TABLE user_account ADD COLUMN primary_college_id varchar(100);

ALTER TABLE user_account ADD CONSTRAINT fk_primary_college 
    FOREIGN KEY (user_account_id, primary_college_id)
    REFERENCES user_account_college(user_account_id, college_ccc_id);

alter table user_account_college drop constraint user_account_college_user_account_id_fkey;

alter table user_account_college add constraint user_account_college_user_account_id_fkey foreign key (user_account_id) references user_account deferrable initially deferred;
