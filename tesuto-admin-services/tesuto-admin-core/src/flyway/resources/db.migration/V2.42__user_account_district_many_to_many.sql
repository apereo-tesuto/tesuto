create table user_account_district (
       user_account_id varchar(256) not null references user_account on delete cascade,
       ccc_id varchar(100) not null references district on delete cascade,
       primary key (user_account_id, ccc_id)
);

create index user_account_district_district_ix on user_account_district (ccc_id);

