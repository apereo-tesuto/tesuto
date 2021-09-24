drop table activation_assessment_setting;
drop table activation;

create table activation (
       activation_id  varchar(100) primary key,
       user_id varchar(100) not null,
       assessment_id varchar(100) not null,
       location_id varchar(100),
       start_date timestamp not null,
       end_date timestamp not null,
       status integer not null,
       creator_id varchar(100),
       create_date timestamp not null,
       current_assessment_session_id varchar(100)
);


--Kind of wild guesses as to what indexes we need to support different kinds of  searches
--Assuming either a user_id or a location_id

create index activation_user_date_idx on activation (user_id, start_date, end_date, status);
create index activation_user_assessment_idx on activation (user_id, assessment_id, status);
create index activation_user_location_idx on activation (user_id, location_id, status);
create index activation_user_status_idx on activation (user_id, status);

create index activation_location_date_idx on activation (location_id, start_date, end_date, status);
create index activation_location_status_idx on activation (location_id, status);

create index activation_creator_idx on activation(creator_id, create_date);

create table activation_assessment_session (
       activation_id varchar(100) not null references activation,
       assessment_session_id varchar(100) not null
);

alter table activation_assessment_session add constraint activation_assessment_session_pk 
primary key (activation_id, assessment_session_id);

create index activation_assessment_session_idx on activation_assessment_session (assessment_session_id);

create table activation_status_change (
       activation_status_change_id serial primary key,
       activation_id varchar(100) not null references activation on delete cascade,
       change_date timestamp not null,
       new_status  integer not null,
       user_id varchar(100)
);
create index activation_status_change_activation_idx on activation_status_change(activation_id);


create table activation_attribute (
       activation_id varchar(100) not null references activation on delete cascade,
       name varchar(100) not null,
       value text
);

alter table activation_attribute add constraint activation_attribute_pk 
primary key (activation_id, name);







