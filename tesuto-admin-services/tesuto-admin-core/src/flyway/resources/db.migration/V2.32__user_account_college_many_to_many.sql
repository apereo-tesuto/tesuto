create table user_account_college (
       user_account_id varchar(256) not null references user_account on delete cascade,
       college_ccc_id varchar(100) not null references college on delete cascade,
       primary key (user_account_id, college_ccc_id)
);

insert into user_account_college
select distinct u.user_account_id, loc.college_ccc_id
from user_account_test_location u
     inner join test_location loc on loc.id = u.test_location_id;

create index user_account_college_college_ix on user_account_college (college_ccc_id);

